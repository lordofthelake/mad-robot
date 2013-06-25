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

public interface MarshallingContext extends DataHolder {

	/**
	 * Converts another object searching for the default converter
	 * 
	 * @param nextItem
	 *            the next item to convert
	 */
	void convertAnother(Object nextItem);

	/**
	 * Converts another object using the specified converter
	 * 
	 * @param nextItem
	 *            the next item to convert
	 * @param converter
	 *            the Converter to use
	 * @since 1.2
	 */
	void convertAnother(Object nextItem, Converter converter);

}
