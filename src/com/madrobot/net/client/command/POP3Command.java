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
package com.madrobot.net.client.command;


/***
 * POP3Command stores POP3 command code constants.
 * <p>
 * <p>
 * @see com.madrobot.net.client.POP3Client
 ***/

public final class POP3Command {
	static final String[] _commands = { "USER", "PASS", "QUIT", "STAT", "LIST", "RETR", "DELE", "NOOP",
			"RSET", "APOP", "TOP", "UIDL" };
	/*** Authorization. ***/
	public static final int APOP = 9;
	/*** Delete message(s). ***/
	public static final int DELE = 6;
	/*** List message(s). ***/
	public static final int LIST = 4;
	/*** No operation. Used as a session keepalive. ***/
	public static final int NOOP = 7;
	/*** Send password. ***/
	public static final int PASS = 1;
	/*** Quit session. ***/
	public static final int QUIT = 2;
	/*** Retrieve message(s). ***/
	public static final int RETR = 5;
	/*** Reset session. ***/
	public static final int RSET = 8;
	/*** Get status. ***/
	public static final int STAT = 3;
	/*** Retrieve top number lines from message. ***/
	public static final int TOP = 10;
	/*** List unique message identifier(s). ***/
	public static final int UIDL = 11;

	/*** Send user name. ***/
	public static final int USER = 0;

	/***
	 * Get the POP3 protocol string command corresponding to a command code.
	 * <p>
	 * 
	 * @return The POP3 protocol string command corresponding to a command code.
	 ***/
	public static final String getCommand(int command) {
		return _commands[command];
	}

	// Cannot be instantiated.
	private POP3Command() {
	}
}
