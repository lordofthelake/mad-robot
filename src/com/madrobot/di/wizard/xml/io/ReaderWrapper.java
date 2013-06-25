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

import com.madrobot.di.wizard.xml.converters.ErrorWriter;

/**
 * Base class to make it easy to create wrappers (decorators) for HierarchicalStreamReader.
 * 
 * @author Joe Walnes
 */
public abstract class ReaderWrapper implements ExtendedHierarchicalStreamReader {

	protected HierarchicalStreamReader wrapped;

	protected ReaderWrapper(HierarchicalStreamReader reader) {
		this.wrapped = reader;
	}

	@Override
	public void appendErrors(ErrorWriter errorWriter) {
		wrapped.appendErrors(errorWriter);
	}

	@Override
	public void close() {
		wrapped.close();
	}

	@Override
	public String getAttribute(int index) {
		return wrapped.getAttribute(index);
	}

	@Override
	public String getAttribute(String name) {
		return wrapped.getAttribute(name);
	}

	@Override
	public int getAttributeCount() {
		return wrapped.getAttributeCount();
	}

	@Override
	public String getAttributeName(int index) {
		return wrapped.getAttributeName(index);
	}

	@Override
	public Iterator getAttributeNames() {
		return wrapped.getAttributeNames();
	}

	@Override
	public String getNodeName() {
		return wrapped.getNodeName();
	}

	@Override
	public String getValue() {
		return wrapped.getValue();
	}

	@Override
	public boolean hasMoreChildren() {
		return wrapped.hasMoreChildren();
	}

	@Override
	public void moveDown() {
		wrapped.moveDown();
	}

	@Override
	public void moveUp() {
		wrapped.moveUp();
	}

	@Override
	public String peekNextChild() {
		if (!(wrapped instanceof ExtendedHierarchicalStreamReader)) {
			throw new UnsupportedOperationException("peekNextChild");
		}
		return ((ExtendedHierarchicalStreamReader) wrapped).peekNextChild();
	}

	@Override
	public HierarchicalStreamReader underlyingReader() {
		return wrapped.underlyingReader();
	}
}
