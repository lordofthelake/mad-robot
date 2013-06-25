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

import java.util.ArrayList;

/**
 * XML-RPC method call
 *
 * @author Elton Kent
 */
public class MethodCall {

	private static final int TOPIC = 1;
	String methodName;
	ArrayList<Object> params = new ArrayList<Object>();
	
	public String getMethodName() { return methodName; }
	public ArrayList<Object> getParams() { return params; }

	public String getTopic() {
		return (String)params.get(TOPIC);
	}
	void setMethodName(String methodName) { this.methodName = methodName; }

	void setParams(ArrayList<Object> params) { this.params = params; }
}
