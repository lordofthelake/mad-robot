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

public class PolygonUtils {

	/**
	 * Compute centorid (center of gravity) of specified polygon.
	 * 
	 * @param x
	 *            X coordinates of polygon.
	 * @param y
	 *            Y coordinates of polygon.
	 * @return Centroid [x,y] of specified polygon.
	 * @throws IllegalArgumentException
	 *             if <code>x</code> or <code>y</code> is null or their array
	 *             lengths do not match
	 */
	public static PointF computeCentreOfGravity(float[] x, float[] y) throws IllegalArgumentException {
		if(isValidPolygon(x, y))
			throw new IllegalArgumentException("points length do not match or one of the points is null");
		float cx = 0.0f;
		float cy = 0.0f;

		int n = x.length;
		for(int i = 0; i < n - 1; i++){
			float a = x[i] * y[i + 1] - x[i + 1] * y[i];
			cx += (x[i] + x[i + 1]) * a;
			cy += (y[i] + y[i + 1]) * a;
		}
		float a = x[n - 1] * y[0] - x[0] * y[n - 1];
		cx += (x[n - 1] + x[0]) * a;
		cy += (y[n - 1] + y[0]) * a;

		float area = computePolygonArea(x, y);

		cx /= 6 * area;
		cy /= 6 * area;

		return new PointF(cx, cy);
	}

	/**
	 * Compute the area of the specfied polygon.
	 * 
	 * @param x
	 *            X coordinates of polygon.
	 * @param y
	 *            Y coordinates of polygon.
	 * @return Area of specified polygon.
	 * @throws IllegalArgumentException
	 *             if <code>x</code> or <code>y</code> is null or their array
	 *             lengths do not match
	 */
	public static float computePolygonArea(float[] x, float[] y) throws IllegalArgumentException {
		if(isValidPolygon(x, y))
			throw new IllegalArgumentException("points length do not match or one of the points is null");
		int n = x.length;

		float area = 0.0f;
		for(int i = 0; i < n - 1; i++){
			area += (x[i] * y[i + 1]) - (x[i + 1] * y[i]);
		}
		area += (x[n - 1] * y[0]) - (x[0] * y[n - 1]);

		area *= 0.5;

		return area;
	}

	/**
	 * Return the x,y position at distance "length" into the given polyline.
	 * 
	 * @param x
	 *            X coordinates of polyline
	 * @param y
	 *            Y coordinates of polyline
	 * @param length
	 *            Requested position
	 * @param position
	 *            Preallocated to int[2]
	 * @return True if point is within polyline, false otherwise
	 */
	public static boolean findPolygonPosition(int[] x, int[] y, double length, int[] position) {
		if(length < 0){
			return false;
		}

		double accumulatedLength = 0.0;
		for(int i = 1; i < x.length; i++){
			double legLength = LineUtils.length(new PointF(x[i - 1], y[i - 1]), new PointF(x[i], y[i]));
			if(legLength + accumulatedLength >= length){
				double part = length - accumulatedLength;
				double fraction = part / legLength;
				position[0] = (int) Math.round(x[i - 1] + fraction * (x[i] - x[i - 1]));
				position[1] = (int) Math.round(y[i - 1] + fraction * (y[i] - y[i - 1]));
				return true;
			}

			accumulatedLength += legLength;
		}

		// Length is longer than polyline
		return false;
	}

	/**
	 * Check if a given point is inside a given (complex) polygon.
	 * 
	 * @param x
	 *            , y Polygon.
	 * @param pointX
	 *            , pointY Point to check.
	 * @return True if the given point is inside the polygon, false otherwise.
	 * @throws IllegalArgumentException
	 *             if <code>x</code> or <code>y</code> is null or their array
	 *             lengths do not match
	 */
	public static boolean isPointInsidePolygon(float[] x, float[] y, PointF point)
			throws IllegalArgumentException {
		if(isValidPolygon(x, y))
			throw new IllegalArgumentException("points length do not match or one of the points is null");
		boolean isInside = false;
		int nPoints = x.length;

		int j = 0;
		for(int i = 0; i < nPoints; i++){
			j++;
			if(j == nPoints){
				j = 0;
			}

			if(((y[i] < point.y) && (y[j] >= point.y)) || ((y[j] < point.y) && (y[i] >= point.y))){
				if(x[i] + (point.y - y[i]) / (y[j] - y[i]) * (x[j] - x[i]) < point.x){
					isInside = !isInside;
				}
			}
		}

		return isInside;
	}

	public static boolean isValidPolygon(float[] x, float[] y) {
		if(x == null || y == null){
			return false;
		} else if(x.length != y.length){
			return false;
		}
		return true;
	}

	/**
	 * Moves a Polygon (with respect to it's origin) to specified point.
	 * 
	 * @param x
	 *            polygon's x coordinates
	 * @param y
	 *            polygon's y coordinates
	 * @param point
	 *            the point to move to
	 */
	public static void moveTo(float[] x, float[] y, PointF point) {
		translate(x, y, point.x, point.y);
	}

	public static void rotate(float[] x, float[] y, float ox, float oy, float cos, float sin)
			throws IllegalArgumentException {
		if(isValidPolygon(x, y))
			throw new IllegalArgumentException("points length do not match or one of the points is null");

		PointF temp;
		for(int i = 0; i < x.length; i++){
			temp = PointUtils.rotate(x[i], y[i], ox, oy, cos, sin);
			x[i] = temp.x;
			y[i] = temp.y;
		}
	}

	/**
	 * Rotate the polygon with the given angle
	 * 
	 * @param x
	 *            list of x coordinates of the polygon
	 * @param y
	 *            list of y coordinates of the polygon
	 * @param origin
	 *            Point around which to rotate
	 * 
	 * @param angle
	 *            to rotate
	 * @throws IllegalArgumentException
	 *             if <code>x</code> or <code>y</code> is null or their array
	 *             lengths do not match
	 */
	public static void rotate(float[] x, float[] y, PointF origin, float angle)
			throws IllegalArgumentException {
		float cos = (float) Math.cos((Math.PI * angle) / 180);
		float sin = (float) Math.sin((Math.PI * angle) / 180);
		rotate(x, y, origin.x, origin.y, cos, sin);

	}

	/**
	 * 
	 * 
	 * @param x
	 * @param y
	 * @param dx
	 * @param dy
	 * @throws IllegalArgumentException
	 *             if <code>x</code> or <code>y</code> is null or their array
	 *             lengths do not match
	 */
	public static void translate(float[] x, float[] y, float dx, float dy) throws IllegalArgumentException {
		if(isValidPolygon(x, y))
			throw new IllegalArgumentException("points length do not match or one of the points is null");
		PointF temp;
		for(int i = 0; i < x.length; i++){
			temp = new PointF(x[i], y[i]);
			PointUtils.translate(temp, dx, dy);
			x[i] = temp.x;
			y[i] = temp.y;
		}
	}
}
