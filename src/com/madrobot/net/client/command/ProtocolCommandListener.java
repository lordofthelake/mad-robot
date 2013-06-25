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

import java.util.EventListener;

/***
 * There exists a large class of IETF protocols that work by sending an
 * ASCII text command and arguments to a server, and then receiving an
 * ASCII text reply. For debugging and other purposes, it is extremely
 * useful to log or keep track of the contents of the protocol messages.
 * The ProtocolCommandListener interface coupled with the
 * {@link ProtocolCommandEvent} class facilitate this process.
 * <p>
 * To receive ProtocolCommandEvents, you merely implement the
 * ProtocolCommandListener interface and register the class as a listener with a
 * ProtocolCommandEvent source.
 * <p>
 * <p>
 * 
 * @see ProtocolCommandEvent
 * @see ProtocolCommandSupport
 ***/

public interface ProtocolCommandListener extends EventListener {

	/***
	 * This method is invoked by a ProtocolCommandEvent source after
	 * sending a protocol command to a server.
	 * <p>
	 * 
	 * @param event
	 *            The ProtocolCommandEvent fired.
	 ***/
	public void protocolCommandSent(ProtocolCommandEvent event);

	/***
	 * This method is invoked by a ProtocolCommandEvent source after
	 * receiving a reply from a server.
	 * <p>
	 * 
	 * @param event
	 *            The ProtocolCommandEvent fired.
	 ***/
	public void protocolReplyReceived(ProtocolCommandEvent event);

}
