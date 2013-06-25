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

/**
 * @since 1.4.2
 */
public interface ExtendedHierarchicalStreamReader extends HierarchicalStreamReader {

	/**
	 * Peek the name of the next child. In situation where {@link #hasMoreChildren()} returns true, peek the tag name of
	 * the child.
	 * 
	 * @since 1.4.2
	 */
	String peekNextChild();
}
