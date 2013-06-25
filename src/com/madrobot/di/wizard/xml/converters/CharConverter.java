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

import com.madrobot.di.wizard.xml.io.HierarchicalStreamReader;
import com.madrobot.di.wizard.xml.io.HierarchicalStreamWriter;

/**
 * Converts a char primitive or java.lang.Character wrapper to a String. If char is \0 the representing String is empty.
 * 
 */
public class CharConverter implements Converter, SingleValueConverter {

	@Override
	public boolean canConvert(Class type) {
		return type.equals(char.class) || type.equals(Character.class);
	}

	@Override
	public Object fromString(String str) {
		if (str.length() == 0) {
			return new Character('\0');
		} else {
			return new Character(str.charAt(0));
		}
	}

	@Override
	public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
		writer.setValue(toString(source));
	}

	@Override
	public String toString(Object obj) {
		char ch = ((Character) obj).charValue();
		return ch == '\0' ? "" : obj.toString();
	}

	@Override
	public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
		String nullAttribute = reader.getAttribute("null");
		if (nullAttribute != null && nullAttribute.equals("true")) {
			return new Character('\0');
		} else {
			return fromString(reader.getValue());
		}
	}

}
