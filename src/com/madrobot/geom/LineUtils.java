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

import java.util.Arrays;

import android.graphics.PointF;

public class LineUtils {

	/**
	 * Defines the distance below which two points are considered to coincide.
	 */
	public static final double VERY_SMALL_DISTANCE = 0.000001;

	/**
	 * Checks if two lines are parallel
	 * 
	 * @param line1
	 *            he first line
	 * @param line2
	 *            the second line
	 * @return returns true if the lines are parallel, false otherwise
	 */
	public static boolean areParallel(PointF line1Start, PointF line1End, PointF line2Start, PointF line2End) {
		// If both lines are vertical, they are parallel
		if(isVertical(line1Start, line1End) && isVertical(line2Start, line2End)){
			return true;
		} else // If one of them is vertical, they are not parallel
		if(isVertical(line1Start, line1End) || isVertical(line2Start, line2End)){
			return false;
		} else // General case. If their slopes are the same, they are parallel
		{
			return (Math.abs(getSlope(line1Start, line1End) - getSlope(line2Start, line2End)) < VERY_SMALL_DISTANCE);
		}
	}

	/**
	 * Find the point on the line lineStart,lineEnd a given fraction from p0.
	 * 
	 * 
	 * @param lineStart
	 *            First coordinate of line [x,y].
	 * @param lineEnd
	 *            Second coordinate of line [x,y].
	 * @param fractionFromP0
	 *            Point we are looking for coordinates of
	 */
	public static PointF computePointOnLine(PointF lineStart, PointF lineEnd, float fractionFromP0) {
		PointF point = new PointF();

		point.x = lineStart.x + fractionFromP0 * (lineEnd.x - lineStart.x);
		point.y = lineStart.y + fractionFromP0 * (lineEnd.y - lineStart.y);
		return point;
	}

	public static PointF createVector(float ax, float ay, float bx, float by) {
		PointF point = new PointF();
		point.x = bx - ax;
		point.y = by - ay;
		return point;
	}

	/**
	 * Construct the vector specified by two points.
	 * 
	 * @param lineStart
	 *            The coordinate of the line starting point
	 * @param lineEnd
	 *            The coordinate of the line ending point
	 * 
	 * @return The Vector point
	 */
	public static PointF createVector(PointF lineStart, PointF lineEnd) {
		return createVector(lineStart.x, lineStart.y, lineEnd.x, lineEnd.y);
	}

	/**
	 * Removes unnecessary vertices from a line strip. Uses the
	 * Ramer–Douglas–Peucker algorithm
	 * 
	 * @param input
	 *            in x,y,x,y format
	 * @param maxD
	 *            The maximum distance a point from the input set will
	 *            be from the output shape
	 * @param loop
	 *            <code>true</code> for a line loop rather than a strip
	 * @return a decimated vertex array, in x,y,x,y... format
	 */
	public static float[] decimate(final float[] input, final float maxD, final boolean loop) {
		boolean[] marked = new boolean[input.length / 2];
		Arrays.fill(marked, false);

		int end = loop ? marked.length : marked.length - 1;

		rdp(input, marked, maxD, 0, end);

		// build output list
		int count = 0;
		for(int i = 0; i < marked.length; i++){
			if(marked[i]){
				count++;
			}
		}

		float[] output = new float[count * 2];
		int index = 0;
		for(int i = 0; i < marked.length; i++){
			if(marked[i]){
				output[index++] = input[2 * i];
				output[index++] = input[2 * i + 1];
			}
		}
		return output;
	}

	/**
	 * @param ax
	 * @param ay
	 * @param bx
	 * @param by
	 * @param px
	 * @param py
	 * @return The distance from p to the line segment a-b
	 */
	public static float distanceToSegment(float ax, float ay, float bx, float by, float px, float py) {
		float vx = bx - ax;
		float vy = by - ay;
		float wx = px - ax;
		float wy = py - ay;

		double c1 = wx * vx + wy * vy;
		double c2 = vx * vx + vy * vy;

		if(c1 <= 0){
			return (float) Math.hypot((ax - px), (ay - py));
		}
		if(c1 >= c2){
			return (float) Math.hypot(bx - px, (by - py));
		}

		double b = c1 / c2;
		vx *= b;
		vy *= b;

		vx += ax;
		vy += ay;

		return (float) Math.hypot((vx - px), (vy - py));
	}

	public static float distanceToSegment(PointF pointStart, PointF pointEnd, PointF point) {
		return distanceToSegment(pointStart.x, pointStart.y, pointEnd.x, pointEnd.y, point.x, point.y);

	}

	/**
	 * Check if two double precision numbers are "equal", i.e. close enough
	 * to a prespecified limit.
	 * 
	 * @param a
	 *            First number to check
	 * @param b
	 *            Second number to check
	 * @return True if the twho numbers are "equal", false otherwise
	 */
	private static boolean equals(float a, float b) {
		return equals(a, b, 1.0e-5f);
	}

	/**
	 * Check if two double precision numbers are "equal", i.e. close enough
	 * to a given limit.
	 * 
	 * @param a
	 *            First number to check
	 * @param b
	 *            Second number to check
	 * @param limit
	 *            The definition of "equal".
	 * @return True if the twho numbers are "equal", false otherwise
	 */
	private static boolean equals(float a, float b, float limit) {
		return Math.abs(a - b) < limit;
	}

	public static PointF extendLine(float ax, float ay, float bx, float by, float toLength) {
		float oldLength = length(ax, ay, bx, by);
		float lengthFraction = oldLength != 0.0f ? toLength / oldLength : 0.0f;
		PointF newEnd = new PointF();
		newEnd.x = ax + (bx - ax) * lengthFraction;
		newEnd.y = ay + (by - ay) * lengthFraction;
		return newEnd;
	}

	/**
	 * Extend a given line segment to a given length and holding the first
	 * point of the line as fixed.
	 * 
	 * @param lineStart
	 * @param lineEnd
	 *            Line segment to extend. lineStart is fixed during extension
	 * @param length
	 *            Length of new line segment.
	 * @param The
	 *            new end point of the line.
	 */
	public static PointF extendLine(PointF lineStart, PointF lineEnd, float toLength) {
		return extendLine(lineStart.x, lineStart.y, lineEnd.x, lineEnd.y, toLength);
	}

	/**
	 * Compute the intersection between two line segments, or two lines
	 * of infinite length.
	 * 
	 * @param x0
	 *            X coordinate first end point first line segment.
	 * @param y0
	 *            Y coordinate first end point first line segment.
	 * @param x1
	 *            X coordinate second end point first line segment.
	 * @param y1
	 *            Y coordinate second end point first line segment.
	 * @param x2
	 *            X coordinate first end point second line segment.
	 * @param y2
	 *            Y coordinate first end point second line segment.
	 * @param x3
	 *            X coordinate second end point second line segment.
	 * @param y3
	 *            Y coordinate second end point second line segment.
	 * @param intersection
	 *            [2] Preallocated by caller to double[2]
	 * @return -1 if lines are parallel (x,y unset),
	 *         -2 if lines are parallel and overlapping (x, y center)
	 *         0 if intesrection outside segments (x,y set)
	 *         +1 if segments intersect (x,y set)
	 */
	public static int findLineSegmentIntersection(float x0, float y0, float x1, float y1, float x2,
			float y2, float x3, float y3, float[] intersection) {
		// TODO: Make limit depend on input domain
		final float LIMIT = 1e-5f;
		final float INFINITY = 1e10f;

		float x, y;

		//
		// Convert the lines to the form y = ax + b
		//

		// Slope of the two lines
		float a0 = equals(x0, x1, LIMIT) ? INFINITY : (y0 - y1) / (x0 - x1);
		float a1 = equals(x2, x3, LIMIT) ? INFINITY : (y2 - y3) / (x2 - x3);

		float b0 = y0 - a0 * x0;
		float b1 = y2 - a1 * x2;

		// Check if lines are parallel
		if(equals(a0, a1)){
			if(!equals(b0, b1)){
				return -1; // Parallell non-overlapping
			} else{
				if(equals(x0, x1)){
					if((Math.min(y0, y1) < Math.max(y2, y3)) || (Math.max(y0, y1) > Math.min(y2, y3))){
						float twoMiddle = y0 + y1 + y2 + y3 - min(y0, y1, y2, y3) - max(y0, y1, y2, y3);
						y = (twoMiddle) / 2.0f;
						x = (y - b0) / a0;
					} else{
						return -1; // Parallell non-overlapping
					}
				} else{
					if((Math.min(x0, x1) < Math.max(x2, x3)) || (Math.max(x0, x1) > Math.min(x2, x3))){
						float twoMiddle = x0 + x1 + x2 + x3 - min(x0, x1, x2, x3) - max(x0, x1, x2, x3);
						x = (twoMiddle) / 2.0f;
						y = a0 * x + b0;
					} else{
						return -1;
					}
				}

				intersection[0] = x;
				intersection[1] = y;
				return -2;
			}
		}

		// Find correct intersection point
		if(equals(a0, INFINITY)){
			x = x0;
			y = a1 * x + b1;
		} else if(equals(a1, INFINITY)){
			x = x2;
			y = a0 * x + b0;
		} else{
			x = -(b0 - b1) / (a0 - a1);
			y = a0 * x + b0;
		}

		intersection[0] = x;
		intersection[1] = y;

		// Then check if intersection is within line segments
		float distanceFrom1;
		if(equals(x0, x1)){
			if(y0 < y1){
				distanceFrom1 = y < y0 ? length(new PointF(x, y), new PointF(x0, y0)) : y > y1 ? length(
						new PointF(x, y), new PointF(x1, y1)) : 0.0f;
			} else{
				distanceFrom1 = y < y1 ? length(new PointF(x, y), new PointF(x1, y1)) : y > y0 ? length(
						new PointF(x, y), new PointF(x0, y0)) : 0.0f;
			}
		} else{
			if(x0 < x1){
				distanceFrom1 = x < x0 ? length(new PointF(x, y), new PointF(x0, y0)) : x > x1 ? length(
						new PointF(x, y), new PointF(x1, y1)) : 0.0f;
			} else{
				distanceFrom1 = x < x1 ? length(new PointF(x, y), new PointF(x1, y1)) : x > x0 ? length(
						new PointF(x, y), new PointF(x0, y0)) : 0.0f;
			}
		}

		float distanceFrom2;
		if(equals(x2, x3)){
			if(y2 < y3){
				distanceFrom2 = y < y2 ? length(new PointF(x, y), new PointF(x2, y2)) : y > y3 ? length(
						new PointF(x, y), new PointF(x3, y3)) : 0.0f;
			} else{
				distanceFrom2 = y < y3 ? length(new PointF(x, y), new PointF(x3, y3)) : y > y2 ? length(
						new PointF(x, y), new PointF(x2, y2)) : 0.0f;
			}
		} else{
			if(x2 < x3){
				distanceFrom2 = x < x2 ? length(new PointF(x, y), new PointF(x2, y2)) : x > x3 ? length(
						new PointF(x, y), new PointF(x3, y3)) : 0.0f;
			} else{
				distanceFrom2 = x < x3 ? length(new PointF(x, y), new PointF(x3, y3)) : x > x2 ? length(
						new PointF(x, y), new PointF(x2, y2)) : 0.0f;
			}
		}

		return equals(distanceFrom1, 0.0f) && equals(distanceFrom2, 0.0f) ? 1 : 0;
	}

	/**
	 * Return the slope of a line
	 * 
	 * @param lineStart
	 * @param lineEnd
	 * @return
	 */
	public static double getSlope(PointF lineStart, PointF lineEnd) {
		return (((lineStart.y) - lineEnd.y) / ((lineStart.x) - lineEnd.x));
	}

	/**
	 * Return true if c is between a and b.
	 */
	private static boolean isBetween(float a, float b, float c) {
		return b > a ? (c >= a) && (c <= b) : (c >= b) && (c <= a);
	}

	/**
	 * Check if two line segments intersects. Integer domain.
	 * 
	 * @param x0
	 *            , y0, x1, y1 End points of first line to check.
	 * @param x2
	 *            , yy, x3, y3 End points of second line to check.
	 * @return True if the two lines intersects.
	 */
	public static boolean isLineIntersectingLine(float x0, float y0, float x1, float y1, float x2,
			float y2, float x3, float y3) {
		float s1 = sameSide(x0, y0, x1, y1, x2, y2, x3, y3);
		float s2 = sameSide(x2, y2, x3, y3, x0, y0, x1, y1);

		return (s1 <= 0) && (s2 <= 0);
	}

	/**
	 * Check if a specified line intersects a specified rectangle.
	 * Integer domain.
	 * 
	 * @param lx0
	 *            , ly0 1st end point of line
	 * @param ly1
	 *            , ly1 2nd end point of line
	 * @param x0
	 *            , y0, x1, y1 Upper left and lower right corner of rectangle
	 *            (inclusive).
	 * @return True if the line intersects the rectangle,
	 *         false otherwise.
	 */
	public static boolean isLineIntersectingRectangle(float lx0, float ly0, float lx1, float ly1, float x0,
			float y0, float x1, float y1) {
		// Is one of the line endpoints inside the rectangle
		if(RectangleUtils.isPointInsideRectangle(x0, y0, x1, y1, lx0, ly0)
				|| RectangleUtils.isPointInsideRectangle(x0, y0, x1, y1, lx1, ly1)){
			return true;
		}

		// If it intersects it goes through. Need to check three sides only.

		// Check against top rectangle line
		if(isLineIntersectingLine(lx0, ly0, lx1, ly1, x0, y0, x1, y0)){
			return true;
		}

		// Check against left rectangle line
		if(isLineIntersectingLine(lx0, ly0, lx1, ly1, x0, y0, x0, y1)){
			return true;
		}

		// Check against bottom rectangle line
		if(isLineIntersectingLine(lx0, ly0, lx1, ly1, x0, y1, x1, y1)){
			return true;
		}

		return false;
	}

	/**
	 * Checks if a line is vertical.
	 * 
	 * @param line
	 *            the line to check
	 * @return returns true if the line is vertical, false otherwise.
	 */
	public static boolean isVertical(PointF lineStart, PointF lineEnd) {
		return ((Math.abs(lineStart.x - lineEnd.x) < VERY_SMALL_DISTANCE));
	}

	public static float length(float ax, float ay) {
		return (float) Math.sqrt(ax * ax + ay * ay);
	}

	public static float length(float ax, float ay, float bx, float by) {
		PointF v = createVector(ax, ay, bx, by);
		return length(v);
	}

	/**
	 * Return the length of a vector.
	 * 
	 * @param v
	 *            Vector Point
	 * @return Length of vector.
	 */
	public static float length(PointF v) {
		return length(v.x, v.y);
	}

	/**
	 * Compute distance between two points.
	 * 
	 * @param p0
	 *            starting point
	 * @param p1
	 *            ending point
	 * 
	 * @return Distance between points.
	 */
	public static float length(PointF p0, PointF p1) {
		PointF v = createVector(p0, p1);
		return length(v);
	}

	/**
	 * Return largest of four numbers.
	 * 
	 * @param a
	 *            First number to find largest among.
	 * @param b
	 *            Second number to find largest among.
	 * @param c
	 *            Third number to find largest among.
	 * @param d
	 *            Fourth number to find largest among.
	 * @return Largest of a, b, c and d.
	 */
	private static float max(float a, float b, float c, float d) {
		return Math.max(Math.max(a, b), Math.max(c, d));
	}

	/**
	 * Return smallest of four numbers.
	 * 
	 * @param a
	 *            First number to find smallest among.
	 * @param b
	 *            Second number to find smallest among.
	 * @param c
	 *            Third number to find smallest among.
	 * @param d
	 *            Fourth number to find smallest among.
	 * @return Smallest of a, b, c and d.
	 */
	private static float min(float a, float b, float c, float d) {
		return Math.min(Math.min(a, b), Math.min(c, d));
	}

	/**
	 * Recursive step. Finds the point furthest from the start-end
	 * segment, divide and conquer on that vertex if it is outside the
	 * tolerance value
	 * 
	 * @param verts
	 * @param marked
	 * @param maxD
	 * @param start
	 * @param end
	 */
	private static void rdp(final float[] verts, final boolean[] marked, final float maxD, final int start,
			final int end) {
		marked[start % marked.length] = true;
		marked[end % marked.length] = true;

		// find furthest from line
		float maxDistance = -1;
		int maxIndex = -1;

		float ax = verts[2 * start % verts.length];
		float ay = verts[(2 * start + 1) % verts.length];
		float bx = verts[2 * end % verts.length];
		float by = verts[(2 * end + 1) % verts.length];

		for(int i = start + 1; i < end; i++){
			float px = verts[2 * i % verts.length];
			float py = verts[(2 * i + 1) % verts.length];

			float d = distanceToSegment(ax, ay, bx, by, px, py);

			if((d > maxD) && (d > maxDistance)){
				maxDistance = d;
				maxIndex = i;
			}
		}

		if(maxIndex != -1){
			// recurse
			rdp(verts, marked, maxD, start, maxIndex);
			rdp(verts, marked, maxD, maxIndex, end);
		}
	}

	/**
	 * return -1 if point is on left, 1 if on right
	 * 
	 * @param ax
	 * @param ay
	 * @param bx
	 * @param by
	 * @param px
	 * @param py
	 * @return -1 if the point is on the left of the line, 1 if the
	 *         point is on the right of the line, 0 if the point lies
	 *         on the line
	 */
	public static int relativeCCW(float ax, float ay, float bx, float by, float px, float py) {
		bx -= ax;
		by -= ay;
		px -= ax;
		py -= ay;
		float ccw = px * by - py * bx;

		return ccw < 0.0 ? -1 : ccw > 0.0 ? 1 : 0;
	}

	/**
	 * 
	 * 
	 * @param lineStart
	 *            Line start
	 * @param lineEnd
	 *            Line End
	 * @param point
	 *            Point
	 * @return
	 */
	public static int relativeCCW(PointF lineStart, PointF lineEnd, PointF point) {
		return relativeCCW(lineStart.x, lineStart.y, lineEnd.x, lineEnd.y, point.x, point.y);
	}

	/**
	 * Rotates a Line2D object around a Point2D object.
	 * 
	 * @param line
	 *            the line to rotate
	 * @param origin
	 *            the point around which to rotate
	 * @param angle
	 *            the angle (in degrees) of rotation
	 */
	public static void rotate(PointF lineStart, PointF lineEnd, PointF origin, double angle) {
		float cos = (float) Math.cos((Math.PI * angle) / 180);
		float sin = (float) Math.sin((Math.PI * angle) / 180);
		rotate(lineStart, lineEnd, origin, cos, sin);
	}

	/**
	 * Rotates a Line2D object around a Point2D object.
	 * 
	 * @param line
	 *            the line to rotate
	 * @param origin
	 *            the point around which to rotate
	 * @param cos
	 *            the cosine of the rotation angle
	 * @param sin
	 *            the sine of the rotation angle
	 */
	private static void rotate(PointF lineStart, PointF lineEnd, PointF origin, float cos, float sin) {
		PointF test = PointUtils.rotate(lineStart, origin, cos, sin);
		lineStart.x = test.x;
		lineStart.y = test.y;
		test = PointUtils.rotate(lineEnd, origin, cos, sin);
		lineEnd.x = test.x;
		lineEnd.y = test.y;
	}

	/**
	 * Check if two points are on the same side of a given line.
	 * Algorithm from Sedgewick page 350.
	 * 
	 * @param x0
	 *            , y0, x1, y1 The line.
	 * @param px0
	 *            , py0 First point.
	 * @param px1
	 *            , py1 Second point.
	 * @return <0 if points on opposite sides.
	 *         =0 if one of the points is exactly on the line
	 *         >0 if points on same side.
	 */
	private static int sameSide(float x0, float y0, float x1, float y1, float px0, float py0, float px1,
			float py1) {
		int sameSide = 0;

		float dx = x1 - x0;
		float dy = y1 - y0;
		float dx1 = px0 - x0;
		float dy1 = py0 - y0;
		float dx2 = px1 - x1;
		float dy2 = py1 - y1;

		// Cross product of the vector from the endpoint of the line to the
		// point
		float c1 = dx * dy1 - dy * dx1;
		float c2 = dx * dy2 - dy * dx2;

		if((c1 != 0) && (c2 != 0)){
			sameSide = (c1 < 0) != (c2 < 0) ? -1 : 1;
		} else if((dx == 0) && (dx1 == 0) && (dx2 == 0)){
			sameSide = !isBetween(y0, y1, py0) && !isBetween(y0, y1, py1) ? 1 : 0;
		} else if((dy == 0) && (dy1 == 0) && (dy2 == 0)){
			sameSide = !isBetween(x0, x1, px0) && !isBetween(x0, x1, px1) ? 1 : 0;
		}

		return sameSide;
	}

	
	
	/**
	 * Translate (move) a Line2D object on the X and Y axes.
	 * 
	 * @param line
	 *            the line to translate
	 * @param dx
	 *            the X-axis offset
	 * @param dy
	 *            the Y-axis offset
	 */
	public static void translate(PointF lineStart, PointF lineEnd, double dx, double dy) {
		lineStart.x += dx;
		lineStart.y += dy;
		lineEnd.x += dx;
		lineEnd.y += dy;
	}

}
