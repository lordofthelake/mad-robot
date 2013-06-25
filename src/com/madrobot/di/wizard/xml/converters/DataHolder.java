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

import java.util.Iterator;

/**
 * Holds generic data, to be used as seen fit by the user.
 * 
 * @author Joe Walnes
 */
public interface DataHolder {

	Object get(Object key);

	Iterator keys();

	void put(Object key, Object value);

}
