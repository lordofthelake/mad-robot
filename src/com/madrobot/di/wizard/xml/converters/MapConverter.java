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

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import com.madrobot.di.wizard.xml.Mapper;
import com.madrobot.di.wizard.xml.io.ExtendedHierarchicalStreamWriterHelper;
import com.madrobot.di.wizard.xml.io.HierarchicalStreamReader;
import com.madrobot.di.wizard.xml.io.HierarchicalStreamWriter;

/**
 * Converts a java.util.Map to XML, specifying an 'entry' element with 'key' and 'value' children.
 * <p>
 * Note: 'key' and 'value' is not the name of the generated tag. The children are serialized as normal elements and the
 * implementation expects them in the order 'key'/'value'.
 * </p>
 * <p>
 * Supports java.util.HashMap, java.util.Hashtable and java.util.LinkedHashMap.
 * </p>
 * 
 */
public class MapConverter extends AbstractCollectionConverter {

	public MapConverter(Mapper mapper) {
		super(mapper);
	}

	@Override
	public boolean canConvert(Class type) {
		return type.equals(HashMap.class) || type.equals(Hashtable.class)
				|| type.getName().equals("java.util.LinkedHashMap") || type.getName().equals("sun.font.AttributeMap") // Used
																														// by
																														// java.awt.Font
																														// in
																														// JDK
																														// 6
		;
	}

	@Override
	public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
		Map map = (Map) source;
		for (Iterator iterator = map.entrySet().iterator(); iterator.hasNext();) {
			Map.Entry entry = (Map.Entry) iterator.next();
			ExtendedHierarchicalStreamWriterHelper.startNode(writer, mapper().serializedClass(Map.Entry.class),
					Map.Entry.class);

			writeItem(entry.getKey(), context, writer);
			writeItem(entry.getValue(), context, writer);

			writer.endNode();
		}
	}

	protected void populateMap(HierarchicalStreamReader reader, UnmarshallingContext context, Map map) {
		populateMap(reader, context, map, map);
	}

	protected void populateMap(HierarchicalStreamReader reader, UnmarshallingContext context, Map map, Map target) {
		while (reader.hasMoreChildren()) {
			reader.moveDown();
			putCurrentEntryIntoMap(reader, context, map, target);
			reader.moveUp();
		}
	}

	protected void putCurrentEntryIntoMap(HierarchicalStreamReader reader, UnmarshallingContext context, Map map, Map target) {
		reader.moveDown();
		Object key = readItem(reader, context, map);
		reader.moveUp();

		reader.moveDown();
		Object value = readItem(reader, context, map);
		reader.moveUp();

		target.put(key, value);
	}

	@Override
	public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
		Map map = (Map) createCollection(context.getRequiredType());
		populateMap(reader, context, map);
		return map;
	}

}
