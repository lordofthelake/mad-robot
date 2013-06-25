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

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import com.madrobot.di.wizard.xml.converters.EnumSingleValueConverter;
import com.madrobot.di.wizard.xml.converters.SingleValueConverter;
import com.madrobot.di.wizard.xml.core.Caching;

/**
 * Mapper that handles the special case of polymorphic enums in Java 1.5. This renames MyEnum$1 to MyEnum making it less
 * bloaty in the XML and avoiding the need for an alias per enum value to be specified. Additionally every enum is
 * treated automatically as immutable type and can be written as attribute.
 * 
 */
class EnumMapper extends MapperWrapper implements Caching {

	private transient AttributeMapper attributeMapper;
	private transient Map<Class, SingleValueConverter> enumConverterMap;

	// /**
	// * @deprecated As of 1.3.1, use {@link #EnumMapper(Mapper)}
	// */
	// @Deprecated
	// EnumMapper(Mapper wrapped, ConverterLookup lookup) {
	// super(wrapped);
	// readResolve();
	// }

	public EnumMapper(Mapper wrapped) {
		super(wrapped);
		readResolve();
	}

	@Override
	public void flushCache() {
		if (enumConverterMap.size() > 0) {
			synchronized (enumConverterMap) {
				enumConverterMap.clear();
			}
		}
	}

	@Override
	public SingleValueConverter getConverterFromAttribute(Class definedIn, String attribute, Class type) {
		SingleValueConverter converter = getLocalConverter(attribute, type, definedIn);
		return converter == null ? super.getConverterFromAttribute(definedIn, attribute, type) : converter;
	}

	@Override
	public SingleValueConverter getConverterFromItemType(String fieldName, Class type, Class definedIn) {
		SingleValueConverter converter = getLocalConverter(fieldName, type, definedIn);
		return converter == null ? super.getConverterFromItemType(fieldName, type, definedIn) : converter;
	}

	private SingleValueConverter getLocalConverter(String fieldName, Class type, Class definedIn) {
		if (attributeMapper != null && Enum.class.isAssignableFrom(type)
				&& attributeMapper.shouldLookForSingleValueConverter(fieldName, type, definedIn)) {
			synchronized (enumConverterMap) {
				SingleValueConverter singleValueConverter = enumConverterMap.get(type);
				if (singleValueConverter == null) {
					singleValueConverter = super.getConverterFromItemType(fieldName, type, definedIn);
					if (singleValueConverter == null) {
						@SuppressWarnings("unchecked")
						Class<? extends Enum> enumType = type;
						singleValueConverter = new EnumSingleValueConverter(enumType);
					}
					enumConverterMap.put(type, singleValueConverter);
				}
				return singleValueConverter;
			}
		}
		return null;
	}

	@Override
	public boolean isImmutableValueType(Class type) {
		return (Enum.class.isAssignableFrom(type)) || super.isImmutableValueType(type);
	}

	private Object readResolve() {
		this.enumConverterMap = new HashMap<Class, SingleValueConverter>();
		this.attributeMapper = (AttributeMapper) lookupMapperOfType(AttributeMapper.class);
		return this;
	}

	@Override
	public String serializedClass(Class type) {
		if (type == null) {
			return super.serializedClass(type);
		}
		if (Enum.class.isAssignableFrom(type) && type.getSuperclass() != Enum.class) {
			return super.serializedClass(type.getSuperclass());
		} else if (EnumSet.class.isAssignableFrom(type)) {
			return super.serializedClass(EnumSet.class);
		} else {
			return super.serializedClass(type);
		}
	}
}
