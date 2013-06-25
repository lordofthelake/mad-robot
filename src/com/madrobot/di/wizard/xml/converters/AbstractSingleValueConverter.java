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
 * Base abstract implementation of {@link com.madrobot.di.wizard.xml.converters.SingleValueConverter}.
 * <p/>
 * <p>
 * Subclasses should implement methods canConvert(Class) and fromString(String) for the conversion.
 * </p>
 * 
 * @author Joe Walnes
 * @author J&ouml;rg Schaible
 * @author Mauro Talevi
 * @see com.madrobot.di.wizard.xml.converters.SingleValueConverter
 */
public abstract class AbstractSingleValueConverter implements SingleValueConverter {

	@Override
	public abstract boolean canConvert(Class type);

	@Override
	public abstract Object fromString(String str);

	@Override
	public String toString(Object obj) {
		return obj == null ? null : obj.toString();
	}

}
