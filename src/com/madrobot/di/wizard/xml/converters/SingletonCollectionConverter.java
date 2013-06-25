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

import java.util.Collections;

import com.madrobot.di.wizard.xml.Mapper;
import com.madrobot.di.wizard.xml.io.HierarchicalStreamReader;

/**
 * Converts singleton collections (list and set) to XML, specifying a nested element for the item.
 * <p>
 * Supports Collections.singleton(Object) and Collections.singletonList(Object).
 * </p>
 * 
 * @since 1.4.2
 */
public class SingletonCollectionConverter extends CollectionConverter {

	private static final Class LIST = Collections.singletonList(Boolean.TRUE).getClass();
	private static final Class SET = Collections.singleton(Boolean.TRUE).getClass();

	/**
	 * Construct a SingletonCollectionConverter.
	 * 
	 * @param mapper
	 *            the mapper
	 * @since 1.4.2
	 */
	public SingletonCollectionConverter(Mapper mapper) {
		super(mapper);
	}

	@Override
	public boolean canConvert(Class type) {
		return LIST == type || SET == type;
	}

	@Override
	public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
		reader.moveDown();
		Object item = readItem(reader, context, null);
		reader.moveUp();
		return context.getRequiredType() == LIST ? (Object) Collections.singletonList(item) : (Object) Collections
				.singleton(item);
	}
}
