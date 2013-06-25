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

import java.util.HashMap;
import java.util.Map;

import com.madrobot.di.wizard.xml.converters.ConversionException;
import com.madrobot.di.wizard.xml.converters.Converter;
import com.madrobot.di.wizard.xml.converters.ConverterLookup;
import com.madrobot.di.wizard.xml.io.HierarchicalStreamReader;
import com.madrobot.util.FastStack;

/**
 * Abstract base class for a TreeUnmarshaller, that resolves references.
 * 
 * @since 1.2
 */
abstract class AbstractReferenceUnmarshaller extends TreeUnmarshaller {

	private static final Object NULL = new Object();
	private FastStack parentStack = new FastStack(16);
	private Map values = new HashMap();

	AbstractReferenceUnmarshaller(
			Object root,
			HierarchicalStreamReader reader,
			ConverterLookup converterLookup,
			Mapper mapper) {
		super(root, reader, converterLookup, mapper);
	}

	@Override
	protected Object convert(Object parent, Class type, Converter converter) {
		if (parentStack.size() > 0) { // handles circular references
			Object parentReferenceKey = parentStack.peek();
			if (parentReferenceKey != null) {
				if (!values.containsKey(parentReferenceKey)) { // see
																// AbstractCircularReferenceTest.testWeirdCircularReference()
					values.put(parentReferenceKey, parent);
				}
			}
		}
		final Object result;
		String attributeName = getMapper().aliasForSystemAttribute("reference");
		String reference = attributeName == null ? null : reader.getAttribute(attributeName);
		if (reference != null) {
			Object cache = values.get(getReferenceKey(reference));
			if (cache == null) {
				final ConversionException ex = new ConversionException("Invalid reference");
				ex.add("reference", reference);
				throw ex;
			}
			result = cache == NULL ? null : cache;
		} else {
			Object currentReferenceKey = getCurrentReferenceKey();
			parentStack.push(currentReferenceKey);
			result = super.convert(parent, type, converter);
			if (currentReferenceKey != null) {
				values.put(currentReferenceKey, result == null ? NULL : result);
			}
			parentStack.popSilently();
		}
		return result;
	}

	protected abstract Object getCurrentReferenceKey();

	protected abstract Object getReferenceKey(String reference);
}
