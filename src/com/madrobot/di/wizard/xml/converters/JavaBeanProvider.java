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

/**
 * @author J&ouml;rg Schaible
 * 
 * @since 1.4
 */
public interface JavaBeanProvider {

	public interface Visitor {
		boolean shouldVisit(String name, Class definedIn);

		void visit(String name, Class type, Class definedIn, Object value);
	}

	/**
	 * Returns true if the Bean provider can instantiate the specified class
	 */
	boolean canInstantiate(Class type);

	Class getPropertyType(Object object, String name);

	Object newInstance(Class type);

	boolean propertyDefinedInClass(String name, Class type);

	void visitSerializableProperties(Object object, Visitor visitor);

	void writeProperty(Object object, String propertyName, Object value);

}
