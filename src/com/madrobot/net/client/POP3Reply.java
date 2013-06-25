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
 * POP3Reply stores POP3 reply code constants.
 * <p>
 * <p>
 ***/

public final class POP3Reply
{
    /*** The reply code indicating failure of an operation. ***/
    public static final int ERROR = 1;

    /*** The reply code indicating success of an operation. ***/
    public static final int OK = 0;

    // Cannot be instantiated.
    private POP3Reply()
    {}
}
