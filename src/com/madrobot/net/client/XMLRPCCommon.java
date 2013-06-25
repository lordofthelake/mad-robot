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
package com.madrobot.net.client;

import java.io.IOException;

import org.xmlpull.v1.XmlSerializer;

import android.util.Xml;

class XMLRPCCommon {

	protected IXMLRPCSerializer iXMLRPCSerializer;
	protected XmlSerializer serializer;
	
	XMLRPCCommon() {
		serializer = Xml.newSerializer();
		iXMLRPCSerializer = new XMLRPCSerializer();
	}

	protected void serializeParams(Object[] params) throws IllegalArgumentException, IllegalStateException, IOException {
		if (params != null && params.length != 0)
		{
			// set method params
			serializer.startTag(null, Tag.PARAMS);
			for (int i=0; i<params.length; i++) {
				serializer.startTag(null, Tag.PARAM).startTag(null, IXMLRPCSerializer.TAG_VALUE);
				iXMLRPCSerializer.serialize(serializer, params[i]);
				serializer.endTag(null, IXMLRPCSerializer.TAG_VALUE).endTag(null, Tag.PARAM);
			}
			serializer.endTag(null, Tag.PARAMS);
		}
	}
			
	/**
	 * Sets custom IXMLRPCSerializer serializer (in case when server doesn't support
	 * standard XMLRPC protocol)
	 * 
	 * @param serializer custom serializer
	 */
	public void setSerializer(IXMLRPCSerializer serializer) {
		iXMLRPCSerializer = serializer;
	}

}
