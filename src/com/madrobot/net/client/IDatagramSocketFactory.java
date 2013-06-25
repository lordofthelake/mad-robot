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

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

/***
 * The DatagramSocketFactory interface provides a means for the
 * programmer to control the creation of datagram sockets and
 * provide his own DatagramSocket implementations for use by all
 * classes derived from
 * {@link com.madrobot.net.client.DatagramSocketClient} .
 * This allows you to provide your own DatagramSocket implementations and
 * to perform security checks or browser capability requests before
 * creating a DatagramSocket.
 * <p>
 * <p>
 * 
 ***/

public interface IDatagramSocketFactory {

	/***
	 * Creates a DatagramSocket on the local host at the first available port.
	 * <p>
	 * 
	 * @exception SocketException
	 *                If the socket could not be created.
	 ***/
	public DatagramSocket createDatagramSocket() throws SocketException;

	/***
	 * Creates a DatagramSocket on the local host at a specified port.
	 * <p>
	 * 
	 * @param port
	 *            The port to use for the socket.
	 * @exception SocketException
	 *                If the socket could not be created.
	 ***/
	public DatagramSocket createDatagramSocket(int port) throws SocketException;

	/***
	 * Creates a DatagramSocket at the specified address on the local host
	 * at a specified port.
	 * <p>
	 * 
	 * @param port
	 *            The port to use for the socket.
	 * @param laddr
	 *            The local address to use.
	 * @exception SocketException
	 *                If the socket could not be created.
	 ***/
	public DatagramSocket createDatagramSocket(int port, InetAddress laddr) throws SocketException;
}
