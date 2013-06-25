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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * Abstract base class for all HierarchicalStreamDriver implementations. Implementations of
 * {@link HierarchicalStreamDriver} should rather be derived from this class then implementing the interface directly.
 * 
 * @since 1.4
 */
public abstract class AbstractDriver implements HierarchicalStreamDriver {

	private NameCoder replacer;

	/**
	 * Creates an AbstractDriver with a NameCoder that does nothing.
	 */
	public AbstractDriver() {
		this(new NoNameCoder());
	}

	/**
	 * Creates an AbstractDriver with a provided {@link NameCoder}.
	 * 
	 * @param nameCoder
	 *            the name coder for the target format
	 */
	public AbstractDriver(NameCoder nameCoder) {
		this.replacer = nameCoder;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public HierarchicalStreamReader createReader(File in) {
		try {
			return createReader(new FileInputStream(in));
		} catch (FileNotFoundException e) {
			throw new StreamException(e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public HierarchicalStreamReader createReader(URL in) {
		InputStream stream = null;
		try {
			stream = in.openStream();
		} catch (IOException e) {
			throw new StreamException(e);
		}
		return createReader(stream);
	}

	protected NameCoder getNameCoder() {
		return replacer;
	}
}
