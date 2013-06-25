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

import java.util.regex.Pattern;

import com.madrobot.di.wizard.xml.io.HierarchicalStreamReader;
import com.madrobot.di.wizard.xml.io.HierarchicalStreamWriter;

/**
 * Ensures java.util.regex.Pattern is compiled upon deserialization.
 */
public class RegexPatternConverter implements Converter {

	private Converter defaultConverter;

	public RegexPatternConverter(Converter defaultConverter) {
		this.defaultConverter = defaultConverter;
	}

	@Override
	public boolean canConvert(final Class type) {
		return type.equals(Pattern.class);
	}

	@Override
	public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
		defaultConverter.marshal(source, writer, context);
	}

	@Override
	public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
		Pattern notCompiled = (Pattern) defaultConverter.unmarshal(reader, context);
		return Pattern.compile(notCompiled.pattern(), notCompiled.flags());
	}

}
