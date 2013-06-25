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
// If you are using an earlier version of Java, just don't try to build this class. XMLWizard should work fine without it.

package com.madrobot.di.wizard.xml.converters;

import java.lang.reflect.Field;
import java.util.EnumMap;

import com.madrobot.di.wizard.xml.Mapper;
import com.madrobot.di.wizard.xml.io.HierarchicalStreamReader;
import com.madrobot.di.wizard.xml.io.HierarchicalStreamWriter;
import com.madrobot.reflect.FieldUtils;

/**
 * Serializes an Java 5 EnumMap, including the type of Enum it's for. If a SecurityManager is set, the converter will
 * only work with permissions for SecurityManager.checkPackageAccess, SecurityManager.checkMemberAccess(this,
 * EnumSet.MEMBER) and ReflectPermission("suppressAccessChecks").
 * 
 * @author Joe Walnes
 */
public class EnumMapConverter extends MapConverter {

	private final static Field typeField;
	static {
		// field name is "keyType" in Sun JDK, but different in IKVM
		Field assumedTypeField = null;
		try {
			Field[] fields = EnumMap.class.getDeclaredFields();
			for (int i = 0; i < fields.length; i++) {
				if (fields[i].getType() == Class.class) {
					// take the fist member of type "Class"
					assumedTypeField = fields[i];
					assumedTypeField.setAccessible(true);
					break;
				}
			}
			if (assumedTypeField == null) {
				throw new ExceptionInInitializerError("Cannot detect key type of EnumMap");
			}

		} catch (SecurityException ex) {
			// ignore, no access possible with current SecurityManager
		}
		typeField = assumedTypeField;
	}

	public EnumMapConverter(Mapper mapper) {
		super(mapper);
	}

	@Override
	public boolean canConvert(Class type) {
		return typeField != null && type == EnumMap.class;
	}

	@Override
	public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
		Class type = null;
		try {
			type = (Class) FieldUtils.readField(typeField, source);
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String attributeName = mapper().aliasForSystemAttribute("enum-type");
		if (attributeName != null) {
			writer.addAttribute(attributeName, mapper().serializedClass(type));
		}
		super.marshal(source, writer, context);
	}

	@Override
	@SuppressWarnings("unchecked")
	public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
		String attributeName = mapper().aliasForSystemAttribute("enum-type");
		if (attributeName == null) {
			throw new ConversionException("No EnumType specified for EnumMap");
		}
		Class type = mapper().realClass(reader.getAttribute(attributeName));
		EnumMap map = new EnumMap(type);
		populateMap(reader, context, map);
		return map;
	}
}
