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

import java.nio.charset.Charset;

/**
 * Converts a java.nio.charset.Carset to a string.
 * 
 * @since 1.2
 */
public class CharsetConverter extends AbstractSingleValueConverter {

	@Override
	public boolean canConvert(Class type) {
		return Charset.class.isAssignableFrom(type);
	}

	@Override
	public Object fromString(String str) {
		return Charset.forName(str);
	}

	@Override
	public String toString(Object obj) {
		return obj == null ? null : ((Charset) obj).name();
	}
}
