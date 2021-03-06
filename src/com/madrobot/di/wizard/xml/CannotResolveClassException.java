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

/**
 * Exception thrown if a mapper cannot locate the appropriate class for an element.
 * 
 * @since 1.2
 */
public class CannotResolveClassException extends XMLWizardException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6193525574710063951L;

	public CannotResolveClassException(String className) {
		super(className);
	}

	/**
	 * @since 1.4.2
	 */
	public CannotResolveClassException(String className, Throwable cause) {
		super(className, cause);
	}
}
