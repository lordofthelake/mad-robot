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
package com.madrobot.db;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.madrobot.beans.Introspector;
import com.madrobot.reflect.MethodUtils;

public final class DBUtils {

	/**
	 * Compares two cursors to see if they contain the same data.
	 * 
	 * @return Returns true of the cursors contain the same data and are not null, false otherwise
	 */
	public static boolean compareCursors(Cursor c1, Cursor c2) {
		if (c1 == null || c2 == null)
			return false;

		final int numColumns = c1.getColumnCount();
		if (numColumns != c2.getColumnCount())
			return false;

		if (c1.getCount() != c2.getCount())
			return false;

		c1.moveToPosition(-1);
		c2.moveToPosition(-1);
		while (c1.moveToNext() && c2.moveToNext()) {
			for (int i = 0; i < numColumns; i++) {
				if (!TextUtils.equals(c1.getString(i), c2.getString(i)))
					return false;
			}
		}

		return true;
	}

	/**
	 * This method cleans up the cache's created by the toBean methods.
	 * <p>
	 * Should be called in critical memory conditions or a application shutdown.
	 * </p>
	 */
	public static void doCacheCleanUp() {
		Introspector.flushCaches();
		MethodUtils.flushCaches();
	}

	public static List<String> getColumns(final SQLiteDatabase db, final String tableName) {
		List<String> ar = null;
		Cursor c = null;
		try {
			c = db.rawQuery("select * from " + tableName + " limit 1", null);
			if (c != null) {
				ar = new ArrayList<String>(Arrays.asList(c.getColumnNames()));
			}
		} catch (final Exception e) {
			e.printStackTrace();
		} finally {
			if (c != null) {
				c.close();
			}
		}
		return ar;
	}

	public static MatrixCursor matrixCursorFromCursor(Cursor cursor) {
		final MatrixCursor newCursor = new MatrixCursor(cursor.getColumnNames());
		final int numColumns = cursor.getColumnCount();
		final String data[] = new String[numColumns];
		cursor.moveToPosition(-1);
		while (cursor.moveToNext()) {
			for (int i = 0; i < numColumns; i++) {
				data[i] = cursor.getString(i);
			}
			newCursor.addRow(data);
		}
		return newCursor;
	}

	public static String printCursor(Cursor cursor) {
		StringBuilder retval = new StringBuilder();

		retval.append("|");
		final int numcolumns = cursor.getColumnCount();
		for (int column = 0; column < numcolumns; column++) {
			String columnName = cursor.getColumnName(column);
			retval.append(String.format("%-20s |", columnName.substring(0, Math.min(20, columnName.length()))));
		}
		retval.append("\n|");
		for (int column = 0; column < numcolumns; column++) {
			for (int i = 0; i < 21; i++) {
				retval.append("-");
			}
			retval.append("+");
		}
		retval.append("\n|");

		while (cursor.moveToNext()) {
			for (int column = 0; column < numcolumns; column++) {
				String columnValue = cursor.getString(column);
				if (columnValue != null) {
					columnValue = columnValue.substring(0, Math.min(20, columnValue.length()));
				}
				retval.append(String.format("%-20s |", columnValue));
			}
			retval.append("\n");
		}

		return retval.toString();
	}

	/**
	 * Convert a <code>ResultSet</code> row into an <code>Object[]</code>. This implementation copies column values into
	 * the array in the same order they're returned from the <code>ResultSet</code>. Array elements will be set to
	 * <code>null</code> if the column was SQL NULL.
	 * 
	 * @param rs
	 *            ResultSet that supplies the array data
	 * @throws SQLException
	 *             if a database access error occurs
	 * @return the newly created array
	 */
	public Object[] toArray(ResultSet rs) throws SQLException {
		ResultSetMetaData meta = rs.getMetaData();
		int cols = meta.getColumnCount();
		Object[] result = new Object[cols];

		for (int i = 0; i < cols; i++) {
			result[i] = rs.getObject(i + 1);
		}

		return result;
	}

}
