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

package com.madrobot.di.wizard.xml.converters;

import java.util.Map;

/**
 * A sorter that keeps the natural order of the bean properties as they are returned by the JavaBean introspection.
 * 
 * @author J&ouml;rg Schaible
 * @since 1.4
 */
public class NativePropertySorter implements PropertySorter {

	@Override
	public Map sort(final Class type, final Map nameMap) {
		return nameMap;
	}

}
