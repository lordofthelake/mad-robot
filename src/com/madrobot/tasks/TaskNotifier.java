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
package com.madrobot.tasks;

/**
 * Task Progress callback
 * <p>
 * </p>
 * 
 */
public interface TaskNotifier {
	/**
	 * Called when there is a change in the current carrier . WiFi/3G
	 * 
	 * @param bearerStatus
	 */
	public void onCarrierChanged(int bearerStatus);

	/**
	 * Called if there is an error in
	 * 
	 * @param response
	 */
	public void onError(Throwable t);

	/**
	 * Called when the task completed successfully
	 * 
	 * @param response
	 */
	public void onSuccess(DataResponse response);

	/**
	 * Called when the task is completed
	 */
	public void onTaskCompleted();

	/**
	 * Called when the task is started
	 */
	public void onTaskStarted();

}
