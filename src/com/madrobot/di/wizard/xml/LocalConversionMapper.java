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

import java.util.HashMap;
import java.util.Map;

import com.madrobot.di.wizard.xml.converters.Converter;
import com.madrobot.di.wizard.xml.converters.SingleValueConverter;
import com.madrobot.di.wizard.xml.core.FastField;

/**
 * A Mapper for locally defined converters for a member field.
 * 
 * @since 1.3
 */
class LocalConversionMapper extends MapperWrapper {

	private transient AttributeMapper attributeMapper;
	private final Map localConverters = new HashMap();

	/**
	 * Constructs a LocalConversionMapper.
	 * 
	 * @param wrapped
	 * @since 1.3
	 */
	LocalConversionMapper(Mapper wrapped) {
		super(wrapped);
		readResolve();
	}

	@Override
	public SingleValueConverter getConverterFromAttribute(Class definedIn, String attribute, Class type) {
		SingleValueConverter converter = getLocalSingleValueConverter(definedIn, attribute, type);
		return converter == null ? super.getConverterFromAttribute(definedIn, attribute, type) : converter;
	}

	@Override
	public SingleValueConverter getConverterFromItemType(String fieldName, Class type, Class definedIn) {
		SingleValueConverter converter = getLocalSingleValueConverter(definedIn, fieldName, type);
		return converter == null ? super.getConverterFromItemType(fieldName, type, definedIn) : converter;
	}

	@Override
	public Converter getLocalConverter(Class definedIn, String fieldName) {
		return (Converter) localConverters.get(new FastField(definedIn, fieldName));
	}

	private SingleValueConverter getLocalSingleValueConverter(Class definedIn, String fieldName, Class type) {
		if (attributeMapper != null && attributeMapper.shouldLookForSingleValueConverter(fieldName, type, definedIn)) {
			Converter converter = getLocalConverter(definedIn, fieldName);
			if (converter != null && converter instanceof SingleValueConverter) {
				return (SingleValueConverter) converter;
			}
		}
		return null;
	}

	private Object readResolve() {
		this.attributeMapper = (AttributeMapper) lookupMapperOfType(AttributeMapper.class);
		return this;
	}

	public void registerLocalConverter(Class definedIn, String fieldName, Converter converter) {
		localConverters.put(new FastField(definedIn, fieldName), converter);
	}
}
