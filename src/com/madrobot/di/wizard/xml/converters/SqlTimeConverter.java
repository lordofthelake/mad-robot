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

package com.madrobot.di.wizard.xml.converters;

import java.sql.Time;

/**
 * Converts a java.sql.Time to text. Warning: Any granularity smaller than seconds is lost.
 * 
 * @author Jose A. Illescas
 */
public class SqlTimeConverter extends AbstractSingleValueConverter {

	@Override
	public boolean canConvert(Class type) {
		return type.equals(Time.class);
	}

	@Override
	public Object fromString(String str) {
		return Time.valueOf(str);
	}

}
