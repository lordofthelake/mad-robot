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

import android.graphics.PointF;

/**
 * Utilities that operate on android's {@code PointF} class
 * 
 */
public class PointUtils {

	public static PointF add(PointF p1, PointF p2) {
		return new PointF(p1.x + p2.x, p1.y + p2.y);
	}

	public static float getDistanceBetween(PointF p1, PointF p2) {
		float deltaX = Math.abs(p1.x - p2.x);
		float deltaY = Math.abs(p1.y - p2.y);
		return (float) Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaY, 2));
	}

	public static PointF rotate(float x, float y, float angle) {
		float newX = (float) (Math.cos(angle) * x - Math.sin(angle) * y);
		float newY = (float) (Math.sin(angle) * x + Math.cos(angle) * y);
		return new PointF(newX, newY);
	}

	public static PointF rotate(float px, float py, float ox, float oy, float cos, float sin) {
		float x = px - ox;
		float y = py - oy;
		px = ((x * cos) - (y * sin));
		py = ((x * sin) + (y * cos));
		px += ox;
		py += oy;
		return new PointF(px, py);
	}

	/**
	 * Rotate the given point at the given angle
	 * 
	 * @param point
	 * @param angle
	 * @return
	 */
	public static PointF rotate(PointF point, float angle) {
		return rotate(point.x, point.y, angle);
	}

	/**
	 * Rotate a Point object around another Point
	 * 
	 * @param point
	 *            the point to rotate
	 * @param origin
	 *            the point around which to rotate
	 * @param angle
	 *            the angle (in degrees) of rotation
	 */
	public static PointF rotate(PointF point, PointF origin, float angle) {
		float cos = (float) Math.cos((Math.PI * angle) / 180f);
		float sin = (float) Math.cos((Math.PI * angle) / 180f);
		return rotate(point, origin, cos, sin);
	}

	/**
	 * Rotate a Point object around another Point object
	 * <p>
	 * The new coordinates are set in <code>point</code>
	 * </p>
	 * 
	 * @param point
	 *            the point to rotate
	 * @param origin
	 *            the point around which to rotate
	 * @param cos
	 *            the cosine of the rotation angle
	 * @param sin
	 *            the sine of the rotation angle
	 * @return The new coordinate of <code>point</code>
	 */
	public static PointF rotate(PointF point, PointF origin, float cos, float sin) {
		return rotate(point.x, point.y, origin.x, origin.y, cos, sin);
	}

	/*
	 * Subracts p's x and y values from this points x and y values
	 */
	public static PointF subtract(PointF p1, PointF p2) {
		return new PointF(p1.x - p2.x, p1.y - p2.y);
	}

	/**
	 * Translate a Point object on the X and Y axes.
	 * 
	 * @param point
	 *            the point to translate
	 * @param dx
	 *            the X-axis offset
	 * @param dy
	 *            the Y-axis offset
	 */
	public static void translate(PointF point, float dx, float dy) {
		point.x += dx;
		point.y += dy;
	}
	
}
