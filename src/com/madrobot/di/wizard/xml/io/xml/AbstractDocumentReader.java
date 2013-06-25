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

package com.madrobot.di.wizard.xml.io.xml;

import java.util.Iterator;

import com.madrobot.di.wizard.xml.converters.ErrorWriter;
import com.madrobot.di.wizard.xml.io.AbstractReader;
import com.madrobot.di.wizard.xml.io.AttributeNameIterator;
import com.madrobot.di.wizard.xml.io.NameCoder;
import com.madrobot.util.FastStack;

public abstract class AbstractDocumentReader extends AbstractReader implements DocumentReader {

	private static class Pointer {
		public int v;
	}
	private Object current;

	private FastStack pointers = new FastStack(16);

	protected AbstractDocumentReader(Object rootElement) {
		this(rootElement, new XmlFriendlyNameCoder());
	}

	/**
	 * @since 1.4
	 */
	protected AbstractDocumentReader(Object rootElement, NameCoder nameCoder) {
		super(nameCoder);
		this.current = rootElement;
		pointers.push(new Pointer());
		reassignCurrentElement(current);
	}

	/**
	 * @since 1.2
	 * @deprecated As of 1.4, use {@link AbstractDocumentReader#AbstractDocumentReader(Object, NameCoder)} instead.
	 */
	@Deprecated
	protected AbstractDocumentReader(Object rootElement, XmlFriendlyNameCoder replacer) {
		this(rootElement, (NameCoder) replacer);
	}

	@Override
	public void appendErrors(ErrorWriter errorWriter) {
	}

	@Override
	public void close() {
		// don't need to do anything
	}

	@Override
	public Iterator getAttributeNames() {
		return new AttributeNameIterator(this);
	}

	protected abstract Object getChild(int index);

	protected abstract int getChildCount();

	@Override
	public Object getCurrent() {
		return this.current;
	}

	protected abstract Object getParent();

	@Override
	public boolean hasMoreChildren() {
		Pointer pointer = (Pointer) pointers.peek();

		if (pointer.v < getChildCount()) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void moveDown() {
		Pointer pointer = (Pointer) pointers.peek();
		pointers.push(new Pointer());

		current = getChild(pointer.v);

		pointer.v++;
		reassignCurrentElement(current);
	}

	@Override
	public void moveUp() {
		current = getParent();
		pointers.popSilently();
		reassignCurrentElement(current);
	}

	protected abstract void reassignCurrentElement(Object current);
}
