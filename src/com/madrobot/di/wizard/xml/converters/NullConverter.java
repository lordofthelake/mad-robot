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

import com.madrobot.di.wizard.xml.Mapper;
import com.madrobot.di.wizard.xml.io.ExtendedHierarchicalStreamWriterHelper;
import com.madrobot.di.wizard.xml.io.HierarchicalStreamReader;
import com.madrobot.di.wizard.xml.io.HierarchicalStreamWriter;

/**
 * Special converter to signify nulls at the root level.
 * 
 * @author Joe Walnes
 */
public class NullConverter implements Converter {

	@Override
	public boolean canConvert(Class type) {
		return type == null || Mapper.Null.class.isAssignableFrom(type);
	}

	@Override
	public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
		ExtendedHierarchicalStreamWriterHelper.startNode(writer, "null", null);
		writer.endNode();
	}

	@Override
	public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
		return null;
	}
}
