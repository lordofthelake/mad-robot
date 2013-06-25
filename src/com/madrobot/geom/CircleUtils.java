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
 *Utility methods for circle primitives
 * 
 * 
 * @author Elton Kent
 */
public class CircleUtils {
	
	public static PointF findPointOnCircle(float radius, float angleInDegrees, PointF origin) {
		// Convert from degrees to radians via multiplication by PI/180
		float x = (float) (radius * Math.cos(angleInDegrees * Math.PI / 180F)) + origin.x;
		float y = (float) (radius * Math.sin(angleInDegrees * Math.PI / 180F)) + origin.y;
		return new PointF(x, y);
	}

	/**
	 * 
	 * Get the new point in a circle if the given <code>point</code> is rotated
	 * by an <code>angle</code>
	 * 
	 * @param point
	 *            to be rotated
	 * @param center
	 *            of the circle
	 * @param angle
	 *            the point should be rotated by
	 * @return The new point after rotation
	 */
	public static PointF getRotatedPoint(PointF point, PointF center, float angle) {
		PointF newPoint = new PointF();
		float centerX = point.x - center.x;
		float centerY = point.y - center.y;
		newPoint.x = (float) (Math.cos(angle) * (centerX) - Math.sin(angle) * (centerY) + center.x);
		newPoint.y = (float) (Math.sin(angle) * (centerX) + Math.cos(angle) * (centerY) + center.y);
		return newPoint;
	}

	/**
	 * Check if the two circle are colliding
	 * 
	 * @param circle1Center
	 *            Center point of the first circle
	 * @param circle1Radius
	 *            Radius of the first circle
	 * @param circle2Center
	 *            Center point of the second circle
	 * @param circle2Radius
	 *            Radius of the second circle
	 * @return true,if the two circles are colliding.
	 */
	public static boolean isColliding(PointF circle1Center, float circle1Radius, PointF circle2Center,
			float circle2Radius) {
		final double a = circle1Radius + circle2Radius;
		final double dx = circle1Center.x - circle2Center.x;
		final double dy = circle1Center.y - circle2Center.y;
		return a * a > (dx * dx + dy * dy);
	}

	/**
	 * Check if the given point is in the cicle
	 * 
	 * @param x
	 *            Point x coordinate
	 * @param y
	 *            Point y coordinate
	 * @param circleX
	 *            x coordinate of the circle's radius
	 * @param circleY
	 *            y coordinate of the circle's radius
	 * @param circleRadius
	 *            radius of the circle
	 * @return
	 */
	public static boolean isPointInCircle(float x, float y, float circleX, float circleY, float circleRadius) {
		double magic = Math.sqrt(Math.pow(circleX - x, 2) + Math.pow(circleY - y, 2));
		return magic < circleRadius;
	}

	public static boolean isPointInCircle(PointF point, PointF circleCenter, float circleRadius) {
		return isPointInCircle(point.x, point.y, circleCenter.x, circleCenter.y, circleRadius);
	}
}
