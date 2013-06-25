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

import android.util.Base64;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.madrobot.di.wizard.xml.io.HierarchicalStreamReader;
import com.madrobot.di.wizard.xml.io.HierarchicalStreamWriter;

/**
 * Converts a byte array to a single Base64 encoding string.
 * 
 * @author Joe Walnes
 * @author J&ouml;rg Schaible
 */
public class EncodedByteArrayConverter implements Converter, SingleValueConverter {

	// private static final Base64Encoder base64 = new Base64Encoder();
	private static final ByteConverter byteConverter = new ByteConverter();

	@Override
	public boolean canConvert(Class type) {
		return type.isArray() && type.getComponentType().equals(byte.class);
	}

	@Override
	public Object fromString(String str) {
		return Base64.decode(str,Base64.DEFAULT);
	}

	@Override
	public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
		writer.setValue(toString(source));
	}

	@Override
	public String toString(Object obj) {
		return new String(Base64.encode(((byte[]) obj),Base64.DEFAULT));
	}

	@Override
	public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
		String data = reader.getValue(); // needs to be called before hasMoreChildren.
		if (!reader.hasMoreChildren()) {
			return fromString(data);
		} else {
			// backwards compatibility ... try to unmarshal byte arrays that haven't been encoded
			return unmarshalIndividualByteElements(reader, context);
		}
	}

	private Object unmarshalIndividualByteElements(HierarchicalStreamReader reader, UnmarshallingContext context) {
		List bytes = new ArrayList(); // have to create a temporary list because don't know the size of the array
		boolean firstIteration = true;
		while (firstIteration || reader.hasMoreChildren()) { // hangover from previous hasMoreChildren
			reader.moveDown();
			bytes.add(byteConverter.fromString(reader.getValue()));
			reader.moveUp();
			firstIteration = false;
		}
		// copy into real array
		byte[] result = new byte[bytes.size()];
		int i = 0;
		for (Iterator iterator = bytes.iterator(); iterator.hasNext();) {
			Byte b = (Byte) iterator.next();
			result[i] = b.byteValue();
			i++;
		}
		return result;
	}
}
