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

abstract class MapperWrapper implements Mapper {

	private final Mapper wrapped;

	public MapperWrapper(Mapper wrapped) {
		this.wrapped = wrapped;
	}

	/**
	 * @deprecated As of 1.3, use combination of {@link #serializedMember(Class, String)} and
	 *             {@link #getConverterFromItemType(String, Class, Class)}
	 */
	@Deprecated
	@Override
	public String aliasForAttribute(Class definedIn, String fieldName) {
		return wrapped.aliasForAttribute(definedIn, fieldName);
	}

	@Override
	public String aliasForAttribute(String attribute) {
		return wrapped.aliasForAttribute(attribute);
	}

	@Override
	public String aliasForSystemAttribute(String attribute) {
		return wrapped.aliasForSystemAttribute(attribute);
	}

	/**
	 * @deprecated As of 1.3, use combination of {@link #realMember(Class, String)} and
	 *             {@link #getConverterFromItemType(String, Class, Class)}
	 */
	@Deprecated
	@Override
	public String attributeForAlias(Class definedIn, String alias) {
		return wrapped.attributeForAlias(definedIn, alias);
	}

	@Override
	public String attributeForAlias(String alias) {
		return wrapped.attributeForAlias(alias);
	}

	@Override
	public Class defaultImplementationOf(Class type) {
		return wrapped.defaultImplementationOf(type);
	}

	/**
	 * @deprecated As of 1.3.1, use {@link #getConverterFromAttribute(Class, String, Class)}
	 */
	@Deprecated
	@Override
	public SingleValueConverter getConverterFromAttribute(Class type, String attribute) {
		return wrapped.getConverterFromAttribute(type, attribute);
	}

	@Override
	public SingleValueConverter getConverterFromAttribute(Class definedIn, String attribute, Class type) {
		return wrapped.getConverterFromAttribute(definedIn, attribute, type);
	}

	/**
	 * @deprecated As of 1.3, use {@link #getConverterFromAttribute(Class, String, Class)}
	 */
	@Deprecated
	@Override
	public SingleValueConverter getConverterFromAttribute(String name) {
		return wrapped.getConverterFromAttribute(name);
	}

	/**
	 * @deprecated As of 1.3, use {@link #getConverterFromItemType(String, Class, Class)}
	 */
	@Deprecated
	@Override
	public SingleValueConverter getConverterFromItemType(Class type) {
		return wrapped.getConverterFromItemType(type);
	}

	/**
	 * @deprecated As of 1.3, use {@link #getConverterFromItemType(String, Class, Class)}
	 */
	@Deprecated
	@Override
	public SingleValueConverter getConverterFromItemType(String fieldName, Class type) {
		return wrapped.getConverterFromItemType(fieldName, type);
	}

	@Override
	public SingleValueConverter getConverterFromItemType(String fieldName, Class type, Class definedIn) {
		return wrapped.getConverterFromItemType(fieldName, type, definedIn);
	}

	@Override
	public String getFieldNameForItemTypeAndName(Class definedIn, Class itemType, String itemFieldName) {
		return wrapped.getFieldNameForItemTypeAndName(definedIn, itemType, itemFieldName);
	}

	@Override
	public ImplicitCollectionMapping getImplicitCollectionDefForFieldName(Class itemType, String fieldName) {
		return wrapped.getImplicitCollectionDefForFieldName(itemType, fieldName);
	}

	@Override
	public Class getItemTypeForItemFieldName(Class definedIn, String itemFieldName) {
		return wrapped.getItemTypeForItemFieldName(definedIn, itemFieldName);
	}

	@Override
	public Converter getLocalConverter(Class definedIn, String fieldName) {
		return wrapped.getLocalConverter(definedIn, fieldName);
	}

	@Override
	public boolean isImmutableValueType(Class type) {
		return wrapped.isImmutableValueType(type);
	}

	@Override
	public Mapper lookupMapperOfType(Class type) {
		return type.isAssignableFrom(getClass()) ? this : wrapped.lookupMapperOfType(type);
	}

	@Override
	public Class realClass(String elementName) {
		return wrapped.realClass(elementName);
	}

	@Override
	public String realMember(Class type, String serialized) {
		return wrapped.realMember(type, serialized);
	}

	@Override
	public String serializedClass(Class type) {
		return wrapped.serializedClass(type);
	}

	@Override
	public String serializedMember(Class type, String memberName) {
		return wrapped.serializedMember(type, memberName);
	}

	@Override
	public boolean shouldSerializeMember(Class definedIn, String fieldName) {
		return wrapped.shouldSerializeMember(definedIn, fieldName);
	}

}
