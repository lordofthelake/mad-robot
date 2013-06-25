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

public class ComplexPrimitives {
	/**
	 * Create the geometry of an arrow. The arrow is positioned at the
	 * end (last point) of the specified polyline, as follows:
	 * 
	 * 0,4--,
	 * \ --,
	 * \ --,
	 * \ --,
	 * \ --,
	 * -------------------------3-----------1
	 * / --'
	 * / --'
	 * / --'
	 * / --'
	 * 2--'
	 * 
	 * @param x
	 *            X coordinates of polyline of where arrow is positioned
	 *            in the end. Must contain at least two points.
	 * @param y
	 *            Y coordinates of polyline of where arrow is positioned
	 *            in the end.
	 * @param length
	 *            Length along the main axis from point 1 to the
	 *            projection of point 0.
	 * @param angle
	 *            Angle between the main axis and the line 1,0
	 *            (and 1,2) in radians.
	 * @param inset
	 *            Specification of point 3 [0.0-1.0], 1.0 will put
	 *            point 3 at distance length from 1, 0.0 will put it
	 *            at point 1.
	 * @return Array of the five coordinates [x,y,...].
	 */
	public static int[] createArrow(int[] x, int[] y, double length, double angle, double inset) {
		int[] arrow = new int[10];

		int x0 = x[x.length - 1];
		int y0 = y[y.length - 1];

		arrow[2] = x0;
		arrow[3] = y0;

		// Find position of interior of the arrow along the polyline
		int[] pos1 = new int[2];
		PolygonUtils.findPolygonPosition(x, y, length, pos1);

		// Angles
		double dx = x0 - pos1[0];
		double dy = y0 - pos1[1];

		// Polyline angle
		double v = dx == 0.0 ? Math.PI / 2.0 : Math.atan(Math.abs(dy / dx));

		v = (dx > 0.0) && (dy <= 0.0) ? Math.PI + v : (dx > 0.0) && (dy >= 0.0) ? Math.PI - v : (dx <= 0.0)
				&& (dy < 0.0) ? -v : (dx <= 0.0) && (dy > 0.0) ? +v : 0.0;

		double v0 = v + angle;
		double v1 = v - angle;

		double edgeLength = length / Math.cos(angle);

		arrow[0] = x0 + (int) Math.round(edgeLength * Math.cos(v0));
		arrow[1] = y0 - (int) Math.round(edgeLength * Math.sin(v0));

		arrow[4] = x0 + (int) Math.round(edgeLength * Math.cos(v1));
		arrow[5] = y0 - (int) Math.round(edgeLength * Math.sin(v1));

		double c1 = inset * length;

		arrow[6] = x0 + (int) Math.round(c1 * Math.cos(v));
		arrow[7] = y0 - (int) Math.round(c1 * Math.sin(v));

		// Close polygon
		arrow[8] = arrow[0];
		arrow[9] = arrow[1];

		return arrow;
	}

	/**
	 * Create the geometry of a sector of an ellipse.
	 * 
	 * @param x0
	 *            X coordinate of center of ellipse.
	 * @param y0
	 *            Y coordinate of center of ellipse.
	 * @param dx
	 *            X radius of ellipse.
	 * @param dy
	 *            Y radius of ellipse.
	 * @param angle0
	 *            First angle of sector (in radians).
	 * @param angle1
	 *            Second angle of sector (in radians).
	 * @return Geometry of secor [x,y,...]
	 */
	public static int[] createSector(int x0, int y0, int dx, int dy, double angle0, double angle1) {
		// Determine a sensible number of points for arc
		double angleSpan = Math.abs(angle1 - angle0);
		double arcDistance = Math.max(dx, dy) * angleSpan;
		int nPoints = (int) Math.round(arcDistance / 15);
		double angleStep = angleSpan / (nPoints - 1);

		int[] xy = new int[nPoints * 2 + 4];

		int index = 0;
		for(int i = 0; i < nPoints; i++){
			double angle = angle0 + angleStep * i;
			double x = dx * Math.cos(angle);
			double y = dy * Math.sin(angle);

			xy[index + 0] = x0 + (int) Math.round(x);
			xy[index + 1] = y0 - (int) Math.round(y);

			index += 2;
		}

		// Start and end geometry at center of ellipse to make it a closed
		// polygon
		xy[nPoints * 2 + 0] = x0;
		xy[nPoints * 2 + 1] = y0;
		xy[nPoints * 2 + 2] = xy[0];
		xy[nPoints * 2 + 3] = xy[1];

		return xy;
	}

	/**
	 * Create geometry of a star. Integer domain.
	 * 
	 * @param x0
	 *            X center of star.
	 * @param y0
	 *            Y center of star.
	 * @param innerRadius
	 *            Inner radis of arms.
	 * @param outerRadius
	 *            Outer radius of arms.
	 * @param nArms
	 *            Number of arms.
	 * @return Geometry of star [x,y,x,y,...].
	 */
	public static int[] createStar(int x0, int y0, int innerRadius, int outerRadius, int nArms) {
		int nPoints = nArms * 2 + 1;

		int[] xy = new int[nPoints * 2];

		double angleStep = 2.0 * Math.PI / nArms / 2.0;

		for(int i = 0; i < nArms * 2; i++){
			double angle = i * angleStep;
			double radius = (i % 2) == 0 ? innerRadius : outerRadius;

			double x = x0 + radius * Math.cos(angle);
			double y = y0 + radius * Math.sin(angle);

			xy[i * 2 + 0] = (int) Math.round(x);
			xy[i * 2 + 1] = (int) Math.round(y);
		}

		// Close polygon
		xy[nPoints * 2 - 2] = xy[0];
		xy[nPoints * 2 - 1] = xy[1];

		return xy;
	}

}
