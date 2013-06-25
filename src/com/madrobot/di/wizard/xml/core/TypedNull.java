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

package com.madrobot.di.wizard.xml.core;

/**
 * A placeholder for a <code>null</code> value of a specific type.
 * 
 * @since 1.2.2
 */
public class TypedNull {
	private final Class type;

	public TypedNull(Class type) {
		super();
		this.type = type;
	}

	public Class getType() {
		return this.type;
	}
}
