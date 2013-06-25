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

package com.madrobot.di.wizard.xml;

import com.madrobot.di.wizard.xml.converters.ConverterLookup;
import com.madrobot.di.wizard.xml.converters.DataHolder;
import com.madrobot.di.wizard.xml.io.HierarchicalStreamReader;
import com.madrobot.di.wizard.xml.io.HierarchicalStreamWriter;

/**
 * Basic functionality of a tree based marshalling strategy.
 * 
 * @since 1.3
 */
abstract class AbstractTreeMarshallingStrategy implements MarshallingStrategy {

	protected abstract TreeMarshaller createMarshallingContext(HierarchicalStreamWriter writer, ConverterLookup converterLookup, Mapper mapper);

	protected abstract TreeUnmarshaller createUnmarshallingContext(Object root, HierarchicalStreamReader reader, ConverterLookup converterLookup, Mapper mapper);

	@Override
	public void marshal(HierarchicalStreamWriter writer, Object obj, ConverterLookup converterLookup, Mapper mapper, DataHolder dataHolder) {
		TreeMarshaller context = createMarshallingContext(writer, converterLookup, mapper);
		context.start(obj, dataHolder);
	}

	@Override
	public Object unmarshal(Object root, HierarchicalStreamReader reader, DataHolder dataHolder, ConverterLookup converterLookup, Mapper mapper) {
		TreeUnmarshaller context = createUnmarshallingContext(root, reader, converterLookup, mapper);
		return context.start(dataHolder);
	}
}
