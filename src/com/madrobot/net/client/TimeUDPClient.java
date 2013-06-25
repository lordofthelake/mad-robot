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
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.Date;

/***
 * The TimeUDPClient class is a UDP implementation of a client for the
 * Time protocol described in RFC 868.  To use the class, merely
 * open a local datagram socket with
 * {@link com.madrobot.net.client.DatagramSocketClient#open  open }
 * and call {@link #getTime  getTime } or
 * {@link #getTime  getDate } to retrieve the time. Then call
 * {@link com.madrobot.net.client.DatagramSocketClient#close  close }
 * to close the connection properly.  Unlike
 * {@link com.madrobot.io.net.client.time.TimeTCPClient},
 * successive calls to {@link #getTime  getTime } or
 * {@link #getDate  getDate } are permitted
 * without re-establishing a connection.  That is because UDP is a
 * connectionless protocol and the Time protocol is stateless.
 * <p>
 * <p>
 * @see TimeTCPClient
 ***/

public final class TimeUDPClient extends DatagramSocketClient
{
    /*** The default time port.  It is set to 37 according to RFC 868. ***/
    public static final int DEFAULT_PORT = 37;

    /***
     * The number of seconds between 00:00 1 January 1900 and
     * 00:00 1 January 1970.  This value can be useful for converting
     * time values to other formats.
     ***/
    public static final long SECONDS_1900_TO_1970 = 2208988800L;

    private final byte[] __dummyData = new byte[1];
    private final byte[] __timeData = new byte[4];

    /*** Same as <code> getTime(host, DEFAULT_PORT); </code> ***/
    public Date getDate(InetAddress host) throws IOException
    {
        return new Date((getTime(host, DEFAULT_PORT) -
                         SECONDS_1900_TO_1970)*1000L);
    }

    /***
     * Retrieves the time from the server and returns a Java Date
     * containing the time converted to the local timezone.
     * <p>
     * @param host The address of the server.
     * @param port The port of the service.
     * @return A Date value containing the time retrieved from the server
     *     converted to the local timezone.
     * @exception IOException  If an error occurs while fetching the time.
     ***/
    public Date getDate(InetAddress host, int port) throws IOException
    {
        return new Date((getTime(host, port) - SECONDS_1900_TO_1970)*1000L);
    }


    /*** Same as <code> getTime(host, DEFAULT_PORT); </code> ***/
    public long getTime(InetAddress host) throws IOException
    {
        return getTime(host, DEFAULT_PORT);
    }


    /***
     * Retrieves the time from the specified server and port and
     * returns it. The time is the number of seconds since
     * 00:00 (midnight) 1 January 1900 GMT, as specified by RFC 868.
     * This method reads the raw 32-bit big-endian
     * unsigned integer from the server, converts it to a Java long, and
     * returns the value.
     * <p>
     * @param host The address of the server.
     * @param port The port of the service.
     * @return The time value retrieved from the server.
     * @exception IOException If an error occurs while retrieving the time.
     ***/
    public long getTime(InetAddress host, int port) throws IOException
    {
        long time;
        DatagramPacket sendPacket, receivePacket;

        sendPacket =
            new DatagramPacket(__dummyData, __dummyData.length, host, port);
        receivePacket = new DatagramPacket(__timeData, __timeData.length);

        _socket_.send(sendPacket);
        _socket_.receive(receivePacket);

        time = 0L;
        time |= (((__timeData[0] & 0xff) << 24) & 0xffffffffL);
        time |= (((__timeData[1] & 0xff) << 16) & 0xffffffffL);
        time |= (((__timeData[2] & 0xff) << 8) & 0xffffffffL);
        time |= ((__timeData[3] & 0xff) & 0xffffffffL);

        return time;
    }

}

