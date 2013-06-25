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

package com.madrobot.di.wizard.xml.core;

import java.util.Iterator;

/**
 * A key to a persistent storage and vice-versa strategy interface.
 * 
 */
public interface PersistenceStrategy {

	Object get(Object key);

	Iterator iterator();

	Object put(Object key, Object value);

	Object remove(Object key);

	int size();

}
