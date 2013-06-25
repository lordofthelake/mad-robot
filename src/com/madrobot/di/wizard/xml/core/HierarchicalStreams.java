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
package com.madrobot.di.wizard.xml.core;

import com.madrobot.di.wizard.xml.Mapper;
import com.madrobot.di.wizard.xml.io.HierarchicalStreamReader;
import com.madrobot.di.wizard.xml.io.HierarchicalStreamWriter;

/**
 * Helper methods for {@link HierarchicalStreamReader} and {@link HierarchicalStreamWriter}.
 * 
 * @since 1.3.1
 */
public class HierarchicalStreams {

	public static String readClassAttribute(HierarchicalStreamReader reader, Mapper mapper) {
		String attributeName = mapper.aliasForSystemAttribute("resolves-to");
		String classAttribute = attributeName == null ? null : reader.getAttribute(attributeName);
		if (classAttribute == null) {
			attributeName = mapper.aliasForSystemAttribute("class");
			if (attributeName != null) {
				classAttribute = reader.getAttribute(attributeName);
			}
		}
		return classAttribute;
	}

	public static Class readClassType(HierarchicalStreamReader reader, Mapper mapper) {
		String classAttribute = readClassAttribute(reader, mapper);
		Class type;
		if (classAttribute == null) {
			type = mapper.realClass(reader.getNodeName());
		} else {
			type = mapper.realClass(classAttribute);
		}
		return type;
	}

}
