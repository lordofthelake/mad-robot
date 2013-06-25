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
package com.madrobot.reflect;

/**
 * Exception thrown when a clone cannot be created. In contrast to
 * {@link CloneNotSupportedException} this is a {@link RuntimeException}.
 * 
 */
public class CloneFailedException extends RuntimeException {
    // ~ Static fields/initializers ---------------------------------------------

    private static final long serialVersionUID = 20091223L;

    // ~ Constructors -----------------------------------------------------------

    /**
     * Constructs a CloneFailedException.
     * 
     * @param message description of the exception
     * @since upcoming
     */
    public CloneFailedException(final String message) {
        super(message);
    }

    /**
     * Constructs a CloneFailedException.
     * 
     * @param message description of the exception
     * @param cause cause of the exception
     * @since upcoming
     */
    public CloneFailedException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a CloneFailedException.
     * 
     * @param cause cause of the exception
     * @since upcoming
     */
    public CloneFailedException(final Throwable cause) {
        super(cause);
    }
}
