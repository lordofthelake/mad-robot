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

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Iterator;
import java.util.Map;
import java.util.WeakHashMap;

import com.madrobot.di.wizard.xml.core.JVM;
import com.madrobot.reflect.ClassUtils;

/**
 * Pure Java ObjectFactory that instantiates objects using standard Java reflection, however the types of objects that
 * can be constructed are limited.
 * <p/>
 * Can newInstance: classes with public visibility, outer classes, static inner classes, classes with default
 * constructors and any class that implements java.io.Serializable. Cannot newInstance: classes without public
 * visibility, non-static inner classes, classes without default constructors. Note that any code in the constructor of
 * a class will be executed when the ObjectFactory instantiates the object.
 * </p>
 * 
 * @author Joe Walnes
 */
public class PureJavaReflectionProvider implements ReflectionProvider {

	protected FieldDictionary fieldDictionary;
	private transient Map serializedDataCache = new WeakHashMap();

	public PureJavaReflectionProvider() {
		this(new FieldDictionary(new ImmutableFieldKeySorter()));
	}

	public PureJavaReflectionProvider(FieldDictionary fieldDictionary) {
		this.fieldDictionary = fieldDictionary;
	}

	@Override
	public boolean fieldDefinedInClass(String fieldName, Class type) {
		Field field = fieldDictionary.fieldOrNull(type, fieldName, null);
		return field != null && (fieldModifiersSupported(field) || Modifier.isTransient(field.getModifiers()));
	}

	protected boolean fieldModifiersSupported(Field field) {
		int modifiers = field.getModifiers();
		return !(Modifier.isStatic(modifiers) || Modifier.isTransient(modifiers));
	}

	@Override
	public Field getField(Class definedIn, String fieldName) {
		return fieldDictionary.field(definedIn, fieldName, null);
	}

	@Override
	public Class getFieldType(Object object, String fieldName, Class definedIn) {
		return fieldDictionary.field(object.getClass(), fieldName, definedIn).getType();
	}

	@Override
	public Object newInstance(Class type) {
		return ClassUtils.newInstance(type);
	}

	protected Object readResolve() {
		serializedDataCache = new WeakHashMap();
		return this;
	}

	public void setFieldDictionary(FieldDictionary dictionary) {
		this.fieldDictionary = dictionary;
	}

	protected void validateFieldAccess(Field field) {
		if (Modifier.isFinal(field.getModifiers())) {
			if (JVM.is15()) {
				field.setAccessible(true);
			} else {
				throw new ObjectAccessException("Invalid final field " + field.getDeclaringClass().getName() + "."
						+ field.getName());
			}
		}
	}

	@Override
	public void visitSerializableFields(Object object, ReflectionProvider.Visitor visitor) {
		for (Iterator iterator = fieldDictionary.fieldsFor(object.getClass()); iterator.hasNext();) {
			Field field = (Field) iterator.next();
			if (!fieldModifiersSupported(field)) {
				continue;
			}
			validateFieldAccess(field);
			try {
				Object value = field.get(object);
				visitor.visit(field.getName(), field.getType(), field.getDeclaringClass(), value);
			} catch (IllegalArgumentException e) {
				throw new ObjectAccessException("Could not get field " + field.getClass() + "." + field.getName(), e);
			} catch (IllegalAccessException e) {
				throw new ObjectAccessException("Could not get field " + field.getClass() + "." + field.getName(), e);
			}
		}
	}

	@Override
	public void writeField(Object object, String fieldName, Object value, Class definedIn) {
		Field field = fieldDictionary.field(object.getClass(), fieldName, definedIn);
		validateFieldAccess(field);
		try {
			field.set(object, value);
		} catch (IllegalArgumentException e) {
			throw new ObjectAccessException("Could not set field " + object.getClass() + "." + field.getName(), e);
		} catch (IllegalAccessException e) {
			throw new ObjectAccessException("Could not set field " + object.getClass() + "." + field.getName(), e);
		}
	}

}
