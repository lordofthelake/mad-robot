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
 * This exception is used to indicate that the reply from a server
 * could not be interpreted.  Most of the NetComponents classes attempt
 * to be as lenient as possible when receiving server replies.  Many
 * server implementations deviate from IETF protocol specifications, making
 * it necessary to be as flexible as possible.  However, there will be
 * certain situations where it is not possible to continue an operation
 * because the server reply could not be interpreted in a meaningful manner.
 * In these cases, a MalformedServerReplyException should be thrown.
 * <p>
 * <p>
 ***/

public class MalformedServerReplyException extends IOException
{

    /**
	 * Provide a brief description of serialVersionUID.
	 * Specify the purpose of this field.
	 *
	 */
	private static final long serialVersionUID = 7623512792287443981L;

	/*** Constructs a MalformedServerReplyException with no message ***/
    public MalformedServerReplyException()
    {
        super();
    }

    /***
     * Constructs a MalformedServerReplyException with a specified message.
     * <p>
     * @param message  The message explaining the reason for the exception.
     ***/
    public MalformedServerReplyException(String message)
    {
        super(message);
    }

}
