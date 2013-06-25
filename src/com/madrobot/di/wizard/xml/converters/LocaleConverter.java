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

import java.util.Locale;

/**
 * Converts a java.util.Locale to a string.
 * 
 * @author Jose A. Illescas
 * @author Joe Walnes
 */
public class LocaleConverter extends AbstractSingleValueConverter {

	@Override
	public boolean canConvert(Class type) {
		return type.equals(Locale.class);
	}

	@Override
	public Object fromString(String str) {
		int[] underscorePositions = underscorePositions(str);
		String language, country, variant;
		if (underscorePositions[0] == -1) { // "language"
			language = str;
			country = "";
			variant = "";
		} else if (underscorePositions[1] == -1) { // "language_country"
			language = str.substring(0, underscorePositions[0]);
			country = str.substring(underscorePositions[0] + 1);
			variant = "";
		} else { // "language_country_variant"
			language = str.substring(0, underscorePositions[0]);
			country = str.substring(underscorePositions[0] + 1, underscorePositions[1]);
			variant = str.substring(underscorePositions[1] + 1);
		}
		return new Locale(language, country, variant);
	}

	private int[] underscorePositions(String in) {
		int[] result = new int[2];
		for (int i = 0; i < result.length; i++) {
			int last = i == 0 ? 0 : result[i - 1];
			result[i] = in.indexOf('_', last + 1);
		}
		return result;
	}

}
