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

import java.io.File;

/**
 * This converter will take care of storing and retrieving File with either an absolute path OR a relative path
 * depending on how they were created.
 * 
 * @author Joe Walnes
 */
public class FileConverter extends AbstractSingleValueConverter {

	@Override
	public boolean canConvert(Class type) {
		return type.equals(File.class);
	}

	@Override
	public Object fromString(String str) {
		return new File(str);
	}

	@Override
	public String toString(Object obj) {
		return ((File) obj).getPath();
	}

}
