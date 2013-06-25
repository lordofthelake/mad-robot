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

package com.madrobot.di.csv;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 
 * 
 * 
 * 
 * 
 */
public interface ResultSetHelper {
	public String[] getColumnNames(ResultSet rs) throws SQLException;

	public String[] getColumnValues(ResultSet rs) throws SQLException, IOException;
}
