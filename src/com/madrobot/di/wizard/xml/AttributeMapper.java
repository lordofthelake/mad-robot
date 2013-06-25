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

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.madrobot.di.wizard.xml.converters.Converter;
import com.madrobot.di.wizard.xml.converters.ConverterLookup;
import com.madrobot.di.wizard.xml.converters.ReflectionProvider;
import com.madrobot.di.wizard.xml.converters.SingleValueConverter;

/**
 * Mapper that allows the usage of attributes for fields and corresponding types or specified arbitrary types. It is
 * responsible for the lookup of the {@link SingleValueConverter} for item types and attribute names.
 * 
 * @since 1.2
 */
class AttributeMapper extends MapperWrapper {

	private ConverterLookup converterLookup;
	private final Map fieldNameToTypeMap = new HashMap();
	private final Set fieldToUseAsAttribute = new HashSet();
	private ReflectionProvider reflectionProvider;
	private final Set typeSet = new HashSet();

	/**
	 * @deprecated As of 1.3
	 */
	@Deprecated
	AttributeMapper(Mapper wrapped) {
		this(wrapped, null, null);
	}

	AttributeMapper(Mapper wrapped, ConverterLookup converterLookup, ReflectionProvider refProvider) {
		super(wrapped);
		this.converterLookup = converterLookup;
		this.reflectionProvider = refProvider;
	}

	public void addAttributeFor(final Class type) {
		typeSet.add(type);
	}

	/**
	 * Tells this mapper to use an attribute for this field.
	 * 
	 * @param definedIn
	 *            the declaring class of the field
	 * @param fieldName
	 *            the name of the field
	 * @throws IllegalArgumentException
	 *             if the field does not exist
	 * @since 1.3
	 */
	public void addAttributeFor(Class definedIn, String fieldName) {
		fieldToUseAsAttribute.add(reflectionProvider.getField(definedIn, fieldName));
	}

	/**
	 * Tells this mapper to use an attribute for this field.
	 * 
	 * @param field
	 *            the field itself
	 * @since 1.2.2
	 */
	public void addAttributeFor(Field field) {
		fieldToUseAsAttribute.add(field);
	}

	void addAttributeFor(final String fieldName, final Class type) {
		fieldNameToTypeMap.put(fieldName, type);
	}

	/**
	 * @deprecated As of 1.3.1, use {@link #getConverterFromAttribute(Class, String, Class)}
	 */
	@Deprecated
	@Override
	public SingleValueConverter getConverterFromAttribute(Class definedIn, String attribute) {
		Field field = reflectionProvider.getField(definedIn, attribute);
		return getConverterFromAttribute(definedIn, attribute, field.getType());
	}

	@Override
	public SingleValueConverter getConverterFromAttribute(Class definedIn, String attribute, Class type) {
		if (shouldLookForSingleValueConverter(attribute, type, definedIn)) {
			SingleValueConverter converter = getLocalConverterFromItemType(type);
			if (converter != null) {
				return converter;
			}
		}
		return super.getConverterFromAttribute(definedIn, attribute, type);
	}

	/**
	 * @deprecated As of 1.3, use {@link #getConverterFromAttribute(Class, String, Class)}
	 */
	@Deprecated
	@Override
	public SingleValueConverter getConverterFromAttribute(String attributeName) {
		SingleValueConverter converter = null;
		Class type = (Class) fieldNameToTypeMap.get(attributeName);
		if (type != null) {
			converter = getLocalConverterFromItemType(type);
		}
		return converter;
	}

	/**
	 * @deprecated As of 1.3, use {@link #getConverterFromItemType(String, Class, Class)}
	 */
	@Deprecated
	@Override
	public SingleValueConverter getConverterFromItemType(Class type) {
		if (typeSet.contains(type)) {
			return getLocalConverterFromItemType(type);
		} else {
			return null;
		}
	}

	/**
	 * @deprecated As of 1.3, use {@link #getConverterFromItemType(String, Class, Class)}
	 */
	@Deprecated
	@Override
	public SingleValueConverter getConverterFromItemType(String fieldName, Class type) {
		if (fieldNameToTypeMap.get(fieldName) == type) {
			return getLocalConverterFromItemType(type);
		} else {
			return null;
		}
	}

	@Override
	public SingleValueConverter getConverterFromItemType(String fieldName, Class type, Class definedIn) {
		if (shouldLookForSingleValueConverter(fieldName, type, definedIn)) {
			SingleValueConverter converter = getLocalConverterFromItemType(type);
			if (converter != null) {
				return converter;
			}
		}
		return super.getConverterFromItemType(fieldName, type, definedIn);
	}

	private SingleValueConverter getLocalConverterFromItemType(Class type) {
		Converter converter = converterLookup.lookupConverterForType(type);
		if (converter != null && converter instanceof SingleValueConverter) {
			return (SingleValueConverter) converter;
		} else {
			return null;
		}
	}

	/**
	 * @deprecated As of 1.3
	 */
	@Deprecated
	void setConverterLookup(ConverterLookup converterLookup) {
		this.converterLookup = converterLookup;
	}

	public boolean shouldLookForSingleValueConverter(String fieldName, Class type, Class definedIn) {
		Field field = reflectionProvider.getField(definedIn, fieldName);
		return fieldToUseAsAttribute.contains(field) || fieldNameToTypeMap.get(fieldName) == type
				|| typeSet.contains(type);
	}
}
