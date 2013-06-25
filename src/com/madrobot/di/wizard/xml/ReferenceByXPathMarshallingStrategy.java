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

class ReferenceByXPathMarshallingStrategy extends AbstractTreeMarshallingStrategy {

	public static int ABSOLUTE = 1;
	public static int RELATIVE = 0;
	public static int SINGLE_NODE = 2;
	private final int mode;

	ReferenceByXPathMarshallingStrategy(int mode) {
		this.mode = mode;
	}

	@Override
	protected TreeMarshaller createMarshallingContext(HierarchicalStreamWriter writer, ConverterLookup converterLookup, Mapper mapper) {
		return new ReferenceByXPathMarshaller(writer, converterLookup, mapper, mode);
	}

	@Override
	protected TreeUnmarshaller createUnmarshallingContext(Object root, HierarchicalStreamReader reader, ConverterLookup converterLookup, Mapper mapper) {
		return new ReferenceByXPathUnmarshaller(root, reader, converterLookup, mapper);
	}
}
