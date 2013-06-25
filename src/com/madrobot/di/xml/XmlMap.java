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

package com.madrobot.di.xml;

import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Set;

import com.madrobot.di.wizard.xml.core.PersistenceStrategy;

/**
 * A persistent map. Its values are actually serialized as xml files. If you need an application-wide synchronized
 * version of this map, try the respective Collections methods.
 * 
 */
public class XmlMap extends AbstractMap {

	class XmlMapEntries extends AbstractSet {

		@Override
		public boolean isEmpty() {
			return XmlMap.this.isEmpty();
		}

		@Override
		public Iterator iterator() {
			return persistenceStrategy.iterator();
		}

		@Override
		public int size() {
			return XmlMap.this.size();
		}

	}

	private final PersistenceStrategy persistenceStrategy;

	public XmlMap(PersistenceStrategy streamStrategy) {
		this.persistenceStrategy = streamStrategy;
	}

	@Override
	public Set entrySet() {
		return new XmlMapEntries();
	}

	@Override
	public Object get(Object key) {
		// faster lookup
		return persistenceStrategy.get(key);
	}

	@Override
	public Object put(Object key, Object value) {
		return persistenceStrategy.put(key, value);
	}

	@Override
	public Object remove(Object key) {
		return persistenceStrategy.remove(key);
	}

	@Override
	public int size() {
		return persistenceStrategy.size();
	}

}
