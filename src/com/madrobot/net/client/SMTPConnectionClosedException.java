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

import java.io.IOException;

/***
 * SMTPConnectionClosedException is used to indicate the premature or
 * unexpected closing of an SMTP connection resulting from a
 * {@link com.madrobot.net.client.SMTPReply#SERVICE_NOT_AVAILABLE
 * SMTPReply.SERVICE_NOT_AVAILABLE } response (SMTP reply code 421) to a
 * failed SMTP command. This exception is derived from IOException and
 * therefore may be caught either as an IOException or specifically as an
 * SMTPConnectionClosedException.
 * <p>
 * <p>
 * 
 * @see SMTP
 * @see SMTPClient
 ***/

public final class SMTPConnectionClosedException extends IOException {

	/**
	 * Provide a brief description of serialVersionUID.
	 * Specify the purpose of this field.
	 *
	 */
	private static final long serialVersionUID = -3552263612970518360L;

	/*** Constructs a SMTPConnectionClosedException with no message ***/
	public SMTPConnectionClosedException() {
		super();
	}

	/***
	 * Constructs a SMTPConnectionClosedException with a specified message.
	 * <p>
	 * 
	 * @param message
	 *            The message explaining the reason for the exception.
	 ***/
	public SMTPConnectionClosedException(String message) {
		super(message);
	}

}
