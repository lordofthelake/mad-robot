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

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import com.madrobot.di.wizard.xml.core.Caching;
import com.madrobot.di.wizard.xml.io.StreamException;
import com.madrobot.util.OrderRetainingMap;

/**
 * The default implementation for sorting fields. Invoke registerFieldOrder in order to set the field order for an
 * specific type.
 * 
 * @since 1.2.2
 */
public class SortableFieldKeySorter implements FieldKeySorter, Caching {

	private class FieldComparator implements Comparator {

		private final String[] fieldOrder;

		public FieldComparator(String[] fields) {
			this.fieldOrder = fields;
		}

		@Override
		public int compare(Object firstObject, Object secondObject) {
			FieldKey first = (FieldKey) firstObject, second = (FieldKey) secondObject;
			return compare(first.getFieldName(), second.getFieldName());
		}

		public int compare(String first, String second) {
			int firstPosition = -1, secondPosition = -1;
			for (int i = 0; i < fieldOrder.length; i++) {
				if (fieldOrder[i].equals(first)) {
					firstPosition = i;
				}
				if (fieldOrder[i].equals(second)) {
					secondPosition = i;
				}
			}
			if (firstPosition == -1 || secondPosition == -1) {
				// field not defined!!!
				throw new StreamException("You have not given XStream a list of all fields to be serialized.");
			}
			return firstPosition - secondPosition;
		}

	}

	private final Map map = new HashMap();

	@Override
	public void flushCache() {
		map.clear();
	}

	/**
	 * Registers the field order to use for a specific type. This will not affect any of the type's super or sub
	 * classes. If you skip a field which will be serialized, XStream will thrown an StreamException during the
	 * serialization process.
	 * 
	 * @param type
	 *            the type
	 * @param fields
	 *            the field order
	 */
	public void registerFieldOrder(Class type, String[] fields) {
		map.put(type, new FieldComparator(fields));
	}

	@Override
	public Map sort(Class type, Map keyedByFieldKey) {
		if (map.containsKey(type)) {
			Map result = new OrderRetainingMap();
			FieldKey[] fieldKeys = (FieldKey[]) keyedByFieldKey.keySet().toArray(new FieldKey[keyedByFieldKey.size()]);
			Arrays.sort(fieldKeys, (Comparator) map.get(type));
			for (int i = 0; i < fieldKeys.length; i++) {
				result.put(fieldKeys[i], keyedByFieldKey.get(fieldKeys[i]));
			}
			return result;
		} else {
			return keyedByFieldKey;
		}
	}
}
