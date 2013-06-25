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

package com.madrobot.di.wizard.xml;

import com.madrobot.di.wizard.xml.converters.MarshallingContext;
import com.madrobot.di.wizard.xml.io.path.Path;

/**
 * A {@link MarshallingContext} that manages references.
 * 
 * @author J&ouml;rg Schaible
 * @since 1.4
 */
public interface ReferencingMarshallingContext extends MarshallingContext {

	/**
	 * Retrieve the current path.
	 * 
	 * @return the current path
	 * @since 1.4
	 * @deprecated As of 1.4.2
	 */
	@Deprecated
	Path currentPath();

	/**
	 * Request the reference key for the given item
	 * 
	 * @param item
	 *            the item to lookup
	 * @return the reference key or <code>null</code>
	 * @since 1.4
	 */
	Object lookupReference(Object item);

	/**
	 * Register an implicit element. This is typically some kind of collection. Note, that this object may not be
	 * referenced anywhere else in the object stream.
	 * 
	 * @param item
	 *            the object that is implicit
	 * @since 1.4
	 */
	void registerImplicit(Object item);

	/**
	 * Replace the currently marshalled item.
	 * 
	 * <p>
	 * <strong>Use this method only, if you know exactly what you do!</strong> It is a special solution for Serializable
	 * types that make usage of the writeReplace method where the replacing object itself is referenced.
	 * </p>
	 * 
	 * @param original
	 *            the original item to convert
	 * @param replacement
	 *            the replacement item that is converted instead
	 * @since 1.4
	 */
	void replace(Object original, Object replacement);
}
