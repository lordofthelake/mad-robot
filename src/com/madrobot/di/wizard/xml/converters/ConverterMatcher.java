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
 * ConverterMatcher allows to match converters to classes by determining if a given type can be converted by the
 * converter instance. ConverterMatcher is the base interface of any converter.
 * 
 * @see com.madrobot.di.wizard.xml.converters.Converter
 * @see com.madrobot.di.wizard.xml.converters.SingleValueConverter
 * @since 1.2
 */
public interface ConverterMatcher {

	/**
	 * Determines whether the converter can marshall a particular type.
	 * 
	 * @param type
	 *            the Class representing the object type to be converted
	 */
	boolean canConvert(Class type);

}
