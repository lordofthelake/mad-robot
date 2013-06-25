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
package com.madrobot.media;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.regex.Pattern;

import android.content.Context;
import android.graphics.Point;
import android.hardware.Camera;
import android.os.IBinder;
import android.view.Display;
import android.view.WindowManager;

public final class CameraUtils {

	private static final Pattern COMMA_PATTERN = Pattern.compile(",");
	private static final Object iHardwareService;
	private static final Method setFlashEnabledMethod;

	static{
		iHardwareService = getHardwareService();
		setFlashEnabledMethod = getSetFlashEnabledMethod(iHardwareService);
	}

	public static void disableFlashlight() {
		setFlashlight(false);
	}

	public static void enableFlashlight() {
		setFlashlight(true);
	}

	private static Point findBestPreviewSizeValue(CharSequence previewSizeValueString,
			Point screenResolution) {
		int bestX = 0;
		int bestY = 0;
		int diff = Integer.MAX_VALUE;
		for(String previewSize : COMMA_PATTERN.split(previewSizeValueString)){

			previewSize = previewSize.trim();
			int dimPosition = previewSize.indexOf('x');
			if(dimPosition < 0){
				continue;
			}

			int newX;
			int newY;
			try{
				newX = Integer.parseInt(previewSize.substring(0, dimPosition));
				newY = Integer.parseInt(previewSize.substring(dimPosition + 1));
			} catch(NumberFormatException nfe){
				continue;
			}

			int newDiff = Math.abs(newX - screenResolution.x) + Math.abs(newY - screenResolution.y);
			if(newDiff == 0){
				bestX = newX;
				bestY = newY;
				break;
			} else if(newDiff < diff){
				bestX = newX;
				bestY = newY;
				diff = newDiff;
			}

		}

		if(bestX > 0 && bestY > 0){
			return new Point(bestX, bestY);
		}
		return null;
	}

	private static Point getCameraResolution(Camera.Parameters parameters, Point screenResolution) {

		String previewSizeValueString = parameters.get("preview-size-values");
		// saw this on Xperia
		if(previewSizeValueString == null){
			previewSizeValueString = parameters.get("preview-size-value");
		}

		Point cameraResolution = null;

		if(previewSizeValueString != null){
			cameraResolution = findBestPreviewSizeValue(previewSizeValueString, screenResolution);
		}

		if(cameraResolution == null){
			// Ensure that the camera resolution is a multiple of 8, as the
			// screen may not be.
			cameraResolution = new Point((screenResolution.x >> 3) << 3, (screenResolution.y >> 3) << 3);
		}

		return cameraResolution;
	}

	/**
	 * Get the resolution of the device camera relevant to the screen resolution
	 * 
	 * @param context
	 * @return
	 */
	public static Point getCameraResolution(Context context) {
		Camera cam = Camera.open();
		if(cam == null)
			return null;
		Camera.Parameters params = cam.getParameters();
		WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display display = manager.getDefaultDisplay();
		Point screenResolution = new Point(display.getWidth(), display.getHeight());
		Point cameraResolution = getCameraResolution(params, screenResolution);
		cam.release();
		cam = null;
		return cameraResolution;
	}

	private static Object getHardwareService() {
		Class<?> serviceManagerClass = maybeForName("android.os.ServiceManager");
		if(serviceManagerClass == null){
			return null;
		}

		Method getServiceMethod = maybeGetMethod(serviceManagerClass, "getService", String.class);
		if(getServiceMethod == null){
			return null;
		}

		Object hardwareService = invoke(getServiceMethod, null, "hardware");
		if(hardwareService == null){
			return null;
		}

		Class<?> iHardwareServiceStubClass = maybeForName("android.os.IHardwareService$Stub");
		if(iHardwareServiceStubClass == null){
			return null;
		}

		Method asInterfaceMethod = maybeGetMethod(iHardwareServiceStubClass, "asInterface", IBinder.class);
		if(asInterfaceMethod == null){
			return null;
		}

		return invoke(asInterfaceMethod, null, hardwareService);
	}

	private static Method getSetFlashEnabledMethod(Object iHardwareService) {
		if(iHardwareService == null){
			return null;
		}
		Class<?> proxyClass = iHardwareService.getClass();
		return maybeGetMethod(proxyClass, "setFlashlightEnabled", boolean.class);
	}

	private static Object invoke(Method method, Object instance, Object... args) {
		try{
			return method.invoke(instance, args);
		} catch(IllegalAccessException e){
			return null;
		} catch(InvocationTargetException e){
			return null;
		} catch(RuntimeException re){
			return null;
		}
	}

	private static Class<?> maybeForName(String name) {
		try{
			return Class.forName(name);
		} catch(ClassNotFoundException cnfe){
			// OK
			return null;
		} catch(RuntimeException re){
			return null;
		}
	}

	private static Method maybeGetMethod(Class<?> clazz, String name, Class<?>... argClasses) {
		try{
			return clazz.getMethod(name, argClasses);
		} catch(NoSuchMethodException nsme){
			// OK
			return null;
		} catch(RuntimeException re){
			return null;
		}
	}

	private static void setFlashlight(boolean active) {
		if(iHardwareService != null){
			invoke(setFlashEnabledMethod, iHardwareService, active);
		}
	}
}
