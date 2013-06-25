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

package com.madrobot.di.wizard.xml.converters;

import java.util.HashSet;
import java.util.Set;

import com.madrobot.di.wizard.xml.Mapper;
import com.madrobot.di.wizard.xml.core.FastField;
import com.madrobot.di.wizard.xml.io.ExtendedHierarchicalStreamWriterHelper;
import com.madrobot.di.wizard.xml.io.HierarchicalStreamReader;
import com.madrobot.di.wizard.xml.io.HierarchicalStreamWriter;

/**
 * Can convert any bean with a public default constructor. The {@link BeanProvider} used as default is based on
 * {@link java.beans.BeanInfo}. Indexed properties are currently not supported.
 */
public class JavaBeanConverter implements Converter {

	/**
	 * @deprecated As of 1.3
	 */
	@Deprecated
	public static class DuplicateFieldException extends ConversionException {
		public DuplicateFieldException(String msg) {
			super(msg);
		}
	}
	/**
	 * Exception to indicate double processing of a property to avoid silent clobbering.
	 * 
	 * @author J&ouml;rg Schaible
	 * @since 1.4.2
	 */
	public static class DuplicatePropertyException extends ConversionException {
		public DuplicatePropertyException(String msg) {
			super("Duplicate property " + msg);
			add("property", msg);
		}
	}
	protected final JavaBeanProvider beanProvider;

	/**
	 * @deprecated As of 1.3, no necessity for field anymore.
	 */
	@Deprecated
	private String classAttributeIdentifier;

	/*
	 * TODO: - support indexed properties - support attributes (XSTR-620) - support local converters (XSTR-601) Problem:
	 * Mappers take definitions based on reflection, they don't know about bean info
	 */
	protected final Mapper mapper;

	public JavaBeanConverter(Mapper mapper) {
		this(mapper, new BeanProvider());
	}

	public JavaBeanConverter(Mapper mapper, JavaBeanProvider beanProvider) {
		this.mapper = mapper;
		this.beanProvider = beanProvider;
	}

	/**
	 * @deprecated As of 1.3, use {@link #JavaBeanConverter(Mapper)} and
	 *             {@link com.madrobot.di.wizard.xml.XMLWizard#aliasAttribute(String, String)}
	 */
	@Deprecated
	public JavaBeanConverter(Mapper mapper, String classAttributeIdentifier) {
		this(mapper, new BeanProvider());
		this.classAttributeIdentifier = classAttributeIdentifier;
	}

	/**
	 * Only checks for the availability of a public default constructor. If you need stricter checks, subclass
	 * JavaBeanConverter
	 */
	@Override
	public boolean canConvert(Class type) {
		return beanProvider.canInstantiate(type);
	}

	private Class determineType(HierarchicalStreamReader reader, Object result, String fieldName) {
		final String classAttributeName = classAttributeIdentifier != null ? classAttributeIdentifier : mapper
				.aliasForSystemAttribute("class");
		String classAttribute = classAttributeName == null ? null : reader.getAttribute(classAttributeName);
		if (classAttribute != null) {
			return mapper.realClass(classAttribute);
		} else {
			return mapper.defaultImplementationOf(beanProvider.getPropertyType(result, fieldName));
		}
	}

	private Object instantiateNewInstance(UnmarshallingContext context) {
		Object result = context.currentObject();
		if (result == null) {
			result = beanProvider.newInstance(context.getRequiredType());
		}
		return result;
	}

	@Override
	public void marshal(final Object source, final HierarchicalStreamWriter writer, final MarshallingContext context) {
		final String classAttributeName = classAttributeIdentifier != null ? classAttributeIdentifier : mapper
				.aliasForSystemAttribute("class");
		beanProvider.visitSerializableProperties(source, new JavaBeanProvider.Visitor() {
			@Override
			public boolean shouldVisit(String name, Class definedIn) {
				return mapper.shouldSerializeMember(definedIn, name);
			}

			@Override
			public void visit(String propertyName, Class fieldType, Class definedIn, Object newObj) {
				if (newObj != null) {
					writeField(propertyName, fieldType, newObj, definedIn);
				}
			}

			private void writeField(String propertyName, Class fieldType, Object newObj, Class definedIn) {
				String serializedMember = mapper.serializedMember(source.getClass(), propertyName);
				ExtendedHierarchicalStreamWriterHelper.startNode(writer, serializedMember, fieldType);
				Class actualType = newObj.getClass();
				Class defaultType = mapper.defaultImplementationOf(fieldType);
				if (!actualType.equals(defaultType) && classAttributeName != null) {
					writer.addAttribute(classAttributeName, mapper.serializedClass(actualType));
				}
				context.convertAnother(newObj);

				writer.endNode();
			}
		});
	}

	@Override
	public Object unmarshal(final HierarchicalStreamReader reader, final UnmarshallingContext context) {
		final Object result = instantiateNewInstance(context);
		final Set seenProperties = new HashSet() {
			@Override
			public boolean add(Object e) {
				if (!super.add(e)) {
					throw new DuplicatePropertyException(((FastField) e).getName());
				}
				return true;
			}
		};

		Class resultType = result.getClass();
		while (reader.hasMoreChildren()) {
			reader.moveDown();

			String propertyName = mapper.realMember(resultType, reader.getNodeName());

			if (mapper.shouldSerializeMember(resultType, propertyName)) {
				boolean propertyExistsInClass = beanProvider.propertyDefinedInClass(propertyName, resultType);

				if (propertyExistsInClass) {
					Class type = determineType(reader, result, propertyName);
					Object value = context.convertAnother(result, type);
					beanProvider.writeProperty(result, propertyName, value);
					seenProperties.add(new FastField(resultType, propertyName));
				} else {
					throw new MissingFieldException(resultType.getName(), propertyName);
				}
			}
			reader.moveUp();
		}

		return result;
	}
}
