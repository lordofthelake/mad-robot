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
package com.madrobot.di;

import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.madrobot.di.wizard.json.JSONDeserializer;
import com.madrobot.di.wizard.json.annotations.BooleanFormat;

import android.util.Log;

/**
 * Primitive mapper for XML and JSON serialization classes
 * 
 * @author elton.stephen.kent
 * 
 */
public final class Converter {

	private static Map<Class<?>, Integer> clzTypeKeyMap = new HashMap<Class<?>, Integer>();

	private static final int TYPE_BOOLEAN = 8;
	private static final int TYPE_CHAR = 5;
	private static final int TYPE_DATE = 9;
	private static final int TYPE_DOUBLE = 7;
	private static final int TYPE_FLOAT = 6;
	private static final int TYPE_INT = 3;
	private static final int TYPE_LONG = 4;
	private static final int TYPE_SHORT = 2;
	private static final int TYPE_STRING = 1;

	static {
		clzTypeKeyMap.put(String.class, TYPE_STRING);
		clzTypeKeyMap.put(short.class, TYPE_SHORT);
		clzTypeKeyMap.put(int.class, TYPE_INT);
		clzTypeKeyMap.put(long.class, TYPE_LONG);
		clzTypeKeyMap.put(char.class, TYPE_CHAR);
		clzTypeKeyMap.put(float.class, TYPE_FLOAT);
		clzTypeKeyMap.put(double.class, TYPE_DOUBLE);
		clzTypeKeyMap.put(boolean.class, TYPE_BOOLEAN);
		clzTypeKeyMap.put(Date.class, TYPE_DATE);
	}

	public static Object convertTo(final JSONObject jsonObject, final String fieldName, final Class<?> clz, final Field field) {

		Object value = null;

		if (clzTypeKeyMap.containsKey(clz)) {
			try {
				final int code = clzTypeKeyMap.get(clz);
				switch (code) {
				case TYPE_STRING:
					value = jsonObject.optString(fieldName);
					break;
				case TYPE_SHORT:
					value = Short.parseShort(jsonObject.optString(fieldName, "0"));
					break;
				case TYPE_INT:
					value = jsonObject.optInt(fieldName);
					break;
				case TYPE_LONG:
					value = jsonObject.optLong(fieldName);
					break;
				case TYPE_CHAR:
					String chatValue = jsonObject.optString(fieldName);
					if (chatValue.length() > 0) {
						value = chatValue.charAt(0);
					} else {
						value = '\0';
					}
					break;
				case TYPE_FLOAT:
					value = Float.parseFloat(jsonObject.optString(fieldName, "0.0f"));
					break;
				case TYPE_DOUBLE:
					value = jsonObject.optDouble(fieldName);
					break;
				case TYPE_BOOLEAN:
					value = jsonObject.optString(fieldName);
					if (field.isAnnotationPresent(BooleanFormat.class)) {
						BooleanFormat formatAnnotation = field.getAnnotation(BooleanFormat.class);
						String trueFormat = formatAnnotation.trueFormat();
						String falseFormat = formatAnnotation.falseFormat();
						if (trueFormat.equals(value)) {
							value = true;
						} else if (falseFormat.equals(value)) {
							value = false;
						} else {
							Log.e(JSONDeserializer.TAG, "Expecting " + trueFormat + " / " + falseFormat + " but its "
									+ value);
						}
					} else {
						value = Boolean.parseBoolean((String) value);
					}
					break;
				case TYPE_DATE:
					value = DateFormat.getDateInstance().parse(jsonObject.optString(fieldName));
					break;
				}
			} catch (NumberFormatException e) {
				Log.e(JSONDeserializer.TAG, e.getMessage());
			} catch (ParseException e) {
				Log.e(JSONDeserializer.TAG, e.getMessage());
			}
		}
		return value;
	}

	/**
	 * Converts a {@link String} value to specified type, if possible.
	 * 
	 * @param raw
	 *            Raw, string value, to be converted
	 * @param clz
	 *            Target type to be converted to
	 * @return Converted value, if converstion was possible, null otherwise
	 * @throws NumberFormatException
	 *             If the value was not in correct format, while converting to numeric type
	 * @throws RuntimeException
	 *             If the value was not in correct format, while converting to Date or Boolean type
	 */
	public static Object convertTo(final String raw, final Class<?> clz) {
		Object value = null;
		if (clzTypeKeyMap.containsKey(clz)) {
			final int code = clzTypeKeyMap.get(clz);
			switch (code) {
			case TYPE_STRING:
				value = raw;
				break;
			case TYPE_SHORT:
				value = Short.parseShort(raw);
				break;
			case TYPE_INT:
				value = Integer.parseInt(raw);
				break;
			case TYPE_LONG:
				value = Long.parseLong(raw);
				break;
			case TYPE_CHAR:
				if ((raw != null) && (raw.length() > 0)) {
					value = raw.charAt(0);
				} else {
					value = '\0';
				}
				break;
			case TYPE_FLOAT:
				value = Float.parseFloat(raw);
				break;
			case TYPE_DOUBLE:
				value = Double.parseDouble(raw);
				break;
			case TYPE_BOOLEAN:
				value = Boolean.parseBoolean(raw);
				break;
			case TYPE_DATE:
				value = Date.parse(raw);
				break;
			default:
				break;
			}
		}
		return value;
	}

	public static boolean isBoolean(final Class<?> clz) {
		Integer type = clzTypeKeyMap.get(clz);
		if (type != null && type == TYPE_BOOLEAN)
			return true;
		else
			return false;
	}

	public static boolean isCollectionType(Class<?> type) {
		return Collection.class.isAssignableFrom(type) || Map.class.isAssignableFrom(type);
	}

	public static boolean isPseudoPrimitive(final Class<?> clz) {
		return clzTypeKeyMap.containsKey(clz);
	}

	public static void storeValue(final JSONObject jsonObject, final String key, Object value, final Field field)
			throws JSONException {

		Class<?> classType = field.getType();

		if (clzTypeKeyMap.containsKey(classType)) {
			final int code = clzTypeKeyMap.get(classType);
			switch (code) {
			case TYPE_STRING:
			case TYPE_SHORT:
			case TYPE_INT:
			case TYPE_LONG:
			case TYPE_CHAR:
			case TYPE_FLOAT:
			case TYPE_DOUBLE:
				break;
			case TYPE_BOOLEAN:
				Boolean userValue = (Boolean) value;
				if (field.isAnnotationPresent(BooleanFormat.class)) {
					BooleanFormat formatAnnotation = field.getAnnotation(BooleanFormat.class);
					String trueFormat = formatAnnotation.trueFormat();
					String falseFormat = formatAnnotation.falseFormat();
					if (userValue) {
						value = trueFormat;
					} else {
						value = falseFormat;
					}
				} else {
					value = userValue;
				}
				break;
			case TYPE_DATE:
				Date date = (Date) value;
				SimpleDateFormat simpleDateFormat = null;
				if (field.isAnnotationPresent(com.madrobot.di.wizard.json.annotations.DateFormat.class)) {
					com.madrobot.di.wizard.json.annotations.DateFormat formatAnnotation = field
							.getAnnotation(com.madrobot.di.wizard.json.annotations.DateFormat.class);
					String dateFormat = formatAnnotation.format();
					simpleDateFormat = new SimpleDateFormat(dateFormat);
				} else {
					simpleDateFormat = new SimpleDateFormat();
				}
				value = simpleDateFormat.format(date);
				break;
			}
			jsonObject.put(key, value);
		}
	}

	private Converter() {
	}
}
