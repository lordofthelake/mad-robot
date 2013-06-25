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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Vector;

import com.madrobot.di.wizard.xml.Mapper;
import com.madrobot.di.wizard.xml.core.JVM;
import com.madrobot.di.wizard.xml.io.HierarchicalStreamReader;
import com.madrobot.di.wizard.xml.io.HierarchicalStreamWriter;

/**
 * Converts most common Collections (Lists and Sets) to XML, specifying a nested element for each item.
 * <p/>
 * <p>
 * Supports java.util.ArrayList, java.util.HashSet, java.util.LinkedList, java.util.Vector and java.util.LinkedHashSet.
 * </p>
 * 
 */
public class CollectionConverter extends AbstractCollectionConverter {

	public CollectionConverter(Mapper mapper) {
		super(mapper);
	}

	protected void addCurrentElementToCollection(HierarchicalStreamReader reader, UnmarshallingContext context, Collection collection, Collection target) {
		Object item = readItem(reader, context, collection);
		target.add(item);
	}

	@Override
	public boolean canConvert(Class type) {
		return type.equals(ArrayList.class) || type.equals(HashSet.class) || type.equals(LinkedList.class)
				|| type.equals(Vector.class) || (JVM.is14() && type.getName().equals("java.util.LinkedHashSet"));
	}

	@Override
	public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
		Collection collection = (Collection) source;
		for (Iterator iterator = collection.iterator(); iterator.hasNext();) {
			Object item = iterator.next();
			writeItem(item, context, writer);
		}
	}

	protected void populateCollection(HierarchicalStreamReader reader, UnmarshallingContext context, Collection collection) {
		populateCollection(reader, context, collection, collection);
	}

	protected void populateCollection(HierarchicalStreamReader reader, UnmarshallingContext context, Collection collection, Collection target) {
		while (reader.hasMoreChildren()) {
			reader.moveDown();
			addCurrentElementToCollection(reader, context, collection, target);
			reader.moveUp();
		}
	}

	@Override
	public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
		Collection collection = (Collection) createCollection(context.getRequiredType());
		populateCollection(reader, context, collection);
		return collection;
	}
}
