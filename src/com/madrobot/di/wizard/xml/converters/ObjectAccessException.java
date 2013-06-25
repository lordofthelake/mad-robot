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

package com.madrobot.di.wizard.xml.converters;

import com.madrobot.di.wizard.xml.XMLWizardException;

public class ObjectAccessException extends XMLWizardException {
	public ObjectAccessException(String message) {
		super(message);
	}

	public ObjectAccessException(String message, Throwable cause) {
		super(message, cause);
	}
}
