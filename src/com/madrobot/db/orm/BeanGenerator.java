/*******************************************************************************
 * Copyright (c) 2011 MadRobot.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *  Elton Kent - initial API and implementation
 ******************************************************************************/
package com.madrobot.db.orm;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.database.Cursor;

import com.madrobot.beans.BeanInfo;
import com.madrobot.beans.IntrospectionException;
import com.madrobot.beans.Introspector;
import com.madrobot.beans.PropertyDescriptor;
import com.madrobot.db.DBUtils;
import com.madrobot.reflect.ClassUtils;

/**
 * Utility class to map a database cursor/resultset to a java bean
 * @author elton.kent
 *
 */
public class BeanGenerator {
	/**
	 * Set a bean's primitive properties to these defaults when SQL NULL is
	 * returned. These are the same as the defaults that ResultSet get* methods
	 * return in the event of a NULL column.
	 */
	private static final Map<Class<?>, Object> primitiveDefaults = new HashMap<Class<?>, Object>();

	/**
	 * Special array value used by <code>mapColumnsToProperties</code> that
	 * indicates there is no bean property that matches a column from a
	 * <code>ResultSet</code>.
	 */
	private static final int PROPERTY_NOT_FOUND = -1;

	static {
		primitiveDefaults.put(Integer.TYPE, 0);
		primitiveDefaults.put(Short.TYPE, ((short) 0));
		primitiveDefaults.put(Byte.TYPE, ((byte) 0));
		primitiveDefaults.put(Float.TYPE, (float) (0));
		primitiveDefaults.put(Double.TYPE, (double) (0));
		primitiveDefaults.put(Long.TYPE, (0L));
		primitiveDefaults.put(Boolean.TYPE, Boolean.FALSE);
		primitiveDefaults.put(Character.TYPE, '\u0000');
	}

	/**
	 * Calls the setter method on the target object for the given property. If
	 * no setter method exists for the property, this method does nothing.
	 * 
	 * @param target
	 *            The object to set the property on.
	 * @param prop
	 *            The property to set.
	 * @param value
	 *            The value to pass into the setter.
	 * @throws SQLException
	 *             if an error occurs setting the property.
	 */
	private static void callSetter(Object target, PropertyDescriptor prop,
			Object value) throws SQLException {

		Method setter = prop.getWriteMethod();

		if (setter == null) {
			return;
		}

		Class<?>[] params = setter.getParameterTypes();
		try {
			// convert types for some popular ones
			if (value != null) {
				if (value instanceof java.util.Date) {
					if (params[0].getName().equals("java.sql.Date")) {
						value = new java.sql.Date(
								((java.util.Date) value).getTime());
					} else if (params[0].getName().equals("java.sql.Time")) {
						value = new java.sql.Time(
								((java.util.Date) value).getTime());
					} else if (params[0].getName().equals("java.sql.Timestamp")) {
						value = new java.sql.Timestamp(
								((java.util.Date) value).getTime());
					}
				}
			}

			// Don't call setter if the value object isn't the right type
			if (isCompatibleType(value, params[0])) {
				setter.invoke(target, new Object[] { value });
			} else {
				throw new SQLException("Cannot set " + prop.getName()
						+ ": incompatible types.");
			}

		} catch (IllegalArgumentException e) {
			throw new SQLException("Cannot set " + prop.getName() + ": "
					+ e.getMessage());

		} catch (IllegalAccessException e) {
			throw new SQLException("Cannot set " + prop.getName() + ": "
					+ e.getMessage());

		} catch (InvocationTargetException e) {
			throw new SQLException("Cannot set " + prop.getName() + ": "
					+ e.getMessage());
		}
	}

	/**
	 * Creates a new object and initializes its fields from the ResultSet.
	 * 
	 * @param <T>
	 *            The type of bean to create
	 * @param rs
	 *            The result set.
	 * @param type
	 *            The bean type (the return type of the object).
	 * @param props
	 *            The property descriptors.
	 * @param columnToProperty
	 *            The column indices in the result set.
	 * @return An initialized object.
	 * @throws SQLException
	 *             if a database error occurs.
	 * @throws InstantiationException
	 *             If the bean's constructor is not visible
	 * @throws IllegalAccessException
	 *             if the bean class is not public
	 */
	private static <T> T createBean(ResultSet rs, Class<T> type,
			PropertyDescriptor[] props, int[] columnToProperty)
			throws SQLException, IllegalAccessException, InstantiationException {

		T bean = ClassUtils.newInstance(type);

		for (int i = 1; i < columnToProperty.length; i++) {

			if (columnToProperty[i] == PROPERTY_NOT_FOUND) {
				continue;
			}
			PropertyDescriptor prop = props[columnToProperty[i]];
			Class<?> propType = prop.getPropertyType();

			Object value = processColumn(rs, i, propType);

			if (propType != null && value == null && propType.isPrimitive()) {
				value = primitiveDefaults.get(propType);
			}
			callSetter(bean, prop, value);
		}
		return bean;
	}

	private static Object[] getMethodParamter(Method method, Cursor cursor,
			int columnIndex) {
		// method.get
		Class[] paramTypes = method.getParameterTypes();
		if (paramTypes != null) {
			if (paramTypes.length > 0) {
				Class type = paramTypes[0];
				if (type.equals(int.class)) {
					// if(cursor.getType(columnIndex) ==
					// Cursor.FIELD_TYPE_INTEGER)
					return new Object[] { cursor.getInt(columnIndex) };
				} else if (type.equals(String.class)) {
					return new Object[] { cursor.getString(columnIndex) };
				} else if (type.equals(float.class)) {
					return new Object[] { cursor.getFloat(columnIndex) };
				} else if (type.equals(long.class)) {
					return new Object[] { cursor.getLong(columnIndex) };
				} else if (type.equals(double.class)) {
					return new Object[] { cursor.getDouble(columnIndex) };
				} else if (type.equals(byte[].class)) {
					return new Object[] { cursor.getBlob(columnIndex) };
				} else if (type.equals(short.class)) {
					return new Object[] { cursor.getShort(columnIndex) };
				}

			}

		}
		return null;
	}

	/**
	 * ResultSet.getObject() returns an Integer object for an INT column. The
	 * setter method for the property might take an Integer or a primitive int.
	 * This method returns true if the value can be successfully passed into the
	 * setter method. Remember, Method.invoke() handles the unwrapping of
	 * Integer into an int.
	 * 
	 * @param value
	 *            The value to be passed into the setter method.
	 * @param type
	 *            The setter's parameter type.
	 * @return boolean True if the value is compatible.
	 */
	private static boolean isCompatibleType(Object value, Class<?> type) {
		// Do object check first, then primitives
		if (value == null || type.isInstance(value)) {
			return true;

		} else if (type.equals(Integer.TYPE) && Integer.class.isInstance(value)) {
			return true;

		} else if (type.equals(Long.TYPE) && Long.class.isInstance(value)) {
			return true;

		} else if (type.equals(Double.TYPE) && Double.class.isInstance(value)) {
			return true;

		} else if (type.equals(Float.TYPE) && Float.class.isInstance(value)) {
			return true;

		} else if (type.equals(Short.TYPE) && Short.class.isInstance(value)) {
			return true;

		} else if (type.equals(Byte.TYPE) && Byte.class.isInstance(value)) {
			return true;

		} else if (type.equals(Character.TYPE)
				&& Character.class.isInstance(value)) {
			return true;

		} else if (type.equals(Boolean.TYPE) && Boolean.class.isInstance(value)) {
			return true;

		} else {
			return false;
		}

	}

	/**
	 * The positions in the returned array represent column numbers. The values
	 * stored at each position represent the index in the
	 * <code>PropertyDescriptor[]</code> for the bean property that matches the
	 * column name. If no bean property was found for a column, the position is
	 * set to <code>PROPERTY_NOT_FOUND</code>.
	 * 
	 * @param rsmd
	 *            The <code>ResultSetMetaData</code> containing column
	 *            information.
	 * 
	 * @param props
	 *            The bean property descriptors.
	 * 
	 * @throws SQLException
	 *             if a database access error occurs
	 * 
	 * @return An int[] with column index to property index mappings. The 0th
	 *         element is meaningless because JDBC column indexing starts at 1.
	 */
	private static int[] mapColumnsToProperties(ResultSetMetaData rsmd,
			PropertyDescriptor[] props) throws SQLException {

		int cols = rsmd.getColumnCount();
		int columnToProperty[] = new int[cols + 1];
		Arrays.fill(columnToProperty, PROPERTY_NOT_FOUND);

		for (int col = 1; col <= cols; col++) {
			String columnName = rsmd.getColumnLabel(col);
			if (null == columnName || 0 == columnName.length()) {
				columnName = rsmd.getColumnName(col);
			}
			for (int i = 0; i < props.length; i++) {

				if (columnName.equalsIgnoreCase(props[i].getName())) {
					columnToProperty[col] = i;
					break;
				}
			}
		}
		return columnToProperty;
	}

	private static void populateBean(Object bean, Cursor cursor,
			String[] columns, PropertyDescriptor[] propDesc)
			throws IllegalArgumentException, IllegalAccessException,
			InvocationTargetException {
		for (int i = 0; i < columns.length; i++) {
			for (int j = 0; j < propDesc.length; j++) {
				if (columns[i].equalsIgnoreCase(propDesc[j].getName())) {
					Method method = propDesc[j].getWriteMethod();
					if (method != null) {
						int columnIndex = cursor.getColumnIndex(columns[i]);
						Object[] params = getMethodParamter(method, cursor,
								columnIndex);
						if (params != null)
							method.invoke(bean, params);
					}
				}
			}
		}
	}

	/**
	 * Convert a <code>ResultSet</code> column into an object. Simple
	 * implementations could just call <code>rs.getObject(index)</code> while
	 * more complex implementations could perform type manipulation to match the
	 * column's type to the bean property type.
	 * 
	 * <p>
	 * This implementation calls the appropriate <code>ResultSet</code> getter
	 * method for the given property type to perform the type conversion. If the
	 * property type doesn't match one of the supported <code>ResultSet</code>
	 * types, <code>getObject</code> is called.
	 * </p>
	 * 
	 * @param rs
	 *            The <code>ResultSet</code> currently being processed. It is
	 *            positioned on a valid row before being passed into this
	 *            method.
	 * 
	 * @param index
	 *            The current column index being processed.
	 * 
	 * @param propType
	 *            The bean property type that this column needs to be converted
	 *            into.
	 * 
	 * @throws SQLException
	 *             if a database access error occurs
	 * 
	 * @return The object from the <code>ResultSet</code> at the given column
	 *         index after optional type processing or <code>null</code> if the
	 *         column value was SQL NULL.
	 */
	private static Object processColumn(ResultSet rs, int index,
			Class<?> propType) throws SQLException {

		if (!propType.isPrimitive() && rs.getObject(index) == null) {
			return null;
		}

		if (!propType.isPrimitive() && rs.getObject(index) == null) {
			return null;
		}

		if (propType.equals(String.class)) {
			return rs.getString(index);

		} else if (propType.equals(Integer.TYPE)
				|| propType.equals(Integer.class)) {
			return (rs.getInt(index));

		} else if (propType.equals(Boolean.TYPE)
				|| propType.equals(Boolean.class)) {
			return (rs.getBoolean(index));

		} else if (propType.equals(Long.TYPE) || propType.equals(Long.class)) {
			return (rs.getLong(index));

		} else if (propType.equals(Double.TYPE)
				|| propType.equals(Double.class)) {
			return (rs.getDouble(index));

		} else if (propType.equals(Float.TYPE) || propType.equals(Float.class)) {
			return (rs.getFloat(index));

		} else if (propType.equals(Short.TYPE) || propType.equals(Short.class)) {
			return (rs.getShort(index));

		} else if (propType.equals(Byte.TYPE) || propType.equals(Byte.class)) {
			return (rs.getByte(index));

		} else if (propType.equals(Timestamp.class)) {
			return rs.getTimestamp(index);

		} else {
			return rs.getObject(index);
		}

	}

	/**
	 * Returns a PropertyDescriptor[] for the given Class.
	 * 
	 * @param c
	 *            The Class to retrieve PropertyDescriptors for.
	 * @return A PropertyDescriptor[] describing the Class.
	 * @throws SQLException
	 *             if introspection failed.
	 * @throws IntrospectionException
	 */
	private static PropertyDescriptor[] propertyDescriptors(Class<?> c)
			throws IntrospectionException {
		// Introspector caches BeanInfo classes for better performance
		BeanInfo beanInfo = null;
		beanInfo = Introspector.getBeanInfo(c);
		return beanInfo.getPropertyDescriptors();
	}

	/**
	 * Convert a <code>ResultSet</code> row into a JavaBean. This implementation
	 * uses reflection and <code>BeanInfo</code> classes to match column names
	 * to bean property names. Properties are matched to columns based on
	 * several factors: <br/>
	 * <ol>
	 * <li>
	 * The class has a writable property with the same name as a column. The
	 * name comparison is case insensitive.</li>
	 * 
	 * <li>
	 * The column type can be converted to the property's set method parameter
	 * type with a ResultSet.get* method. If the conversion fails (ie. the
	 * property was an int and the column was a Timestamp) an SQLException is
	 * thrown.</li>
	 * </ol>
	 * 
	 * <p>
	 * Primitive bean properties are set to their defaults when SQL NULL is
	 * returned from the <code>ResultSet</code>. Numeric fields are set to 0 and
	 * booleans are set to false. Object bean properties are set to
	 * <code>null</code> when SQL NULL is returned. This is the same behavior as
	 * the <code>ResultSet</code> get* methods.
	 * </p>
	 * 
	 * To generate table from java beans use {@link DatabaseBuilder} class
	 * 
	 * @param <T>
	 *            The type of bean to create
	 * @param rs
	 *            ResultSet that supplies the bean data
	 * @param type
	 *            Class from which to create the bean instance
	 * @throws SQLException
	 *             if a database access error occurs
	 * @return the newly created bean
	 * @throws InstantiationException
	 *             If the constructor of <code>type</code> is not accessible
	 * @throws IllegalAccessException
	 *             If the mentioned <code>type</code> is not accessible.
	 * @throws IntrospectionException
	 */
	public static <T> T toBean(ResultSet rs, Class<T> type)
			throws IllegalAccessException, InstantiationException,
			IntrospectionException, SQLException {

		PropertyDescriptor[] props = propertyDescriptors(type);

		ResultSetMetaData rsmd = rs.getMetaData();
		int[] columnToProperty = mapColumnsToProperties(rsmd, props);

		return createBean(rs, type, props, columnToProperty);
	}

	/**
	 * Populates a list of the specified bean type
	 * <p>
	 * The column names/types must be the same as the bean's field name and
	 * types.<br/>
	 * It supports all the native types return by Cursor include blob (byte[])
	 * <b><u>Table</u></b>
	 * <table>
	 * <tr>
	 * <td>Column Name</td>
	 * <td>Column Type</td>.
	 * </tr>
	 * <tr>
	 * <td>Name</td>
	 * <td>String</td>
	 * </tr>
	 * <tr>
	 * <td>Age</td>
	 * <td>Integer</td>
	 * </tr>
	 * </table>
	 * <br/>
	 * <b><u>Bean</u></b><br/>
	 * 
	 * <font color="green"> //Bean class and setter methods has to be public.
	 * </font>
	 * 
	 * <pre>
	 * public class Bean {
	 * 	private String name;
	 * 	private int age;
	 * 
	 * 	public String getName() {
	 * 		return name;
	 * 	}
	 * 
	 * 	public void setName(String name) {
	 * 		this.name = name;
	 * 	}
	 * 
	 * 	public int getAge() {
	 * 		return age;
	 * 	}
	 * 
	 * 	public void setAge(int age) {
	 * 		this.age = age;
	 * 	}
	 * 
	 * }
	 * </pre>
	 * 
	 * <b><u>Populating the Bean</u></b><br/>
	 * 
	 * <pre>
	 * Cursor cursor=database.query(...);
	 * List<Bean>=DBUtils.toBeanList(cursor,Bean.class);
	 * </pre>
	 * 
	 * 
	 * 
	 * </p>
	 * To generate table from java beans use {@link DatabaseBuilder} class
	 * 
	 * @param <T>
	 * @param cursor
	 * @param type
	 * @return
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 *             If the class mentioned is not accessible
	 * @throws IntrospectionException
	 *             If there is trouble in getting the infromation from the class
	 * @throws InvocationTargetException
	 *             If the setter methods in the bean are not public
	 * @throws IllegalArgumentException
	 */
	public static <T> List<T> toBeanList(Cursor cursor, Class<T> type)
			throws IllegalAccessException, InstantiationException,
			IntrospectionException, IllegalArgumentException,
			InvocationTargetException {
		List results = new ArrayList();
		if (cursor.getCount() == 0)
			return results;

		String[] columns = cursor.getColumnNames();
		if (columns == null || columns.length == 0)
			return results;
		cursor.moveToFirst();
		PropertyDescriptor[] propDesc = propertyDescriptors(type);
		for (int i = 0; i < cursor.getCount(); i++) {
			Object bean = ClassUtils.newInstance(type);
			populateBean(bean, cursor, columns, propDesc);
			results.add(bean);
			cursor.moveToNext();
		}
		return results;
	}

	/**
	 * Convert a <code>ResultSet</code> into a <code>List</code> of JavaBeans.
	 * This implementation uses reflection and <code>BeanInfo</code> classes to
	 * match column names to bean property names. Properties are matched to
	 * columns based on several factors: <br/>
	 * <ol>
	 * <li>
	 * The class has a writable property with the same name as a column. The
	 * name comparison is case insensitive.</li>
	 * 
	 * <li>
	 * The column type can be converted to the property's set method parameter
	 * type with a ResultSet.get* method. If the conversion fails (ie. the
	 * property was an int and the column was a Timestamp) an SQLException is
	 * thrown.</li>
	 * </ol>
	 * 
	 * <p>
	 * Primitive bean properties are set to their defaults when SQL NULL is
	 * returned from the <code>ResultSet</code>. Numeric fields are set to 0 and
	 * booleans are set to false. Object bean properties are set to
	 * <code>null</code> when SQL NULL is returned. This is the same behavior as
	 * the <code>ResultSet</code> get* methods.
	 * </p>
	 * 
	 * @param <T>
	 *            The type of bean to create
	 * @param rs
	 *            ResultSet that supplies the bean data
	 * @param type
	 *            Class from which to create the bean instance
	 * @throws SQLException
	 *             if a database access error occurs
	 * @return the newly created List of beans
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws IntrospectionException
	 * @see DBUtils#toBean(ResultSet, Class)
	 */
	public static <T> List<T> toBeanList(ResultSet rs, Class<T> type)
			throws SQLException, IllegalAccessException,
			InstantiationException, IntrospectionException {
		List<T> results = new ArrayList<T>();

		if (!rs.next()) {
			return results;
		}

		PropertyDescriptor[] props = propertyDescriptors(type);
		ResultSetMetaData rsmd = rs.getMetaData();
		int[] columnToProperty = mapColumnsToProperties(rsmd, props);

		do {
			results.add(createBean(rs, type, props, columnToProperty));
		} while (rs.next());

		return results;
	}
}
