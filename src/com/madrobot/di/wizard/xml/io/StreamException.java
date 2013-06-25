/*******************************************************************************
 * Copyright (c) 2012 MadRobot.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the GNU Lesser Public License v2.1
 *  which accompanies this distribution, and is available at
 *  http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 *  
 *  Contributors:
 *  Elton Kent - initial API and implementation
 ******************************************************************************/

package com.madrobot.di.wizard.xml.io;

import com.madrobot.di.wizard.xml.XMLWizardException;

public class StreamException extends XMLWizardException {
	public StreamException(String message) {
		super(message);
	}

	/**
	 * @since 1.4
	 */
	public StreamException(String message, Throwable cause) {
		super(message, cause);
	}

	public StreamException(Throwable e) {
		super(e);
	}
}
