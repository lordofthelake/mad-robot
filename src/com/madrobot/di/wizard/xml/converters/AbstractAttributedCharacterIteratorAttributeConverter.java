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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.text.AttributedCharacterIterator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * An abstract converter implementation for constants of {@link AttributedCharacterIterator.Attribute} and derived
 * types.
 * 
 * @author J&ouml;rg Schaible
 * @since 1.2.2
 */
public class AbstractAttributedCharacterIteratorAttributeConverter extends AbstractSingleValueConverter {

	private static final Method getName;
	static {
		try {
			getName = AttributedCharacterIterator.Attribute.class.getDeclaredMethod("getName", (Class[]) null);
		} catch (NoSuchMethodException e) {
			throw new ExceptionInInitializerError("Missing AttributedCharacterIterator.Attribute.getName()");
		}
	}

	private transient Map attributeMap;
	private transient FieldDictionary fieldDictionary;
	private final Class type;

	public AbstractAttributedCharacterIteratorAttributeConverter(final Class type) {
		super();
		this.type = type;
		readResolve();
	}

	@Override
	public boolean canConvert(final Class type) {
		return type == this.type;
	}

	@Override
	public Object fromString(final String str) {
		return attributeMap.get(str);
	}

	private Object readResolve() {
		fieldDictionary = new FieldDictionary();
		attributeMap = new HashMap();
		for (final Iterator iterator = fieldDictionary.fieldsFor(type); iterator.hasNext();) {
			final Field field = (Field) iterator.next();
			if (field.getType() == type && Modifier.isStatic(field.getModifiers())) {
				try {
					final Object attribute = field.get(null);
					attributeMap.put(toString(attribute), attribute);
				} catch (IllegalAccessException e) {
					throw new ObjectAccessException("Cannot get object of " + field, e);
				}
			}
		}
		return this;
	}

	@Override
	public String toString(final Object source) {
		AttributedCharacterIterator.Attribute attribute = (AttributedCharacterIterator.Attribute) source;
		try {
			if (!getName.isAccessible()) {
				getName.setAccessible(true);
			}
			return (String) getName.invoke(attribute, (Object[]) null);
		} catch (IllegalAccessException e) {
			throw new ObjectAccessException("Cannot get name of AttributedCharacterIterator.Attribute", e);
		} catch (InvocationTargetException e) {
			throw new ObjectAccessException("Cannot get name of AttributedCharacterIterator.Attribute",
					e.getTargetException());
		}
	}

}
