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

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.madrobot.reflect.PrimitiveUtils;

class ImplicitCollectionMapper extends MapperWrapper {

	private static class ImplicitCollectionMapperForClass {
		// { fieldName (String) -> (ImplicitCollectionDefImpl) }
		private Map fieldNameToDef = new HashMap();
		// { itemFieldName (String) -> (ImplicitCollectionDefImpl) }
		private Map itemFieldNameToDef = new HashMap();
		// { (NamedItemType) -> (ImplicitCollectionDefImpl) }
		private Map namedItemTypeToDef = new HashMap();

		public void add(ImplicitCollectionMappingImpl def) {
			fieldNameToDef.put(def.getFieldName(), def);
			namedItemTypeToDef.put(def.createNamedItemType(), def);
			if (def.getItemFieldName() != null) {
				itemFieldNameToDef.put(def.getItemFieldName(), def);
			}
		}

		public String getFieldNameForItemTypeAndName(Class itemType, String itemFieldName) {
			ImplicitCollectionMappingImpl unnamed = null;
			for (Iterator iterator = namedItemTypeToDef.keySet().iterator(); iterator.hasNext();) {
				NamedItemType itemTypeForFieldName = (NamedItemType) iterator.next();
				ImplicitCollectionMappingImpl def = (ImplicitCollectionMappingImpl) namedItemTypeToDef
						.get(itemTypeForFieldName);
				if (itemType == Mapper.Null.class) {
					unnamed = def;
					break;
				} else if (itemTypeForFieldName.itemType.isAssignableFrom(itemType)) {
					if (def.getItemFieldName() != null) {
						if (def.getItemFieldName().equals(itemFieldName)) {
							return def.getFieldName();
						}
					} else {
						unnamed = def;
						if (itemFieldName == null) {
							break;
						}
					}
				}
			}
			return unnamed != null ? unnamed.getFieldName() : null;
		}

		private ImplicitCollectionMappingImpl getImplicitCollectionDefByItemFieldName(String itemFieldName) {
			if (itemFieldName == null) {
				return null;
			} else {
				return (ImplicitCollectionMappingImpl) itemFieldNameToDef.get(itemFieldName);
			}
		}

		public ImplicitCollectionMapping getImplicitCollectionDefForFieldName(String fieldName) {
			return (ImplicitCollectionMapping) fieldNameToDef.get(fieldName);
		}

		public Class getItemTypeForItemFieldName(String itemFieldName) {
			ImplicitCollectionMappingImpl def = getImplicitCollectionDefByItemFieldName(itemFieldName);
			if (def != null) {
				return def.getItemType();
			} else {
				return null;
			}
		}

	}

	private static class ImplicitCollectionMappingImpl implements ImplicitCollectionMapping {
		private static boolean isEquals(Object a, Object b) {
			if (a == null) {
				return b == null;
			} else {
				return a.equals(b);
			}
		}
		private final String fieldName;
		private final String itemFieldName;
		private final Class itemType;

		private final String keyFieldName;

		ImplicitCollectionMappingImpl(String fieldName, Class itemType, String itemFieldName, String keyFieldName) {
			this.fieldName = fieldName;
			this.itemFieldName = itemFieldName;
			this.itemType = itemType;
			this.keyFieldName = keyFieldName;
		}

		public NamedItemType createNamedItemType() {
			return new NamedItemType(itemType, itemFieldName);
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof ImplicitCollectionMappingImpl) {
				ImplicitCollectionMappingImpl b = (ImplicitCollectionMappingImpl) obj;
				return fieldName.equals(b.fieldName) && isEquals(itemFieldName, b.itemFieldName);
			} else {
				return false;
			}
		}

		@Override
		public String getFieldName() {
			return fieldName;
		}

		@Override
		public String getItemFieldName() {
			return itemFieldName;
		}

		@Override
		public Class getItemType() {
			return itemType;
		}

		@Override
		public String getKeyFieldName() {
			return keyFieldName;
		}

		@Override
		public int hashCode() {
			int hash = fieldName.hashCode();
			if (itemFieldName != null) {
				hash += itemFieldName.hashCode() << 7;
			}
			return hash;
		}
	}

	private static class NamedItemType {
		private static boolean isEquals(Object a, Object b) {
			if (a == null) {
				return b == null;
			} else {
				return a.equals(b);
			}
		}
		String itemFieldName;

		Class itemType;

		NamedItemType(Class itemType, String itemFieldName) {
			this.itemType = itemType == null ? Object.class : itemType;
			this.itemFieldName = itemFieldName;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof NamedItemType) {
				NamedItemType b = (NamedItemType) obj;
				return itemType.equals(b.itemType) && isEquals(itemFieldName, b.itemFieldName);
			} else {
				return false;
			}
		}

		@Override
		public int hashCode() {
			int hash = itemType.hashCode() << 7;
			if (itemFieldName != null) {
				hash += itemFieldName.hashCode();
			}
			return hash;
		}
	}

	// { definedIn (Class) -> (ImplicitCollectionMapperForClass) }
	private final Map classNameToMapper = new HashMap();

	public ImplicitCollectionMapper(Mapper wrapped) {
		super(wrapped);
	}

	public void add(Class definedIn, String fieldName, Class itemType) {
		add(definedIn, fieldName, null, itemType);
	}

	public void add(Class definedIn, String fieldName, String itemFieldName, Class itemType) {
		add(definedIn, fieldName, itemFieldName, itemType, null);
	}

	public void add(Class definedIn, String fieldName, String itemFieldName, Class itemType, String keyFieldName) {
		Field field = null;
		while (definedIn != Object.class) {
			try {
				field = definedIn.getDeclaredField(fieldName);
				break;
			} catch (SecurityException e) {
				throw new InitializationException("Access denied for field with implicit collection", e);
			} catch (NoSuchFieldException e) {
				definedIn = definedIn.getSuperclass();
			}
		}
		if (field == null) {
			throw new InitializationException("No field \"" + fieldName + "\" for implicit collection");
		} else if (Map.class.isAssignableFrom(field.getType())) {
			if (itemFieldName == null && keyFieldName == null) {
				itemType = Map.Entry.class;
			}
		} else if (!Collection.class.isAssignableFrom(field.getType())) {
			Class fieldType = field.getType();
			if (!fieldType.isArray()) {
				throw new InitializationException("Field \"" + fieldName + "\" declares no collection or array");
			} else {
				Class componentType = fieldType.getComponentType();
				componentType = componentType.isPrimitive() ? PrimitiveUtils.box(componentType) : componentType;
				if (itemType == null) {
					itemType = componentType;
				} else {
					itemType = itemType.isPrimitive() ? PrimitiveUtils.box(itemType) : itemType;
					if (!componentType.isAssignableFrom(itemType)) {
						throw new InitializationException("Field \"" + fieldName
								+ "\" declares an array, but the array type is not compatible with "
								+ itemType.getName());

					}
				}
			}
		}
		ImplicitCollectionMapperForClass mapper = getOrCreateMapper(definedIn);
		mapper.add(new ImplicitCollectionMappingImpl(fieldName, itemType, itemFieldName, keyFieldName));
	}

	@Override
	public String getFieldNameForItemTypeAndName(Class definedIn, Class itemType, String itemFieldName) {
		ImplicitCollectionMapperForClass mapper = getMapper(definedIn);
		if (mapper != null) {
			return mapper.getFieldNameForItemTypeAndName(itemType, itemFieldName);
		} else {
			return null;
		}
	}

	@Override
	public ImplicitCollectionMapping getImplicitCollectionDefForFieldName(Class itemType, String fieldName) {
		ImplicitCollectionMapperForClass mapper = getMapper(itemType);
		if (mapper != null) {
			return mapper.getImplicitCollectionDefForFieldName(fieldName);
		} else {
			return null;
		}
	}

	@Override
	public Class getItemTypeForItemFieldName(Class definedIn, String itemFieldName) {
		ImplicitCollectionMapperForClass mapper = getMapper(definedIn);
		if (mapper != null) {
			return mapper.getItemTypeForItemFieldName(itemFieldName);
		} else {
			return null;
		}
	}

	private ImplicitCollectionMapperForClass getMapper(Class definedIn) {
		while (definedIn != null) {
			ImplicitCollectionMapperForClass mapper = (ImplicitCollectionMapperForClass) classNameToMapper
					.get(definedIn);
			if (mapper != null) {
				return mapper;
			}
			definedIn = definedIn.getSuperclass();
		}
		return null;
	}

	private ImplicitCollectionMapperForClass getOrCreateMapper(Class definedIn) {
		ImplicitCollectionMapperForClass mapper = getMapper(definedIn);
		if (mapper == null) {
			mapper = new ImplicitCollectionMapperForClass();
			classNameToMapper.put(definedIn, mapper);
		}
		return mapper;
	}
}
