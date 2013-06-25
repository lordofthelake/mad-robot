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

/***
 * POP3MessageInfo is used to return information about messages stored on
 * a POP3 server. Its fields are used to mean slightly different things
 * depending on the information being returned.
 * <p>
 * In response to a status command, <code> number </code> contains the number of
 * messages in the mailbox, <code> size </code> contains the size of the mailbox
 * in bytes, and <code> identifier </code> is null.
 * <p>
 * In response to a message listings, <code> number </code> contains the message
 * number, <code> size </code> contains the size of the message in bytes, and
 * <code> identifier </code> is null.
 * <p>
 * In response to unique identifier listings, <code> number </code> contains the
 * message number, <code> size </code> is undefined, and
 * <code> identifier </code> contains the message's unique identifier.
 * <p>
 * <p>
 * 
 ***/

public final class POP3MessageInfo {
	public final String identifier;
	public final int number;
	public final int size;

	/***
	 * Creates a POP3MessageInfo instance with <code>number</code> and
	 * <code> size </code> set to 0, and <code>identifier</code> set to
	 * null.
	 ***/
	public POP3MessageInfo() {
		number = size = 0;
		identifier = null;
	}

	/***
	 * Creates a POP3MessageInfo instance with <code>number</code> set
	 * to <code> num </code>, <code> size </code> set to <code> octets </code>,
	 * and <code>identifier</code> set to null.
	 ***/
	public POP3MessageInfo(int num, int octets) {
		number = num;
		size = octets;
		identifier = null;
	}

	/***
	 * Creates a POP3MessageInfo instance with <code>number</code> set
	 * to <code> num </code>, <code> size </code> undefined,
	 * and <code>identifier</code> set to <code>uid</code>.
	 ***/
	public POP3MessageInfo(int num, String uid) {
		number = num;
		size = -1;
		identifier = uid;
	}
}
