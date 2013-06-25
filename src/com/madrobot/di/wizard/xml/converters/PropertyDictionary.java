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

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.madrobot.beans.BeanInfo;
import com.madrobot.beans.IntrospectionException;
import com.madrobot.beans.Introspector;
import com.madrobot.beans.PropertyDescriptor;
import com.madrobot.di.wizard.xml.core.Caching;
import com.madrobot.util.OrderRetainingMap;

/**
 * Builds the properties maps for each bean and caches them.
 * 
 */
public class PropertyDictionary implements Caching {
	private transient Map propertyNameCache = Collections.synchronizedMap(new HashMap());
	private final PropertySorter sorter;

	public PropertyDictionary() {
		this(new NativePropertySorter());
	}

	public PropertyDictionary(PropertySorter sorter) {
		this.sorter = sorter;
	}

	// /**
	// * @deprecated As of 1.3.1, use {@link #propertiesFor(Class)} instead
	// */
	// public Iterator serializablePropertiesFor(Class type) {
	// Collection beanProperties = new ArrayList();
	// Collection descriptors = buildMap(type).values();
	// for (Iterator iter = descriptors.iterator(); iter.hasNext();) {
	// PropertyDescriptor descriptor = (PropertyDescriptor)iter.next();
	// if (descriptor.getReadMethod() != null && descriptor.getWriteMethod() != null) {
	// beanProperties.add(new BeanProperty(type, descriptor.getName(), descriptor
	// .getPropertyType()));
	// }
	// }
	// return beanProperties.iterator();
	// }

	// /**
	// * Locates a serializable property.
	// *
	// * @param cls
	// * @param name
	// * @deprecated As of 1.3.1, use {@link #propertyDescriptor(Class, String)} instead
	// */
	// public BeanProperty property(Class cls, String name) {
	// BeanProperty beanProperty = null;
	// PropertyDescriptor descriptor = (PropertyDescriptor)buildMap(cls).get(name);
	// if (descriptor == null) {
	// throw new MissingFieldException(cls.getName(), name);
	// }
	// if (descriptor.getReadMethod() != null && descriptor.getWriteMethod() != null) {
	// beanProperty = new BeanProperty(
	// cls, descriptor.getName(), descriptor.getPropertyType());
	// }
	// return beanProperty;
	// }

	private Map buildMap(Class type) {
		Map nameMap = (Map) propertyNameCache.get(type);
		if (nameMap == null) {
			BeanInfo beanInfo;
			try {
				beanInfo = Introspector.getBeanInfo(type, Object.class);
			} catch (IntrospectionException e) {
				throw new ObjectAccessException("Cannot get BeanInfo of type " + type.getName(), e);
			}
			nameMap = new OrderRetainingMap();
			PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
			for (int i = 0; i < propertyDescriptors.length; i++) {
				PropertyDescriptor descriptor = propertyDescriptors[i];
				nameMap.put(descriptor.getName(), descriptor);
			}
			nameMap = sorter.sort(type, nameMap);
			propertyNameCache.put(type, nameMap);
		}
		return nameMap;
	}

	@Override
	public void flushCache() {
		propertyNameCache.clear();
	}

	public Iterator propertiesFor(Class type) {
		return buildMap(type).values().iterator();
	}

	/**
	 * Locates a property descriptor.
	 * 
	 * @param type
	 * @param name
	 */
	public PropertyDescriptor propertyDescriptor(Class type, String name) {
		PropertyDescriptor descriptor = (PropertyDescriptor) buildMap(type).get(name);
		if (descriptor == null) {
			throw new MissingFieldException(type.getName(), name);
		}
		return descriptor;
	}
}
