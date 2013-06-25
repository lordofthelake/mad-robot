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
import java.lang.reflect.InvocationTargetException;

/**
 * Convenient converter for classes with natural string representation.
 * 
 * Converter for classes that adopt the following convention: - a constructor that takes a single string parameter - a
 * toString() that is overloaded to issue a string that is meaningful
 * 
 */
public class ToStringConverter extends AbstractSingleValueConverter {
	private final Class clazz;
	private final Constructor ctor;

	public ToStringConverter(Class clazz) throws NoSuchMethodException {
		this.clazz = clazz;
		ctor = clazz.getConstructor(new Class[] { String.class });
	}

	@Override
	public boolean canConvert(Class type) {
		return type.equals(clazz);
	}

	@Override
	public Object fromString(String str) {
		try {
			return ctor.newInstance(new Object[] { str });
		} catch (InstantiationException e) {
			throw new ConversionException("Unable to instantiate single String param constructor", e);
		} catch (IllegalAccessException e) {
			throw new ConversionException("Unable to access single String param constructor", e);
		} catch (InvocationTargetException e) {
			throw new ConversionException("Unable to target single String param constructor", e.getTargetException());
		}
	}

	@Override
	public String toString(Object obj) {
		return obj == null ? null : obj.toString();
	}
}
