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

import java.util.Map;

/**
 * An interface capable of sorting fields. Implement this interface if you want to customize the field order in which
 * XStream serializes objects.
 * 
 * @author Guilherme Silveira
 * @since 1.2.2
 */
public interface FieldKeySorter {

	/**
	 * Sort the fields of a type. The method will be called with the class type that contains all the fields and a Map
	 * that retains the order in which the elements have been added. The sequence in which elements are returned by an
	 * iterator defines the processing order of the fields. An implementation may create a different Map with similar
	 * semantic, add all elements of the original map and return the new one.
	 * 
	 * @param type
	 *            the class that contains all the fields
	 * @param keyedByFieldKey
	 *            a Map containing a {@link FieldKey} as key element and a {@link java.lang.reflect.Field} as value.
	 * @return a Map with all the entries of the original Map
	 * @since 1.2.2
	 */
	Map sort(Class type, Map keyedByFieldKey);

}
