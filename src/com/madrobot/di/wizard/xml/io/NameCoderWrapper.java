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
 * A wrapper for another NameCoder.
 * 
 * @since 1.4
 */
public class NameCoderWrapper implements NameCoder {

	private final NameCoder wrapped;

	/**
	 * Construct a new wrapper for a NameCoder.
	 * 
	 * @param inner
	 *            the wrapped NameCoder
	 * @since 1.4
	 */
	public NameCoderWrapper(NameCoder inner) {
		this.wrapped = inner;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String decodeAttribute(String attributeName) {
		return wrapped.decodeAttribute(attributeName);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String decodeNode(String nodeName) {
		return wrapped.decodeNode(nodeName);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String encodeAttribute(String name) {
		return wrapped.encodeAttribute(name);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String encodeNode(String name) {
		return wrapped.encodeNode(name);
	}

}
