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
 * Converts the contents of a StringBuilder to XML.
 * 
 */
public class StringBuilderConverter extends AbstractSingleValueConverter {

	@Override
	public boolean canConvert(Class type) {
		return type.equals(StringBuilder.class);
	}

	@Override
	public Object fromString(String str) {
		return new StringBuilder(str);
	}
}
