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

package com.madrobot.di.wizard.xml.converters;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

import com.madrobot.di.wizard.xml.io.HierarchicalStreamReader;
import com.madrobot.di.wizard.xml.io.HierarchicalStreamWriter;
import com.madrobot.reflect.FieldUtils;

/**
 * Special converter for java.util.Properties that stores properties in a more compact form than java.util.Map.
 * <p>
 * Because all entries of a Properties instance are Strings, a single element is used for each property with two
 * attributes; one for key and one for value.
 * </p>
 * <p>
 * Additionally, default properties are also serialized, if they are present or if a SecurityManager is set, and it has
 * permissions for SecurityManager.checkPackageAccess, SecurityManager.checkMemberAccess(this, EnumSet.MEMBER) and
 * ReflectPermission("suppressAccessChecks").
 * </p>
 * 
 */
public class PropertiesConverter implements Converter {

	private final static Field defaultsField;
	static {
		Field field = null;
		try {
			field = FieldUtils.find(Properties.class, "defaults");
		} catch (SecurityException ex) {
			// ignore, no access possible with current SecurityManager
		} catch (RuntimeException ex) {
			throw new ExceptionInInitializerError("No field 'defaults' in type Properties found");
		}
		defaultsField = field;
	}
	private final boolean sort;

	public PropertiesConverter() {
		this(false);
	}

	public PropertiesConverter(boolean sort) {
		this.sort = sort;
	}

	@Override
	public boolean canConvert(Class type) {
		return Properties.class == type;
	}

	@Override
	public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
		final Properties properties = (Properties) source;
		Map map = sort ? (Map) new TreeMap(properties) : (Map) properties;
		for (Iterator iterator = map.entrySet().iterator(); iterator.hasNext();) {
			Map.Entry entry = (Map.Entry) iterator.next();
			writer.startNode("property");
			writer.addAttribute("name", entry.getKey().toString());
			writer.addAttribute("value", entry.getValue().toString());
			writer.endNode();
		}
		if (defaultsField != null) {
			Properties defaults = null;
			try {
				defaults = (Properties) FieldUtils.readField(defaultsField, properties);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
			if (defaults != null) {
				writer.startNode("defaults");
				marshal(defaults, writer, context);
				writer.endNode();
			}
		}
	}

	@Override
	public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
		Properties properties = new Properties();
		Properties defaults = null;
		while (reader.hasMoreChildren()) {
			reader.moveDown();
			if (reader.getNodeName().equals("defaults")) {
				defaults = (Properties) unmarshal(reader, context);
			} else {
				String name = reader.getAttribute("name");
				String value = reader.getAttribute("value");
				properties.setProperty(name, value);
			}
			reader.moveUp();
		}
		if (defaults == null) {
			return properties;
		} else {
			Properties propertiesWithDefaults = new Properties(defaults);
			propertiesWithDefaults.putAll(properties);
			return propertiesWithDefaults;
		}
	}

}
