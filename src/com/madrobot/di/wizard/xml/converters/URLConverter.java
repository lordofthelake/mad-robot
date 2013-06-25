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

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Converts a java.net.URL to a string.
 * 
 */
public class URLConverter extends AbstractSingleValueConverter {

	@Override
	public boolean canConvert(Class type) {
		return type.equals(URL.class);
	}

	@Override
	public Object fromString(String str) {
		try {
			return new URL(str);
		} catch (MalformedURLException e) {
			throw new ConversionException(e);
		}
	}

}
