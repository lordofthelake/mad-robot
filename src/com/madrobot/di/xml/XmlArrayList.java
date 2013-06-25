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

import java.util.AbstractList;

import com.madrobot.di.wizard.xml.core.PersistenceStrategy;

/**
 * A persistent list implementation backed on a XmlMap.
 * 
 * @author Guilherme Silveira
 */
public class XmlArrayList extends AbstractList {

	private final XmlMap map;

	public XmlArrayList(PersistenceStrategy persistenceStrategy) {
		this.map = new XmlMap(persistenceStrategy);
	}

	@Override
	public void add(int index, Object element) {
		int size = size();
		if (index >= (size + 1) || index < 0) {
			throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
		}
		int to = index != size ? index - 1 : index;
		for (int i = size; i > to; i--) {
			map.put(new Integer(i + 1), map.get(new Integer(i)));
		}
		map.put(new Integer(index), element);
	}

	@Override
	public Object get(int index) {
		rangeCheck(index);
		return map.get(new Integer(index));
	}

	private void rangeCheck(int index) {
		int size = size();
		if (index >= size || index < 0) {
			throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
		}
	}

	@Override
	public Object remove(int index) {
		int size = size();
		rangeCheck(index);
		Object value = map.get(new Integer(index));
		for (int i = index; i < size - 1; i++) {
			map.put(new Integer(i), map.get(new Integer(i + 1)));
		}
		map.remove(new Integer(size - 1));
		return value;
	}

	@Override
	public Object set(int index, Object element) {
		rangeCheck(index);
		Object value = get(index);
		map.put(new Integer(index), element);
		return value;
	}

	@Override
	public int size() {
		return map.size();
	}

}
