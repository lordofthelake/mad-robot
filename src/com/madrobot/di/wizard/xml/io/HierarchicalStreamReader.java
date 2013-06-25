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

import java.util.Iterator;

import com.madrobot.di.wizard.xml.converters.ErrorReporter;
import com.madrobot.di.wizard.xml.converters.ErrorWriter;

/**
 */
public interface HierarchicalStreamReader extends ErrorReporter {

	/**
	 * If any errors are detected, allow the reader to add any additional information that can aid debugging (such as
	 * line numbers, XPath expressions, etc).
	 */
	@Override
	void appendErrors(ErrorWriter errorWriter);

	/**
	 * Close the reader, if necessary.
	 */
	void close();

	/**
	 * Get the value of an attribute of the current node, by index.
	 */
	String getAttribute(int index);

	/**
	 * Get the value of an attribute of the current node.
	 */
	String getAttribute(String name);

	/**
	 * Number of attributes in current node.
	 */
	int getAttributeCount();

	/**
	 * Name of attribute in current node.
	 */
	String getAttributeName(int index);

	/**
	 * Names of attributes (as Strings).
	 */
	Iterator getAttributeNames();

	/**
	 * Get the name of the current node.
	 */
	String getNodeName();

	/**
	 * Get the value (text content) of the current node.
	 */
	String getValue();

	/**
	 * Does the node have any more children remaining that have not yet been read?
	 */
	boolean hasMoreChildren();

	/**
	 * Select the current child as current node. A call to this function must be balanced with a call to
	 * {@link #moveUp()}.
	 */
	void moveDown();

	/**
	 * Select the parent node as current node.
	 */
	void moveUp();

	/**
	 * Return the underlying HierarchicalStreamReader implementation.
	 * 
	 * <p>
	 * If a Converter needs to access methods of a specific HierarchicalStreamReader implementation that are not defined
	 * in the HierarchicalStreamReader interface, it should call this method before casting. This is because the reader
	 * passed to the Converter is often wrapped/decorated by another implementation to provide additional functionality
	 * (such as XPath tracking).
	 * </p>
	 * 
	 * <p>
	 * For example:
	 * </p>
	 * 
	 * <pre>
	 * MySpecificReader mySpecificReader = (MySpecificReader)reader; <b>// INCORRECT!</b>
	 * mySpecificReader.doSomethingSpecific();
	 * </pre>
	 * 
	 * <pre>
	 * MySpecificReader mySpecificReader = (MySpecificReader)reader.underlyingReader();  <b>// CORRECT!</b>
	 * mySpecificReader.doSomethingSpecific();
	 * </pre>
	 * 
	 * <p>
	 * Implementations of HierarchicalStreamReader should return 'this', unless they are a decorator, in which case they
	 * should delegate to whatever they are wrapping.
	 * </p>
	 */
	HierarchicalStreamReader underlyingReader();

}
