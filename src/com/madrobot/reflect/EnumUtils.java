/*******************************************************************************
 * Copyright (c) 2011 MadRobot.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *  Elton Kent - initial API and implementation
 ******************************************************************************/
package com.madrobot.reflect;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Utility library to provide helper methods for Java enums.
 * 
 * <p>
 * #ThreadSafe#
 * </p>
 * 
 */
public class EnumUtils {

	/**
	 * Gets the <code>enum</code> for the class, returning <code>null</code> if
	 * not found.
	 * <p>
	 * This method differs from {@link Enum#valueOf} in that it does not throw
	 * an exception for an invalid enum name.
	 * 
	 * @param enumClass
	 *            the class of the <code>enum</code> to get, not null
	 * @param enumName
	 *            the enum name
	 * @return the enum or null if not found
	 */
	public static <E extends Enum<E>> E getEnum(Class<E> enumClass, String enumName) {
		try{
			return Enum.valueOf(enumClass, enumName);
		} catch(IllegalArgumentException ex){
			return null;
		}
	}

	/**
	 * Gets the <code>List</code> of <code>enums</code>.
	 * <p>
	 * This method is useful when you need a list of enums rather than an array.
	 * 
	 * @param enumClass
	 *            the class of the <code>enum</code> to get, not null
	 * @return the modifiable list of enums, never null
	 */
	public static <E extends Enum<E>> List<E> getEnumList(Class<E> enumClass) {
		return new ArrayList<E>(Arrays.asList(enumClass.getEnumConstants()));
	}

	/**
	 * Gets the <code>Map</code> of <code>enums</code> by name.
	 * <p>
	 * This method is useful when you need a map of enums by name.
	 * 
	 * @param enumClass
	 *            the class of the <code>enum</code> to get, not null
	 * @return the modifiable map of enum names to enums, never null
	 */
	public static <E extends Enum<E>> Map<String, E> getEnumMap(Class<E> enumClass) {
		Map<String, E> map = new LinkedHashMap<String, E>();
		for(E e : enumClass.getEnumConstants()){
			map.put(e.name(), e);
		}
		return map;
	}

	/**
	 * Checks if the specified name is a valid <code>enum</code> for the class.
	 * <p>
	 * This method differs from {@link Enum#valueOf} in that checks if the name
	 * is a valid enum without needing to catch the exception.
	 * 
	 * @param enumClass
	 *            the class of the <code>enum</code> to get, not null
	 * @param enumName
	 *            the enum name
	 * @return true if the enum name is valid, otherwise false
	 */
	public static <E extends Enum<E>> boolean isValidEnum(Class<E> enumClass, String enumName) {
		try{
			Enum.valueOf(enumClass, enumName);
			return true;
		} catch(IllegalArgumentException ex){
			return false;
		}
	}

	/**
	 * This constructor is public to permit tools that require a JavaBean
	 * instance to operate.
	 */
	public EnumUtils() {
	}

}
