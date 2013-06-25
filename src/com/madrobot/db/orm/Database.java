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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.madrobot.db.DBException;

/**
 * Represents a database to be used by Android Active Record entities.
 * 
 */
public class Database {
	static Map<String, DatabaseBuilder> _builders = new HashMap<String, DatabaseBuilder>();

	static final String CNAME = Database.class.getSimpleName();

	/**
	 * Creates new DB instance. Returned DB instances is not initially opened.
	 * Calling application must explicitly open it by calling open() method
	 * 
	 * @param ctx
	 * @param dbName
	 * @param dbVersion
	 * @return
	 * @throws DBException
	 */
	public static Database createInstance(Context ctx, String dbName,
			int dbVersion) throws DBException {
		DatabaseBuilder builder = getBuilder(dbName);
		if (null == builder)
			throw new DBException(
					"Schema wasn't initialized. Call Database.setBuilder() first");
		return new Database(ctx, dbName, dbVersion, builder);
	}
	public static Database createInstance(Context ctx, String dbName,
			int dbVersion, DatabaseBuilder builder) {
		return new Database(ctx, dbName, dbVersion, builder);
	}
	/**
	 * Returns DatabaseBuilder object assosicted with Database
	 * 
	 * @param dbName
	 *            database name
	 * @return DatabaseBuilder object assosicted with Database
	 */
	public static DatabaseBuilder getBuilder(String dbName) {
		return _builders.get(dbName);
	}
	/**
	 * Get the SQLite type for an input class.
	 * 
	 * @param c
	 *            The class to convert.
	 * @return A string representing the SQLite type that would be used to store
	 *         that class.
	 */
	protected static String getSQLiteTypeString(Class<?> c) {
		String name = c.getName();
		if (name.equals("java.lang.String"))
			return "text";
		if (name.equals("short"))
			return "int";
		if (name.equals("int"))
			return "int";
		if (name.equals("long"))
			return "int";
		if (name.equals("long"))
			return "int";
		if (name.equals("java.sql.Timestamp"))
			return "int";
		if (name.equals("double"))
			return "real";
		if (name.equals("float"))
			return "real";
		if (name.equals("[B"))
			return "blob";
		if (name.equals("boolean"))
			return "bool";
		if (c.getSuperclass() == DatabaseClient.class)
			return "int";
		throw new IllegalArgumentException(
				"Class cannot be stored in Sqlite3 database.");
	}

	/**
	 * Creates and opens new DB instances. DB instance returned to calling
	 * application already opened.
	 * 
	 * @param ctx
	 * @param dbName
	 * @param dbVersion
	 * @return
	 * @throws DBException
	 */
	public static Database open(Context ctx, String dbName, int dbVersion)
			throws DBException {
		Database db = Database.createInstance(ctx, dbName, dbVersion);
		db.open();
		return db;
	}

	/**
	 * Initializes Database framework. This method must be called for each used
	 * database only once before using database. This is required for proper
	 * setup static attributes of the Database
	 * 
	 * @param builder
	 * @return
	 */
	static public void setBuilder(DatabaseBuilder builder) {
		_builders.put(builder.getDatabaseName(), builder);
	}

	@SuppressWarnings("unused")
	private Context _context;

	private SQLiteDatabase _database;

	private DatabaseOpenHelper _dbHelper;

	private String _path;

	/**
	 * Creates a new DatabaseWrapper object
	 * 
	 * @param dbName
	 *            The file name to use for the SQLite database.
	 * @param dbVersion
	 *            Database version
	 * @param context
	 *            The context used for database creation, its package name will
	 *            be used to place the database on external storage if any is
	 *            present, otherwise the context's application data directory.
	 */
	Database(Context context, String dbName, int dbVersion,
			DatabaseBuilder builder) {
		_context = context;
		// String dbPath = (Environment.getExternalStorageState().equals(
		// Environment.MEDIA_MOUNTED) ? appendFilePath(Environment
		// .getExternalStorageDirectory().getAbsolutePath(), String
		// .format("android%1$sdata%1$s%2$s%1$s", File.separator, _context
		// .getPackageName())) : _context.getDir(
		// _context.getPackageName(), 0).getAbsolutePath());
		// new File(dbPath).mkdirs();
		_path = dbName; // temporary workaround - DB is created only on device,
		// not SDcard
		// _path = appendFilePath(dbPath, dbName);
		_dbHelper = new DatabaseOpenHelper(context, _path, dbVersion, builder);
		_context = context;
	}

	public void beginTransaction() {
		_database.beginTransaction();
	}

	public void close() {
		String d = _database.toString();
		if (_database != null)
			_database.close();
		_database = null;
		Log.e("MadRobot", CNAME+" .close(): db obj "+d+" set to null");
	}

	/**
	 * Delete from a table in the database
	 * 
	 * @param table
	 *            The table to delete from.
	 * @param whereClause
	 *            The condition to match (Don't include WHERE).
	 * @param whereArgs
	 *            The arguments to replace "?" with.
	 * @return The number of rows affected.
	 */
	public int delete(String table, String whereClause, String[] whereArgs) {
		return _database.delete(table, whereClause, whereArgs);
	}

	public void endTransaction() {
		_database.endTransaction();
	}

	/**
	 * Execute some raw SQL.
	 * 
	 * @param sql
	 *            Standard SQLite compatible SQL.
	 */
	public void execute(String sql) {
		_database.execSQL(sql);
	}

	public String[] getColumnsForTable(String table) {
		Cursor c = rawQuery(String.format("PRAGMA table_info(%s)", table));
		List<String> columns = new ArrayList<String>();
		try {
			while (c.moveToNext()) {
				columns.add(c.getString(c.getColumnIndex("name")));
			}
		} finally {
			c.close();
		}
		return columns.toArray(new String[0]);
	}

	/**
	 * Returns array of database tables names
	 * 
	 * @throws DBException
	 */
	public String[] getTables() throws DBException {
		if (null == _database || !_database.isOpen()) {
			Log.e("MadRobot", CNAME
					+ " .getTables(): ERROR - db object is null or closed");
			throw new DBException(
					"Database is closed. Did you forget to open database?");
		}

		Cursor c = query("sqlite_master", new String[] { "name" }, "type = ?",
				new String[] { "table" });
		List<String> tables = new ArrayList<String>();
		try {
			while (c.moveToNext()) {
				tables.add(c.getString(0));
			}
		} finally {
			c.close();
		}
		return tables.toArray(new String[0]);
	}

	public int getVersion() throws DBException {
		if (null == _database || !_database.isOpen()) {
			Log.e("MadRobot", CNAME
					+ ".getVersion(): ERROR - db object is null or closed");
			throw new DBException(
					"Database is closed. Did you forget to open database?");
		}

		return _database.getVersion();
	}

	/**
	 * Insert into a table in the database.
	 * 
	 * @param table
	 *            The table to insert into.
	 * @param parameters
	 *            The data.
	 * @return the row ID of the newly inserted row, or -1 if an error occurred.
	 */
	public long insert(String table, ContentValues parameters) {
		return _database.insert(table, null, parameters);
	}

	public boolean isOpen() {
		if (null != _database && _database.isOpen())
			return true;
		else
			return false;
	}

	/**
	 * Opens or creates the database file. Uses external storage if present,
	 * otherwise uses local storage.
	 */
	public void open() {
		if (_database != null && _database.isOpen()) {
			_database.close();
			_database = null;
		}
		_database = _dbHelper.getReadableDatabase();
		Log.e("MadRobot", CNAME+ " .open(): new db obj  "+_database.toString());
	}

	/**
	 * Execute a query.
	 * 
	 * @param distinct
	 * @param table
	 *            The table to query.
	 * @param selectColumns
	 *            The columns to select.
	 * @param where
	 *            The condition to match (Don't include "where").
	 * @param whereArgs
	 *            The arguments to replace "?" with.
	 * @param groupBy
	 * @param having
	 * @param orderBy
	 * @param limit
	 * @return A cursor over the data returned.
	 * @throws DBException
	 *             is database is null or closed
	 */
	public Cursor query(boolean distinct, String table, String[] selectColumns,
			String where, String[] whereArgs, String groupBy, String having,
			String orderBy, String limit) throws DBException {
		if (null == _database || !_database.isOpen()) {
			Log.e("MadRobot", CNAME
					+ ".query(): ERROR - db object is null or closed");
			throw new DBException(
					"Database is closed. Did you forget to open database?");
		}

		return _database.query(distinct, table, selectColumns, where,
				whereArgs, groupBy, having, orderBy, limit);
	}

	/**
	 * Execute a query.
	 * 
	 * @param table
	 *            The table to query.
	 * @param selectColumns
	 *            The columns to select.
	 * @param where
	 *            The condition to match (Don't include "where").
	 * @param whereArgs
	 *            The arguments to replace "?" with.
	 * @return A cursor over the data returned.
	 * @throws DBException
	 *             is database is null or closed
	 */
	public Cursor query(String table, String[] selectColumns, String where,
			String[] whereArgs) throws DBException {
		return query(false, table, selectColumns, where, whereArgs, null, null,
				null, null);
	}

	/**
	 * Execute a raw SQL query.
	 * 
	 * @param sql
	 *            Standard SQLite compatible SQL.
	 * @return A cursor over the data returned.
	 */
	public Cursor rawQuery(String sql) {
		return rawQuery(sql, null);
	}

	/**
	 * Execute a raw SQL query.
	 * 
	 * @param sql
	 *            Standard SQLite compatible SQL.
	 * @param params
	 *            The values to replace "?" with.
	 * @return A cursor over the data returned.
	 */
	public Cursor rawQuery(String sql, String[] params) {
		return _database.rawQuery(sql, params);
	}

	public void setVersion(int version) throws DBException {
		if (null == _database || !_database.isOpen()) {
			Log.e("MadRobot", CNAME
					+ ".setVersion(): ERROR - db object is null or closed");
			throw new DBException(
					"Database is closed. Did you forget to open database?");
		}

		_database.setVersion(version);
	}

	/**
	 * Update a table in the database.
	 * 
	 * @param table
	 *            The table to update.
	 * @param values
	 *            The new values.
	 * @param whereClause
	 *            The condition to match (Don't include "where").
	 * @param whereArgs
	 *            The arguments to replace "?" with.
	 * @return The number of rows affected.
	 */
	public int update(String table, ContentValues values, String whereClause,
			String[] whereArgs) {
		return _database.update(table, values, whereClause, whereArgs);
	}

	// /**
	// * Append to a file path, takes extra or missing separator characters into
	// * account.
	// *
	// * @param path
	// * The root path.
	// * @param append
	// * What to add.
	// * @return The new path.
	// */
	// private static String appendFilePath(String path, String append) {
	// return path.concat(path.endsWith(File.separator) ? (append
	// .startsWith(File.separator) ? append.substring(1) : append)
	// : File.separator
	// .concat((append.startsWith(File.separator) ? append
	// .substring(1) : append)));
	// }

	// -------------------------------------------------------------------------//
}
