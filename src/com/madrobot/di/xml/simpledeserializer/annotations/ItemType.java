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

package com.madrobot.di.xml.simpledeserializer.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.madrobot.di.xml.simpledeserializer.XMLDeserializer;

/**
 * Annotation to specify the item type when the field is a collection. <br/>
 * See {@link XMLDeserializer} for usage
 * 
 * @see XMLDeserializer
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD })
public @interface ItemType {
	/**
	 * Represents the type of the item in the collection field.
	 */
	Class<?> value();
}
