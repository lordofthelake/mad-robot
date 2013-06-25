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

package com.madrobot.di.wizard.xml.io;

/**
 */
public interface HierarchicalStreamWriter {

	void addAttribute(String name, String value);

	/**
	 * Close the writer, if necessary.
	 */
	void close();

	void endNode();

	/**
	 * Flush the writer, if necessary.
	 */
	void flush();

	/**
	 * Write the value (text content) of the current node.
	 */
	void setValue(String text);

	void startNode(String name);

	/**
	 * Return the underlying HierarchicalStreamWriter implementation.
	 * 
	 * <p>
	 * If a Converter needs to access methods of a specific HierarchicalStreamWriter implementation that are not defined
	 * in the HierarchicalStreamWriter interface, it should call this method before casting. This is because the writer
	 * passed to the Converter is often wrapped/decorated by another implementation to provide additional functionality
	 * (such as XPath tracking).
	 * </p>
	 * 
	 * <p>
	 * For example:
	 * </p>
	 * 
	 * <pre>
	 * MySpecificWriter mySpecificWriter = (MySpecificWriter)writer; <b>// INCORRECT!</b>
	 * mySpecificWriter.doSomethingSpecific();
	 * </pre>
	 * 
	 * <pre>
	 * MySpecificWriter mySpecificWriter = (MySpecificWriter)writer.underlyingWriter();  <b>// CORRECT!</b>
	 * mySpecificWriter.doSomethingSpecific();
	 * </pre>
	 * 
	 * <p>
	 * Implementations of HierarchicalStreamWriter should return 'this', unless they are a decorator, in which case they
	 * should delegate to whatever they are wrapping.
	 * </p>
	 */
	HierarchicalStreamWriter underlyingWriter();

}
