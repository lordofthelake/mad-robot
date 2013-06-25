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
 * Sort the fields in the order of XStream 1.2.x. Fields are returned in their declaration order, fields of base classes
 * last.
 * 
 * @since 1.3
 */
public class XStream12FieldKeySorter implements FieldKeySorter {

	@Override
	public Map sort(final Class type, final Map keyedByFieldKey) {
		final Map map = new TreeMap(new Comparator() {

			@Override
			public int compare(final Object o1, final Object o2) {
				final FieldKey fieldKey1 = (FieldKey) o1;
				final FieldKey fieldKey2 = (FieldKey) o2;
				int i = fieldKey2.getDepth() - fieldKey1.getDepth();
				if (i == 0) {
					i = fieldKey1.getOrder() - fieldKey2.getOrder();
				}
				return i;
			}
		});
		map.putAll(keyedByFieldKey);
		return map;
	}

}
