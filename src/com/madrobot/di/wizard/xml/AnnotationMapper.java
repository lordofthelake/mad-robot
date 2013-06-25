/*******************************************************************************
 * Copyright (c) 2012 MadRobot.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the GNU Lesser Public License v2.1
 *  which accompanies this distribution, and is available at
 *  http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 *  
 *  Contributors:
 *  Elton Kent - initial API and implementation
 ******************************************************************************/

package com.madrobot.di.wizard.xml;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.madrobot.di.wizard.xml.annotations.Alias;
import com.madrobot.di.wizard.xml.annotations.AsAttribute;
import com.madrobot.di.wizard.xml.annotations.Implicit;
import com.madrobot.di.wizard.xml.annotations.Include;
import com.madrobot.di.wizard.xml.annotations.OmitField;
import com.madrobot.di.wizard.xml.annotations.UseConverter;
import com.madrobot.di.wizard.xml.annotations.Converters;
import com.madrobot.di.wizard.xml.converters.Converter;
import com.madrobot.di.wizard.xml.converters.ConverterLookup;
import com.madrobot.di.wizard.xml.converters.ConverterMatcher;
import com.madrobot.di.wizard.xml.converters.ConverterRegistry;
import com.madrobot.di.wizard.xml.converters.ReflectionProvider;
import com.madrobot.di.wizard.xml.converters.SingleValueConverter;
import com.madrobot.di.wizard.xml.converters.SingleValueConverterWrapper;
import com.madrobot.di.wizard.xml.core.JVM;
import com.madrobot.reflect.ClassUtils;

/**
 * A mapper that uses annotations to prepare the remaining mappers in the chain.
 * 
 * @since 1.3
 */
class AnnotationMapper extends MapperWrapper implements AnnotationConfiguration {

	private final class UnprocessedTypesSet extends LinkedHashSet<Class<?>> {
		@Override
		public boolean add(Class<?> type) {
			if (type == null) {
				return false;
			}
			while (type.isArray()) {
				type = type.getComponentType();
			}
			final String name = type.getName();
			if (name.startsWith("java.") || name.startsWith("javax.")) {
				return false;
			}
			final boolean ret = annotatedTypes.contains(type) ? false : super.add(type);
			if (ret) {
				final Include inc = type.getAnnotation(Include.class);
				if (inc != null) {
					final Class<?>[] incTypes = inc.value();
					if (incTypes != null) {
						for (final Class<?> incType : incTypes) {
							add(incType);
						}
					}
				}
			}
			return ret;
		}
	}
	private final Set<Class<?>> annotatedTypes = new HashSet<Class<?>>();
	private final Object[] arguments;
	private final AttributeMapper attributeMapper;
	private final ClassAliasingMapper classAliasingMapper;
	private final Map<Class<?>, Map<List<Object>, Converter>> converterCache = new HashMap<Class<?>, Map<List<Object>, Converter>>();
	private final ConverterRegistry converterRegistry;
	private final DefaultImplementationsMapper defaultImplementationsMapper;
	private final FieldAliasingMapper fieldAliasingMapper;
	private final ImplicitCollectionMapper implicitCollectionMapper;
	private final LocalConversionMapper localConversionMapper;

	private boolean locked;

	/**
	 * Construct an AnnotationMapper.
	 * 
	 * @param wrapped
	 *            the next {@link Mapper} in the chain
	 * @since 1.3
	 */
	public AnnotationMapper(
			final Mapper wrapped,
			final ConverterRegistry converterRegistry,
			final ConverterLookup converterLookup,
			final ClassLoader classLoader,
			final ReflectionProvider reflectionProvider,
			final JVM jvm) {
		super(wrapped);
		this.converterRegistry = converterRegistry;
		annotatedTypes.add(Object.class);
		classAliasingMapper = (ClassAliasingMapper) lookupMapperOfType(ClassAliasingMapper.class);
		defaultImplementationsMapper = (DefaultImplementationsMapper) lookupMapperOfType(DefaultImplementationsMapper.class);
		implicitCollectionMapper = (ImplicitCollectionMapper) lookupMapperOfType(ImplicitCollectionMapper.class);
		fieldAliasingMapper = (FieldAliasingMapper) lookupMapperOfType(FieldAliasingMapper.class);
		attributeMapper = (AttributeMapper) lookupMapperOfType(AttributeMapper.class);
		localConversionMapper = (LocalConversionMapper) lookupMapperOfType(LocalConversionMapper.class);
		locked = true;
		arguments = new Object[] { this, classLoader, reflectionProvider, jvm, converterLookup };
	}

	private void addParametrizedTypes(Type type, final Set<Class<?>> types) {
		final Set<Type> processedTypes = new HashSet<Type>();
		final Set<Type> localTypes = new LinkedHashSet<Type>() {

			@Override
			public boolean add(final Type o) {
				if (o instanceof Class) {
					return types.add((Class<?>) o);
				}
				return o == null || processedTypes.contains(o) ? false : super.add(o);
			}

		};
		while (type != null) {
			processedTypes.add(type);
			if (type instanceof Class) {
				final Class<?> clazz = (Class<?>) type;
				types.add(clazz);
				if (!clazz.isPrimitive()) {
					final TypeVariable<?>[] typeParameters = clazz.getTypeParameters();
					for (final TypeVariable<?> typeVariable : typeParameters) {
						localTypes.add(typeVariable);
					}
					localTypes.add(clazz.getGenericSuperclass());
					for (final Type iface : clazz.getGenericInterfaces()) {
						localTypes.add(iface);
					}
				}
			} else if (type instanceof TypeVariable) {
				final TypeVariable<?> typeVariable = (TypeVariable<?>) type;
				final Type[] bounds = typeVariable.getBounds();
				for (final Type bound : bounds) {
					localTypes.add(bound);
				}
			} else if (type instanceof ParameterizedType) {
				final ParameterizedType parametrizedType = (ParameterizedType) type;
				localTypes.add(parametrizedType.getRawType());
				final Type[] actualArguments = parametrizedType.getActualTypeArguments();
				for (final Type actualArgument : actualArguments) {
					localTypes.add(actualArgument);
				}
			} else if (type instanceof GenericArrayType) {
				final GenericArrayType arrayType = (GenericArrayType) type;
				localTypes.add(arrayType.getGenericComponentType());
			}

			if (!localTypes.isEmpty()) {
				final Iterator<Type> iter = localTypes.iterator();
				type = iter.next();
				iter.remove();
			} else {
				type = null;
			}
		}
	}

	@Override
	public void autodetectAnnotations(final boolean mode) {
		locked = !mode;
	}

	private Converter cacheConverter(final UseConverter annotation, final Class targetType) {
		Converter result = null;
		final Object[] args;
		final List<Object> parameter = new ArrayList<Object>();
		if (targetType != null) {
			parameter.add(targetType);
		}
		final List<Object> arrays = new ArrayList<Object>();
		arrays.add(annotation.booleans());
		arrays.add(annotation.bytes());
		arrays.add(annotation.chars());
		arrays.add(annotation.doubles());
		arrays.add(annotation.floats());
		arrays.add(annotation.ints());
		arrays.add(annotation.longs());
		arrays.add(annotation.shorts());
		arrays.add(annotation.strings());
		arrays.add(annotation.types());
		for (Object array : arrays) {
			if (array != null) {
				int length = Array.getLength(array);
				for (int i = 0; i < length; i++) {
					Object object = Array.get(array, i);
					if (!parameter.contains(object)) {
						parameter.add(object);
					}
				}
			}
		}
		final Class<? extends ConverterMatcher> converterType = annotation.value();
		Map<List<Object>, Converter> converterMapping = converterCache.get(converterType);
		if (converterMapping != null) {
			result = converterMapping.get(parameter);
		}
		if (result == null) {
			int size = parameter.size();
			if (size > 0) {
				args = new Object[arguments.length + size];
				System.arraycopy(arguments, 0, args, size, arguments.length);
				System.arraycopy(parameter.toArray(new Object[size]), 0, args, 0, size);
			} else {
				args = arguments;
			}

			final BitSet usedArgs = new BitSet();
			final Converter converter;
			try {
				if (SingleValueConverter.class.isAssignableFrom(converterType)
						&& !Converter.class.isAssignableFrom(converterType)) {
					final SingleValueConverter svc = (SingleValueConverter) ClassUtils.newInstance(converterType, args,
							usedArgs);
					converter = new SingleValueConverterWrapper(svc);
				} else {
					converter = (Converter) ClassUtils.newInstance(converterType, args, usedArgs);
				}
			} catch (final Exception e) {
				throw new InitializationException("Cannot instantiate converter " + converterType.getName()
						+ (targetType != null ? " for type " + targetType.getName() : ""), e);
			}
			if (converterMapping == null) {
				converterMapping = new HashMap<List<Object>, Converter>();
				converterCache.put(converterType, converterMapping);
			}
			converterMapping.put(parameter, converter);
			result = converter;
		}
		return result;
	}

	@Override
	public Class defaultImplementationOf(final Class type) {
		if (!locked) {
			processAnnotations(type);
		}
		final Class defaultImplementation = super.defaultImplementationOf(type);
		if (!locked) {
			processAnnotations(defaultImplementation);
		}
		return defaultImplementation;
	}

	private Class<?> getClass(final Type typeArgument) {
		Class<?> type = null;
		if (typeArgument instanceof ParameterizedType) {
			type = (Class<?>) ((ParameterizedType) typeArgument).getRawType();
		} else if (typeArgument instanceof Class) {
			type = (Class<?>) typeArgument;
		}
		return type;
	}

	@Override
	public Converter getLocalConverter(final Class definedIn, final String fieldName) {
		if (!locked) {
			processAnnotations(definedIn);
		}
		return super.getLocalConverter(definedIn, fieldName);
	}

	private void processAliasAnnotation(final Class<?> type, final Set<Class<?>> types) {
		final Alias aliasAnnotation = type.getAnnotation(Alias.class);
		if (aliasAnnotation != null) {
			if (classAliasingMapper == null) {
				throw new InitializationException("No " + ClassAliasingMapper.class.getName() + " available");
			}
			if (aliasAnnotation.impl() != Void.class) {
				// Alias for Interface/Class with an impl
				classAliasingMapper.addClassAlias(aliasAnnotation.value(), type);
				defaultImplementationsMapper.addDefaultImplementation(aliasAnnotation.impl(), type);
				if (type.isInterface()) {
					types.add(aliasAnnotation.impl()); // alias Interface's impl
				}
			} else {
				classAliasingMapper.addClassAlias(aliasAnnotation.value(), type);
			}
		}
	}

	private void processAnnotations(final Class initialType) {
		if (initialType == null) {
			return;
		}
		synchronized (annotatedTypes) {
			final Set<Class<?>> types = new UnprocessedTypesSet();
			types.add(initialType);
			processTypes(types);
		}
	}

	@Override
	public void processAnnotations(final Class[] initialTypes) {
		if (initialTypes == null || initialTypes.length == 0) {
			return;
		}
		locked = true;
		synchronized (annotatedTypes) {
			final Set<Class<?>> types = new UnprocessedTypesSet();
			for (final Class initialType : initialTypes) {
				types.add(initialType);
			}
			processTypes(types);
		}
	}

	private void processAsAttributeAnnotation(final Field field) {
		final AsAttribute asAttributeAnnotation = field.getAnnotation(AsAttribute.class);
		if (asAttributeAnnotation != null) {
			if (attributeMapper == null) {
				throw new InitializationException("No " + AttributeMapper.class.getName() + " available");
			}
			attributeMapper.addAttributeFor(field);
		}
	}

	// @Deprecated
	// private void processImplicitCollectionAnnotation(final Class<?> type) {
	// final ImplicitCollection implicitColAnnotation = type
	// .getAnnotation(ImplicitCollection.class);
	// if (implicitColAnnotation != null) {
	// if (implicitCollectionMapper == null) {
	// throw new InitializationException("No "
	// + ImplicitCollectionMapper.class.getName()
	// + " available");
	// }
	// final String fieldName = implicitColAnnotation.value();
	// final String itemFieldName = implicitColAnnotation.item();
	// final Field field;
	// try {
	// field = type.getDeclaredField(fieldName);
	// } catch (final NoSuchFieldException e) {
	// throw new InitializationException(type.getName()
	// + " does not have a field named '" + fieldName
	// + "' as required by "
	// + ImplicitCollection.class.getName());
	// }
	// Class itemType = null;
	// final Type genericType = field.getGenericType();
	// if (genericType instanceof ParameterizedType) {
	// final Type typeArgument = ((ParameterizedType) genericType)
	// .getActualTypeArguments()[0];
	// itemType = getClass(typeArgument);
	// }
	// if (itemType == null) {
	// implicitCollectionMapper.add(type, fieldName, null,
	// Object.class);
	// } else {
	// if (itemFieldName.equals("")) {
	// implicitCollectionMapper.add(type, fieldName, null,
	// itemType);
	// } else {
	// implicitCollectionMapper.add(type, fieldName,
	// itemFieldName, itemType);
	// }
	// }
	// }
	// }

	private void processConverterAnnotations(final Class<?> type) {
		if (converterRegistry != null) {
			final Converters convertersAnnotation = type.getAnnotation(Converters.class);
			final UseConverter converterAnnotation = type.getAnnotation(UseConverter.class);
			final List<UseConverter> annotations = convertersAnnotation != null ? new ArrayList<UseConverter>(
					Arrays.asList(convertersAnnotation.value())) : new ArrayList<UseConverter>();
			if (converterAnnotation != null) {
				annotations.add(converterAnnotation);
			}
			for (final UseConverter annotation : annotations) {
				final Converter converter = cacheConverter(annotation, converterAnnotation != null ? type : null);
				if (converter != null) {
					if (converterAnnotation != null || converter.canConvert(type)) {
						converterRegistry.registerConverter(converter, annotation.priority());
					} else {
						throw new InitializationException("Converter " + annotation.value().getName()
								+ " cannot handle annotated class " + type.getName());
					}
				}
			}
		}
	}

	private void processFieldAliasAnnotation(final Field field) {
		final Alias aliasAnnotation = field.getAnnotation(Alias.class);
		if (aliasAnnotation != null) {
			if (fieldAliasingMapper == null) {
				throw new InitializationException("No " + FieldAliasingMapper.class.getName() + " available");
			}
			fieldAliasingMapper.addFieldAlias(aliasAnnotation.value(), field.getDeclaringClass(), field.getName());
		}
	}

	private void processImplicitAnnotation(final Field field) {
		final Implicit implicitAnnotation = field.getAnnotation(Implicit.class);
		if (implicitAnnotation != null) {
			if (implicitCollectionMapper == null) {
				throw new InitializationException("No " + ImplicitCollectionMapper.class.getName() + " available");
			}
			final String fieldName = field.getName();
			final String itemFieldName = implicitAnnotation.itemFieldName();
			final String keyFieldName = implicitAnnotation.keyFieldName();
			boolean isMap = Map.class.isAssignableFrom(field.getType());
			Class itemType = null;
			if (!field.getType().isArray()) {
				final Type genericType = field.getGenericType();
				if (genericType instanceof ParameterizedType) {
					final Type[] actualTypeArguments = ((ParameterizedType) genericType).getActualTypeArguments();
					final Type typeArgument = actualTypeArguments[isMap ? 1 : 0];
					itemType = getClass(typeArgument);
				}
			}
			if (isMap) {
				implicitCollectionMapper.add(field.getDeclaringClass(), fieldName,
						itemFieldName != null && !"".equals(itemFieldName) ? itemFieldName : null, itemType,
						keyFieldName != null && !"".equals(keyFieldName) ? keyFieldName : null);
			} else {
				if (itemFieldName != null && !"".equals(itemFieldName)) {
					implicitCollectionMapper.add(field.getDeclaringClass(), fieldName, itemFieldName, itemType);
				} else {
					implicitCollectionMapper.add(field.getDeclaringClass(), fieldName, itemType);
				}
			}
		}
	}

	private void processLocalConverterAnnotation(final Field field) {
		final UseConverter annotation = field.getAnnotation(UseConverter.class);
		if (annotation != null) {
			final Converter converter = cacheConverter(annotation, field.getType());
			if (converter != null) {
				if (localConversionMapper == null) {
					throw new InitializationException("No " + LocalConversionMapper.class.getName() + " available");
				}
				localConversionMapper.registerLocalConverter(field.getDeclaringClass(), field.getName(), converter);
			}
		}
	}

	private void processOmitFieldAnnotation(final Field field) {
		final OmitField omitFieldAnnotation = field.getAnnotation(OmitField.class);
		if (omitFieldAnnotation != null) {
			if (fieldAliasingMapper == null) {
				throw new InitializationException("No " + FieldAliasingMapper.class.getName() + " available");
			}
			fieldAliasingMapper.omitField(field.getDeclaringClass(), field.getName());
		}
	}

	private void processTypes(final Set<Class<?>> types) {
		while (!types.isEmpty()) {
			final Iterator<Class<?>> iter = types.iterator();
			final Class<?> type = iter.next();
			iter.remove();

			if (annotatedTypes.add(type)) {
				if (type.isPrimitive()) {
					continue;
				}

				addParametrizedTypes(type, types);

				processConverterAnnotations(type);
				processAliasAnnotation(type, types);

				if (type.isInterface()) {
					continue;
				}

				// processImplicitCollectionAnnotation(type);

				final Field[] fields = type.getDeclaredFields();
				for (int i = 0; i < fields.length; i++) {
					final Field field = fields[i];
					if (field.isEnumConstant() || (field.getModifiers() & (Modifier.STATIC | Modifier.TRANSIENT)) > 0) {
						continue;
					}

					addParametrizedTypes(field.getGenericType(), types);

					if (field.isSynthetic()) {
						continue;
					}

					processFieldAliasAnnotation(field);
					processAsAttributeAnnotation(field);
					processImplicitAnnotation(field);
					processOmitFieldAnnotation(field);
					processLocalConverterAnnotation(field);
				}
			}
		}
	}

	@Override
	public String realMember(final Class type, final String serialized) {
		if (!locked) {
			processAnnotations(type);
		}
		return super.realMember(type, serialized);
	}

	@Override
	public String serializedClass(final Class type) {
		if (!locked) {
			processAnnotations(type);
		}
		return super.serializedClass(type);
	}
}
