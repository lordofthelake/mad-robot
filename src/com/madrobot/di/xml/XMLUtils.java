/*******************************************************************************
 * Copyright (c) 2011 MadRobot.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *  Elton Kent - initial API and implementation
 ******************************************************************************/
package com.madrobot.di.xml;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.AttributeSet;
import android.util.Xml;

/**
 * XML helper utilities
 * XMLUtility.java
 * 
 * @author Elton Kent
 */
public final class XMLUtils {
	/**
	 * Get the xml as a AttributeSet
	 *
	 * @param is XML InputStream
	 * @return AttributeSet representation of the xml stream 
	 * @throws XmlPullParserException
	 */
	public static AttributeSet getAttributeSet(InputStream is) throws XmlPullParserException {
		XmlPullParser parser = Xml.newPullParser();
		parser.setInput(is, null);
		AttributeSet set = Xml.asAttributeSet(parser);
		return set;

	}

	/**
	 * Loads an XML stream into a Document instance
	 * 
	 * @param is
	 * @return XML file loaded in a document
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public static Document loadDoc(InputStream is) throws ParserConfigurationException, SAXException,
			IOException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		return builder.parse(is);
	}

	private XMLUtils() {
	}
}
