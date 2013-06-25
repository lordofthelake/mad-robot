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
import java.util.Iterator;
import java.util.Map;

/**
 * Abstract base class for AttributeAliassingMapper and its system version.
 * 
 * @since 1.3.1
 */
abstract class AbstractAttributeAliasingMapper extends MapperWrapper {

	protected final Map aliasToName = new HashMap();
	protected transient Map nameToAlias = new HashMap();

	AbstractAttributeAliasingMapper(Mapper wrapped) {
		super(wrapped);
	}

	public void addAliasFor(final String attributeName, final String alias) {
		aliasToName.put(alias, attributeName);
		nameToAlias.put(attributeName, alias);
	}

	private Object readResolve() {
		nameToAlias = new HashMap();
		for (final Iterator iter = aliasToName.keySet().iterator(); iter.hasNext();) {
			final Object alias = iter.next();
			nameToAlias.put(aliasToName.get(alias), alias);
		}
		return this;
	}

}
