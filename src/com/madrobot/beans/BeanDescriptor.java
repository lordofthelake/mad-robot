/*******************************************************************************
 * Copyright (c) 2011 MadRobot.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *  Elton Kent - initial API and implementation
 ******************************************************************************/
package com.madrobot.beans;

import java.lang.ref.Reference;

/**
 * A BeanDescriptor provides global information about a "bean",
 * including its Java class, its displayName, etc.
 * <p>
 * This is one of the kinds of descriptor returned by a BeanInfo object, which
 * also returns descriptors for properties, method, and events.
 */

public class BeanDescriptor extends FeatureDescriptor {

	private Reference beanClassRef;
	private Reference customizerClassRef;

	/*
	 * Package-private dup constructor
	 * This must isolate the new object from any changes to the old object.
	 */
	BeanDescriptor(BeanDescriptor old) {
		super(old);
		beanClassRef = old.beanClassRef;
		customizerClassRef = old.customizerClassRef;
	}

	/**
	 * Create a BeanDescriptor for a bean that doesn't have a customizer.
	 * 
	 * @param beanClass
	 *            The Class object of the Java class that implements
	 *            the bean. For example sun.beans.OurButton.class.
	 */
	public BeanDescriptor(Class<?> beanClass) {
		this(beanClass, null);
	}

	/**
	 * Create a BeanDescriptor for a bean that has a customizer.
	 * 
	 * @param beanClass
	 *            The Class object of the Java class that implements
	 *            the bean. For example sun.beans.OurButton.class.
	 * @param customizerClass
	 *            The Class object of the Java class that implements
	 *            the bean's Customizer. For example
	 *            sun.beans.OurButtonCustomizer.class.
	 */
	public BeanDescriptor(Class<?> beanClass, Class<?> customizerClass) {
		beanClassRef = createReference(beanClass);
		customizerClassRef = createReference(customizerClass);

		String name = beanClass.getName();
		while(name.indexOf('.') >= 0){
			name = name.substring(name.indexOf('.') + 1);
		}
		setName(name);
	}

	/**
	 * Gets the bean's Class object.
	 * 
	 * @return The Class object for the bean.
	 */
	public Class<?> getBeanClass() {
		return (Class) getObject(beanClassRef);
	}

	/**
	 * Gets the Class object for the bean's customizer.
	 * 
	 * @return The Class object for the bean's customizer. This may
	 *         be null if the bean doesn't have a customizer.
	 */
	public Class<?> getCustomizerClass() {
		return (Class) getObject(customizerClassRef);
	}
}
