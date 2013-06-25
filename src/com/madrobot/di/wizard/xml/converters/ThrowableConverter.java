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
 * Converter for Throwable (and Exception) that retains stack trace, for JDK1.4 only.
 * 
 */
public class ThrowableConverter implements Converter {

	private Converter defaultConverter;

	public ThrowableConverter(Converter defaultConverter) {
		this.defaultConverter = defaultConverter;
	}

	@Override
	public boolean canConvert(final Class type) {
		return Throwable.class.isAssignableFrom(type);
	}

	@Override
	public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
		Throwable throwable = (Throwable) source;
		if (throwable.getCause() == null) {
			try {
				throwable.initCause(null);
			} catch (IllegalStateException e) {
				// ignore, initCause failed, cause was already set
			}
		}
		throwable.getStackTrace(); // Force stackTrace field to be lazy loaded by special JVM native witchcraft (outside
									// our control).
		defaultConverter.marshal(throwable, writer, context);
	}

	@Override
	public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
		return defaultConverter.unmarshal(reader, context);
	}
}
