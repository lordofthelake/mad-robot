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

package com.madrobot.di.wizard.xml.core;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import com.madrobot.di.wizard.xml.converters.PureJavaReflectionProvider;
import com.madrobot.di.wizard.xml.converters.ReflectionProvider;
import com.madrobot.util.PresortedMap;
import com.madrobot.util.PresortedSet;
import com.madrobot.util.WeakCache;

public class JVM implements Caching {

	private static final boolean canParseUTCDateFormat;
	static final float DEFAULT_JAVA_VERSION = 1.3f;

	private static final float majorJavaVersion = getMajorJavaVersion();

	private static final boolean optimizedTreeMapPutAll;
	private static final boolean optimizedTreeSetAddAll;
	private static final boolean reverseFieldOrder = isHarmony() || (isIBM() && !is15());

	private static final String vendor = System.getProperty("java.vm.vendor");
	static {
		Comparator comparator = new Comparator() {
			@Override
			public int compare(Object o1, Object o2) {
				throw new RuntimeException();
			}
		};
		boolean test = true;
		SortedMap map = new PresortedMap(comparator);
		map.put("one", null);
		map.put("two", null);
		try {
			new TreeMap(comparator).putAll(map);
		} catch (RuntimeException e) {
			test = false;
		}
		optimizedTreeMapPutAll = test;
		SortedSet set = new PresortedSet(comparator);
		set.addAll(map.keySet());
		try {
			new TreeSet(comparator).addAll(set);
			test = true;
		} catch (RuntimeException e) {
			test = false;
		}
		optimizedTreeSetAddAll = test;
		try {
			new SimpleDateFormat("z").parse("UTC");
			test = true;
		} catch (ParseException e) {
			test = false;
		}
		canParseUTCDateFormat = test;
	}
	public static boolean canParseUTCDateFormat() {
		return canParseUTCDateFormat;
	}

	/**
	 * Parses the java version system property to determine the major java version, i.e. 1.x
	 * 
	 * @return A float of the form 1.x
	 */
	private static final float getMajorJavaVersion() {
		try {
			return isAndroid() ? 1.5f : Float.parseFloat(System.getProperty("java.specification.version"));
		} catch (NumberFormatException e) {
			// Some JVMs may not conform to the x.y.z java.version format
			return DEFAULT_JAVA_VERSION;
		}
	}

	/**
	 * Checks if TreeMap.putAll is optimized for SortedMap argument.
	 * 
	 * @since 1.4
	 */
	public static boolean hasOptimizedTreeMapPutAll() {
		return optimizedTreeMapPutAll;
	}

	/**
	 * Checks if TreeSet.addAll is optimized for SortedSet argument.
	 * 
	 * @since 1.4
	 */
	public static boolean hasOptimizedTreeSetAddAll() {
		return optimizedTreeSetAddAll;
	}

	public static boolean is14() {
		return majorJavaVersion >= 1.4f;
	}

	public static boolean is15() {
		return majorJavaVersion >= 1.5f;
	}

	/**
	 * @since 1.4
	 */
	private static boolean isAndroid() {
		return vendor.indexOf("Android") != -1;
	}

	private static boolean isHarmony() {
		return vendor.indexOf("Apache Software Foundation") != -1;
	}

	private static boolean isIBM() {
		return vendor.indexOf("IBM") != -1;
	}

	public static boolean reverseFieldDefinition() {
		return reverseFieldOrder;
	}

	private transient Map loaderCache = new WeakCache(new HashMap());

	private ReflectionProvider reflectionProvider;

	private final boolean supportsSQL = loadClass("java.sql.Date") != null;

	public synchronized ReflectionProvider bestReflectionProvider() {
		if (reflectionProvider == null) {
			// try {

			reflectionProvider = new PureJavaReflectionProvider();

			// } catch (AccessControlException e) {
			// // thrown when trying to access sun.misc package in Applet
			// // context.
			// reflectionProvider = new PureJavaReflectionProvider();
			// }
		}
		return reflectionProvider;
	}

	@Override
	public void flushCache() {
		loaderCache.clear();
	}

	public Class loadClass(String name) {
		try {
			Class cached = (Class) loaderCache.get(name);
			if (cached != null) {
				return cached;
			}

			Class clazz = Class.forName(name, false, getClass().getClassLoader());
			loaderCache.put(name, clazz);
			return clazz;
		} catch (ClassNotFoundException e) {
			return null;
		}
	}

	/**
	 * Checks if the jvm supports sql.
	 */
	public boolean supportsSQL() {
		return this.supportsSQL;
	}

}
