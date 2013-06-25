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
 * Converts a byte primitive or java.lang.Byte wrapper to a String.
 * 
 */
public class ByteConverter extends AbstractSingleValueConverter {

	@Override
	public boolean canConvert(Class type) {
		return type.equals(byte.class) || type.equals(Byte.class);
	}

	@Override
	public Object fromString(String str) {
		int value = Integer.decode(str).intValue();
		if (value < Byte.MIN_VALUE || value > 0xFF) {
			throw new NumberFormatException("For input string: \"" + str + '"');
		}
		return new Byte((byte) value);
	}

}
