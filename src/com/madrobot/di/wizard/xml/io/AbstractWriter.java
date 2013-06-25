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

import com.madrobot.reflect.ObjectUtils;

/**
 * Abstract base class for all HierarchicalStreamWriter implementations. Implementations of
 * {@link HierarchicalStreamWriter} should rather be derived from this class then implementing the interface directly.
 * 
 * @since 1.4
 */
public abstract class AbstractWriter implements ExtendedHierarchicalStreamWriter {

	private NameCoder nameCoder;

	/**
	 * Creates an AbstractWriter with a NameCoder that does nothing.
	 * 
	 * @since 1.4
	 */
	protected AbstractWriter() {
		this(new NoNameCoder());
	}

	/**
	 * Creates an AbstractWriter with a provided {@link NameCoder}.
	 * 
	 * @param nameCoder
	 *            the name coder used to write names in the target format
	 * @since 1.4
	 */
	protected AbstractWriter(NameCoder nameCoder) {
		this.nameCoder = ObjectUtils.cloneIfPossible(nameCoder);
	}

	/**
	 * Encode the attribute name into the name of the target format.
	 * 
	 * @param name
	 *            the original name
	 * @return the name in the target format
	 * @since 1.4
	 */
	public String encodeAttribute(String name) {
		return nameCoder.encodeAttribute(name);
	}

	/**
	 * Encode the node name into the name of the target format.
	 * 
	 * @param name
	 *            the original name
	 * @return the name in the target format
	 * @since 1.4
	 */
	public String encodeNode(String name) {
		return nameCoder.encodeNode(name);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void startNode(String name, Class clazz) {
		startNode(name);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public HierarchicalStreamWriter underlyingWriter() {
		return this;
	}
}
