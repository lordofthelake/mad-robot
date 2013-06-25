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

import java.util.Iterator;

/**
 * Provide an iterator over the attribute names of the current node of a reader.
 * 
 */
public class AttributeNameIterator implements Iterator {

	private final int count;
	private int current;
	private final HierarchicalStreamReader reader;

	public AttributeNameIterator(HierarchicalStreamReader reader) {
		this.reader = reader;
		count = reader.getAttributeCount();
	}

	@Override
	public boolean hasNext() {
		return current < count;
	}

	@Override
	public Object next() {
		return reader.getAttributeName(current++);
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

}
