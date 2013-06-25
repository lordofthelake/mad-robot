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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

/**
 * Mapper that allows a package name to be replaced with an alias.
 * 
 */
class PackageAliasingMapper extends MapperWrapper implements Serializable {

	private static final Comparator REVERSE = new Comparator() {

		@Override
		public int compare(final Object o1, final Object o2) {
			return ((String) o2).compareTo((String) o1);
		}
	};

	/**
	 * 
	 */
	private static final long serialVersionUID = 2304459264591819842L;

	protected transient Map nameToPackage = new HashMap();
	private Map packageToName = new TreeMap(REVERSE);

	PackageAliasingMapper(final Mapper wrapped) {
		super(wrapped);
	}

	void addPackageAlias(String name, String pkg) {
		if (name.length() > 0 && name.charAt(name.length() - 1) != '.') {
			name += '.';
		}
		if (pkg.length() > 0 && pkg.charAt(pkg.length() - 1) != '.') {
			pkg += '.';
		}
		nameToPackage.put(name, pkg);
		packageToName.put(pkg, name);
	}

	private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
		packageToName = new TreeMap(REVERSE);
		packageToName.putAll((Map) in.readObject());
		nameToPackage = new HashMap();
		for (final Iterator iter = packageToName.keySet().iterator(); iter.hasNext();) {
			final Object type = iter.next();
			nameToPackage.put(packageToName.get(type), type);
		}
	}

	@Override
	public Class realClass(String elementName) {
		int length = elementName.length();
		int dot = -1;
		do {
			dot = elementName.lastIndexOf('.', length);
			final String name = dot < 0 ? "" : elementName.substring(0, dot) + '.';
			final String packageName = (String) nameToPackage.get(name);

			if (packageName != null) {
				elementName = packageName + (dot < 0 ? elementName : elementName.substring(dot + 1));
				break;
			}
			length = dot - 1;
		} while (dot >= 0);

		return super.realClass(elementName);
	}

	@Override
	public String serializedClass(final Class type) {
		final String className = type.getName();
		int length = className.length();
		int dot = -1;
		do {
			dot = className.lastIndexOf('.', length);
			final String pkg = dot < 0 ? "" : className.substring(0, dot + 1);
			final String alias = (String) packageToName.get(pkg);
			if (alias != null) {
				return alias + (dot < 0 ? className : className.substring(dot + 1));
			}
			length = dot - 1;
		} while (dot >= 0);
		return super.serializedClass(type);
	}

	private void writeObject(final ObjectOutputStream out) throws IOException {
		out.writeObject(new HashMap(packageToName));
	}
}
