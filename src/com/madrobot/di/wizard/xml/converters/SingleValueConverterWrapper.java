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
 * Wrapper to convert a {@link com.madrobot.di.wizard.xml.converters.SingleValueConverter} into a
 * {@link com.madrobot.di.wizard.xml.converters.Converter}.
 * 
 * @see com.madrobot.di.wizard.xml.converters.Converter
 * @see com.madrobot.di.wizard.xml.converters.SingleValueConverter
 */
public class SingleValueConverterWrapper implements Converter, SingleValueConverter, ErrorReporter {

	private final SingleValueConverter wrapped;

	public SingleValueConverterWrapper(SingleValueConverter wrapped) {
		this.wrapped = wrapped;
	}

	@Override
	public void appendErrors(ErrorWriter errorWriter) {
		errorWriter.add("wrapped-converter", wrapped.getClass().getName());
		if (wrapped instanceof ErrorReporter) {
			((ErrorReporter) wrapped).appendErrors(errorWriter);
		}
	}

	@Override
	public boolean canConvert(Class type) {
		return wrapped.canConvert(type);
	}

	@Override
	public Object fromString(String str) {
		return wrapped.fromString(str);
	}

	@Override
	public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
		writer.setValue(toString(source));
	}

	@Override
	public String toString(Object obj) {
		return wrapped.toString(obj);
	}

	@Override
	public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
		return fromString(reader.getValue());
	}
}
