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
package com.madrobot.log;

abstract class ALogMethod {

	abstract void d(String tag, String message);

	abstract void e(String tag, String message);

	abstract void i(String tag, String message);

	abstract void shutdown();

	abstract void v(String tag, String message);

	abstract void w(String tag, String message);

	abstract void write(int level, String tag, String message);

}
