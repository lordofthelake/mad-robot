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

import java.util.Currency;

/**
 * Converts a java.util.Currency to String. Despite the name of this class, it has nothing to do with converting
 * currencies between exchange rates! It makes sense in the context of XMLWizard.
 * 
 * @author Jose A. Illescas
 * @author Joe Walnes
 */
public class CurrencyConverter extends AbstractSingleValueConverter {

	@Override
	public boolean canConvert(Class type) {
		return type.equals(Currency.class);
	}

	@Override
	public Object fromString(String str) {
		return Currency.getInstance(str);
	}

}
