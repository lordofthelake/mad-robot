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

import java.lang.reflect.Field;
import java.util.AbstractList;
import java.util.Comparator;
import java.util.Map;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import com.madrobot.di.wizard.xml.Mapper;
import com.madrobot.di.wizard.xml.core.JVM;
import com.madrobot.di.wizard.xml.io.HierarchicalStreamReader;
import com.madrobot.di.wizard.xml.io.HierarchicalStreamWriter;
import com.madrobot.util.PresortedSet;

/**
 * Converts a java.util.TreeSet to XML, and serializes the associated java.util.Comparator. The converter assumes that
 * the elements in the XML are already sorted according the comparator.
 * 
 */
public class TreeSetConverter extends CollectionConverter {
	private final static Field sortedMapField;
	static {
		Field smField = null;
		if (!JVM.hasOptimizedTreeSetAddAll()) {
			try {
				Field[] fields = TreeSet.class.getDeclaredFields();
				for (int i = 0; i < fields.length; i++) {
					if (SortedMap.class.isAssignableFrom(fields[i].getType())) {
						// take the fist member assignable to type "SortedMap"
						smField = fields[i];
						smField.setAccessible(true);
						break;
					}
				}
				if (smField == null) {
					throw new ExceptionInInitializerError("Cannot detect field of backing map of TreeSet");
				}

			} catch (SecurityException ex) {
				// ignore, no access possible with current SecurityManager
			}
		}
		sortedMapField = smField;
	}
	private transient TreeMapConverter treeMapConverter;

	public TreeSetConverter(Mapper mapper) {
		super(mapper);
		readResolve();
	}

	@Override
	public boolean canConvert(Class type) {
		return type.equals(TreeSet.class);
	}

	@Override
	public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
		SortedSet sortedSet = (SortedSet) source;
		treeMapConverter.marshalComparator(sortedSet.comparator(), writer, context);
		super.marshal(source, writer, context);
	}

	private Object readResolve() {
		treeMapConverter = new TreeMapConverter(mapper()) {

			@Override
			protected void populateMap(HierarchicalStreamReader reader, UnmarshallingContext context, Map map, final Map target) {
				populateCollection(reader, context, new AbstractList() {
					@Override
					public boolean add(Object object) {
						return target.put(object, object) != null;
					}

					@Override
					public Object get(int location) {
						return null;
					}

					@Override
					public int size() {
						return target.size();
					}
				});
			}

			@Override
			protected void putCurrentEntryIntoMap(HierarchicalStreamReader reader, UnmarshallingContext context, Map map, Map target) {
				Object key = readItem(reader, context, map);
				target.put(key, key);
			}
		};
		return this;
	}

	@Override
	public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
		TreeSet result = null;
		final TreeMap treeMap;
		Comparator unmarshalledComparator = treeMapConverter.unmarshalComparator(reader, context, null);
		boolean inFirstElement = unmarshalledComparator instanceof Mapper.Null;
		Comparator comparator = inFirstElement ? null : unmarshalledComparator;
		if (sortedMapField != null) {
			TreeSet possibleResult = comparator == null ? new TreeSet() : new TreeSet(comparator);
			Object backingMap = null;
			try {
				backingMap = sortedMapField.get(possibleResult);
			} catch (IllegalAccessException e) {
				throw new ConversionException("Cannot get backing map of TreeSet", e);
			}
			if (backingMap instanceof TreeMap) {
				treeMap = (TreeMap) backingMap;
				result = possibleResult;
			} else {
				treeMap = null;
			}
		} else {
			treeMap = null;
		}
		if (treeMap == null) {
			final PresortedSet set = new PresortedSet(comparator);
			result = comparator == null ? new TreeSet() : new TreeSet(comparator);
			if (inFirstElement) {
				// we are already within the first element
				addCurrentElementToCollection(reader, context, result, set);
				reader.moveUp();
			}
			populateCollection(reader, context, result, set);
			if (set.size() > 0) {
				result.addAll(set); // comparator will not be called if internally optimized
			}
		} else {
			treeMapConverter.populateTreeMap(reader, context, treeMap, unmarshalledComparator);
		}
		return result;
	}
}
