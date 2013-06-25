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

import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

/**
 * A sorter that uses a comparator to determine the order of the bean properties.
 * 
 * @since 1.4
 */
public class ComparingPropertySorter implements PropertySorter {

	private final Comparator comparator;

	public ComparingPropertySorter(final Comparator propertyNameComparator) {
		this.comparator = propertyNameComparator;
	}

	@Override
	public Map sort(final Class type, final Map nameMap) {
		TreeMap map = new TreeMap(comparator);
		map.putAll(nameMap);
		return map;
	}

}
