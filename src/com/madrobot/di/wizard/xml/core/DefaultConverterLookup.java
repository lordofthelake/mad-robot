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

package com.madrobot.di.wizard.xml.core;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.WeakHashMap;

import com.madrobot.di.wizard.xml.converters.ConversionException;
import com.madrobot.di.wizard.xml.converters.Converter;
import com.madrobot.di.wizard.xml.converters.ConverterLookup;
import com.madrobot.di.wizard.xml.converters.ConverterRegistry;
import com.madrobot.util.PrioritizedList;

/**
 * The default implementation of converters lookup.
 * 
 */
public class DefaultConverterLookup implements ConverterLookup, ConverterRegistry, Caching {

	private final PrioritizedList converters = new PrioritizedList();
	private transient Map typeToConverterMap = Collections.synchronizedMap(new WeakHashMap());

	public DefaultConverterLookup() {
	}

	@Override
	public void flushCache() {
		typeToConverterMap.clear();
		Iterator iterator = converters.iterator();
		while (iterator.hasNext()) {
			Converter converter = (Converter) iterator.next();
			if (converter instanceof Caching) {
				((Caching) converter).flushCache();
			}
		}
	}

	@Override
	public Converter lookupConverterForType(Class type) {
		Converter cachedConverter = (Converter) typeToConverterMap.get(type);
		if (cachedConverter != null) {
			return cachedConverter;
		}
		Iterator iterator = converters.iterator();
		while (iterator.hasNext()) {
			Converter converter = (Converter) iterator.next();
			if (converter.canConvert(type)) {
				typeToConverterMap.put(type, converter);
				return converter;
			}
		}
		throw new ConversionException("No converter specified for " + type);
	}

	private Object readResolve() {
		typeToConverterMap = Collections.synchronizedMap(new HashMap());
		return this;
	}

	@Override
	public void registerConverter(Converter converter, int priority) {
		converters.add(converter, priority);
		for (Iterator iter = typeToConverterMap.keySet().iterator(); iter.hasNext();) {
			Class type = (Class) iter.next();
			if (converter.canConvert(type)) {
				iter.remove();
			}
		}
	}
}
