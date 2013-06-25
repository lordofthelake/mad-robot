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

import com.madrobot.reflect.CompositeClassLoader;

/**
 * ClassLoader that refers to another ClassLoader, allowing a single instance to be passed around the codebase that can
 * later have its destination changed.
 * 
 * @since 1.1.1
 */
public class ClassLoaderReference extends ClassLoader {

	static class Replacement {

		private Object readResolve() {
			return new ClassLoaderReference(new CompositeClassLoader());
		}

	}

	private transient ClassLoader reference;

	public ClassLoaderReference(ClassLoader reference) {
		this.reference = reference;
	}

	public ClassLoader getReference() {
		return reference;
	}

	@Override
	public Class loadClass(String name) throws ClassNotFoundException {
		return reference.loadClass(name);
	}

	public void setReference(ClassLoader reference) {
		this.reference = reference;
	}

	private Object writeReplace() {
		return new Replacement();
	};
}
