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

import java.io.Writer;

import com.madrobot.di.wizard.xml.io.NameCoder;

public class CompactWriter extends PrettyPrintWriter {

	public CompactWriter(Writer writer) {
		super(writer);
	}

	/**
	 * @since 1.3
	 */
	public CompactWriter(Writer writer, int mode) {
		super(writer, mode);
	}

	/**
	 * @since 1.4
	 */
	public CompactWriter(Writer writer, int mode, NameCoder nameCoder) {
		super(writer, mode, nameCoder);
	}

	/**
	 * @since 1.3
	 * @deprecated As of 1.4 use {@link CompactWriter#CompactWriter(Writer, int, NameCoder)} instead.
	 */
	@Deprecated
	public CompactWriter(Writer writer, int mode, XmlFriendlyNameCoder replacer) {
		super(writer, mode, replacer);
	}

	/**
	 * @since 1.4
	 */
	public CompactWriter(Writer writer, NameCoder nameCoder) {
		super(writer, nameCoder);
	}

	/**
	 * @deprecated As of 1.4 use {@link CompactWriter#CompactWriter(Writer, NameCoder)} instead.
	 */
	@Deprecated
	public CompactWriter(Writer writer, XmlFriendlyNameCoder replacer) {
		super(writer, replacer);
	}

	@Override
	protected void endOfLine() {
		// override parent: don't write anything at end of line
	}
}
