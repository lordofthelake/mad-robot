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

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import com.madrobot.di.wizard.xml.io.HierarchicalStreamDriver;
import com.madrobot.di.wizard.xml.io.NameCoder;

/**
 * A {@link HierarchicalStreamDriver} using the XmlPullParserFactory to locate an XML Pull Parser.
 * 
 */
public class XppDriver extends AbstractXppDriver {

	private static XmlPullParserFactory factory;

	public XppDriver() {
		super(new XmlFriendlyNameCoder());
	}

	/**
	 * @since 1.4
	 */
	public XppDriver(NameCoder nameCoder) {
		super(nameCoder);
	}

	/**
	 * @since 1.2
	 * @deprecated As of 1.4, use {@link XppDriver#XppDriver(NameCoder)} instead.
	 */
	@Deprecated
	public XppDriver(XmlFriendlyNameCoder replacer) {
		this((NameCoder) replacer);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected synchronized XmlPullParser createParser() throws XmlPullParserException {
		if (factory == null) {
			factory = XmlPullParserFactory.newInstance(null, XppDriver.class);
		}
		return factory.newPullParser();
	}
}
