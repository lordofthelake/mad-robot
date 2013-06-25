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
import com.madrobot.di.wizard.xml.core.HierarchicalStreams;
import com.madrobot.di.wizard.xml.io.ExtendedHierarchicalStreamWriterHelper;
import com.madrobot.di.wizard.xml.io.HierarchicalStreamReader;
import com.madrobot.di.wizard.xml.io.HierarchicalStreamWriter;

/**
 * Base helper class for converters that need to handle collections of items (arrays, Lists, Maps, etc).
 * <p/>
 * <p>
 * Typically, subclasses of this will converter the outer structure of the collection, loop through the contents and
 * call readItem() or writeItem() for each item.
 * </p>
 * 
 * @author Joe Walnes
 */
public abstract class AbstractCollectionConverter implements Converter {

	private final Mapper mapper;

	public AbstractCollectionConverter(Mapper mapper) {
		this.mapper = mapper;
	}

	@Override
	public abstract boolean canConvert(Class type);

	protected Object createCollection(Class type) {
		Class defaultType = mapper().defaultImplementationOf(type);
		try {
			return defaultType.newInstance();
		} catch (InstantiationException e) {
			throw new ConversionException("Cannot instantiate " + defaultType.getName(), e);
		} catch (IllegalAccessException e) {
			throw new ConversionException("Cannot instantiate " + defaultType.getName(), e);
		}
	}

	protected Mapper mapper() {
		return mapper;
	}

	@Override
	public abstract void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context);

	protected Object readItem(HierarchicalStreamReader reader, UnmarshallingContext context, Object current) {
		Class type = HierarchicalStreams.readClassType(reader, mapper());
		return context.convertAnother(current, type);
	}

	@Override
	public abstract Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context);

	protected void writeItem(Object item, MarshallingContext context, HierarchicalStreamWriter writer) {
		// PUBLISHED API METHOD! If changing signature, ensure backwards compatibility.
		if (item == null) {
			// todo: this is duplicated in TreeMarshaller.start()
			String name = mapper().serializedClass(null);
			ExtendedHierarchicalStreamWriterHelper.startNode(writer, name, Mapper.Null.class);
			writer.endNode();
		} else {
			String name = mapper().serializedClass(item.getClass());
			ExtendedHierarchicalStreamWriterHelper.startNode(writer, name, item.getClass());
			context.convertAnother(item);
			writer.endNode();
		}
	}
}
