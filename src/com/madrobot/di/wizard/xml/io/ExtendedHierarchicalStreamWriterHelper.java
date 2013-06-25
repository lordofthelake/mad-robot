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

package com.madrobot.di.wizard.xml.io;

public class ExtendedHierarchicalStreamWriterHelper {
	public static void startNode(HierarchicalStreamWriter writer, String name, Class clazz) {
		if (writer instanceof ExtendedHierarchicalStreamWriter) {
			((ExtendedHierarchicalStreamWriter) writer).startNode(name, clazz);
		} else {
			writer.startNode(name);
		}
	}
}
