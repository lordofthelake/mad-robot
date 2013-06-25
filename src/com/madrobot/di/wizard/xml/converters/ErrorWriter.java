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

import java.util.Iterator;

/**
 * To aid debugging, some components are passed an ErrorWriter when things go wrong, allowing them to add information to
 * the error message that may be helpful to diagnose problems.
 * 
 * @author Joe Walnes
 * @author J&ouml;rg Schaible
 */
public interface ErrorWriter {

	/**
	 * Add some information to the error message. The information will be added even if the identifier is already in
	 * use.
	 * 
	 * @param name
	 *            something to identify the type of information (e.g. 'XPath').
	 * @param information
	 *            detail of the message (e.g. '/blah/moo[3]'
	 */
	void add(String name, String information);

	/**
	 * Retrieve information of the error message.
	 * 
	 * @param errorKey
	 *            the key of the message
	 * @return the value
	 * @since 1.3
	 */
	String get(String errorKey);

	/**
	 * Retrieve an iterator over all keys of the error message.
	 * 
	 * @return an Iterator
	 * @since 1.3
	 */
	Iterator keys();

	/**
	 * Set some information to the error message. If the identifier is already in use, the new information will replace
	 * the old one.
	 * 
	 * @param name
	 *            something to identify the type of information (e.g. 'XPath').
	 * @param information
	 *            detail of the message (e.g. '/blah/moo[3]'
	 * @since 1.4
	 */
	void set(String name, String information);
}
