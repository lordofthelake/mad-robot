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
package com.madrobot.io.file;

import java.io.File;

import android.os.StatFs;
import android.util.Log;

public class SDCardUtils {
	/**
	 * Check if the sdcard is writable
	 * 
	 * @return true if the card is writable.
	 */
	public static boolean canWrite() {
		return canWrite(0);
	}

	/**
	 * Checks if there is space to write on the SDcard
	 * 
	 * @param sizeToWrite
	 *            The size (in bytes) of the data to write.
	 * @return true if the size can be written
	 */
	public static boolean canWrite(long sizeToWrite) {
		/* check if the card is mounted */
		if (!isMounted()) {
			Log.e("MadRobot","SDcard is not mounted!");
			return false;
			/* check if the card is read only */
		}
		if (isReadOnly()) {
			Log.e("MadRobot","SDcard is readonly!");
			return false;
		}
		if (getFreeSpaceOnSDCard() < sizeToWrite) {
			Log.e("MadRobot","No Space on SDcard!");
			return false;
		}
		return true;
	}

	/**
	 * Get the SDcard directory
	 * 
	 * @return
	 */
	public static File getDirectory() {
		return android.os.Environment.getExternalStorageDirectory();
	}

	/**
	 * Get the free space on the SDcard
	 * 
	 * @return
	 */
	public static long getFreeSpaceOnSDCard() {
		StatFs cardStatistics = new StatFs(getDirectory().toString());
		long freeSpace = (long) cardStatistics.getBlockSize()
				* cardStatistics.getFreeBlocks();
		return freeSpace;
	}

	/**
	 * Check if the device has an SDcard.
	 * 
	 * @return
	 */
	public static boolean isMounted() {
		return android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED);
	}

	public static boolean isReadOnly() {
		return android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED_READ_ONLY);
	}

}
