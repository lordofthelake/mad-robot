/*******************************************************************************
 * Copyright (c) 2011 MadRobot.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *  Elton Kent - initial API and implementation
 ******************************************************************************/
package com.madrobot.net.client;

public class XMLRPCException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7499675036625522379L;

	public XMLRPCException(Exception e) {
		super(e);
	}

	public XMLRPCException(String string) {
		super(string);
	}
}
