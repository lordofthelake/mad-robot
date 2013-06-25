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

/**
 * Allows to pass any XMLRPCSerializable object as input parameter.
 * When implementing getSerializable() you should return 
 * one of XMLRPC primitive types (or another XMLRPCSerializable: be careful not going into
 * recursion by passing this object reference!)  
 */
public interface XMLRPCSerializable {
	
	/**
	 * Gets XMLRPC serialization object
	 * @return object to serialize This object is most likely one of XMLRPC primitive types,
	 * however you can return also another XMLRPCSerializable
	 */
	Object getSerializable();
}
