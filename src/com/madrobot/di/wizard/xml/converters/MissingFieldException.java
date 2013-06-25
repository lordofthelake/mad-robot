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
 * Indicates a missing field or property creating an object.
 * 
 * @since 1.4.2
 */
public class MissingFieldException extends ObjectAccessException {

	private final String className;
	private final String fieldName;

	/**
	 * Construct a MissingFieldException.
	 * 
	 * @param className
	 *            the name of the class missing the field
	 * @param fieldName
	 *            the name of the missed field
	 * @since 1.4.2
	 */
	public MissingFieldException(final String className, final String fieldName) {
		super("No field '" + fieldName + "' found in class '" + className + "'");
		this.className = className;
		this.fieldName = fieldName;
	}

	/**
	 * Retrieve the name of the class with the missing field.
	 * 
	 * @return the class name
	 * @since 1.4.2
	 */
	protected String getClassName() {
		return className;
	}

	/**
	 * Retrieve the name of the missing field.
	 * 
	 * @return the field name
	 * @since 1.4.2
	 */
	public String getFieldName() {
		return fieldName;
	}
}
