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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.madrobot.di.wizard.xml.Mapper;
import com.madrobot.di.wizard.xml.io.HierarchicalStreamReader;
import com.madrobot.di.wizard.xml.io.HierarchicalStreamWriter;

/**
 * Converts an array of objects or primitives to XML, using a nested child element for each item.
 * 
 * @author Joe Walnes
 */
public class ArrayConverter extends AbstractCollectionConverter {

	public ArrayConverter(Mapper mapper) {
		super(mapper);
	}

	@Override
	public boolean canConvert(Class type) {
		return type.isArray();
	}

	@Override
	public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
		int length = Array.getLength(source);
		for (int i = 0; i < length; i++) {
			Object item = Array.get(source, i);
			writeItem(item, context, writer);
		}

	}

	@Override
	public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
		// read the items from xml into a list
		List items = new ArrayList();
		while (reader.hasMoreChildren()) {
			reader.moveDown();
			Object item = readItem(reader, context, null); // TODO: arg, what should replace null?
			items.add(item);
			reader.moveUp();
		}
		// now convertAnother the list into an array
		// (this has to be done as a separate list as the array size is not
		// known until all items have been read)
		Object array = Array.newInstance(context.getRequiredType().getComponentType(), items.size());
		int i = 0;
		for (Iterator iterator = items.iterator(); iterator.hasNext();) {
			Array.set(array, i++, iterator.next());
		}
		return array;
	}
}
