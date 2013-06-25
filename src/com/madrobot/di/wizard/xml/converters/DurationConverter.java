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

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;

/**
 * A Converter for the XML Schema datatype <a href="http://www.w3.org/TR/xmlschema-2/#duration">duration</a> and the
 * Java type {@link Duration}. The implementation uses a {@link DatatypeFactory} to create Duration objects. If no
 * factory is provided and the instantiation of the internal factory fails with a {@link DatatypeConfigurationException}
 * , the converter will not claim the responsibility for Duration objects.
 * 
 * @author John Kristian
 * @author J&ouml;rg Schaible
 * @since 1.3
 */
public class DurationConverter extends AbstractSingleValueConverter {
	private final DatatypeFactory factory;

	public DurationConverter() {
		this(new Object() {
			DatatypeFactory getFactory() {
				try {
					return DatatypeFactory.newInstance();
				} catch (final DatatypeConfigurationException e) {
					return null;
				}
			}
		}.getFactory());
	}

	public DurationConverter(DatatypeFactory factory) {
		this.factory = factory;
	}

	@Override
	public boolean canConvert(Class c) {
		return factory != null && Duration.class.isAssignableFrom(c);
	}

	@Override
	public Object fromString(String s) {
		return factory.newDuration(s);
	}
}
