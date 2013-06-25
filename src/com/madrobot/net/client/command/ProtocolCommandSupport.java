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

import java.io.Serializable;
import java.util.EventListener;

import com.madrobot.net.client.util.ListenerList;

/***
 * ProtocolCommandSupport is a convenience class for managing a list of
 * ProtocolCommandListeners and firing ProtocolCommandEvents.  You can
 * simply delegate ProtocolCommandEvent firing and listener
 * registering/unregistering tasks to this class.
 * <p>
 * <p>
 * @see ProtocolCommandEvent
 * @see ProtocolCommandListener
 * @see com.madrobot.net.client.POP3
 ***/
public class ProtocolCommandSupport implements Serializable
{
    /**
	 * Provide a brief description of serialVersionUID.
	 * Specify the purpose of this field.
	 *
	 */
	private static final long serialVersionUID = 8980780843518514989L;
	private final com.madrobot.net.client.util.ListenerList __listeners;
    private final Object __source;

    /***
     * Creates a ProtocolCommandSupport instant using the indicated source
     * as the source of fired ProtocolCommandEvents.
     * <p>
     * @param source  The source to use for all generated ProtocolCommandEvents.
     ***/
    public ProtocolCommandSupport(Object source)
    {
        __listeners = new ListenerList();
        __source = source;
    }


    /***
     * Adds a ProtocolCommandListener.
     * <p>
     * @param listener  The ProtocolCommandListener to add.
     ***/
    public void addProtocolCommandListener(ProtocolCommandListener listener)
    {
        __listeners.addListener(listener);
    }

    /***
     * Fires a ProtocolCommandEvent signalling the sending of a command to all
     * registered listeners, invoking their
     * {@link com.madrobot.net.client.command.ProtocolCommandListener#protocolCommandSent protocolCommandSent() }
     *  methods.
     * <p>
     * @param command The string representation of the command type sent, not
     *      including the arguments (e.g., "STAT" or "GET").
     * @param message The entire command string verbatim as sent to the server,
     *        including all arguments.
     ***/
    public void fireCommandSent(String command, String message)
    {
        ProtocolCommandEvent event;

        event = new ProtocolCommandEvent(__source, command, message);

        for (EventListener listener : __listeners)
        {
           ((ProtocolCommandListener)listener).protocolCommandSent(event);
        }
    }

    /***
     * Fires a ProtocolCommandEvent signalling the reception of a command reply
     * to all registered listeners, invoking their
     * {@link com.madrobot.net.client.command.ProtocolCommandListener#protocolReplyReceived protocolReplyReceived() }
     *  methods.
     * <p>
     * @param replyCode The integer code indicating the natureof the reply.
     *   This will be the protocol integer value for protocols
     *   that use integer reply codes, or the reply class constant
     *   corresponding to the reply for protocols like POP3 that use
     *   strings like OK rather than integer codes (i.e., POP3Repy.OK).
     * @param message The entire reply as received from the server.
     ***/
    public void fireReplyReceived(int replyCode, String message)
    {
        ProtocolCommandEvent event;
        event = new ProtocolCommandEvent(__source, replyCode, message);

        for (EventListener listener : __listeners)
        {
            ((ProtocolCommandListener)listener).protocolReplyReceived(event);
        }
    }

    /***
     * Returns the number of ProtocolCommandListeners currently registered.
     * <p>
     * @return The number of ProtocolCommandListeners currently registered.
     ***/
    public int getListenerCount()
    {
        return __listeners.getListenerCount();
    }


    /***
     * Removes a ProtocolCommandListener.
     * <p>
     * @param listener  The ProtocolCommandListener to remove.
     ***/
    public void removeProtocolCommandListener(ProtocolCommandListener listener)
    {
        __listeners.removeListener(listener);
    }

}

