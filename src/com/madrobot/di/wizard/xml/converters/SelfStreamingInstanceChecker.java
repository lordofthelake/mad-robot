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

import com.madrobot.di.wizard.xml.io.HierarchicalStreamReader;
import com.madrobot.di.wizard.xml.io.HierarchicalStreamWriter;

/**
 * A special converter that prevents self-serialization. The serializing XStream instance adds a converter of this type
 * to prevent self-serialization and will throw an exception instead.
 * 
 * @since 1.2
 */
public class SelfStreamingInstanceChecker implements Converter {

	private Converter defaultConverter;
	private final Object self;

	public SelfStreamingInstanceChecker(Converter defaultConverter, Object xstream) {
		this.defaultConverter = defaultConverter;
		this.self = xstream;
	}

	@Override
	public boolean canConvert(Class type) {
		return type == self.getClass();
	}

	@Override
	public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
		if (source == self) {
			throw new ConversionException("Cannot marshal the XStream instance in action");
		}
		defaultConverter.marshal(source, writer, context);
	}

	@Override
	public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
		return defaultConverter.unmarshal(reader, context);
	}

}
