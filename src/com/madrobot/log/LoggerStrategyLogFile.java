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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Date;

import android.os.Environment;
import android.util.Log;

import com.madrobot.io.file.SDCardUtils;

/**
 * Simple log file strategy.
 * <p>
 * Creates a simple log at the path mentioned in the initializer.<br/>
 * The SDCard write permission needs to be set for this logging strategy
 * </p>
 */
public class LoggerStrategyLogFile extends ALogMethod implements LoggerStrategy {
	private File currentLogFile;
	private final String[] LEVELS = { "Verbose", "Debug", "Info", "Warn", "Error" };
	private File logDirectory;
	private boolean noSdCard;
	protected FileWriter writer;

	/**
	 * Creates a new Log file strategy instance
	 * 
	 * @param directory
	 *            of the log file
	 * @param fileName
	 *            of the log file
	 * 
	 */
	public LoggerStrategyLogFile(String directory, String fileName) {
		logDirectory = new File(Environment.getExternalStorageDirectory(), directory);
		if(SDCardUtils.canWrite()){
			try{
				String fname = fileName;// "log_" + new Date().getTime() +
				// ".txt";
				if(logDirectory.isFile()){
					logDirectory.delete();
				}
				if(!logDirectory.exists()){
					logDirectory.mkdir();
				}

				currentLogFile = new File(logDirectory, fname);
				writer = new FileWriter(currentLogFile, true);
			} catch(IOException e){
				e.printStackTrace();
				Log.e("Logger", e.getMessage());
			}
		} else{
			noSdCard = true;

		}
	}

	@Override
	void d(String tag, String message) {
		write(Log.DEBUG, tag, message);
	}

	@Override
	void e(String tag, String message) {
		write(Log.ERROR, tag, message);
	}

	protected String formatDate(Date date) {
		return MessageFormat.format("{0,date} {0,time}", date);
	}

	@Override
	void i(String tag, String message) {
		write(Log.INFO, tag, message);
	}

	protected String levelToString(int level) {
		return LEVELS[level - 2];
	}

	/**
	 * Removes all other files at the given log directory except the current
	 * one.
	 * <p>
	 * Note: all files even if not a log file will be deleted from the given
	 * directory.
	 * </p>
	 */
	public void removeOldLogFiles() {
		// remove all files but the current file
		File[] allfiles = logDirectory.listFiles();
		for(int i = 0; i < allfiles.length; i++){
			if(!currentLogFile.equals(allfiles[i])){
				allfiles[i].delete();
			}
		}
	}

	@Override
	void shutdown() {
		Log.d("Logger", "Shutting down logger");
		if(!noSdCard){
			if(writer != null){
				try{
					writer.close();
					writer = null;
				} catch(IOException e){
					e.printStackTrace();
				}
			}

		}
	}

	@Override
	void v(String tag, String message) {
		write(Log.VERBOSE, tag, message);
	}

	@Override
	void w(String tag, String message) {
		write(Log.WARN, tag, message);
	}

	@Override
	void write(int level, String tag, String message) {
		writeImpl(level, tag, message);
	}

	private void writeImpl(int level, String tag, String message) {
		if(writer != null){
			try{
				writer.write("[" + formatDate(new Date()) + "] " + levelToString(level) + " [" + tag + "] "
						+ message + "\r\n");
				writer.flush();
			} catch(IOException e){
				e.printStackTrace();
			}
		}/* else { */
		Log.println(level, tag, message);
		// }
	}
}
