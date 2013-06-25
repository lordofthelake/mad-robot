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

package com.madrobot.di.wizard.json.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.madrobot.di.wizard.json.JSONDeserializer;
import com.madrobot.di.wizard.json.JSONSerializer;

/**
 * Annotation to specify the item type when the field is a collection. <br/>
 * See {@link JSONDeserializer} {@link JSONSerializer} for usage
 * 
 * @see {@link JSONDeserializer}, {@link JSONSerializer}
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD })
public @interface ItemType {

	/**
	 * Decision to json should hold empty array or not
	 * 
	 * @see JSONSerializer
	 * 
	 * @return decision
	 */
	boolean canEmpty() default true;

	/**
	 * 
	 * Represent the size of the collection
	 * 
	 * @return size of the collection
	 */
	int size() default JSONDeserializer.DEFAULT_ITEM_COLLECTION_SIZE;

	/**
	 * Represent the item type
	 * 
	 * @return item type
	 */
	Class<?> value();
}
