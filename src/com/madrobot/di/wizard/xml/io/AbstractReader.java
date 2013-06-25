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
 * Abstract base class for all HierarchicalStreamReader implementations. Implementations of
 * {@link HierarchicalStreamReader} should rather be derived from this class then implementing the interface directly.
 * 
 * @since 1.4
 */
public abstract class AbstractReader implements ExtendedHierarchicalStreamReader {

	private NameCoder nameCoder;

	/**
	 * Creates an AbstractReader with a NameCoder that does nothing.
	 * 
	 * @since 1.4
	 */
	protected AbstractReader() {
		this(new NoNameCoder());
	}

	/**
	 * Creates an AbstractReader with a provided {@link NameCoder}.
	 * 
	 * @param nameCoder
	 *            the name coder used to read names from the incoming format
	 * @since 1.4
	 */
	protected AbstractReader(NameCoder nameCoder) {
		this.nameCoder = ObjectUtils.cloneIfPossible(nameCoder);
	}

	/**
	 * Decode an attribute name from the target format.
	 * 
	 * @param name
	 *            the name in the target format
	 * @return the original name
	 * @since 1.4
	 */
	public String decodeAttribute(String name) {
		return nameCoder.decodeAttribute(name);
	}

	/**
	 * Decode a node name from the target format.
	 * 
	 * @param name
	 *            the name in the target format
	 * @return the original name
	 * @since 1.4
	 */
	public String decodeNode(String name) {
		return nameCoder.decodeNode(name);
	}

	/**
	 * Encode the attribute name again into the name of the target format. Internally used.
	 * 
	 * @param name
	 *            the original name
	 * @return the name in the target format
	 * @since 1.4
	 */
	protected String encodeAttribute(String name) {
		return nameCoder.encodeAttribute(name);
	}

	/**
	 * Encode the node name again into the name of the target format. Internally used.
	 * 
	 * @param name
	 *            the original name
	 * @return the name in the target format
	 * @since 1.4
	 */
	protected String encodeNode(String name) {
		return nameCoder.encodeNode(name);
	}

	@Override
	public String peekNextChild() {
		throw new UnsupportedOperationException("peekNextChild");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public HierarchicalStreamReader underlyingReader() {
		return this;
	}
}
