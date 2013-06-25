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

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.madrobot.di.wizard.xml.io.HierarchicalStreamReader;
import com.madrobot.di.wizard.xml.io.HierarchicalStreamWriter;

/**
 * Converts a java.lang.reflect.Method to XML.
 * 
 * @author Aslak Helles&oslash;y
 * @author J&ouml;rg Schaible
 */
public class JavaMethodConverter implements Converter {

	private final SingleValueConverter javaClassConverter;

	public JavaMethodConverter(ClassLoader classLoader) {
		this.javaClassConverter = new JavaClassConverter(classLoader);
	}

	@Override
	public boolean canConvert(Class type) {
		return type.equals(Method.class) || type.equals(Constructor.class);
	}

	@Override
	public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
		if (source instanceof Method) {
			Method method = (Method) source;
			String declaringClassName = javaClassConverter.toString(method.getDeclaringClass());
			marshalMethod(writer, declaringClassName, method.getName(), method.getParameterTypes());
		} else {
			Constructor method = (Constructor) source;
			String declaringClassName = javaClassConverter.toString(method.getDeclaringClass());
			marshalMethod(writer, declaringClassName, null, method.getParameterTypes());
		}
	}

	private void marshalMethod(HierarchicalStreamWriter writer, String declaringClassName, String methodName, Class[] parameterTypes) {

		writer.startNode("class");
		writer.setValue(declaringClassName);
		writer.endNode();

		if (methodName != null) {
			// it's a method and not a ctor
			writer.startNode("name");
			writer.setValue(methodName);
			writer.endNode();
		}

		writer.startNode("parameter-types");
		for (int i = 0; i < parameterTypes.length; i++) {
			writer.startNode("class");
			writer.setValue(javaClassConverter.toString(parameterTypes[i]));
			writer.endNode();
		}
		writer.endNode();
	}

	@Override
	public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
		try {
			boolean isMethodNotConstructor = context.getRequiredType().equals(Method.class);

			reader.moveDown();
			String declaringClassName = reader.getValue();
			Class declaringClass = (Class) javaClassConverter.fromString(declaringClassName);
			reader.moveUp();

			String methodName = null;
			if (isMethodNotConstructor) {
				reader.moveDown();
				methodName = reader.getValue();
				reader.moveUp();
			}

			reader.moveDown();
			List parameterTypeList = new ArrayList();
			while (reader.hasMoreChildren()) {
				reader.moveDown();
				String parameterTypeName = reader.getValue();
				parameterTypeList.add(javaClassConverter.fromString(parameterTypeName));
				reader.moveUp();
			}
			Class[] parameterTypes = (Class[]) parameterTypeList.toArray(new Class[parameterTypeList.size()]);
			reader.moveUp();

			if (isMethodNotConstructor) {
				return declaringClass.getDeclaredMethod(methodName, parameterTypes);
			} else {
				return declaringClass.getDeclaredConstructor(parameterTypes);
			}
		} catch (NoSuchMethodException e) {
			throw new ConversionException(e);
		}
	}
}
