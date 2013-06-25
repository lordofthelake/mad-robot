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

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import com.madrobot.db.DBException;
import com.madrobot.text.WordUtils;

/**
 * Defines DB schema definition statements from provided Java classes. <br/>
 * Use this class to specify structure of your DB. Call method addClass() for
 * each table and provide corresponding Java class. <br/>
 * Normally this class instantiated only once at the very beginning of the
 * application life cycle. Once instantiated it is used by underlying
 * SQLDatabaseHelper and provides SQL statements for create or upgrade of DB
 * schema. <br/>
 * <br/><b>Building a database from java classes</b>
 * <pre>
 * <code>
 *  DatabaseBuilder builder = new DatabaseBuilder("Test.db");
 *  builder.addClass(Table1.class);
 *  builder.addClass(Table2.class);
 *  Database.setBuilder(builder);
 *  try{
 *  DatabaseClient client= DatabaseClient.open(this, "dbName.db",
 *                                         "dbVersion");
 *  // purge Table1 table
 *  _db.delete(Table1.class, null, null);
 *
 *  }catch(DBException e){
 *  }
 *  </code>
 * 
 * </pre>
 */
public class DatabaseBuilder {

	String _dbName;
	@SuppressWarnings("unchecked")
	Map<String, Class> classes = new HashMap<String, Class>();

	/**
	 * Create a new DatabaseBuilder for a database.
	 */
	public DatabaseBuilder(String dbName) {
		this._dbName = dbName;
	}

	/**
	 * Add or update a table for an AREntity that is stored in the current
	 * database.
	 * 
	 * @param <T>
	 *            Any ActiveRecordBase type.
	 * @param c
	 *            The class to reference when updating or adding a table.
	 */
	public <T extends DatabaseClient> void addClass(Class<T> c) {
		classes.put(c.getSimpleName(), c);
	}

	@SuppressWarnings("unchecked")
	private Class getClassBySqlName(String table) {
		String jName = WordUtils.toJavaClassName(table);
		return classes.get(jName);
	}

	public String getDatabaseName() {
		return _dbName;
	}

	/**
	 * Returns SQL create statement for specified table
	 * 
	 * @param table
	 *            name in SQL notation
	 * @throws DBException
	 */
	@SuppressWarnings("unchecked")
	public <T extends DatabaseClient> String getSQLCreate(String table)
			throws DBException {
		StringBuilder sb = null;
		Class<T> c = getClassBySqlName(table);
		T e = null;
		try {
			e = c.newInstance();
		} catch (IllegalAccessException e1) {
			throw new DBException(e1.getLocalizedMessage());
		} catch (InstantiationException e1) {
			throw new DBException(e1.getLocalizedMessage());
		}
		if (null != c) {
			sb = new StringBuilder("CREATE TABLE ").append(table).append(
					" (_id integer primary key");
			for (Field column : e.getColumnFieldsWithoutID()) {
				String jname = column.getName();
				String qname = WordUtils.toSQLName(jname);
				Class<?> jtype = column.getType();
				String qtype = Database.getSQLiteTypeString(jtype);
				sb.append(", ").append(qname).append(" ").append(qtype);
			}
			sb.append(")");

		}
		return sb.toString();
	}

	/**
	 * Returns SQL drop table statement for specified table
	 * 
	 * @param table
	 *            name in SQL notation
	 */
	public String getSQLDrop(String table) {
		return "DROP TABLE IF EXISTS " + table;
	}

	/**
	 * Returns list of DB tables according to classes added to a schema map
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String[] getTables() {
		String[] ret = new String[classes.size()];
		Class[] arr = new Class[classes.size()];
		arr = classes.values().toArray(arr);
		for (int i = 0; i < arr.length; i++) {
			Class c = arr[i];
			ret[i] = WordUtils.toSQLName(c.getSimpleName());
		}
		return ret;
	}
}
