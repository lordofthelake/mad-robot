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

import java.util.Collections;
import java.util.Map;

import com.madrobot.util.WeakCache;

/**
 * Converts a String to a String ;).
 * <p>
 * Well ok, it doesn't <i>actually</i> do any conversion. The converter uses by default a map with weak references to
 * reuse instances of strings that do not exceed a length limit. This limit is by default 38 characters to cache typical
 * strings containing UUIDs. Only shorter strings are typically repeated more often in XML values.
 * </p>
 * 
 * @author Joe Walnes
 * @author Rene Schwietzke
 * @author J&ouml;rg Schaible
 */
public class StringConverter extends AbstractSingleValueConverter {

	private static final int LENGTH_LIMIT = 38;

	/**
	 * A Map to store strings as long as needed to map similar strings onto the same instance and conserve memory. The
	 * map can be set from the outside during construction, so it can be a LRU map or a weak map, synchronised or not.
	 */
	private final Map cache;
	private final int lengthLimit;

	/**
	 * Construct a StringConverter using a cache with weak references for strings not exceeding 38 characters.
	 */
	public StringConverter() {
		this(LENGTH_LIMIT);
	}

	/**
	 * Construct a StringConverter using a cache with weak references for strings not exceeding the length limit.
	 * 
	 * @param lengthLimit
	 *            maximum string length of a cached string, -1 to cache all, 0 to turn off the cache
	 * @since 1.4.2
	 */
	public StringConverter(int lengthLimit) {
		this(Collections.synchronizedMap(new WeakCache()), lengthLimit);
	}

	/**
	 * Construct a StringConverter using a map-based cache for strings not exceeding 38 characters.
	 * 
	 * @param map
	 *            the map to use for the instances to reuse (may be null to not cache at all)
	 */
	public StringConverter(final Map map) {
		this(map, LENGTH_LIMIT);
	}

	/**
	 * Construct a StringConverter using a map-based cache for strings not exceeding the length limit.
	 * 
	 * @param map
	 *            the map to use for the instances to reuse (may be null to not cache at all)
	 * @param lengthLimit
	 *            maximum string length of a cached string, -1 to cache all, 0 to turn off the cache
	 * @since 1.4.2
	 */
	public StringConverter(final Map map, int lengthLimit) {
		cache = map;
		this.lengthLimit = lengthLimit;
	}

	@Override
	public boolean canConvert(final Class type) {
		return type.equals(String.class);
	}

	@Override
	public Object fromString(final String str) {
		if (cache != null && str != null && (lengthLimit < 0 || str.length() <= lengthLimit)) {
			String s = (String) cache.get(str);

			if (s == null) {
				// fill cache
				cache.put(str, str);

				s = str;
			}

			return s;
		} else {
			return str;
		}
	}
}
