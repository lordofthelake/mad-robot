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

import java.util.List;

import com.madrobot.di.wizard.xml.io.HierarchicalStreamWriter;

/**
 * A generic interface for all {@link HierarchicalStreamWriter} implementations generating a DOM.
 * 
 * @since 1.2.1
 */
public interface DocumentWriter extends HierarchicalStreamWriter {

	/**
	 * Retrieve a {@link List} with the top elements. In the standard use case this list will only contain a single
	 * element. Additional elements can only occur, if {@link HierarchicalStreamWriter#startNode(String)} of the
	 * implementing {@link HierarchicalStreamWriter} was called multiple times with an empty node stack. Such a
	 * situation occurs calling {@link com.madrobot.di.wizard.xml.XMLWizard#marshal(Object, HierarchicalStreamWriter)}
	 * multiple times directly.
	 * 
	 * @return a {@link List} with top nodes
	 * @since 1.2.1
	 */
	List getTopLevelNodes();
}
