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

public class TriangleUtils {

	/**
	 * Point-in-triangle test
	 * 
	 * @param x
	 * @param y
	 * @param ax
	 * @param ay
	 * @param bx
	 * @param by
	 * @param cx
	 * @param cy
	 * @return <code>true</code> if (x,y) lies within the a-b-c
	 *         triangle, <code>false</code> otherwise
	 */
	public static boolean contains(float x, float y, float ax, float ay, float bx, float by, float cx,
			float cy) {
		int cc1 = LineUtils.relativeCCW(ax, ay, bx, by, x, y);
		int cc2 = LineUtils.relativeCCW(bx, by, cx, cy, x, y);

		if(cc1 == cc2){
			int cc3 = LineUtils.relativeCCW(cx, cy, ax, ay, x, y);
			return cc1 == cc3;
		}

		return false;
	}

	public static boolean contains(PointF point, float ax, float ay, float bx, float by, float cx, float cy) {
		return contains(point.x, point.y, ax, ay, bx, by, cx, cy);
	}

	private static boolean sameSide(int p1x, int p1y, int p2x, int p2y, int l1x, int l1y, int l2x, int l2y) {
		long lhs = ((p1x - l1x) * (l2y - l1y) - (l2x - l1x) * (p1y - l1y));
		long rhs = ((p2x - l1x) * (l2y - l1y) - (l2x - l1x) * (p2y - l1y));
		long product = lhs * rhs;
		return product >= 0;
	}

	/**
	 * Checks whether the specified point px, py is within the triangle defined
	 * by ax, ay, bx, by and cx, cy.
	 * 
	 * @param px
	 *            The x of the point to test
	 * @param py
	 *            The y of the point to test
	 * @param ax
	 *            The x of the 1st point of the triangle
	 * @param ay
	 *            The y of the 1st point of the triangle
	 * @param bx
	 *            The x of the 2nd point of the triangle
	 * @param by
	 *            The y of the 2nd point of the triangle
	 * @param cx
	 *            The x of the 3rd point of the triangle
	 * @param cy
	 *            The y of the 3rd point of the triangle
	 * @return true when the point is within the given triangle
	 */
	public static boolean withinBounds(int px, int py, int ax, int ay, int bx, int by, int cx, int cy) {
		if((px < Math.min(ax, Math.min(bx, cx))) || (px > Math.max(ax, Math.max(bx, cx)))
				|| (py < Math.min(ay, Math.min(by, cy))) || (py > Math.max(ay, Math.max(by, cy)))){
			return false;
		}
		boolean sameabc = sameSide(px, py, ax, ay, bx, by, cx, cy);
		boolean samebac = sameSide(px, py, bx, by, ax, ay, cx, cy);
		boolean samecab = sameSide(px, py, cx, cy, ax, ay, bx, by);
		return sameabc && samebac && samecab;
	}
}
