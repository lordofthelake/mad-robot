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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * A NameCoder that encodes and decodes names based on a map.
 * <p>
 * The provided map should contain a mapping between the name of the Java type or field to the proper element in the
 * target format. If a name cannot be found in the map, it is assumed not to be mapped at all. Note that the values of
 * the map should be unique also, otherwise the decoding will produce wrong results.
 * </p>
 * 
 * @since 1.4
 */
public class StaticNameCoder implements NameCoder {

	private transient Map attribute2Java;
	private final Map java2Attribute;

	private final Map java2Node;
	private transient Map node2Java;

	/**
	 * Construct a StaticNameCoder.
	 * 
	 * @param java2Node
	 *            mapping of Java names to nodes
	 * @param java2Attribute
	 *            mapping of Java names to attributes
	 * @since 1.4
	 */
	public StaticNameCoder(Map java2Node, Map java2Attribute) {
		this.java2Node = new HashMap(java2Node);
		if (java2Node == java2Attribute || java2Attribute == null) {
			this.java2Attribute = this.java2Node;
		} else {
			this.java2Attribute = new HashMap(java2Attribute);
		}
		readResolve();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String decodeAttribute(String attributeName) {
		String name = (String) attribute2Java.get(attributeName);
		return name == null ? attributeName : name;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String decodeNode(String nodeName) {
		String name = (String) node2Java.get(nodeName);
		return name == null ? nodeName : name;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String encodeAttribute(String name) {
		String friendlyName = (String) java2Attribute.get(name);
		return friendlyName == null ? name : friendlyName;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String encodeNode(String name) {
		String friendlyName = (String) java2Node.get(name);
		return friendlyName == null ? name : friendlyName;
	}

	private Map invertMap(Map map) {
		Map inverseMap = new HashMap(map.size());
		for (final Iterator iter = map.entrySet().iterator(); iter.hasNext();) {
			final Map.Entry entry = (Map.Entry) iter.next();
			inverseMap.put(entry.getValue(), entry.getKey());
		}
		return inverseMap;
	}

	private Object readResolve() {
		node2Java = invertMap(java2Node);
		if (java2Node == java2Attribute) {
			attribute2Java = node2Java;
		} else {
			attribute2Java = invertMap(java2Attribute);
		}
		return this;
	}
}
