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

// ***** READ THIS *****
// This class will only compile with JDK 1.5.0 or above as it test Java enums.
// If you are using an earlier version of Java, just don't try to build this class. XStream should work fine without it.

package com.madrobot.di.wizard.xml.converters;

import java.lang.reflect.Field;
import java.util.EnumSet;
import java.util.Iterator;

import com.madrobot.di.wizard.xml.Mapper;
import com.madrobot.di.wizard.xml.io.HierarchicalStreamReader;
import com.madrobot.di.wizard.xml.io.HierarchicalStreamWriter;
import com.madrobot.reflect.FieldUtils;

/**
 * Serializes a Java 5 EnumSet. If a SecurityManager is set, the converter will only work with permissions for
 * SecurityManager.checkPackageAccess, SecurityManager.checkMemberAccess(this, EnumSet.MEMBER) and
 * ReflectPermission("suppressAccessChecks").
 * 
 * @author Joe Walnes
 * @author J&ouml;rg Schaible
 */
public class EnumSetConverter implements Converter {

	private final static Field typeField;
	static {
		// field name is "elementType" in Sun JDK, but different in Harmony
		Field assumedTypeField = null;
		try {
			Field[] fields = EnumSet.class.getDeclaredFields();
			for (int i = 0; i < fields.length; i++) {
				if (fields[i].getType() == Class.class) {
					// take the fist member of type "Class"
					assumedTypeField = fields[i];
					assumedTypeField.setAccessible(true);
					break;
				}
			}
			if (assumedTypeField == null) {
				throw new ExceptionInInitializerError("Cannot detect element type of EnumSet");
			}
		} catch (SecurityException ex) {
			// ignore, no access possible with current SecurityManager
		}
		typeField = assumedTypeField;
	}

	private final Mapper mapper;

	public EnumSetConverter(Mapper mapper) {
		this.mapper = mapper;
	}

	@Override
	public boolean canConvert(Class type) {
		return typeField != null && EnumSet.class.isAssignableFrom(type);
	}

	private String joinEnumValues(EnumSet set) {
		boolean seenFirst = false;
		StringBuffer result = new StringBuffer();
		for (Iterator iterator = set.iterator(); iterator.hasNext();) {
			Enum value = (Enum) iterator.next();
			if (seenFirst) {
				result.append(',');
			} else {
				seenFirst = true;
			}
			result.append(value.name());
		}
		return result.toString();
	}

	@Override
	public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
		EnumSet set = (EnumSet) source;
		Class enumTypeForSet = null;
		try {
			enumTypeForSet = (Class) FieldUtils.readField(typeField, set);
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String attributeName = mapper.aliasForSystemAttribute("enum-type");
		if (attributeName != null) {
			writer.addAttribute(attributeName, mapper.serializedClass(enumTypeForSet));
		}
		writer.setValue(joinEnumValues(set));
	}

	@Override
	@SuppressWarnings("unchecked")
	public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
		String attributeName = mapper.aliasForSystemAttribute("enum-type");
		if (attributeName == null) {
			throw new ConversionException("No EnumType specified for EnumSet");
		}
		Class enumTypeForSet = mapper.realClass(reader.getAttribute(attributeName));
		EnumSet set = EnumSet.noneOf(enumTypeForSet);
		String[] enumValues = reader.getValue().split(",");
		for (int i = 0; i < enumValues.length; i++) {
			String enumValue = enumValues[i];
			if (enumValue.length() > 0) {
				set.add(Enum.valueOf(enumTypeForSet, enumValue));
			}
		}
		return set;
	}

}
