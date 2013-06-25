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

package com.madrobot.di.wizard.xml.io;

/**
 * Tool for copying the contents of one HierarichalStreamReader to a HierarichalStreamWriter.
 * <p/>
 * This is useful for transforming the output of one format to another (e.g. binary to XML) without needing to know
 * details about the classes and avoiding the overhead of serialization.
 * 
 * <h3>Example</h3>
 * 
 * <pre>
 * HierarchicalStreamReader reader = new BinaryStreamReader(someBinaryInput);
 * HierarchicalStreamWriter writer = new PrettyPrintWriter(someXmlOutput);
 * HierarchicalStreamCopier copier = new HierarchicalStreamCopier();
 * copier.copy(reader, writer);
 * </pre>
 * 
 * @since 1.2
 */
public class HierarchicalStreamCopier {
	public void copy(HierarchicalStreamReader source, HierarchicalStreamWriter destination) {
		destination.startNode(source.getNodeName());
		int attributeCount = source.getAttributeCount();
		for (int i = 0; i < attributeCount; i++) {
			destination.addAttribute(source.getAttributeName(i), source.getAttribute(i));
		}
		String value = source.getValue();
		if (value != null && value.length() > 0) {
			destination.setValue(value);
		}
		while (source.hasMoreChildren()) {
			source.moveDown();
			copy(source, destination);
			source.moveUp();
		}
		destination.endNode();
	}
}
