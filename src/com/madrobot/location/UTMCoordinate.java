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
package com.madrobot.location;

public class UTMCoordinate {
	/**
	 * The easting
	 */
	private int x;
	/**
	 * The northing
	 */
	private int y;

	/**
	 * Instantites with default values of {@link #getX() x=0} and
	 * {@link #getY() y=0}
	 * 
	 */
	public UTMCoordinate() {
	}

	/**
	 * Instantiates using given values of easting and northing
	 * 
	 * @param x
	 *            Easting
	 * @param y
	 *            Northing
	 */
	public UTMCoordinate(int x, int y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * Instantiates by copying the value from the other point
	 * 
	 * @param other
	 *            Point to copy values from
	 */
	public UTMCoordinate(UTMCoordinate other) {
		this.x = other.x;
		this.y = other.y;
	}

	/**
	 * Compares this instance with another instance and returns the comparision
	 * results.
	 * <p>
	 * Two instances of {@link UTMPoint} are considered equal if {@link #getX()
	 * x} and {@link #getY() y} are equal
	 * </p>
	 * 
	 * @param o
	 *            The other instance to compare with
	 * @return true if the current instance is equal to the other, false other
	 */
	@Override
	public boolean equals(Object o) {
		if(this == o){
			return true;
		}
		if(o instanceof UTMCoordinate){
			UTMCoordinate that = (UTMCoordinate) o;
			return (this.x == that.x) && (this.y == that.y);
		}
		return false;
	}

	/**
	 * Returns the x-coordinate associated
	 * 
	 * @return The X-coordinate
	 */
	public int getX() {
		return x;
	}

	/**
	 * Returns the y-coordinate associated
	 * 
	 * @return The Y-coordinate
	 */
	public int getY() {
		return y;
	}

	/**
	 * Returns the hashcode to uniquely identify the instance
	 * 
	 * @return The hashcode
	 */
	@Override
	public int hashCode() {
		return toString().hashCode();
	}

	/**
	 * Checks is the current point is absolute zero
	 * 
	 * @return true if x and y both are zero, false otherwise
	 */
	public boolean isZero() {
		return (x == 0) && (y == 0);
	}

	/**
	 * Sets the value of the coordinates
	 * 
	 * @param x
	 *            New x coordinate
	 * @param y
	 *            New y coordinate
	 */
	public void set(int x, int y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * Sets the value of the coordinates
	 * 
	 * @param other
	 *            New coordinates
	 */
	public void set(UTMCoordinate other) {
		this.x = other.x;
		this.y = other.y;
	}

	/**
	 * Sets the x-coordinate associated
	 * 
	 * @param x
	 *            The X-coordinate
	 */
	public void setX(int x) {
		this.x = x;
	}

	/**
	 * Sets the y-coordinate associated
	 * 
	 * @param y
	 *            The Y-coordinate
	 */
	public void setY(int y) {
		this.y = y;
	}

	/**
	 * Returns the string repsentation of the instance
	 * 
	 * @return The string representation
	 */
	@Override
	public String toString() {
		return "{UTMPoint[x=" + x + ",y=" + y + "]}";
	}
}
