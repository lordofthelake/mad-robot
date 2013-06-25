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

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.madrobot.di.wizard.xml.core.Caching;

/**
 * Mapper that caches which names map to which classes. Prevents repetitive searching and class loading.
 * 
 */
class CachingMapper extends MapperWrapper implements Caching {

	private transient Map realClassCache;

	CachingMapper(Mapper wrapped) {
		super(wrapped);
		readResolve();
	}

	@Override
	public void flushCache() {
		realClassCache.clear();
	}

	private Object readResolve() {
		realClassCache = Collections.synchronizedMap(new HashMap(128));
		return this;
	}

	@Override
	public Class realClass(String elementName) {
		WeakReference reference = (WeakReference) realClassCache.get(elementName);
		if (reference != null) {
			Class cached = (Class) reference.get();
			if (cached != null) {
				return cached;
			}
		}

		Class result = super.realClass(elementName);
		realClassCache.put(elementName, new WeakReference(result));
		return result;
	}
}
