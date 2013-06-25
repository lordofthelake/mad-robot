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

import android.util.Log;

public class LoggerStrategyDDMSLog extends ALogMethod implements LoggerStrategy {

	@Override
	void d(String tag, String message) {
		Log.d(tag, message);
	}

	@Override
	void e(String tag, String message) {
		Log.e(tag, message);
	}

	@Override
	void i(String tag, String message) {
		Log.i(tag, message);
	}

	@Override
	void shutdown() {
		// TODO Auto-generated method stub

	}

	@Override
	void v(String tag, String message) {
		Log.v(tag, message);
	}

	@Override
	void w(String tag, String message) {
		Log.w(tag, message);
	}

	@Override
	void write(int level, String tag, String message) {
		Log.println(level, tag, message);
	}

}
