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

import java.lang.reflect.Field;

/**
 * Provides core reflection services.
 * 
 */
public interface ReflectionProvider {

	/**
	 * A visitor interface for serializable fields defined in a class.
	 * 
	 */
	interface Visitor {

		/**
		 * Callback for each visit
		 * 
		 * @param name
		 *            field name
		 * @param type
		 *            field type
		 * @param definedIn
		 *            where the field was defined
		 * @param value
		 *            field value
		 */
		void visit(String name, Class type, Class definedIn, Object value);
	}

	boolean fieldDefinedInClass(String fieldName, Class type);

	/**
	 * Returns a field defined in some class.
	 * 
	 * @param definedIn
	 *            class where the field was defined
	 * @param fieldName
	 *            field name
	 * @return the field itself
	 */
	Field getField(Class definedIn, String fieldName);

	Class getFieldType(Object object, String fieldName, Class definedIn);

	/**
	 * Creates a new instance of the specified type. It is in the responsibility of the implementation how such an
	 * instance is created.
	 * 
	 * @param type
	 *            the type to instantiate
	 * @return a new instance of this type
	 */
	Object newInstance(Class type);

	void visitSerializableFields(Object object, Visitor visitor);

	void writeField(Object object, String fieldName, Object value, Class definedIn);

}
