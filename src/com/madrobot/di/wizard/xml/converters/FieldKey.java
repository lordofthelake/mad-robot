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
 * A field key.
 * 
 */
public class FieldKey {
	final private Class declaringClass;
	final private int depth;
	final private String fieldName;
	final private int order;

	public FieldKey(String fieldName, Class declaringClass, int order) {
		if (fieldName == null || declaringClass == null) {
			throw new IllegalArgumentException("fieldName or declaringClass is null");
		}
		this.fieldName = fieldName;
		this.declaringClass = declaringClass;
		this.order = order;
		Class c = declaringClass;
		int i = 0;
		while (c.getSuperclass() != null) {
			i++;
			c = c.getSuperclass();
		}
		depth = i;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof FieldKey))
			return false;

		final FieldKey fieldKey = (FieldKey) o;

		if (!declaringClass.equals(fieldKey.declaringClass))
			return false;
		if (!fieldName.equals(fieldKey.fieldName))
			return false;

		return true;
	}

	public Class getDeclaringClass() {
		return this.declaringClass;
	}

	public int getDepth() {
		return this.depth;
	}

	public String getFieldName() {
		return this.fieldName;
	}

	public int getOrder() {
		return this.order;
	}

	@Override
	public int hashCode() {
		int result;
		result = fieldName.hashCode();
		result = 29 * result + declaringClass.hashCode();
		return result;
	}

	@Override
	public String toString() {
		return "FieldKey{" + "order=" + order + ", writer=" + depth + ", declaringClass=" + declaringClass
				+ ", fieldName='" + fieldName + "'" + "}";
	}

}
