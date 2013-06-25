/*******************************************************************************
 * Copyright (c) 2011 MadRobot.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *  Elton Kent - initial API and implementation
 ******************************************************************************/
package com.madrobot.db.orm;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * 
 */
class EntitiesMap {
	WeakHashMap<DatabaseClient, String> _map = new WeakHashMap<DatabaseClient, String>();
	private Map<String, WeakReference<DatabaseClient>> map = new HashMap<String, WeakReference<DatabaseClient>>(); 

	@SuppressWarnings("unchecked")
	<T extends DatabaseClient> T get(Class<T> c, long id) {
		String key = makeKey(c, id);
		WeakReference<DatabaseClient> i = map.get(key);
		if (i == null)
			return null;
		return (T) i.get();
	}

	@SuppressWarnings("unchecked")
	private String makeKey(Class entityType, long id) {
		StringBuilder sb = new StringBuilder();
		sb	.append(entityType.getName())
			.append(id);
		return sb.toString();
	}
	
	void set(DatabaseClient e) {
		String key = makeKey(e.getClass(), e.getID());
		map.put(key, new WeakReference<DatabaseClient>(e));
	}
}
