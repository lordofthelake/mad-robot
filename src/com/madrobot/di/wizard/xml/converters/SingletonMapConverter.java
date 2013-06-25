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
 * Converts a singleton map to XML, specifying an 'entry' element with 'key' and 'value' children.
 * <p>
 * Note: 'key' and 'value' is not the name of the generated tag. The children are serialized as normal elements and the
 * implementation expects them in the order 'key'/'value'.
 * </p>
 * <p>
 * Supports Collections.singletonMap.
 * </p>
 * 
 * @since 1.4.2
 */
public class SingletonMapConverter extends MapConverter {

	private static final Class MAP = Collections.singletonMap(Boolean.TRUE, null).getClass();

	/**
	 * Construct a SingletonMapConverter.
	 * 
	 * @param mapper
	 * @since 1.4.2
	 */
	public SingletonMapConverter(Mapper mapper) {
		super(mapper);
	}

	@Override
	public boolean canConvert(Class type) {
		return MAP == type;
	}

	@Override
	public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
		reader.moveDown();
		reader.moveDown();
		Object key = readItem(reader, context, null);
		reader.moveUp();

		reader.moveDown();
		Object value = readItem(reader, context, null);
		reader.moveUp();
		reader.moveUp();

		return Collections.singletonMap(key, value);
	}

}
