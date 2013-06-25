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

public class RectangleUtils {

	/**
	 * Checks if rectangle r1 is inside rectangle r2
	 * 
	 * @param r1X
	 * @param r1Y
	 * @param r1Width
	 * @param r1Height
	 * @param r2X
	 * @param r2Y
	 * @param r2Width
	 * @param r2Height
	 * @return
	 */
	public static boolean isInside(final int r1X, final int r1Y, int r1Width, int r1Height, int r2X,
			int r2Y, int r2Width, int r2Height) {
		if((r2Width | r2Height | r1Width | r1Height) < 0){
			return false;
		}
		if((r1X < r2X) || (r1Y < r2Y)){
			return false;
		}
		r2Width += r2X;
		r1Width += r1X;
		if(r1Width <= r1X){
			if((r2Width >= r2X) || (r1Width > r2Width)){
				return false;
			}
		} else if((r2Width >= r2X) && (r1Width > r2Width)){
			return false;
		}
		r2Height += r2Y;
		r1Height += r1Y;
		if(r1Height <= r1Y){
			if((r2Height >= r2Y) || (r1Height > r2Height)){
				return false;
			}
		} else if((r2Height >= r2Y) && (r1Height > r2Height)){
			return false;
		}
		return true;
	}

	/**
	 * Check if a specified point is inside a specified rectangle.
	 * 
	 * @param x0
	 *            , y0, x1, y1 Upper left and lower right corner of rectangle
	 *            (inclusive)
	 * @param x
	 *            ,y Point to check.
	 * @return True if the point is inside the rectangle,
	 *         false otherwise.
	 */
	public static boolean isPointInsideRectangle(float x0, float y0, float x1, float y1, float x, float y) {
		return (x >= x0) && (x < x1) && (y >= y0) && (y < y1);
	}

	/**
	 * Check if a specified polyline intersects a specified rectangle.
	 * Integer domain.
	 * 
	 * @param x
	 *            , y Polyline to check.
	 * @param x0
	 *            , y0, x1, y1 Upper left and lower left corner of rectangle
	 *            (inclusive).
	 * @return True if the polyline intersects the rectangle,
	 *         false otherwise.
	 */
	public static boolean isPolylineIntersectingRectangle(float[] x, float[] y, float x0, float y0,
			float x1, float y1) {
		if(x.length == 0){
			return false;
		}

		if(isPointInsideRectangle(x[0], y[0], x0, y0, x1, y1)){
			return true;
		} else if(x.length == 1){
			return false;
		}

		for(int i = 1; i < x.length; i++){
			if((x[i - 1] != x[i]) || (y[i - 1] != y[i])){
				if(LineUtils.isLineIntersectingRectangle(x[i - 1], y[i - 1], x[i], y[i], x0, y0, x1, y1)){
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * Checks if point px,py is within the bounds of the rectangle
	 * 
	 * @param px
	 *            x coordinate of the point to be checked
	 * @param py
	 *            y coordinate of the point to be checked
	 * @param x
	 *            point of the rectangle
	 * @param y
	 *            point of the rectangle
	 * @param width
	 *            of the rectangle
	 * @param height
	 *            of the rectangle
	 * @return
	 */
	public static boolean withinBounds(final float px, final float py, final float x, final int y,
			int width, int height) {
		if((width | height) < 0){
			return false;
		}
		if((px < x) || (py < y)){
			return false;
		} else{
			width += x;
			height += y;
			return ((width < x) || (width > px)) && ((height < y) || (height > py));
		}
	}

}
