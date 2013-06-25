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

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.madrobot.di.wizard.xml.core.Caching;
import com.madrobot.di.wizard.xml.core.FastField;

/**
 * Convenience wrapper to invoke special serialization methods on objects (and perform reflection caching).
 * 
 * @author Joe Walnes
 * @author J&ouml;rg Schaible
 */
public class SerializationMethodInvoker implements Caching {

	private static final Object[] EMPTY_ARGS = new Object[0];
	private static final Method NO_METHOD = (new Object() {
		private void noMethod() {
		}
	}).getClass().getDeclaredMethods()[0];
	private static final FastField[] OBJECT_TYPE_FIELDS = new FastField[] { new FastField(Object.class, "readResolve"),
			new FastField(Object.class, "writeReplace"), new FastField(Object.class, "readObject"),
			new FastField(Object.class, "writeObject") };
	private Map cache = Collections.synchronizedMap(new HashMap());
	{
		for (int i = 0; i < OBJECT_TYPE_FIELDS.length; ++i) {
			cache.put(OBJECT_TYPE_FIELDS[i], NO_METHOD);
		}
	}

	public void callReadObject(Class type, Object object, ObjectInputStream stream) {
		try {
			Method readObjectMethod = getMethod(type, "readObject", new Class[] { ObjectInputStream.class }, false);
			readObjectMethod.invoke(object, new Object[] { stream });
		} catch (IllegalAccessException e) {
			throw new ConversionException("Could not call " + object.getClass().getName() + ".readObject()", e);
		} catch (InvocationTargetException e) {
			throw new ConversionException("Could not call " + object.getClass().getName() + ".readObject()",
					e.getTargetException());
		}
	}

	/**
	 * Resolves an object as native serialization does by calling readResolve(), if available.
	 */
	public Object callReadResolve(Object result) {
		if (result == null) {
			return null;
		} else {
			Method readResolveMethod = getMethod(result.getClass(), "readResolve", null, true);
			if (readResolveMethod != null) {
				try {
					return readResolveMethod.invoke(result, EMPTY_ARGS);
				} catch (IllegalAccessException e) {
					throw new ObjectAccessException("Could not call " + result.getClass().getName() + ".readResolve()",
							e);
				} catch (InvocationTargetException e) {
					throw new ObjectAccessException("Could not call " + result.getClass().getName() + ".readResolve()",
							e.getTargetException());
				}
			} else {
				return result;
			}
		}
	}

	public void callWriteObject(Class type, Object instance, ObjectOutputStream stream) {
		try {
			Method readObjectMethod = getMethod(type, "writeObject", new Class[] { ObjectOutputStream.class }, false);
			readObjectMethod.invoke(instance, new Object[] { stream });
		} catch (IllegalAccessException e) {
			throw new ConversionException("Could not call " + instance.getClass().getName() + ".writeObject()", e);
		} catch (InvocationTargetException e) {
			throw new ConversionException("Could not call " + instance.getClass().getName() + ".writeObject()",
					e.getTargetException());
		}
	}

	public Object callWriteReplace(Object object) {
		if (object == null) {
			return null;
		} else {
			Method writeReplaceMethod = getMethod(object.getClass(), "writeReplace", null, true);
			if (writeReplaceMethod != null) {
				try {
					return writeReplaceMethod.invoke(object, EMPTY_ARGS);
				} catch (IllegalAccessException e) {
					throw new ObjectAccessException(
							"Could not call " + object.getClass().getName() + ".writeReplace()", e);
				} catch (InvocationTargetException e) {
					throw new ObjectAccessException(
							"Could not call " + object.getClass().getName() + ".writeReplace()", e.getTargetException());
				}
			} else {
				return object;
			}
		}
	}

	@Override
	public void flushCache() {
		cache.keySet().retainAll(Arrays.asList(OBJECT_TYPE_FIELDS));
	}

	private Method getMethod(Class type, String name, Class[] parameterTypes) {
		FastField method = new FastField(type, name);
		Method result = (Method) cache.get(method);

		if (result == null) {
			try {
				result = type.getDeclaredMethod(name, parameterTypes);
				if (!result.isAccessible()) {
					result.setAccessible(true);
				}
			} catch (NoSuchMethodException e) {
				result = getMethod(type.getSuperclass(), name, parameterTypes);
			}
			cache.put(method, result);
		}
		return result;
	}

	private Method getMethod(Class type, String name, Class[] parameterTypes, boolean includeBaseclasses) {
		Method method = getMethod(type, name, parameterTypes);
		return method == NO_METHOD || (!includeBaseclasses && !method.getDeclaringClass().equals(type)) ? null : method;
	}

	public boolean supportsReadObject(Class type, boolean includeBaseClasses) {
		return getMethod(type, "readObject", new Class[] { ObjectInputStream.class }, includeBaseClasses) != null;
	}

	public boolean supportsWriteObject(Class type, boolean includeBaseClasses) {
		return getMethod(type, "writeObject", new Class[] { ObjectOutputStream.class }, includeBaseClasses) != null;
	}
}
