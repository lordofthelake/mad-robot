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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Converter for StackTraceElement (the lines of a stack trace) - JDK 1.4+ only.
 * 
 */
public class StackTraceElementConverter extends AbstractSingleValueConverter {

	// Regular expression to parse a line of a stack trace. Returns 4 groups.
	//
	// Example: com.blah.MyClass.doStuff(MyClass.java:123)
	// |-------1------| |--2--| |----3-----| |4|
	// (Note group 4 is optional is optional and only present if a colon char exists.)

	private static final StackTraceElementFactory FACTORY = new StackTraceElementFactory();
	private static final Pattern PATTERN = Pattern.compile("^(.+)\\.([^\\(]+)\\(([^:]*)(:(\\d+))?\\)$");

	@Override
	public boolean canConvert(Class type) {
		return StackTraceElement.class.equals(type);
	}

	@Override
	public Object fromString(String str) {
		Matcher matcher = PATTERN.matcher(str);
		if (matcher.matches()) {
			String declaringClass = matcher.group(1);
			String methodName = matcher.group(2);
			String fileName = matcher.group(3);
			if (fileName.equals("Unknown Source")) {
				return FACTORY.unknownSourceElement(declaringClass, methodName);
			} else if (fileName.equals("Native Method")) {
				return FACTORY.nativeMethodElement(declaringClass, methodName);
			} else {
				if (matcher.group(4) != null) {
					int lineNumber = Integer.parseInt(matcher.group(5));
					return FACTORY.element(declaringClass, methodName, fileName, lineNumber);
				} else {
					return FACTORY.element(declaringClass, methodName, fileName);
				}
			}
		} else {
			throw new ConversionException("Could not parse StackTraceElement : " + str);
		}
	}

	@Override
	public String toString(Object obj) {
		String s = super.toString(obj);
		// JRockit adds ":???" for invalid line number
		return s.replaceFirst(":\\?\\?\\?", "");
	}

}
