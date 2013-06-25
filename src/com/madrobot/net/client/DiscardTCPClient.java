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

import java.io.OutputStream;


/***
 * The DiscardTCPClient class is a TCP implementation of a client for the
 * Discard protocol described in RFC 863.  To use the class, merely
 * establish a connection with
 * {@link com.madrobot.io.net.client.net.SocketClient#connect  connect }
 * and call {@link #getOutputStream  getOutputStream() } to
 * retrieve the discard output stream.  Don't close the output stream
 * when you're done writing to it.  Rather, call
 * {@link com.madrobot.io.net.client.net.SocketClient#disconnect  disconnect }
 * to clean up properly.
 * <p>
 * <p>
 * @see DiscardUDPClient
 ***/

public class DiscardTCPClient extends SocketClient
{
    /*** The default discard port.  It is set to 9 according to RFC 863. ***/
    public static final int DEFAULT_PORT = 9;

    /***
     * The default DiscardTCPClient constructor.  It merely sets the default
     * port to <code> DEFAULT_PORT </code>.
     ***/
    public DiscardTCPClient ()
    {
        setDefaultPort(DEFAULT_PORT);
    }

    /***
     * Returns an OutputStream through which you may write data to the server.
     * You should NOT close the OutputStream when you're finished
     * reading from it.  Rather, you should call
     * {@link com.madrobot.io.net.client.net.SocketClient#disconnect  disconnect }
     * to clean up properly.
     * <p>
     * @return An OutputStream through which you can write data to the server.
     ***/
    public OutputStream getOutputStream()
    {
        return _output_;
    }
}
