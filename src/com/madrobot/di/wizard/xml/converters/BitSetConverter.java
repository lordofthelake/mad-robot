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

import java.util.BitSet;
import java.util.StringTokenizer;

import com.madrobot.di.wizard.xml.io.HierarchicalStreamReader;
import com.madrobot.di.wizard.xml.io.HierarchicalStreamWriter;

/**
 * Converts a java.util.BitSet to XML, as a compact comma delimited list of ones and zeros.
 * 
 * @author Joe Walnes
 */
public class BitSetConverter implements Converter {

	@Override
	public boolean canConvert(Class type) {
		return type.equals(BitSet.class);
	}

	@Override
	public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
		BitSet bitSet = (BitSet) source;
		StringBuffer buffer = new StringBuffer();
		boolean seenFirst = false;
		for (int i = 0; i < bitSet.length(); i++) {
			if (bitSet.get(i)) {
				if (seenFirst) {
					buffer.append(',');
				} else {
					seenFirst = true;
				}
				buffer.append(i);
			}
		}
		writer.setValue(buffer.toString());
	}

	@Override
	public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
		BitSet result = new BitSet();
		StringTokenizer tokenizer = new StringTokenizer(reader.getValue(), ",", false);
		while (tokenizer.hasMoreTokens()) {
			int index = Integer.parseInt(tokenizer.nextToken());
			result.set(index);
		}
		return result;
	}
}
