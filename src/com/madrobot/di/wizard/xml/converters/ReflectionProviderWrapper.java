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

import java.lang.reflect.Field;

/**
 * A wrapper implementation for the ReflectionProvider.
 * 
 * @since 1.2
 */
public class ReflectionProviderWrapper implements ReflectionProvider {

	final protected ReflectionProvider wrapped;

	public ReflectionProviderWrapper(ReflectionProvider wrapper) {
		this.wrapped = wrapper;
	}

	@Override
	public boolean fieldDefinedInClass(String fieldName, Class type) {
		return this.wrapped.fieldDefinedInClass(fieldName, type);
	}

	@Override
	public Field getField(Class definedIn, String fieldName) {
		return this.wrapped.getField(definedIn, fieldName);
	}

	@Override
	public Class getFieldType(Object object, String fieldName, Class definedIn) {
		return this.wrapped.getFieldType(object, fieldName, definedIn);
	}

	@Override
	public Object newInstance(Class type) {
		return this.wrapped.newInstance(type);
	}

	@Override
	public void visitSerializableFields(Object object, Visitor visitor) {
		this.wrapped.visitSerializableFields(object, visitor);
	}

	@Override
	public void writeField(Object object, String fieldName, Object value, Class definedIn) {
		this.wrapped.writeField(object, fieldName, value, definedIn);
	}

}
