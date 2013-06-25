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

import java.io.InputStream;

/***
 * The EchoTCPClient class is a TCP implementation of a client for the
 * Echo protocol described in RFC 862.  To use the class, merely
 * establish a connection with
 * {@link com.madrobot.net.client.SocketClient#connect  connect }
 * and call {@link DiscardTCPClient#getOutputStream  getOutputStream() } to
 * retrieve the echo output stream and
 * {@link #getInputStream getInputStream() }
 *  to get the echo input stream.
 * Don't close either stream when you're done using them.  Rather, call
 * {@link com.madrobot.net.client.SocketClient#disconnect  disconnect }
 * to clean up properly.
 * <p>
 * <p>
 * @see EchoUDPClient
 * @see DiscardTCPClient
 ***/

public final class EchoTCPClient extends DiscardTCPClient
{
    /*** The default echo port.  It is set to 7 according to RFC 862. ***/
    public static final int DEFAULT_PORT = 7;

    /***
     * The default EchoTCPClient constructor.  It merely sets the default
     * port to <code> DEFAULT_PORT </code>.
     ***/
    public EchoTCPClient ()
    {
        setDefaultPort(DEFAULT_PORT);
    }

    /***
     * Returns an InputStream from which you may read echoed data from
     * the server.  You should NOT close the InputStream when you're finished
     * reading from it.  Rather, you should call
     * {@link com.madrobot.net.client.SocketClient#disconnect  disconnect }
     * to clean up properly.
     * <p>
     * @return An InputStream from which you can read echoed data from the
     *         server.
     ***/
    public InputStream getInputStream()
    {
        return _input_;
    }

}
