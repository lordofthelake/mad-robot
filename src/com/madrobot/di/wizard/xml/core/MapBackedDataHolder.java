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

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.madrobot.di.wizard.xml.converters.DataHolder;

public class MapBackedDataHolder implements DataHolder {
	private final Map map;

	public MapBackedDataHolder() {
		this(new HashMap());
	}

	public MapBackedDataHolder(Map map) {
		this.map = map;
	}

	@Override
	public Object get(Object key) {
		return map.get(key);
	}

	@Override
	public Iterator keys() {
		return Collections.unmodifiableCollection(map.keySet()).iterator();
	}

	@Override
	public void put(Object key, Object value) {
		map.put(key, value);
	}
}
