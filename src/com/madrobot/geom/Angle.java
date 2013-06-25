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
package com.madrobot.geom;

public class Angle {

	/**
	 * Get the distance between two angles
	 * 
	 * @param a
	 *            angle
	 * @param b
	 * @return The distance between angles a and b
	 */
	public static float angleDistance(float a, float b) {
		float diff = Math.abs(a - b);
		if(diff > Math.PI){
			diff = (float) (2 * Math.PI - diff);
		}

		return diff;
	}

	public static float convertToDegrees(float rad) {
		return (float) ((rad / (2 * Math.PI)) * 360);
	}

	public static float convertToRadians(float degrees) {
		return (float) ((degrees / 360) * 2 * Math.PI);
	}

	/**
	 * Finds if an angle lies between two other angles
	 *
	 * @param n angle to find
	 * @param a first angle
	 * @param b second angle
	 * @return true if <code>n</code> is between angle<code> a</code> and angle <code>b</code>
	 */
	public static boolean isAngleBetween(float n, float a, float b) {
		n = (360 + (n % 360)) % 360;
		a = (3600000 + a) % 360;
		b = (3600000 + b) % 360;

		if(a < b)
			return a <= n && n <= b;
		return 0 <= n && n <= b || a <= n && n < 360;
	}

	private float anglePercent;

	public Angle(float rad) {
		anglePercent = (float) (rad / (2 * Math.PI));
	}

	public Angle add(Angle a) {
		float newAngleRad = (float) ((anglePercent + a.anglePercent) * 2 * Math.PI);
		return new Angle(newAngleRad);
	}

	public float getDegrees() {
		return (anglePercent * 360);
	}

	public float getRadians() {
		return (float) (anglePercent * 2 * Math.PI);
	}

	public Angle subtract(Angle a) {
		float newAngleRad = (float) ((anglePercent - a.anglePercent) * 2 * Math.PI);
		return new Angle(newAngleRad);
	}

}
