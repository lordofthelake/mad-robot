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

import java.util.UUID;

/**
 * Converts a java.util.UUID to a string.
 * 
 * @author J&ouml;rg Schaible
 */
public class UUIDConverter extends AbstractSingleValueConverter {

	@Override
	public boolean canConvert(Class type) {
		return type.equals(UUID.class);
	}

	@Override
	public Object fromString(String str) {
		try {
			return UUID.fromString(str);
		} catch (IllegalArgumentException e) {
			throw new ConversionException("Cannot create UUID instance", e);
		}
	}

}
