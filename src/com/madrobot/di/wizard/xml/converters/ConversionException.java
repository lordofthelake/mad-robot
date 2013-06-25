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

import java.util.Iterator;
import java.util.Map;

import com.madrobot.di.wizard.xml.XMLWizardException;
import com.madrobot.util.OrderRetainingMap;

/**
 * Thrown by {@link Converter} implementations when they cannot convert an object to/from textual data.
 * 
 * When this exception is thrown it can be passed around to things that accept an {@link ErrorWriter}, allowing them to
 * add diagnostics to the stack trace.
 * 
 * 
 * @see ErrorWriter
 */
public class ConversionException extends XMLWizardException implements ErrorWriter {

	private static final String SEPARATOR = "\n-------------------------------";
	private Map stuff = new OrderRetainingMap();

	public ConversionException(String msg) {
		super(msg);
	}

	public ConversionException(String msg, Throwable cause) {
		super(msg, cause);
		if (msg != null) {
			add("message", msg);
		}
		if (cause != null) {
			add("cause-exception", cause.getClass().getName());
			add("cause-message", cause instanceof ConversionException ? ((ConversionException) cause).getShortMessage()
					: cause.getMessage());
		}
	}

	public ConversionException(Throwable cause) {
		this(cause.getMessage(), cause);
	}

	@Override
	public void add(String name, String information) {
		String key = name;
		int i = 0;
		while (stuff.containsKey(key)) {
			String value = (String) stuff.get(key);
			if (information.equals(value))
				return;
			key = name + "[" + ++i + "]";
		}
		stuff.put(key, information);
	}

	@Override
	public String get(String errorKey) {
		return (String) stuff.get(errorKey);
	}

	@Override
	public String getMessage() {
		StringBuffer result = new StringBuffer();
		if (super.getMessage() != null) {
			result.append(super.getMessage());
		}
		if (!result.toString().endsWith(SEPARATOR)) {
			result.append("\n---- Debugging information ----");
		}
		for (Iterator iterator = keys(); iterator.hasNext();) {
			String k = (String) iterator.next();
			String v = get(k);
			result.append('\n').append(k);
			result.append("                    ".substring(Math.min(20, k.length())));
			result.append(": ").append(v);
		}
		result.append(SEPARATOR);
		return result.toString();
	}

	public String getShortMessage() {
		return super.getMessage();
	}

	@Override
	public Iterator keys() {
		return stuff.keySet().iterator();
	}

	@Override
	public void set(String name, String information) {
		String key = name;
		int i = 0;
		stuff.put(key, information); // keep order
		while (stuff.containsKey(key)) {
			if (i != 0) {
				stuff.remove(key);
			}
			key = name + "[" + ++i + "]";
		}
	}
}
