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

public interface Mapper {
	interface ImplicitCollectionMapping {
		String getFieldName();

		String getItemFieldName();

		Class getItemType();

		String getKeyFieldName();
	}

	/**
	 * Place holder type used for null values.
	 */
	class Null {
	}

	/**
	 * Returns an alias for a single field defined in an specific type.
	 * 
	 * @param definedIn
	 *            the type where the field was defined
	 * @param fieldName
	 *            the field name
	 * @return the alias for this field or its own name if no alias was defined
	 * @since 1.2.2
	 * @deprecated As of 1.3, use combination of {@link #serializedMember(Class, String)} and
	 *             {@link #getConverterFromItemType(String, Class, Class)}
	 */
	@Deprecated
	String aliasForAttribute(Class definedIn, String fieldName);

	/**
	 * Get the alias for an attribute's name.
	 * 
	 * @param attribute
	 *            the attribute
	 * @return the alias
	 * @since 1.2
	 */
	String aliasForAttribute(String attribute);

	/**
	 * Get the alias for a system attribute's name.
	 * 
	 * @param attribute
	 *            the system attribute
	 * @return the alias
	 * @since 1.3.1
	 */
	String aliasForSystemAttribute(String attribute);

	/**
	 * Returns the field name for an aliased attribute.
	 * 
	 * @param definedIn
	 *            the type where the field was defined
	 * @param alias
	 *            the alias
	 * @return the original attribute name
	 * @since 1.2.2
	 * @deprecated As of 1.3, use combination of {@link #realMember(Class, String)} and
	 *             {@link #getConverterFromItemType(String, Class, Class)}
	 */
	@Deprecated
	String attributeForAlias(Class definedIn, String alias);

	/**
	 * Get the attribute's name for an alias.
	 * 
	 * @param alias
	 *            the alias
	 * @return the attribute's name
	 * @since 1.2
	 */
	String attributeForAlias(String alias);

	Class defaultImplementationOf(Class type);

	/**
	 * Returns which converter to use for an specific attribute in a type.
	 * 
	 * @param definedIn
	 *            the field's parent
	 * @param attribute
	 *            the attribute name
	 * @deprecated As of 1.3.1, use {@link #getConverterFromAttribute(Class, String, Class)}
	 */
	@Deprecated
	SingleValueConverter getConverterFromAttribute(Class definedIn, String attribute);

	/**
	 * Returns which converter to use for an specific attribute in a type.
	 * 
	 * @param definedIn
	 *            the field's parent
	 * @param attribute
	 *            the attribute name
	 * @param type
	 *            the type the converter should create
	 * @since 1.3.1
	 */
	SingleValueConverter getConverterFromAttribute(Class definedIn, String attribute, Class type);

	/**
	 * @deprecated As of 1.3, use {@link #getConverterFromAttribute(Class, String, Class)}
	 */
	@Deprecated
	SingleValueConverter getConverterFromAttribute(String name);

	/**
	 * @deprecated As of 1.3, use {@link #getConverterFromItemType(String, Class, Class)}
	 */
	@Deprecated
	SingleValueConverter getConverterFromItemType(Class type);

	/**
	 * @deprecated As of 1.3, use {@link #getConverterFromItemType(String, Class, Class)}
	 */
	@Deprecated
	SingleValueConverter getConverterFromItemType(String fieldName, Class type);

	/**
	 * Returns a single value converter to be used in a specific field.
	 * 
	 * @param fieldName
	 *            the field name
	 * @param type
	 *            the field type
	 * @param definedIn
	 *            the type which defines this field
	 * @return a SingleValueConverter or null if there no such converter should be used for this field.
	 * @since 1.2.2
	 */
	SingleValueConverter getConverterFromItemType(String fieldName, Class type, Class definedIn);

	/**
	 * Get the name of the field that acts as the default collection for an object, or return null if there is none.
	 * 
	 * @param definedIn
	 *            owning type
	 * @param itemType
	 *            item type
	 * @param itemFieldName
	 *            optional item element name
	 */
	String getFieldNameForItemTypeAndName(Class definedIn, Class itemType, String itemFieldName);

	ImplicitCollectionMapping getImplicitCollectionDefForFieldName(Class itemType, String fieldName);

	Class getItemTypeForItemFieldName(Class definedIn, String itemFieldName);

	Converter getLocalConverter(Class definedIn, String fieldName);

	/**
	 * Whether this type is a simple immutable value (int, boolean, String, URL, etc. Immutable types will be repeatedly
	 * written in the serialized stream, instead of using object references.
	 */
	boolean isImmutableValueType(Class type);

	Mapper lookupMapperOfType(Class type);

	/**
	 * How a serialized class representation should be mapped back to a real class.
	 */
	Class realClass(String elementName);

	/**
	 * How a serialized member representation should be mapped back to a real member.
	 */
	String realMember(Class type, String serialized);

	/**
	 * How a class name should be represented in its serialized form.
	 */
	String serializedClass(Class type);

	/**
	 * How a class member should be represented in its serialized form.
	 */
	String serializedMember(Class type, String memberName);

	/**
	 * Determine whether a specific member should be serialized.
	 * 
	 * @since 1.1.3
	 */
	boolean shouldSerializeMember(Class definedIn, String fieldName);
}
