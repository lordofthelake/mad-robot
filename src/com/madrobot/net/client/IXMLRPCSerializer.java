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

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

public interface IXMLRPCSerializer {
	String DATETIME_FORMAT = "yyyyMMdd'T'HH:mm:ss";
	String TAG_DATA = "data";
	String TAG_MEMBER = "member";
	String TAG_NAME = "name";
	
	String TAG_VALUE = "value";
	String TYPE_ARRAY = "array";
	String TYPE_BASE64 = "base64";
	String TYPE_BOOLEAN = "boolean";
	String TYPE_DATE_TIME_ISO8601 = "dateTime.iso8601";
	String TYPE_DOUBLE = "double";
	String TYPE_I4 = "i4";
	String TYPE_I8 = "i8";
	String TYPE_INT = "int";
	// This added by mattias.ellback as part of issue #19
	String TYPE_NULL = "nil";
	String TYPE_STRING = "string";
	
	String TYPE_STRUCT = "struct";

	Object deserialize(XmlPullParser parser) throws XmlPullParserException, IOException;
	void serialize(XmlSerializer serializer, Object object) throws IOException;
}
