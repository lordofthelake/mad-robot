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
 * DefaultDatagramSocketFactory implements the DatagramSocketFactory
 * interface by simply wrapping the java.net.DatagramSocket
 * constructors. It is the default DatagramSocketFactory used by
 * {@link com.madrobot.net.client.DatagramSocketClient} implementations.
 * <p>
 * <p>
 * 
 * @see IDatagramSocketFactory
 * @see DatagramSocketClient
 * @see DatagramSocketClient#setDatagramSocketFactory
 ***/

public class DefaultDatagramSocketFactory implements IDatagramSocketFactory {

	/***
	 * Creates a DatagramSocket on the local host at the first available port.
	 * <p>
	 * 
	 * @exception SocketException
	 *                If the socket could not be created.
	 ***/
	@Override
	public DatagramSocket createDatagramSocket() throws SocketException {
		return new DatagramSocket();
	}

	/***
	 * Creates a DatagramSocket on the local host at a specified port.
	 * <p>
	 * 
	 * @param port
	 *            The port to use for the socket.
	 * @exception SocketException
	 *                If the socket could not be created.
	 ***/
	@Override
	public DatagramSocket createDatagramSocket(int port) throws SocketException {
		return new DatagramSocket(port);
	}

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
	@Override
	public DatagramSocket createDatagramSocket(int port, InetAddress laddr) throws SocketException {
		return new DatagramSocket(port, laddr);
	}
}
