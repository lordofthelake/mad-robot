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

public class XMLRPCFault extends XMLRPCException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5676562456612956519L;
	private int faultCode;
	private String faultString;

	public XMLRPCFault(String faultString, int faultCode) {
		super("XMLRPC Fault: " + faultString + " [code " + faultCode + "]");
		this.faultString = faultString;
		this.faultCode = faultCode;
	}
	
	public int getFaultCode() {
		return faultCode;
	}
	
	public String getFaultString() {
		return faultString;
	}
}
