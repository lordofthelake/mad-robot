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

import com.madrobot.di.wizard.xml.converters.Converter;
import com.madrobot.di.wizard.xml.converters.SingleValueConverter;

/**
 * Default mapper implementation with 'vanilla' functionality. To build up the functionality required, wrap this mapper
 * with other mapper implementations.
 * 
 */
class DefaultMapper implements Mapper {

	private static String XMLWIZARD_PACKAGE_ROOT;
	static {
		String packageName = DefaultMapper.class.getName();
		int idx = packageName.indexOf(".xml.");
		XMLWIZARD_PACKAGE_ROOT = idx > 0 ? packageName.substring(0, idx + 9) : null;
	}

	private final ClassLoader classLoader;

	DefaultMapper(ClassLoader classLoader) {
		this.classLoader = classLoader;
	}

	/**
	 * @deprecated As of 1.3, use combination of {@link #serializedMember(Class, String)} and
	 *             {@link #getConverterFromItemType(String, Class, Class)}
	 */
	@Deprecated
	@Override
	public String aliasForAttribute(Class definedIn, String fieldName) {
		return fieldName;
	}

	@Override
	public String aliasForAttribute(String attribute) {
		return attribute;
	}

	@Override
	public String aliasForSystemAttribute(String attribute) {
		return attribute;
	}

	/**
	 * @deprecated As of 1.3, use combination of {@link #realMember(Class, String)} and
	 *             {@link #getConverterFromItemType(String, Class, Class)}
	 */
	@Deprecated
	@Override
	public String attributeForAlias(Class definedIn, String alias) {
		return alias;
	}

	@Override
	public String attributeForAlias(String alias) {
		return alias;
	}

	@Override
	public Class defaultImplementationOf(Class type) {
		return type;
	}

	/**
	 * @deprecated As of 1.3.1, use {@link #getConverterFromAttribute(Class, String, Class)}
	 */
	@Deprecated
	@Override
	public SingleValueConverter getConverterFromAttribute(Class definedIn, String attribute) {
		return null;
	}

	@Override
	public SingleValueConverter getConverterFromAttribute(Class definedIn, String attribute, Class type) {
		return null;
	}

	/**
	 * @deprecated As of 1.3, use {@link #getConverterFromAttribute(Class, String, Class)}
	 */
	@Deprecated
	@Override
	public SingleValueConverter getConverterFromAttribute(String name) {
		return null;
	}

	/**
	 * @deprecated As of 1.3, use {@link #getConverterFromItemType(String, Class, Class)}
	 */
	@Deprecated
	@Override
	public SingleValueConverter getConverterFromItemType(Class type) {
		return null;
	}

	/**
	 * @deprecated As of 1.3, use {@link #getConverterFromItemType(String, Class, Class)}
	 */
	@Deprecated
	@Override
	public SingleValueConverter getConverterFromItemType(String fieldName, Class type) {
		return null;
	}

	@Override
	public SingleValueConverter getConverterFromItemType(String fieldName, Class type, Class definedIn) {
		return null;
	}

	@Override
	public String getFieldNameForItemTypeAndName(Class definedIn, Class itemType, String itemFieldName) {
		return null;
	}

	@Override
	public ImplicitCollectionMapping getImplicitCollectionDefForFieldName(Class itemType, String fieldName) {
		return null;
	}

	@Override
	public Class getItemTypeForItemFieldName(Class definedIn, String itemFieldName) {
		return null;
	}

	@Override
	public Converter getLocalConverter(Class definedIn, String fieldName) {
		return null;
	}

	@Override
	public boolean isImmutableValueType(Class type) {
		return false;
	}

	@Override
	public Mapper lookupMapperOfType(Class type) {
		return null;
	}

	public String lookupName(Class type) {
		return serializedClass(type);
	}

	public Class lookupType(String elementName) {
		return realClass(elementName);
	}

	@Override
	public Class realClass(String elementName) {
		try {
			if (elementName.startsWith(XMLWIZARD_PACKAGE_ROOT)) {
				return DefaultMapper.class.getClassLoader().loadClass(elementName);
			} else if (elementName.charAt(0) != '[') {
				return classLoader.loadClass(elementName);
			} else if (elementName.endsWith(";")) {
				return Class.forName(elementName.toString(), false, classLoader);
			} else {
				return Class.forName(elementName.toString());
			}
		} catch (ClassNotFoundException e) {
			throw new CannotResolveClassException(elementName);
		}
	}

	@Override
	public String realMember(Class type, String serialized) {
		return serialized;
	}

	@Override
	public String serializedClass(Class type) {
		return type.getName();
	}

	@Override
	public String serializedMember(Class type, String memberName) {
		return memberName;
	}

	@Override
	public boolean shouldSerializeMember(Class definedIn, String fieldName) {
		return true;
	}
}
