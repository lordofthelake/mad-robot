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

/**
 * Factory for creating StackTraceElements. Factory for creating StackTraceElements.
 * 
 */
public class StackTraceElementFactory {

	private StackTraceElement create(String declaringClass, String methodName, String fileName, int lineNumber) {
		StackTraceElement result = new Throwable().getStackTrace()[0];
		setField(result, "declaringClass", declaringClass);
		setField(result, "methodName", methodName);
		setField(result, "fileName", fileName);
		setField(result, "lineNumber", new Integer(lineNumber));
		return result;
	}

	public StackTraceElement element(String declaringClass, String methodName, String fileName) {
		return create(declaringClass, methodName, fileName, -1);
	}

	public StackTraceElement element(String declaringClass, String methodName, String fileName, int lineNumber) {
		return create(declaringClass, methodName, fileName, lineNumber);
	}

	public StackTraceElement nativeMethodElement(String declaringClass, String methodName) {
		return create(declaringClass, methodName, "Native Method", -2);
	}

	private void setField(StackTraceElement element, String fieldName, Object value) {
		try {
			final Field field = StackTraceElement.class.getDeclaredField(fieldName);
			field.setAccessible(true);
			field.set(element, value);
		} catch (Exception e) {
			throw new ConversionException(e);
		}
	}

	public StackTraceElement unknownSourceElement(String declaringClass, String methodName) {
		return create(declaringClass, methodName, "Unknown Source", -1);
	}

}
