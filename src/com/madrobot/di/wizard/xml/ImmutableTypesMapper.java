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

import java.util.HashSet;
import java.util.Set;

/**
 * Mapper that specifies which types are basic immutable types. Types that are marked as immutable will be written
 * multiple times in the serialization stream without using references.
 * 
 */
class ImmutableTypesMapper extends MapperWrapper {

	private final Set immutableTypes = new HashSet();

	ImmutableTypesMapper(Mapper wrapped) {
		super(wrapped);
	}

	public void addImmutableType(Class type) {
		immutableTypes.add(type);
	}

	@Override
	public boolean isImmutableValueType(Class type) {
		if (immutableTypes.contains(type)) {
			return true;
		} else {
			return super.isImmutableValueType(type);
		}
	}

}
