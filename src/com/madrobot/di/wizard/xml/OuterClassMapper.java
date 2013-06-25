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

/**
 * Mapper that uses a more meaningful alias for the field in an inner class (this$0) that refers to the outer class.
 * 
 */
class OuterClassMapper extends MapperWrapper {

	private final String alias;

	OuterClassMapper(Mapper wrapped) {
		this(wrapped, "outer-class");
	}

	OuterClassMapper(Mapper wrapped, String alias) {
		super(wrapped);
		this.alias = alias;
	}

	@Override
	public String realMember(Class type, String serialized) {
		if (serialized.equals(alias)) {
			return "this$0";
		} else {
			return super.realMember(type, serialized);
		}
	}

	@Override
	public String serializedMember(Class type, String memberName) {
		if (memberName.equals("this$0")) {
			return alias;
		} else {
			return super.serializedMember(type, memberName);
		}
	}
}
