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

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Holds key information about a field. <br/>
 * The information held by this type includes:
 * <ol>
 * <li>Name of the XML element</li>
 * <li>Name of the field</li>
 * <li>Class information of the field</li>
 * <li>Type of the field. See {@link FieldType}</li>
 * <li>Reference to the field. See {@link Field}</li>
 * <li>References to the methods to get or set the value, or add in case of a collection. See {@link Method}</li>
 * </ol>
 * <br/>
 * See {@link XMLDeserializer} for usage
 * 
 * @see XMLDeserializer
 */
class FieldInfo {
	/**
	 * Method to add a item to the collection field
	 */
	private Method addMethod;
	/**
	 * Name of the element
	 */
	private String elementName;
	/**
	 * Field reference
	 */
	private Field field;
	/**
	 * Name of the field
	 */
	private String fieldName;
	/**
	 * Type of the field
	 */
	private FieldType fieldType;
	/**
	 * Method to get the value
	 */
	private Method getMethod;
	/**
	 * Method to set the value
	 */
	private Method setMethod;
	/**
	 * Class information about the field type
	 */
	private Class<?> type;

	/**
	 * Gets the method to add an item to the collection field
	 * 
	 * @return The method reference
	 */
	Method getAddMethod() {
		return addMethod;
	}

	/**
	 * Gets the name of the element
	 * 
	 * @return Name of the element
	 */
	String getElementName() {
		return elementName;
	}

	/**
	 * Gets the field reference
	 * 
	 * @return The field reference
	 */
	Field getField() {
		return field;
	}

	/**
	 * Gets the name of the field
	 * 
	 * @return Name of the element
	 */
	String getFieldName() {
		return fieldName;
	}

	/**
	 * Gets the type of the field
	 * 
	 * @return Field type
	 */
	FieldType getFieldType() {
		return fieldType;
	}

	/**
	 * Gets the method to get the value of the field
	 * 
	 * @return The method reference
	 */
	Method getGetMethod() {
		return getMethod;
	}

	/**
	 * Gets the method to set the value of the field
	 * 
	 * @return The method reference
	 */
	Method getSetMethod() {
		return setMethod;
	}

	/**
	 * Gets class of the field
	 * 
	 * @return Field class
	 */
	Class<?> getType() {
		return type;
	}

	/**
	 * Sets the method to get the value of the field
	 * 
	 * @param addMethod
	 *            Method reference
	 */
	void setAddMethod(Method addMethod) {
		this.addMethod = addMethod;
	}

	/**
	 * Sets the name of the element
	 * 
	 * @param name
	 *            Name of the element
	 */
	void setElementName(String name) {
		this.elementName = name;
	}

	/**
	 * Sets the field reference
	 * 
	 * @param field
	 *            Field reference
	 */
	void setField(Field field) {
		this.field = field;
	}

	/**
	 * Sets the name of the field
	 * 
	 * @param fieldName
	 *            name of the field
	 */
	void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	/**
	 * Sets the type of the field
	 * 
	 * @param fieldType
	 *            Field type
	 */
	void setFieldType(FieldType fieldType) {
		this.fieldType = fieldType;
	}

	/**
	 * Sets the method to get the value of the field
	 * 
	 * @param getMethod
	 *            Method reference
	 */
	void setGetMethod(Method getMethod) {
		this.getMethod = getMethod;
	}

	/**
	 * Sets the method to set the value of the field
	 * 
	 * @param setMethod
	 *            Method reference
	 */
	void setSetMethod(Method setMethod) {
		this.setMethod = setMethod;
	}

	/**
	 * Sets the class of the field
	 * 
	 * @param type
	 *            Field class
	 */
	void setType(Class<?> type) {
		this.type = type;
	}
}
