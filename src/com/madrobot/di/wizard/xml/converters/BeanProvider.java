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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import com.madrobot.beans.PropertyDescriptor;

public class BeanProvider implements JavaBeanProvider {

	protected static final Object[] NO_PARAMS = new Object[0];
	protected PropertyDictionary propertyDictionary;

	/**
	 * Construct a BeanProvider that will process the bean properties in their natural order.
	 */
	public BeanProvider() {
		this(new PropertyDictionary(new NativePropertySorter()));
	}

	/**
	 * Construct a BeanProvider with a comparator to sort the bean properties by name in the dictionary.
	 * 
	 * @param propertyNameComparator
	 *            the comparator
	 */
	public BeanProvider(final Comparator propertyNameComparator) {
		this(new PropertyDictionary(new ComparingPropertySorter(propertyNameComparator)));
	}

	/**
	 * Construct a BeanProvider with a provided property dictionary.
	 * 
	 * @param propertyDictionary
	 *            the property dictionary to use
	 * @since 1.4
	 */
	public BeanProvider(final PropertyDictionary propertyDictionary) {
		this.propertyDictionary = propertyDictionary;
	}

	/**
	 * Returns true if the Bean provider can instantiate the specified class
	 */
	@Override
	public boolean canInstantiate(Class type) {
		return getDefaultConstrutor(type) != null;
	}

	protected boolean canStreamProperty(PropertyDescriptor descriptor) {
		return descriptor.getReadMethod() != null && descriptor.getWriteMethod() != null;
	}

	/**
	 * Returns the default constructor, or null if none is found
	 * 
	 * @param type
	 */
	protected Constructor getDefaultConstrutor(Class type) {
		Constructor[] constructors = type.getConstructors();
		for (int i = 0; i < constructors.length; i++) {
			Constructor c = constructors[i];
			if (c.getParameterTypes().length == 0 && Modifier.isPublic(c.getModifiers()))
				return c;
		}
		return null;
	}

	protected PropertyDescriptor getProperty(String name, Class type) {
		return propertyDictionary.propertyDescriptor(type, name);
	}

	@Override
	public Class getPropertyType(Object object, String name) {
		return getProperty(name, object.getClass()).getPropertyType();
	}

	protected PropertyDescriptor[] getSerializableProperties(Object object) {
		List result = new ArrayList();
		for (final Iterator iter = propertyDictionary.propertiesFor(object.getClass()); iter.hasNext();) {
			final PropertyDescriptor descriptor = (PropertyDescriptor) iter.next();
			if (canStreamProperty(descriptor)) {
				result.add(descriptor);
			}
		}
		return (PropertyDescriptor[]) result.toArray(new PropertyDescriptor[result.size()]);
	}

	@Override
	public Object newInstance(Class type) {
		try {
			return getDefaultConstrutor(type).newInstance(NO_PARAMS);
		} catch (InstantiationException e) {
			throw new ObjectAccessException("Cannot construct " + type.getName(), e);
		} catch (IllegalAccessException e) {
			throw new ObjectAccessException("Cannot construct " + type.getName(), e);
		} catch (InvocationTargetException e) {
			if (e.getTargetException() instanceof RuntimeException) {
				throw (RuntimeException) e.getTargetException();
			} else if (e.getTargetException() instanceof Error) {
				throw (Error) e.getTargetException();
			} else {
				throw new ObjectAccessException("Constructor for " + type.getName() + " threw an exception", e);
			}
		}
	}

	@Override
	public boolean propertyDefinedInClass(String name, Class type) {
		return getProperty(name, type) != null;
	}

	public boolean propertyWriteable(String name, Class type) {
		PropertyDescriptor property = getProperty(name, type);
		return property.getWriteMethod() != null;
	}

	@Override
	public void visitSerializableProperties(Object object, JavaBeanProvider.Visitor visitor) {
		PropertyDescriptor[] propertyDescriptors = getSerializableProperties(object);
		for (int i = 0; i < propertyDescriptors.length; i++) {
			PropertyDescriptor property = propertyDescriptors[i];
			try {
				Method readMethod = property.getReadMethod();
				String name = property.getName();
				Class definedIn = readMethod.getDeclaringClass();
				if (visitor.shouldVisit(name, definedIn)) {
					Object value = readMethod.invoke(object, new Object[0]);
					visitor.visit(name, property.getPropertyType(), definedIn, value);
				}
			} catch (IllegalArgumentException e) {
				throw new ObjectAccessException("Could not get property " + object.getClass() + "."
						+ property.getName(), e);
			} catch (IllegalAccessException e) {
				throw new ObjectAccessException("Could not get property " + object.getClass() + "."
						+ property.getName(), e);
			} catch (InvocationTargetException e) {
				throw new ObjectAccessException("Could not get property " + object.getClass() + "."
						+ property.getName(), e);
			}
		}
	}

	@Override
	public void writeProperty(Object object, String propertyName, Object value) {
		PropertyDescriptor property = getProperty(propertyName, object.getClass());
		try {
			property.getWriteMethod().invoke(object, new Object[] { value });
		} catch (IllegalArgumentException e) {
			throw new ObjectAccessException("Could not set property " + object.getClass() + "." + property.getName(), e);
		} catch (IllegalAccessException e) {
			throw new ObjectAccessException("Could not set property " + object.getClass() + "." + property.getName(), e);
		} catch (InvocationTargetException e) {
			throw new ObjectAccessException("Could not set property " + object.getClass() + "." + property.getName(), e);
		}
	}

	// /**
	// * @deprecated As of 1.4 use {@link JavaBeanProvider.Visitor}
	// */
	// public interface Visitor extends JavaBeanProvider.Visitor {
	// }
}
