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
package com.madrobot.db;

/**
 */
public class DBException extends Exception {
	private static final long serialVersionUID = -1305233534054765602L;

	public DBException() {
		super();
	}

	public DBException(String detailMessage) {
		super(detailMessage);
	}

	public DBException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}

	public DBException(Throwable throwable) {
		super(throwable);
	}
}
