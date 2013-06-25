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
 * SingleValueConverter implementations are marshallable to/from a single value String representation.
 * <p/>
 * <p>
 * {@link com.madrobot.di.wizard.xml.converters.AbstractSingleValueConverter} provides a starting point for objects that
 * can store all information in a single value String.
 * </p>
 * 
 * @see com.madrobot.di.wizard.xml.converters.Converter
 * @see com.madrobot.di.wizard.xml.converters.AbstractSingleValueConverter
 * @since 1.2
 */
public interface SingleValueConverter extends ConverterMatcher {

	/**
	 * Unmarshals an Object from its single value representation.
	 * 
	 * @param str
	 *            the String with the single value of the Object
	 * @return the Object
	 */
	public Object fromString(String str);

	/**
	 * Marshals an Object into a single value representation.
	 * 
	 * @param obj
	 *            the Object to be converted
	 * @return a String with the single value of the Object or <code>null</code>
	 */
	public String toString(Object obj);

}
