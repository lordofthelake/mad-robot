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

public class Vector {

	public static final Vector ZERO = new Vector(new PointF(0, 0));

	private PointF vPoint;

	public Vector(float mag, Angle theta) {
		float x = (float) (Math.cos(theta.getRadians()) * mag);
		float y = (float) (Math.sin(theta.getRadians()) * mag);

		vPoint = new PointF(x, y);
	}

	public Vector(PointF pointF) {
		vPoint = pointF;
	}

	public Vector(PointF source, PointF end) {
		float x = end.x - source.x;
		float y = end.y - source.y;

		vPoint = new PointF(x, y);
	}

	public Vector add(Vector v) {
		return new Vector(PointUtils.add(vPoint, v.vPoint));

	}

	public boolean equals(Vector v) {
		if((vPoint.x == v.vPoint.x) && (vPoint.y == v.vPoint.y)){
			return true;
		} else{
			return false;
		}
	}

	public Angle getAngle() {
		return new Angle((float) Math.atan2(vPoint.y, vPoint.x));
	}

	public float getMagnitude() {
		return (float) Math.sqrt(Math.pow(vPoint.x, 2) + Math.pow(vPoint.y, 2));
	}

	public PointF getVectorAsPoint() {
		return vPoint;
	}

	public Vector normalize() {
		float mag = getMagnitude();

		float normalX = vPoint.x / mag;
		float normalY = vPoint.y / mag;

		return new Vector(new PointF(normalX, normalY));
	}

	public Vector scale(float scaleFactor) {
		return new Vector(getMagnitude() * scaleFactor, getAngle());
	}

	/*
	 * Subtracts v from this vector
	 */
	public Vector subtract(Vector v) {
		// return new Vector(vPoint.subtract(v.vPoint));
		return new Vector(PointUtils.subtract(vPoint, v.vPoint));
	}

	@Override
	public String toString() {
		return "(" + vPoint.x + ", " + vPoint.y + ")";
	}

}
