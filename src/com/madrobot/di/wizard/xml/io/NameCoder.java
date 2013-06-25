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
 * Coder between names in the object graph and names of a target format.
 * <p>
 * The names form the object graph are typically names generated from Java identifiers (Java types or field members),
 * but some names may also contain a minus sign. However, the original name might have already been aliased, so a wider
 * range of characters can occur.
 * </p>
 * <p>
 * The target names should satisfy the syntax of the target format. Transforming Java objects to XML this affects names
 * that contain or even start with a dollar sign. Such names violate the XML specification.
 * </p>
 * <p>
 * By default all names from the object graph are used as node names in the target format. Meta-data that is necessary
 * to unmarshal the object again is typically written as attribute. Since such attributes might be represented
 * differently in the target format, the NameCoder distinguishes between the names used for meta-data elements and the
 * ones for the object data. The names in the target format might even have to follow a different syntax. Remember, that
 * XStream can be easily configured to write also object data as attributes.
 * </p>
 * <p>
 * Note that the instance of a NameCoder should be either thread-safe or implement {@link Cloneable}.
 * </p>
 * 
 * @author J&ouml;rg Schaible
 * @since 1.4
 */
public interface NameCoder {
	/**
	 * Decode an attribute name to an object name.
	 * 
	 * @param attributeName
	 *            the name of the attribute
	 * @return the name of the meta-data
	 * @since 1.4
	 */
	String decodeAttribute(String attributeName);

	/**
	 * Decode a node name to an object name.
	 * 
	 * @param nodeName
	 *            the name of the node
	 * @return the name of the object
	 * @since 1.4
	 */
	String decodeNode(String nodeName);

	/**
	 * Encode a meta-data name for an attribute in the target format.
	 * 
	 * @param name
	 *            the name of the meta-data
	 * @return the attribute name in the target format
	 * @since 1.4
	 */
	String encodeAttribute(String name);

	/**
	 * Encode an object name for a node in the target format.
	 * 
	 * @param name
	 *            the name of the object data
	 * @return the node name in the target format
	 * @since 1.4
	 */
	String encodeNode(String name);
}
