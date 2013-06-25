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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.madrobot.di.wizard.xml.io.AbstractDriver;
import com.madrobot.di.wizard.xml.io.HierarchicalStreamReader;
import com.madrobot.di.wizard.xml.io.HierarchicalStreamWriter;
import com.madrobot.di.wizard.xml.io.NameCoder;
import com.madrobot.di.wizard.xml.io.StreamException;

public class DomDriver extends AbstractDriver {

	private final DocumentBuilderFactory documentBuilderFactory;
	private final String encoding;

	/**
	 * Construct a DomDriver.
	 */
	public DomDriver() {
		this(null);
	}

	/**
	 * Construct a DomDriver with a specified encoding. The created DomReader will ignore any encoding attribute of the
	 * XML header though.
	 */
	public DomDriver(String encoding) {
		this(encoding, new XmlFriendlyNameCoder());
	}

	/**
	 * @since 1.4
	 */
	public DomDriver(String encoding, NameCoder nameCoder) {
		super(nameCoder);
		documentBuilderFactory = DocumentBuilderFactory.newInstance();
		this.encoding = encoding;
	}

	/**
	 * @since 1.2
	 * @deprecated As of 1.4, use {@link #DomDriver(String, NameCoder)} instead.
	 */
	@Deprecated
	public DomDriver(String encoding, XmlFriendlyNameCoder replacer) {
		this(encoding, (NameCoder) replacer);
	}

	@Override
	public HierarchicalStreamReader createReader(File in) {
		return createReader(new InputSource(in.toURI().toASCIIString()));
	}

	private HierarchicalStreamReader createReader(InputSource source) {
		try {
			DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
			if (encoding != null) {
				source.setEncoding(encoding);
			}
			Document document = documentBuilder.parse(source);
			return new DomReader(document, getNameCoder());
		} catch (FactoryConfigurationError e) {
			throw new StreamException(e);
		} catch (ParserConfigurationException e) {
			throw new StreamException(e);
		} catch (SAXException e) {
			throw new StreamException(e);
		} catch (IOException e) {
			throw new StreamException(e);
		}
	}

	@Override
	public HierarchicalStreamReader createReader(InputStream in) {
		return createReader(new InputSource(in));
	}

	@Override
	public HierarchicalStreamReader createReader(Reader in) {
		return createReader(new InputSource(in));
	}

	@Override
	public HierarchicalStreamReader createReader(URL in) {
		return createReader(new InputSource(in.toExternalForm()));
	}

	@Override
	public HierarchicalStreamWriter createWriter(OutputStream out) {
		try {
			return createWriter(encoding != null ? new OutputStreamWriter(out, encoding) : new OutputStreamWriter(out));
		} catch (UnsupportedEncodingException e) {
			throw new StreamException(e);
		}
	}

	@Override
	public HierarchicalStreamWriter createWriter(Writer out) {
		return new PrettyPrintWriter(out, getNameCoder());
	}
}
