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

/**
 * Represents a GPS coordinate
 * GPSCoordinate.java
 * 
 * @author Elton Kent
 */
public class GPSCoordinate {

	/**
	 * The internal latitude value.
	 */
	private final double latitude;

	/**
	 * The internal longitude value.
	 */
	private final double longitude;

	/**
	 * Constructs a new GeoCoordinate with the given latitude and longitude
	 * values, measured in
	 * degrees.
	 * 
	 * @param latitude
	 *            the latitude value in degrees.
	 * @param longitude
	 *            the longitude value in degrees.
	 * @throws IllegalArgumentException
	 *             if the latitude or longitude value is invalid.
	 */
	public GPSCoordinate(double latitude, double longitude) throws IllegalArgumentException {
		this.latitude = LocationUtils.validateLatitude(latitude);
		this.longitude = LocationUtils.validateLongitude(longitude);
	}

	/**
	 * Returns the latitude value of this coordinate.
	 * 
	 * @return the latitude value of this coordinate.
	 */
	public double getLatitude() {
		return this.latitude;
	}

	/**
	 * Returns the longitude value of this coordinate.
	 * 
	 * @return the longitude value of this coordinate.
	 */
	public double getLongitude() {
		return this.longitude;
	}

}
