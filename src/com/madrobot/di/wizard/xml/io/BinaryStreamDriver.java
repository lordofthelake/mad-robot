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

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;

/**
 * HierarchicalStreamDriver for binary input and output. The driver uses an optimized binary format to store an object
 * graph. The format is not as compact as Java serialization, but a lot more than typical text-based formats like XML.
 * However, due to its nature it cannot use a {@link Reader} for input or a {@link Writer} for output.
 * 
 * @since 1.4.2
 */
public class BinaryStreamDriver extends AbstractDriver {

	@Override
	public HierarchicalStreamReader createReader(InputStream in) {
		return new BinaryStreamReader(in);
	}

	/**
	 * @throws UnsupportedOperationException
	 *             if called
	 */
	@Override
	public HierarchicalStreamReader createReader(Reader in) {
		throw new UnsupportedOperationException("The BinaryDriver cannot use character-oriented input streams.");
	}

	@Override
	public HierarchicalStreamWriter createWriter(OutputStream out) {
		return new BinaryStreamWriter(out);
	}

	/**
	 * @throws UnsupportedOperationException
	 *             if called
	 */
	@Override
	public HierarchicalStreamWriter createWriter(Writer out) {
		throw new UnsupportedOperationException("The BinaryDriver cannot use character-oriented output streams.");
	}
}
