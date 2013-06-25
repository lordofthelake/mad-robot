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

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.madrobot.db.DBException;

/**
 * Internal framework class. Utilize DatabaseBuilder to produce DDL statements
 * directly out of Java classes.
 * 
 */
class DatabaseOpenHelper extends SQLiteOpenHelper {

	DatabaseBuilder _builder;
	int _version;

	/**
	 * Constructor
	 * 
	 * @param ctx
	 * @param dbPath
	 * @param dbVersion
	 * @param builder
	 */
	public DatabaseOpenHelper(Context ctx, String dbPath, int dbVersion,
			DatabaseBuilder builder) {
		super(ctx, dbPath, null, dbVersion);
		_builder = builder;
		_version = dbVersion;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		for (String table : _builder.getTables()) {
			String sqlStr = null;
			try {
				sqlStr = _builder.getSQLCreate(table);
			} catch (DBException e) {
				Log.e(this.getClass().getName(), e.getMessage(), e);
			}
			if (sqlStr != null)
				db.execSQL(sqlStr);
		}
		db.setVersion(_version);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		for (String table : _builder.getTables()) {
			String sqlStr = _builder.getSQLDrop(table);
			db.execSQL(sqlStr);
		}
		onCreate(db);
	}
}
