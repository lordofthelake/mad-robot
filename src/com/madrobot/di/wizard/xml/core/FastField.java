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

public final class FastField {
	private final String declaringClass;
	private final String name;

	public FastField(Class definedIn, String name) {
		this(definedIn == null ? null : definedIn.getName(), name);
	}

	public FastField(String definedIn, String name) {
		this.name = name;
		this.declaringClass = definedIn;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (obj instanceof FastField) {
			final FastField field = (FastField) obj;
			if ((declaringClass == null && field.declaringClass != null)
					|| (declaringClass != null && field.declaringClass == null)) {
				return false;
			}
			return name.equals(field.getName())
					&& (declaringClass == null || declaringClass.equals(field.getDeclaringClass()));
		}
		return false;
	}

	public String getDeclaringClass() {
		return this.declaringClass;
	}

	public String getName() {
		return this.name;
	}

	@Override
	public int hashCode() {
		return name.hashCode() ^ (declaringClass == null ? 0 : declaringClass.hashCode());
	}

	@Override
	public String toString() {
		return (declaringClass == null ? "" : declaringClass + ".") + name;
	}
}
