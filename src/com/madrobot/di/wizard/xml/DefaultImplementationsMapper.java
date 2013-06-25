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

package com.madrobot.di.wizard.xml;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Mapper that resolves default implementations of classes. For example, mapper.serializedClass(ArrayList.class) will
 * return java.util.List. Calling mapper.defaultImplementationOf(List.class) will return ArrayList.
 * 
 */
class DefaultImplementationsMapper extends MapperWrapper {

	private transient Map implToType = new HashMap();
	private final Map typeToImpl = new HashMap();

	DefaultImplementationsMapper(Mapper wrapped) {
		super(wrapped);
		addDefaults();
	}

	public void addDefaultImplementation(Class defaultImplementation, Class ofType) {
		if (defaultImplementation != null && defaultImplementation.isInterface()) {
			throw new InitializationException("Default implementation is not a concrete class: "
					+ defaultImplementation.getName());
		}
		typeToImpl.put(ofType, defaultImplementation);
		implToType.put(defaultImplementation, ofType);
	}

	protected void addDefaults() {
		// null handling
		addDefaultImplementation(null, Mapper.Null.class);
		// register primitive types
		addDefaultImplementation(Boolean.class, boolean.class);
		addDefaultImplementation(Character.class, char.class);
		addDefaultImplementation(Integer.class, int.class);
		addDefaultImplementation(Float.class, float.class);
		addDefaultImplementation(Double.class, double.class);
		addDefaultImplementation(Short.class, short.class);
		addDefaultImplementation(Byte.class, byte.class);
		addDefaultImplementation(Long.class, long.class);
	}

	@Override
	public Class defaultImplementationOf(Class type) {
		if (typeToImpl.containsKey(type)) {
			return (Class) typeToImpl.get(type);
		} else {
			return super.defaultImplementationOf(type);
		}
	}

	private Object readResolve() {
		implToType = new HashMap();
		for (final Iterator iter = typeToImpl.keySet().iterator(); iter.hasNext();) {
			final Object type = iter.next();
			implToType.put(typeToImpl.get(type), type);
		}
		return this;
	}

	@Override
	public String serializedClass(Class type) {
		Class baseType = (Class) implToType.get(type);
		return baseType == null ? super.serializedClass(type) : super.serializedClass(baseType);
	}
}
