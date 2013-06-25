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
 * A NameCoder that does nothing.
 * <p>
 * The usage of this implementation implies that the names used for the objects can also be used in the target format
 * without any change. This applies also for XML if the object graph contains no object that is an instance of an inner
 * class type or is in the default package.
 * </p>
 * 
 * @since 1.4
 */
public class NoNameCoder implements NameCoder {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String decodeAttribute(String attributeName) {
		return attributeName;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String decodeNode(String nodeName) {
		return nodeName;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String encodeAttribute(String name) {
		return name;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String encodeNode(String name) {
		return name;
	}

}
