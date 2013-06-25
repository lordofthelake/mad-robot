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
import com.madrobot.di.wizard.xml.io.HierarchicalStreamReader;
import com.madrobot.di.wizard.xml.io.HierarchicalStreamWriter;

class TreeMarshallingStrategy extends AbstractTreeMarshallingStrategy {

	@Override
	protected TreeMarshaller createMarshallingContext(HierarchicalStreamWriter writer, ConverterLookup converterLookup, Mapper mapper) {
		return new TreeMarshaller(writer, converterLookup, mapper);
	}

	@Override
	protected TreeUnmarshaller createUnmarshallingContext(Object root, HierarchicalStreamReader reader, ConverterLookup converterLookup, Mapper mapper) {
		return new TreeUnmarshaller(root, reader, converterLookup, mapper);
	}
}
