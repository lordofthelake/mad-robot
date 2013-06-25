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
 * Base exception for all thrown exceptions with XStream. JDK 1.3 friendly cause handling.
 * 
 * @since 1.3
 */
public class XMLWizardException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8815738099633985756L;
	private Throwable cause;

	/**
	 * Default constructor.
	 * 
	 * @since 1.3
	 */
	protected XMLWizardException() {
		this("", null);
	}

	/**
	 * Constructs an XStreamException with a message.
	 * 
	 * @param message
	 * @since 1.3
	 */
	public XMLWizardException(String message) {
		this(message, null);
	}

	/**
	 * Constructs an XStreamException with a message as wrapper for a different causing {@link Throwable}.
	 * 
	 * @param message
	 * @param cause
	 * @since 1.3
	 */
	public XMLWizardException(String message, Throwable cause) {
		super(message + (cause == null ? "" : " : " + cause.getMessage()));
		this.cause = cause;
	}

	/**
	 * Constructs an XStreamException as wrapper for a different causing {@link Throwable}.
	 * 
	 * @param cause
	 * @since 1.3
	 */
	public XMLWizardException(Throwable cause) {
		this("", cause);
	}

	@Override
	public Throwable getCause() {
		return cause;
	}

}
