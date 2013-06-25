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

import com.madrobot.reflect.PrimitiveUtils;

/**
 * Converts a java.lang.Class to XML.
 * 
 * @author Aslak Helles&oslash;y
 * @author Joe Walnes
 * @author Matthew Sandoz
 * @author J&ouml;rg Schaible
 */
public class JavaClassConverter extends AbstractSingleValueConverter {

	private ClassLoader classLoader;

	public JavaClassConverter(ClassLoader classLoader) {
		this.classLoader = classLoader;
	}

	@Override
	public boolean canConvert(Class clazz) {
		return Class.class.equals(clazz); // :)
	}

	@Override
	public Object fromString(String str) {
		try {
			return loadClass(str);
		} catch (ClassNotFoundException e) {
			throw new ConversionException("Cannot load java class " + str, e);
		}
	}

	private Class loadClass(String className) throws ClassNotFoundException {
		Class resultingClass = PrimitiveUtils.primitiveType(className);
		if (resultingClass != null) {
			return resultingClass;
		}
		int dimension;
		for (dimension = 0; className.charAt(dimension) == '['; ++dimension)
			;
		if (dimension > 0) {
			final ClassLoader classLoaderToUse;
			if (className.charAt(dimension) == 'L') {
				String componentTypeName = className.substring(dimension + 1, className.length() - 1);
				classLoaderToUse = classLoader.loadClass(componentTypeName).getClassLoader();
			} else {
				classLoaderToUse = null;
			}
			return Class.forName(className, false, classLoaderToUse);
		}
		return classLoader.loadClass(className);
	}

	@Override
	public String toString(Object obj) {
		return ((Class) obj).getName();
	}
}
