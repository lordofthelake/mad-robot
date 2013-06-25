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
import java.util.Comparator;
import java.util.SortedMap;
import java.util.TreeMap;

import com.madrobot.di.wizard.xml.Mapper;
import com.madrobot.di.wizard.xml.core.HierarchicalStreams;
import com.madrobot.di.wizard.xml.core.JVM;
import com.madrobot.di.wizard.xml.io.HierarchicalStreamReader;
import com.madrobot.di.wizard.xml.io.HierarchicalStreamWriter;
import com.madrobot.util.PresortedMap;

/**
 * Converts a java.util.TreeMap to XML, and serializes the associated java.util.Comparator. The converter assumes that
 * the entries in the XML are already sorted according the comparator.
 * 
 */
public class TreeMapConverter extends MapConverter {

	private static final class NullComparator extends Mapper.Null implements Comparator {
		@Override
		public int compare(Object o1, Object o2) {
			Comparable c1 = (Comparable) o1;
			Comparable c2 = (Comparable) o2;
			return c1.compareTo(o2);
		}
	}

	private final static Field comparatorField;

	private final static Comparator NULL_MARKER = new NullComparator();
	static {
		Field cmpField = null;
		try {
			Field[] fields = TreeMap.class.getDeclaredFields();
			for (int i = 0; i < fields.length; i++) {
				if (fields[i].getType() == Comparator.class) {
					// take the fist member of type "Comparator"
					cmpField = fields[i];
					cmpField.setAccessible(true);
					break;
				}
			}
			if (cmpField == null) {
				throw new ExceptionInInitializerError("Cannot detect comparator field of TreeMap");
			}

		} catch (SecurityException ex) {
			// ignore, no access possible with current SecurityManager
		}
		comparatorField = cmpField;
	}

	public TreeMapConverter(Mapper mapper) {
		super(mapper);
	}

	@Override
	public boolean canConvert(Class type) {
		return type.equals(TreeMap.class);
	}

	@Override
	public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
		SortedMap sortedMap = (SortedMap) source;
		marshalComparator(sortedMap.comparator(), writer, context);
		super.marshal(source, writer, context);
	}

	protected void marshalComparator(Comparator comparator, HierarchicalStreamWriter writer, MarshallingContext context) {
		if (comparator != null) {
			writer.startNode("comparator");
			writer.addAttribute(mapper().aliasForSystemAttribute("class"),
					mapper().serializedClass(comparator.getClass()));
			context.convertAnother(comparator);
			writer.endNode();
		}
	}

	protected void populateTreeMap(HierarchicalStreamReader reader, UnmarshallingContext context, TreeMap result, Comparator comparator) {
		boolean inFirstElement = comparator == NULL_MARKER;
		if (inFirstElement) {
			comparator = null;
		}
		SortedMap sortedMap = new PresortedMap(comparator != null && JVM.hasOptimizedTreeMapPutAll() ? comparator
				: null);
		if (inFirstElement) {
			// we are already within the first entry
			putCurrentEntryIntoMap(reader, context, result, sortedMap);
			reader.moveUp();
		}
		populateMap(reader, context, result, sortedMap);
		try {
			if (JVM.hasOptimizedTreeMapPutAll()) {
				if (comparator != null && comparatorField != null) {
					comparatorField.set(result, comparator);
				}
				result.putAll(sortedMap); // internal optimization will not call comparator
			} else if (comparatorField != null) {
				comparatorField.set(result, sortedMap.comparator());
				result.putAll(sortedMap); // "sort" by index
				comparatorField.set(result, comparator);
			} else {
				result.putAll(sortedMap); // will use comparator for already sorted map
			}
		} catch (final IllegalAccessException e) {
			throw new ConversionException("Cannot set comparator of TreeMap", e);
		}
	}

	@Override
	public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
		TreeMap result = comparatorField != null ? new TreeMap() : null;
		final Comparator comparator = unmarshalComparator(reader, context, result);
		if (result == null) {
			result = comparator == null ? new TreeMap() : new TreeMap(comparator);
		}
		populateTreeMap(reader, context, result, comparator);
		return result;
	}

	protected Comparator unmarshalComparator(HierarchicalStreamReader reader, UnmarshallingContext context, TreeMap result) {
		final Comparator comparator;
		if (reader.hasMoreChildren()) {
			reader.moveDown();
			if (reader.getNodeName().equals("comparator")) {
				Class comparatorClass = HierarchicalStreams.readClassType(reader, mapper());
				comparator = (Comparator) context.convertAnother(result, comparatorClass);
			} else if (reader.getNodeName().equals("no-comparator")) { // pre 1.4 format
				comparator = null;
			} else {
				// we are already within the first entry
				return NULL_MARKER;
			}
			reader.moveUp();
		} else {
			comparator = null;
		}
		return comparator;
	}
}
