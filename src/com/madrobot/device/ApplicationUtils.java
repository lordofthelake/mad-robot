package com.madrobot.device;

import java.io.File;

import android.content.Context;

public final class ApplicationUtils {

	/**
	 * Get the Dex
	 * 
	 * @param context
	 * @return
	 */
	public File getDexFileDirectory(Context context) {
		return context.getDir("dex", 0);
	}
	
	
}
