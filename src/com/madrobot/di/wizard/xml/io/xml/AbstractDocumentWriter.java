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

import java.util.ArrayList;
import java.util.List;

import com.madrobot.di.wizard.xml.io.AbstractWriter;
import com.madrobot.di.wizard.xml.io.NameCoder;
import com.madrobot.util.FastStack;

/**
 * A generic {@link com.madrobot.di.wizard.xml.io.HierarchicalStreamWriter} for DOM writer implementations. The
 * implementation manages a list of top level DOM nodes. Every time the last node is closed on the node stack, the next
 * started node is added to the list. This list can be retrieved using the {@link DocumentWriter#getTopLevelNodes()}
 * method.
 * 
 * @since 1.2.1
 */
public abstract class AbstractDocumentWriter extends AbstractWriter implements DocumentWriter {

	private final FastStack nodeStack = new FastStack(16);
	private final List result = new ArrayList();

	/**
	 * Constructs an AbstractDocumentWriter.
	 * 
	 * @param container
	 *            the top level container for the nodes to create (may be <code>null</code>)
	 * @param nameCoder
	 *            the object that creates XML-friendly names
	 * @since 1.4
	 */
	public AbstractDocumentWriter(final Object container, final NameCoder nameCoder) {
		super(nameCoder);
		if (container != null) {
			nodeStack.push(container);
			result.add(container);
		}
	}

	/**
	 * Constructs an AbstractDocumentWriter.
	 * 
	 * @param container
	 *            the top level container for the nodes to create (may be <code>null</code>)
	 * @param replacer
	 *            the object that creates XML-friendly names
	 * @since 1.2.1
	 * @deprecated As of 1.4 use {@link AbstractDocumentWriter#AbstractDocumentWriter(Object, NameCoder)} instead.
	 */
	@Deprecated
	public AbstractDocumentWriter(final Object container, final XmlFriendlyNameCoder replacer) {
		this(container, (NameCoder) replacer);
	}

	@Override
	public void close() {
		// don't need to do anything
	}

	/**
	 * Create a node. The provided node name is not yet XML friendly. If {@link #getCurrent()} returns <code>null</code>
	 * the node is a top level node.
	 * 
	 * @param name
	 *            the node name
	 * @return the new node
	 * @since 1.2.1
	 */
	protected abstract Object createNode(String name);

	@Override
	public final void endNode() {
		endNodeInternally();
		final Object node = nodeStack.pop();
		if (nodeStack.size() == 0) {
			result.add(node);
		}
	}

	/**
	 * Called when a node ends. Hook for derived implementations.
	 * 
	 * @since 1.2.1
	 */
	public void endNodeInternally() {
	}

	@Override
	public void flush() {
		// don't need to do anything
	}

	/**
	 * @since 1.2.1
	 */
	protected final Object getCurrent() {
		return nodeStack.peek();
	}

	@Override
	public List getTopLevelNodes() {
		return result;
	}

	@Override
	public final void startNode(final String name) {
		final Object node = createNode(name);
		nodeStack.push(node);
	}
}
