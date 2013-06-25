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
 * Annotation to specify the serialized name of the json key. <br/>
 * See {@link JSONDeserializer} {@link JSONSerializer} for usage
 * 
 * @see {@link JSONDeserializer}, {@link JSONSerializer}
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD })
public @interface SerializedName {

	/**
	 * Represent the json key name
	 * 
	 * @return json key name
	 */
	String value();
}
