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

import com.madrobot.di.wizard.xml.io.HierarchicalStreamReader;
import com.madrobot.di.wizard.xml.io.HierarchicalStreamWriter;

/**
 * Converts a java.lang.reflect.Field to XML.
 * 
 * @author J&ouml;rg Schaible
 */
public class JavaFieldConverter implements Converter {

	private final SingleValueConverter javaClassConverter;

	public JavaFieldConverter(ClassLoader classLoader) {
		this.javaClassConverter = new JavaClassConverter(classLoader);
	}

	@Override
	public boolean canConvert(Class type) {
		return type.equals(Field.class);
	}

	@Override
	public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
		Field field = (Field) source;

		writer.startNode("name");
		writer.setValue(field.getName());
		writer.endNode();

		writer.startNode("clazz");
		writer.setValue(javaClassConverter.toString(field.getDeclaringClass()));
		writer.endNode();
	}

	@Override
	public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
		String methodName = null;
		String declaringClassName = null;

		while ((methodName == null || declaringClassName == null) && reader.hasMoreChildren()) {
			reader.moveDown();

			if (reader.getNodeName().equals("name")) {
				methodName = reader.getValue();
			} else if (reader.getNodeName().equals("clazz")) {
				declaringClassName = reader.getValue();
			}
			reader.moveUp();
		}

		Class declaringClass = (Class) javaClassConverter.fromString(declaringClassName);
		try {
			return declaringClass.getDeclaredField(methodName);
		} catch (NoSuchFieldException e) {
			throw new ConversionException(e);
		}
	}
}
