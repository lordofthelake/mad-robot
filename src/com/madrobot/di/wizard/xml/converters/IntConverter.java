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
 * Converts an int primitive or java.lang.Integer wrapper to a String.
 * 
 */
public class IntConverter extends AbstractSingleValueConverter {

	@Override
	public boolean canConvert(Class type) {
		return type.equals(int.class) || type.equals(Integer.class);
	}

	@Override
	public Object fromString(String str) {
		long value = Long.decode(str).longValue();
		if (value < Integer.MIN_VALUE || value > 0xFFFFFFFFl) {
			throw new NumberFormatException("For input string: \"" + str + '"');
		}
		return new Integer((int) value);
	}

}
