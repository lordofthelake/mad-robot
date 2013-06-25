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

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import com.madrobot.util.FastStack;

/**
 * An wrapper for all {@link HierarchicalStreamWriter} implementations, that keeps the state. Writing in a wrong state
 * will throw a {@link StreamException}, that wraps either an {@link IOException} (writing to a closed writer) or an
 * {@link IllegalStateException}. The implementation will also track unbalanced nodes or multiple attributes with the
 * same name.
 * 
 * @since 1.2
 */
public class StatefulWriter extends WriterWrapper {

	/**
	 * <code>STATE_CLOSED</code> is the state if the writer has been closed.
	 * 
	 * @since 1.2
	 */
	public static int STATE_CLOSED = 4;
	/**
	 * <code>STATE_NODE_END</code> is the state if a node has ended
	 * 
	 * @since 1.2
	 */
	public static int STATE_NODE_END = 3;
	/**
	 * <code>STATE_NODE_START</code> is the state of a new node has been started.
	 * 
	 * @since 1.2
	 */
	public static int STATE_NODE_START = 1;
	/**
	 * <code>STATE_OPEN</code> is the initial value of the writer.
	 * 
	 * @since 1.2
	 */
	public static int STATE_OPEN = 0;
	/**
	 * <code>STATE_VALUE</code> is the state if the value of a node has been written.
	 * 
	 * @since 1.2
	 */
	public static int STATE_VALUE = 2;

	private transient FastStack attributes;
	private transient int balance;
	private transient int state = STATE_OPEN;

	/**
	 * Constructs a StatefulWriter.
	 * 
	 * @param wrapped
	 *            the wrapped writer
	 * @since 1.2
	 */
	public StatefulWriter(final HierarchicalStreamWriter wrapped) {
		super(wrapped);
		attributes = new FastStack(16);
	}

	@Override
	public void addAttribute(String name, String value) {
		checkClosed();
		if (state != STATE_NODE_START) {
			throw new StreamException(new IllegalStateException("Writing attribute '" + name
					+ "' without an opened node"));
		}
		Set currentAttributes = (Set) attributes.peek();
		if (currentAttributes.contains(name)) {
			throw new StreamException(new IllegalStateException("Writing attribute '" + name + "' twice"));
		}
		currentAttributes.add(name);
		super.addAttribute(name, value);
	}

	private void checkClosed() {
		if (state == STATE_CLOSED) {
			throw new StreamException(new IOException("Writing on a closed stream"));
		}
	}

	@Override
	public void close() {
		if (state != STATE_NODE_END && state != STATE_OPEN) {
			// calling close in a finally block should not throw again
			// throw new StreamException(new IllegalStateException("Closing with unbalanced tag"));
		}
		state = STATE_CLOSED;
		super.close();
	}

	@Override
	public void endNode() {
		checkClosed();
		if (balance-- == 0) {
			throw new StreamException(new IllegalStateException("Unbalanced node"));
		}
		attributes.popSilently();
		state = STATE_NODE_END;
		super.endNode();
	}

	@Override
	public void flush() {
		checkClosed();
		super.flush();
	}

	private Object readResolve() {
		attributes = new FastStack(16);
		return this;
	}

	@Override
	public void setValue(String text) {
		checkClosed();
		if (state != STATE_NODE_START) {
			// STATE_NODE_END is legal XML, but not in XStream ... ?
			throw new StreamException(new IllegalStateException("Writing text without an opened node"));
		}
		state = STATE_VALUE;
		super.setValue(text);
	}

	@Override
	public void startNode(final String name) {
		startNodeCommon();
		super.startNode(name);
	}

	@Override
	public void startNode(final String name, final Class clazz) {
		startNodeCommon();
		super.startNode(name, clazz);
	}

	private void startNodeCommon() {
		checkClosed();
		if (state == STATE_VALUE) {
			// legal XML, but not in XStream ... ?
			throw new StreamException(new IllegalStateException("Opening node after writing text"));
		}
		state = STATE_NODE_START;
		++balance;
		attributes.push(new HashSet());
	}

	/**
	 * Retrieve the state of the writer.
	 * 
	 * @return one of the states
	 * @see #STATE_OPEN
	 * @see #STATE_NODE_START
	 * @see #STATE_VALUE
	 * @see #STATE_NODE_END
	 * @see #STATE_CLOSED
	 * @since 1.2
	 */
	public int state() {
		return state;
	}
}
