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

package com.madrobot.di.wizard.xml.io.xml;

import com.madrobot.di.wizard.xml.io.HierarchicalStreamReader;

/**
 * A generic interface for all {@link HierarchicalStreamReader} implementations reading a DOM.
 * 
 * @since 1.2.1
 */
public interface DocumentReader extends HierarchicalStreamReader {

	/**
	 * Retrieve the current processed node of the DOM.
	 * 
	 * @return the current node
	 * @since 1.2.1
	 */
	public Object getCurrent();
}
