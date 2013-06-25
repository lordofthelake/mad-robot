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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import com.madrobot.di.wizard.xml.core.XmlHeaderAwareReader;
import com.madrobot.di.wizard.xml.io.AbstractDriver;
import com.madrobot.di.wizard.xml.io.HierarchicalStreamReader;
import com.madrobot.di.wizard.xml.io.HierarchicalStreamWriter;
import com.madrobot.di.wizard.xml.io.NameCoder;
import com.madrobot.di.wizard.xml.io.StreamException;

/**
 * An abstract base class for a driver using an XPP implementation.
 * 
 * @since 1.4
 */
public abstract class AbstractXppDriver extends AbstractDriver {

	/**
	 * Construct an AbstractXppDriver.
	 * 
	 * @param nameCoder
	 *            the replacer for XML friendly tag and attribute names
	 * @since 1.4
	 */
	public AbstractXppDriver(NameCoder nameCoder) {
		super(nameCoder);
	}

	/**
	 * Create the parser of the XPP implementation.
	 * 
	 * @throws XmlPullParserException
	 *             if the parser cannot be created
	 * @since 1.4
	 */
	protected abstract XmlPullParser createParser() throws XmlPullParserException;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public HierarchicalStreamReader createReader(InputStream in) {
		try {
			return createReader(new XmlHeaderAwareReader(in));
		} catch (UnsupportedEncodingException e) {
			throw new StreamException(e);
		} catch (IOException e) {
			throw new StreamException(e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public HierarchicalStreamReader createReader(Reader in) {
		try {
			return new XppReader(in, createParser(), getNameCoder());
		} catch (XmlPullParserException e) {
			throw new StreamException("Cannot create XmlPullParser");
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public HierarchicalStreamWriter createWriter(OutputStream out) {
		return createWriter(new OutputStreamWriter(out));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public HierarchicalStreamWriter createWriter(Writer out) {
		return new PrettyPrintWriter(out, getNameCoder());
	}
}
