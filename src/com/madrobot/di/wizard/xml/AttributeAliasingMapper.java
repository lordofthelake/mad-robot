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
 * Mapper that allows aliasing of attribute names.
 * 
 * @since 1.2
 */
class AttributeAliasingMapper extends AbstractAttributeAliasingMapper {

	AttributeAliasingMapper(Mapper wrapped) {
		super(wrapped);
	}

	@Override
	public String aliasForAttribute(String attribute) {
		String alias = (String) nameToAlias.get(attribute);
		return alias == null ? super.aliasForAttribute(attribute) : alias;
	}

	@Override
	public String attributeForAlias(String alias) {
		String name = (String) aliasToName.get(alias);
		return name == null ? super.attributeForAlias(alias) : name;
	}
}
