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

/**
 * Converts a boolean primitive or java.lang.Boolean wrapper to a String.
 * 
 */
public class BooleanConverter extends AbstractSingleValueConverter {

	public static final BooleanConverter BINARY = new BooleanConverter("1", "0", true);

	public static final BooleanConverter TRUE_FALSE = new BooleanConverter("true", "false", false);

	public static final BooleanConverter YES_NO = new BooleanConverter("yes", "no", false);

	private final boolean caseSensitive;
	private final String negative;
	private final String positive;

	public BooleanConverter() {
		this("true", "false", false);
	}

	public BooleanConverter(final String positive, final String negative, final boolean caseSensitive) {
		this.positive = positive;
		this.negative = negative;
		this.caseSensitive = caseSensitive;
	}

	@Override
	public boolean canConvert(final Class type) {
		return type.equals(boolean.class) || type.equals(Boolean.class);
	}

	@Override
	public Object fromString(final String str) {
		if (caseSensitive) {
			return positive.equals(str) ? Boolean.TRUE : Boolean.FALSE;
		} else {
			return positive.equalsIgnoreCase(str) ? Boolean.TRUE : Boolean.FALSE;
		}
	}

	public boolean shouldConvert(final Class type, final Object value) {
		return true;
	}

	@Override
	public String toString(final Object obj) {
		final Boolean value = (Boolean) obj;
		return obj == null ? null : value.booleanValue() ? positive : negative;
	}
}
