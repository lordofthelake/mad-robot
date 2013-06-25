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

/**
 * Possible field types, during deserialization See {@link XMLDeserializer} for more details on its usage
 * 
 * @see XMLDeserializer
 */
enum FieldType {
	/**
	 * Used to indicate that the type of the field is a collection
	 */
	COLLECTION,
	/**
	 * Used to indicate that the type of the field is composite
	 */
	COMPOSITE,
	/**
	 * Used to indicate that the type of the field is not defined
	 */
	NOT_DEFINED,
	/**
	 * Used to indicate that the type of the field is pseudo-primitive
	 */
	PSEUDO_PRIMITIVE
}
