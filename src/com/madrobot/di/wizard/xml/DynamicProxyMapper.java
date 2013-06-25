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

import java.lang.reflect.Proxy;

/**
 * Mapper for handling special cases of aliasing dynamic proxies. The alias property specifies the name an instance of a
 * dynamic proxy should be serialized with.
 * 
 */
public class DynamicProxyMapper extends MapperWrapper {

	/**
	 * Place holder type used for dynamic proxies.
	 */
	public static class DynamicProxy {
	}

	private String alias;

	DynamicProxyMapper(Mapper wrapped) {
		this(wrapped, "dynamic-proxy");
	}

	DynamicProxyMapper(Mapper wrapped, String alias) {
		super(wrapped);
		this.alias = alias;
	}

	public String getAlias() {
		return alias;
	}

	@Override
	public Class realClass(String elementName) {
		if (elementName.equals(alias)) {
			return DynamicProxy.class;
		} else {
			return super.realClass(elementName);
		}
	}

	@Override
	public String serializedClass(Class type) {
		if (Proxy.isProxyClass(type)) {
			return alias;
		} else {
			return super.serializedClass(type);
		}
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

}
