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
package com.madrobot.di.xml.simpledeserializer;

class ClassInfo {
	/**
	 * Name of the element to be deserialized
	 */
	private String elementName;

	/**
	 * Class being mapped to
	 */
	private Class<?> type;

	/**
	 * Creates an intance of {@link ClassInfo} with unitialized element name and type.
	 */
	ClassInfo() {
	}

	/**
	 * Creates an intance of {@link ClassInfo} with specified element name and type.
	 * 
	 * @param type
	 *            Class to which the element maps
	 * @param elementName
	 *            Name of the element being mapped
	 */
	ClassInfo(Class<?> type, String elementName) {
		this.type = type;
		this.elementName = elementName;
	}

	/**
	 * Gets the element name under consideration.
	 * 
	 * @return Name of the element
	 */
	String getElementName() {
		return elementName;
	}

	/**
	 * Gets the class being mapped.
	 * 
	 * @return Class being mapped
	 */
	Class<?> getType() {
		return type;
	}

	/**
	 * Sets the element name under consideration.
	 * 
	 * @return Name of the element
	 */
	void setElementName(String elementName) {
		this.elementName = elementName;
	}

	/**
	 * Sets the class being mapped.
	 * 
	 * @param type
	 *            Class being mapped
	 */
	void setType(Class<?> type) {
		this.type = type;
	}
}
