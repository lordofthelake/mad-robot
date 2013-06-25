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

/**
 * To aid debugging, some components expose themselves as ErrorReporter indicating that they can add information in case
 * of an error..
 * 
 * @author Joerg Schaible
 * 
 * @since 1.4
 */
public interface ErrorReporter {
	/**
	 * Append context information to an {@link ErrorWriter}.
	 * 
	 * @param errorWriter
	 *            the error writer
	 * @since 1.4
	 */
	void appendErrors(ErrorWriter errorWriter);
}
