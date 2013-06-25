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
 * Responsible for looking up the correct Converter implementation for a specific type.
 * 
 * @author Joe Walnes
 * @see Converter
 */
public interface ConverterLookup {

	/**
	 * Lookup a converter for a specific type.
	 * <p/>
	 * This type may be any Class, including primitive and array types. It may also be null, signifying the value to be
	 * converted is a null type.
	 */
	Converter lookupConverterForType(Class type);
}
