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

/**
 * A single value converter for arbitrary enums. Converter is internally automatically instantiated for enum types.
 * 
 * @author J&ouml;rg Schaible
 * @since 1.3
 */
public class EnumSingleValueConverter extends AbstractSingleValueConverter {

	private final Class<? extends Enum> enumType;

	public EnumSingleValueConverter(Class<? extends Enum> type) {
		if (!Enum.class.isAssignableFrom(type) && type != Enum.class) {
			throw new IllegalArgumentException("Converter can only handle defined enums");
		}
		enumType = type;
	}

	@Override
	public boolean canConvert(Class type) {
		return enumType.isAssignableFrom(type);
	}

	@Override
	public Object fromString(String str) {
		@SuppressWarnings("unchecked")
		Enum result = Enum.valueOf(enumType, str);
		return result;
	}

	@Override
	public String toString(Object obj) {
		return Enum.class.cast(obj).name();
	}
}
