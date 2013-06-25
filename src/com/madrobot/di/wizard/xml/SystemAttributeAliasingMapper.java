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
 * Mapper that allows aliasing of system attribute names.
 * 
 */
class SystemAttributeAliasingMapper extends AbstractAttributeAliasingMapper {

	SystemAttributeAliasingMapper(Mapper wrapped) {
		super(wrapped);
	}

	@Override
	public String aliasForSystemAttribute(String attribute) {
		String alias = (String) nameToAlias.get(attribute);
		if (alias == null && !nameToAlias.containsKey(attribute)) {
			alias = super.aliasForSystemAttribute(attribute);
			if (alias == attribute) {
				alias = super.aliasForAttribute(attribute);
			}
		}
		return alias;
	}
}
