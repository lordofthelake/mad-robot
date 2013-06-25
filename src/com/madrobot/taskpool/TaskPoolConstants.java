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
package com.madrobot.taskpool;

/**
 * Task Pool Settings
 * <p>
 * If these settings are to be modified, It should be done before the very first
 * call to <code>TaskPoolManagerImpl.getTaskPool()</code>
 * 
 * @see TaskPoolManagerImpl
 *      </p>
 */
public class TaskPoolConstants {

	/**
	 * Total count of Task pool thread(s). Should be >1
	 */
	public static int MAX_THREADS_COUNT = 2;

	/**
	 * Initial queue size threshold limit. Should be >1
	 */
	public static int QUEUE_SIZE = 250;// 200;

}
