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

import com.madrobot.di.wizard.xml.io.HierarchicalStreamReader;
import com.madrobot.di.wizard.xml.io.HierarchicalStreamWriter;

/**
 * Converter for JDK 1.5 enums. Combined with EnumMapper this also deals with polymorphic enums.
 * 
 * @author Eric Snell
 * @author Bryan Coleman
 */
public class EnumConverter implements Converter {

	@Override
	public boolean canConvert(Class type) {
		return type.isEnum() || Enum.class.isAssignableFrom(type);
	}

	@Override
	public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
		writer.setValue(((Enum) source).name());
	}

	@Override
	@SuppressWarnings("unchecked")
	public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
		Class type = context.getRequiredType();
		if (type.getSuperclass() != Enum.class) {
			type = type.getSuperclass(); // polymorphic enums
		}
		return Enum.valueOf(type, reader.getValue());
	}

}
