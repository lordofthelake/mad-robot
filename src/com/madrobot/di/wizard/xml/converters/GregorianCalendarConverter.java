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

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import com.madrobot.di.wizard.xml.io.ExtendedHierarchicalStreamWriterHelper;
import com.madrobot.di.wizard.xml.io.HierarchicalStreamReader;
import com.madrobot.di.wizard.xml.io.HierarchicalStreamWriter;

/**
 * Converts a java.util.GregorianCalendar to XML. Note that although it currently only contains one field, it nests it
 * inside a child element, to allow for other fields to be stored in the future.
 */
public class GregorianCalendarConverter implements Converter {

	@Override
	public boolean canConvert(Class type) {
		return type.equals(GregorianCalendar.class);
	}

	@Override
	public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
		GregorianCalendar calendar = (GregorianCalendar) source;
		ExtendedHierarchicalStreamWriterHelper.startNode(writer, "time", long.class);
		long timeInMillis = calendar.getTime().getTime(); // calendar.getTimeInMillis() not available under JDK 1.3
		writer.setValue(String.valueOf(timeInMillis));
		writer.endNode();
		ExtendedHierarchicalStreamWriterHelper.startNode(writer, "timezone", String.class);
		writer.setValue(calendar.getTimeZone().getID());
		writer.endNode();
	}

	@Override
	public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
		reader.moveDown();
		long timeInMillis = Long.parseLong(reader.getValue());
		reader.moveUp();
		final String timeZone;
		if (reader.hasMoreChildren()) {
			reader.moveDown();
			timeZone = reader.getValue();
			reader.moveUp();
		} else { // backward compatibility to XStream 1.1.2 and below
			timeZone = TimeZone.getDefault().getID();
		}

		GregorianCalendar result = new GregorianCalendar();
		result.setTimeZone(TimeZone.getTimeZone(timeZone));
		result.setTime(new Date(timeInMillis)); // calendar.setTimeInMillis() not available under JDK 1.3

		return result;
	}

}
