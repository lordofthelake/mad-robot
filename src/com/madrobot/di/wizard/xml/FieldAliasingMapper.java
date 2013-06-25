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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.madrobot.di.wizard.xml.core.FastField;

/**
 * Mapper that allows a field of a specific class to be replaced with a shorter alias, or omitted entirely.
 * 
 */
class FieldAliasingMapper extends MapperWrapper {

	protected final Map aliasToFieldMap = new HashMap();
	protected final Set fieldsToOmit = new HashSet();
	protected final Map fieldToAliasMap = new HashMap();

	FieldAliasingMapper(Mapper wrapped) {
		super(wrapped);
	}

	public void addFieldAlias(String alias, Class type, String fieldName) {
		fieldToAliasMap.put(key(type, fieldName), alias);
		aliasToFieldMap.put(key(type, alias), fieldName);
	}

	private String getMember(Class type, String name, Map map) {
		String member = null;
		for (Class declaringType = type; member == null && declaringType != Object.class; declaringType = declaringType
				.getSuperclass()) {
			member = (String) map.get(key(declaringType, name));
		}
		return member;
	}

	private Object key(Class type, String name) {
		return new FastField(type, name);
	}

	public void omitField(Class definedIn, String fieldName) {
		fieldsToOmit.add(key(definedIn, fieldName));
	}

	@Override
	public String realMember(Class type, String serialized) {
		String real = getMember(type, serialized, aliasToFieldMap);
		if (real == null) {
			return super.realMember(type, serialized);
		} else {
			return real;
		}
	}

	@Override
	public String serializedMember(Class type, String memberName) {
		String alias = getMember(type, memberName, fieldToAliasMap);
		if (alias == null) {
			return super.serializedMember(type, memberName);
		} else {
			return alias;
		}
	}

	@Override
	public boolean shouldSerializeMember(Class definedIn, String fieldName) {
		return !fieldsToOmit.contains(key(definedIn, fieldName));
	}
}
