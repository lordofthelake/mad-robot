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
 * Converter implementations are responsible marshalling Java objects to/from textual data.
 * <p/>
 * <p>
 * If an exception occurs during processing, a {@link ConversionException} should be thrown.
 * </p>
 * <p/>
 * <p>
 * If working with the high level {@link com.madrobot.di.wizard.xml.XMLWizard} facade, you can register new converters
 * using the XStream.registerConverter() method.
 * </p>
 * <p/>
 * <p>
 * If working with the lower level API, the {@link com.madrobot.di.wizard.xml.converters.ConverterLookup} implementation
 * is responsible for looking up the appropriate converter.
 * </p>
 * <p/>
 * <p>
 * Converters for object that can store all information in a single value should implement
 * {@link com.madrobot.di.wizard.xml.converters.SingleValueConverter}.
 * <p>
 * {@link com.madrobot.di.wizard.xml.converters.AbstractSingleValueConverter} provides a starting point.
 * </p>
 * <p/>
 * <p>
 * {@link com.madrobot.di.wizard.xml.converters.AbstractCollectionConverter} provides a starting point for objects that
 * hold a collection of other objects (such as Lists and Maps).
 * </p>
 * 
 * @author Joe Walnes
 * @see com.madrobot.di.wizard.xml.XMLWizard
 * @see com.madrobot.di.wizard.xml.converters.ConverterLookup
 * @see com.madrobot.di.wizard.xml.converters.AbstractSingleValueConverter
 * @see com.madrobot.di.wizard.xml.converters.AbstractCollectionConverter
 */
public interface Converter extends ConverterMatcher {

	/**
	 * Convert an object to textual data.
	 * 
	 * @param source
	 *            The object to be marshalled.
	 * @param writer
	 *            A stream to write to.
	 * @param context
	 *            A context that allows nested objects to be processed by XMLWizard.
	 */
	void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context);

	/**
	 * Convert textual data back into an object.
	 * 
	 * @param reader
	 *            The stream to read the text from.
	 * @param context
	 * @return The resulting object.
	 */
	Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context);

}
