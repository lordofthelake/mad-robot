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

import com.madrobot.beans.PropertyDescriptor;

/**
 * An interface capable of sorting Java bean properties. Implement this interface if you want to customize the order in
 * which XStream serializes the properties of a bean.
 * 
 * @since 1.4
 */
public interface PropertySorter {

	/**
	 * Sort the properties of a bean type. The method will be called with the class type that contains all the
	 * properties and a Map that retains the order in which the elements have been added. The sequence in which elements
	 * are returned by an iterator defines the processing order of the properties. An implementation may create a
	 * different Map with similar semantic, add all elements of the original map and return the new one.
	 * 
	 * @param type
	 *            the bean class that contains all the properties
	 * @param nameMap
	 *            the map to sort, key is the property name, value the {@link PropertyDescriptor}
	 * @return the sorted nameMap
	 * @since 1.4
	 */
	Map sort(Class type, Map nameMap);

}
