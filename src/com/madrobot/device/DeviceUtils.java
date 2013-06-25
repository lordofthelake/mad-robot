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
package com.madrobot.device;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.content.res.Configuration;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

/**
 * Utility to obtain all device related information
 * <p>
 * </p>
 * 
 */
public class DeviceUtils {
	private static final String DF_COMMAND = "df";

	private static final CharSequence FLASH_FILE_SYSTEM = "yaffs";
	private static final String KERNEL_FORMAT_REGEXP = "\\w+\\s+" + /*
																	 * ignore:
																	 * Linux
																	 */
	"\\w+\\s+" + /* ignore: version */
	"([^\\s]+)\\s+" + /* group 1: 2.6.22-omap1 */
	"\\(([^\\s@]+(?:@[^\\s.]+)?)[^)]*\\)\\s+" + /*
												 * group 2:
												 * (xxxxxx@xxxxx.constant)
												 */
	"\\((?:[^(]*\\([^)]*\\))?[^)]*\\)\\s+" + /* ignore: (gcc ..) */
	"([^\\s]+)\\s+" + /* group 3: #26 */
	"(?:PREEMPT\\s+)?" + /* ignore: PREEMPT (optional) */
	"(.+)"; /* group 4: date */
	private static final String KERNEL_SOURCE = "/proc/version";
	private static final String MANUFACTURER_FIELD = "MANUFACTURER";
	private static final String MEM_INFO_FILE = "/proc/meminfo";
	private static final String MEMORY_METRIC = "K";
	private static final String MOUNT_COMMAND = "mount";
	public static final String TAG = "DeviceUtils";

	private static final CharSequence UBI_FILE_SYSTEM = "ubifs";

	private static String formatKernelVersion(String raw) {
		if (raw != null && raw.length() > 0) {
			try {
				Pattern p = Pattern.compile(KERNEL_FORMAT_REGEXP);
				Matcher m = p.matcher(raw);
				if (!m.matches()) {
					Log.d(TAG, "Regex did not match on /proc/version: " + raw);
					return raw;
				} else if (m.groupCount() < 4) {
					Log.d(TAG, "Regex returned only " + m.groupCount()
							+ "groups");
					return raw;
				} else {
					return (new StringBuilder(m.group(1)).append("\n")
							.append(m.group(2)).append(" ").append(m.group(3))
							.append("\n").append(m.group(4))).toString();
				}
			} catch (Throwable t) {
				Log.e(TAG,
						"Error formatting raw kernel version: "
								+ t.getMessage(), t);
			}
		}
		return raw;
	}

	public static Display[] getAllDisplays(Context context) {
		WindowManager winMgr = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		Display[] displays = new Display[1];
		displays[0] = winMgr.getDefaultDisplay();
		return displays;
	}

	public static float getCpuClockSpeed() {
		float cpuclock = 0;
		try {
			final StringBuffer s = new StringBuffer();
			final Process p = Runtime.getRuntime().exec("cat /proc/cpuinfo");
			final BufferedReader input = new BufferedReader(
					new InputStreamReader(p.getInputStream()));
			String line = "";
			while ((line = input.readLine()) != null
					&& s.toString().length() == 0) {
				if (line.startsWith("BogoMIPS")) {
					s.append(line + "\n");
				}
			}
			final String cpuclockstr = s.substring(s.indexOf(":") + 2,
					s.length());
			cpuclock = Float.parseFloat(cpuclockstr);
		} catch (final Exception err) {
			// if ANYTHING goes wrong, just report 0 since this is only used for
			// performance appraisal.
		}
		return cpuclock;
	}

	public static DeviceInfo getDeviceInfo(Context context) {
		DeviceInfo info = new DeviceInfo();
		info.setFirmwareVersion(android.os.Build.VERSION.RELEASE);
		String kernelRawVersion = readKernelVersionRaw();
		if (kernelRawVersion != null) {
			info.setKernelVersion(formatKernelVersion(kernelRawVersion));
		}
		info.setManufacturer(Build.MANUFACTURER);
		info.setDeviceModel(Build.MODEL);
		info.setDeviceBrand(Build.BRAND);
		return info;
	}

	/**
	 * Get the DisplayInfo for the given display
	 * 
	 * @see DisplayInfo
	 * @param display
	 * @param context
	 * @return
	 */
	public static DisplayInfo getDisplayInfo(Display display, Context context) {
		DisplayMetrics outMetrics = new DisplayMetrics();
		display.getMetrics(outMetrics);
		DisplayInfo info = new DisplayInfo();
		double diagonalSizeInInch = Math.sqrt(Math.pow(outMetrics.widthPixels
				/ outMetrics.xdpi, 2)
				+ Math.pow(outMetrics.heightPixels / outMetrics.ydpi, 2));
		info.setDiagonalSizeInInch(diagonalSizeInInch);
		info.setWidthInPixels(outMetrics.widthPixels);
		info.setHeightInPixels(outMetrics.heightPixels);
		info.setLogicalDensity(outMetrics.density);
		info.setLogicalDPI(outMetrics.densityDpi);
		info.setScaledDensity(outMetrics.scaledDensity);
		info.setHorizontalDensity(outMetrics.xdpi);
		info.setVerticalDensity(outMetrics.ydpi);
		Configuration c = context.getResources().getConfiguration();
		int touchMethod = c.touchscreen;
		if (touchMethod == Configuration.TOUCHSCREEN_UNDEFINED
				|| touchMethod == Configuration.TOUCHSCREEN_NOTOUCH)
			info.setTouchEnabled(false);
		else
			info.setTouchEnabled(true);
		info.setRefreshRate(display.getRefreshRate());

		float aspectRatio = outMetrics.widthPixels / outMetrics.heightPixels;
		if (aspectRatio == 0.60037524f) {
			info.setScreenType(DisplayInfo.SCREENTYPE_WIDE);
		}
		if (aspectRatio < 0.60037524f) {
			info.setScreenType(DisplayInfo.SCREENTYPE_NORMAL);
		}
		if (aspectRatio > 2) {
			info.setScreenType(DisplayInfo.SCREENTYPE_FULL_WIDTH);
		}

		return info;
	}

	public static String getHardwareName() {
		String hardwarenamestr = "{}";
		try {
			final StringBuffer s = new StringBuffer();
			final Process p = Runtime.getRuntime().exec("cat /proc/cpuinfo");
			final BufferedReader input = new BufferedReader(
					new InputStreamReader(p.getInputStream()));
			String line = "";
			while ((line = input.readLine()) != null
					&& s.toString().length() == 0) {
				if (line.startsWith("Hardware")) {
					s.append(line + "\n");
				}
			}
			hardwarenamestr = s.substring(s.indexOf(":") + 2, s.length());
		} catch (final Exception err) {
			// if ANYTHING goes wrong, just report 0 since this is only used for
			// performance appraisal.
		}
		return hardwarenamestr;
	}

	/**
	 * 
	 * 
	 * @return
	 */
	public static int getInternalStorageSize() {
		String cmd = MOUNT_COMMAND;
		Runtime run = Runtime.getRuntime();
		Process pr = null;
		/* Parsing Mount Points */
		try {
			pr = run.exec(cmd);
		} catch (IOException e) {
			return 0;
		}
		try {
			if (pr != null) {
				pr.waitFor();
			}
		} catch (InterruptedException e) {
			return 0;
		}
		BufferedReader buf = null;

		String line = null;
		ArrayList<String> mountPts = new ArrayList<String>(3);
		try {
			buf = new BufferedReader(new InputStreamReader(pr.getInputStream()));
			while ((line = buf.readLine()) != null) {
				if (line.contains(FLASH_FILE_SYSTEM)
						|| line.contains(UBI_FILE_SYSTEM)) {
					StringTokenizer token = new StringTokenizer(line, " ");
					while (token.hasMoreElements()) {
						String mntPoint = token.nextToken();
						if (mntPoint != null && mntPoint.length() > 0
								&& !mntPoint.startsWith("/dev/block")
								&& mntPoint.startsWith("/")) {
							mountPts.add(mntPoint);
							break;
						}
					}
				}
			}
		} catch (IOException e) {
			Log.e(TAG, "Could not parse mount points!", e);
		} finally {
			if (buf != null)
				try {
					buf.close();
				} catch (IOException e) {
				}
		}

		/* Parsing Mount Point sizes */
		cmd = DF_COMMAND;
		try {
			pr = run.exec(cmd);
		} catch (IOException e) {
			Log.e(TAG, "Could not execute command : " + cmd, e);
			return 0;
		}
		try {
			if (pr != null) {
				pr.waitFor();
			}
		} catch (InterruptedException e) {
			Log.e(TAG, "Could not complete command : " + cmd, e);
			return 0;
		}
		ArrayList<String> mountPtsSizes = new ArrayList<String>(mountPts.size());
		try {
			buf = new BufferedReader(new InputStreamReader(pr.getInputStream()));
			while ((line = buf.readLine()) != null) {
				for (String mntPnt : mountPts) {
					boolean found = false;
					if (line.contains(mntPnt)) {
						found = true;
						mountPts.remove(mntPnt);
						StringTokenizer token = new StringTokenizer(line, " ");
						token.nextToken();

						while (token.hasMoreElements()) {
							String size = token.nextToken();

							if (size != null && size.length() > 0) {
								if (size.endsWith("K")) {
									size = size.substring(0, size.length() - 1);
								}
								mountPtsSizes.add(size);
								break;
							}
						}
						if (found)
							break;
					}
				}
				if (mountPts.size() == 0) {
					break;
				}
			}
		} catch (IOException e) {
			Log.e(TAG, "Could not parse mount point sizes!", e);
		} finally {
			if (buf != null)
				try {
					buf.close();
				} catch (IOException e) {
				}
		}

		/* Summing mount point sizes */
		if (mountPtsSizes != null && mountPtsSizes.size() > 0) {
			Integer fullSize = new Integer(0);
			for (String size : mountPtsSizes) {
				fullSize += Integer.parseInt(size);
			}
			Log.d(TAG, "Fullsize: " + fullSize + MEMORY_METRIC);
			return fullSize;
		}
		return 0;

	}

	/**
	 * 
	 * @return
	 * @throws IOException
	 */
	public static InputStream getLogcatLogs() throws IOException{
		ProcessBuilder builder = new ProcessBuilder("logcat", "-d");
		builder.redirectErrorStream(true);
		Process process = builder.start();
		//process.waitFor();
		return process.getInputStream();
	}

	/**
	 * 
	 * Get network related information
	 * 
	 * @param context
	 * @return
	 * @see NetworkInfo
	 */
	public static NetworkInfo getNetworkInfo(Context context) {
		TelephonyManager teleManager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		if (teleManager != null) {
			NetworkInfo info = new NetworkInfo();
			info.setDataState(teleManager.getDataState());
			info.setNetworkType(teleManager.getNetworkType());
			info.setOperatorName(teleManager.getNetworkOperatorName());
			info.setRoaming(teleManager.isNetworkRoaming());
			info.setVoicemailNumber(teleManager.getVoiceMailNumber());
			info.setPhoneType(teleManager.getPhoneType());
			info.setHasTelephoneSupport(teleManager.getDeviceId()!=null);
		}
		return null;

	}

	/**
	 * Usses the <code>meminfo</code> command to get the exact available RAM
	 * 
	 * @return Available RAM size. 0 if the command failed.
	 * @throws IOException
	 */
	public static int getRam() throws IOException {
		String ram = null;
		BufferedReader in = null;
		in = new BufferedReader(new FileReader(MEM_INFO_FILE));
		String str;
		while ((str = in.readLine()) != null) {
			if (str.startsWith("MemTotal:")) {
				ram = str;
				break;
			}
		}
		in.close();
		if (ram != null && ram.length() > 0) {
			StringTokenizer token = new StringTokenizer(ram, ":");
			while (token.hasMoreElements()) {
				ram = (String) token.nextElement();
			}
			ram = ram.trim();
		}
		Integer ramValue = null;
		try {
			ramValue = Integer.parseInt(ram.substring(0, ram.length() - 3));
		} catch (Exception e) {
			return 0;
		}

		if (ramValue > 0) {
			return ramValue;
		}
		return 0;
	}

	public static SensorInfo getSensorInfo(Context context) {
		SensorManager sensorMgr = (SensorManager) context
				.getSystemService(Context.SENSOR_SERVICE);
		SensorInfo info = new SensorInfo();
		info.setHasAccelerometer(!sensorMgr.getSensorList(
				Sensor.TYPE_ACCELEROMETER).isEmpty());
		info.setHasGyroscope(!sensorMgr.getSensorList(Sensor.TYPE_GYROSCOPE)
				.isEmpty());
		info.setHasLightSensor(!sensorMgr.getSensorList(Sensor.TYPE_LIGHT)
				.isEmpty());
		info.setHasMagneticSensor(!sensorMgr.getSensorList(
				Sensor.TYPE_MAGNETIC_FIELD).isEmpty());
		info.setHasOrientationSensor(!sensorMgr.getSensorList(
				Sensor.TYPE_ORIENTATION).isEmpty());
		info.setHasPressureSensor(!sensorMgr
				.getSensorList(Sensor.TYPE_PRESSURE).isEmpty());
		info.setHasProximitySensor(!sensorMgr.getSensorList(
				Sensor.TYPE_PROXIMITY).isEmpty());
		info.setHasTemperatureSensor(!sensorMgr.getSensorList(
				Sensor.TYPE_TEMPERATURE).isEmpty());
		return info;
	}

	private static String readKernelVersionRaw() {
		String kernelVersionRaw = null;
		try {
			BufferedReader bReader = new BufferedReader(new FileReader(
					KERNEL_SOURCE), 256);
			try {
				kernelVersionRaw = bReader.readLine();
			} finally {
				bReader.close();
			}
		} catch (Throwable t) {
		}
		return kernelVersionRaw;
	}
	
	private DeviceUtils() {

	}

}
