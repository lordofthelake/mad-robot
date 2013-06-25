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

import java.util.Iterator;

import com.madrobot.di.wizard.xml.converters.ConversionException;
import com.madrobot.di.wizard.xml.converters.Converter;
import com.madrobot.di.wizard.xml.converters.ConverterLookup;
import com.madrobot.di.wizard.xml.converters.DataHolder;
import com.madrobot.di.wizard.xml.converters.ErrorReporter;
import com.madrobot.di.wizard.xml.converters.ErrorWriter;
import com.madrobot.di.wizard.xml.converters.UnmarshallingContext;
import com.madrobot.di.wizard.xml.core.HierarchicalStreams;
import com.madrobot.di.wizard.xml.core.MapBackedDataHolder;
import com.madrobot.di.wizard.xml.io.HierarchicalStreamReader;
import com.madrobot.util.FastStack;
import com.madrobot.util.PrioritizedList;

class TreeUnmarshaller implements UnmarshallingContext {

	private ConverterLookup converterLookup;
	private DataHolder dataHolder;
	private Mapper mapper;
	protected HierarchicalStreamReader reader;
	private Object root;
	private FastStack types = new FastStack(16);
	private final PrioritizedList validationList = new PrioritizedList();

	TreeUnmarshaller(Object root, HierarchicalStreamReader reader, ConverterLookup converterLookup, Mapper mapper) {
		this.root = root;
		this.reader = reader;
		this.converterLookup = converterLookup;
		this.mapper = mapper;
	}

	@Override
	public void addCompletionCallback(Runnable work, int priority) {
		validationList.add(work, priority);
	}

	private void addInformationTo(ErrorWriter errorWriter, Class type, Converter converter, Object parent) {
		errorWriter.add("class", type.getName());
		errorWriter.add("required-type", getRequiredType().getName());
		errorWriter.add("converter-type", converter.getClass().getName());
		if (converter instanceof ErrorReporter) {
			((ErrorReporter) converter).appendErrors(errorWriter);
		}
		if (parent instanceof ErrorReporter) {
			((ErrorReporter) parent).appendErrors(errorWriter);
		}
		reader.appendErrors(errorWriter);
	}

	protected Object convert(Object parent, Class type, Converter converter) {
		try {
			types.push(type);
			Object result = converter.unmarshal(reader, this);
			types.popSilently();
			return result;
		} catch (ConversionException conversionException) {
			addInformationTo(conversionException, type, converter, parent);
			throw conversionException;
		} catch (RuntimeException e) {
			ConversionException conversionException = new ConversionException(e);
			addInformationTo(conversionException, type, converter, parent);
			throw conversionException;
		}
	}

	@Override
	public Object convertAnother(Object parent, Class type) {
		return convertAnother(parent, type, null);
	}

	@Override
	public Object convertAnother(Object parent, Class type, Converter converter) {
		type = mapper.defaultImplementationOf(type);
		if (converter == null) {
			converter = converterLookup.lookupConverterForType(type);
		} else {
			if (!converter.canConvert(type)) {
				ConversionException e = new ConversionException("Explicit selected converter cannot handle type");
				e.add("item-type", type.getName());
				e.add("converter-type", converter.getClass().getName());
				throw e;
			}
		}
		return convert(parent, type, converter);
	}

	@Override
	public Object currentObject() {
		return types.size() == 1 ? root : null;
	}

	@Override
	public Object get(Object key) {
		lazilyCreateDataHolder();
		return dataHolder.get(key);
	}

	protected Mapper getMapper() {
		return this.mapper;
	}

	@Override
	public Class getRequiredType() {
		return (Class) types.peek();
	}

	@Override
	public Iterator keys() {
		lazilyCreateDataHolder();
		return dataHolder.keys();
	}

	private void lazilyCreateDataHolder() {
		if (dataHolder == null) {
			dataHolder = new MapBackedDataHolder();
		}
	}

	@Override
	public void put(Object key, Object value) {
		lazilyCreateDataHolder();
		dataHolder.put(key, value);
	}

	Object start(DataHolder dataHolder) {
		this.dataHolder = dataHolder;
		Class type = HierarchicalStreams.readClassType(reader, mapper);
		Object result = convertAnother(null, type);
		Iterator validations = validationList.iterator();
		while (validations.hasNext()) {
			Runnable runnable = (Runnable) validations.next();
			runnable.run();
		}
		return result;
	}

}
