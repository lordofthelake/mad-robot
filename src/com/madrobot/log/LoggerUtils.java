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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class LoggerUtils {

	/**
	 * 
	 * @return Buffered reader instance, null if the operation failed.
	 * @throws IOException 
	 */
	public static BufferedReader getLogcatOutput() throws IOException {
		Process process = getLogcatProcess();
		if(process != null)
			return new BufferedReader(new InputStreamReader(process.getInputStream()), 1024);
		else
			return null;
	}

	/**
	 * Get the handle to the logcat process
	 * 
	 * @return
	 * @throws IOException 
	 */

	public static Process getLogcatProcess() throws IOException {
		return Runtime.getRuntime().exec(new String[] { "logcat" });

	}

}
