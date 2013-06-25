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

import com.madrobot.reflect.PrimitiveUtils;

/**
 * Mapper that detects arrays and changes the name so it can identified as an array (for example Foo[] gets serialized
 * as foo-array). Supports multi-dimensional arrays.
 * 
 */
class ArrayMapper extends MapperWrapper {

	ArrayMapper(Mapper wrapped) {
		super(wrapped);
	}

	private String arrayType(int dimensions, Class componentType) {
		StringBuffer className = new StringBuffer();
		for (int i = 0; i < dimensions; i++) {
			className.append('[');
		}
		if (componentType.isPrimitive()) {
			className.append(PrimitiveUtils.representingChar(componentType));
			return className.toString();
		} else {
			className.append('L').append(componentType.getName()).append(';');
			return className.toString();
		}
	}

	private String boxedTypeName(Class type) {
		return PrimitiveUtils.isBoxed(type) ? type.getName() : null;
	}

	@Override
	public Class realClass(String elementName) {
		int dimensions = 0;

		// strip off "-array" suffix
		while (elementName.endsWith("-array")) {
			elementName = elementName.substring(0, elementName.length() - 6); // cut
																				// off
																				// -array
			++dimensions;
		}

		if (dimensions > 0) {
			Class componentType = PrimitiveUtils.primitiveType(elementName);
			if (componentType == null) {
				componentType = super.realClass(elementName);
			}
			while (componentType.isArray()) {
				componentType = componentType.getComponentType();
				++dimensions;
			}
			return super.realClass(arrayType(dimensions, componentType));
		} else {
			return super.realClass(elementName);
		}
	}

	@Override
	public String serializedClass(Class type) {
		StringBuffer arraySuffix = new StringBuffer();
		String name = null;
		while (type.isArray()) {
			name = super.serializedClass(type);
			if (type.getName().equals(name)) {
				type = type.getComponentType();
				arraySuffix.append("-array");
				name = null;
			} else {
				break;
			}
		}
		if (name == null) {
			name = boxedTypeName(type);
		}
		if (name == null) {
			name = super.serializedClass(type);
		}
		if (arraySuffix.length() > 0) {
			return name + arraySuffix;
		} else {
			return name;
		}
	}
}
