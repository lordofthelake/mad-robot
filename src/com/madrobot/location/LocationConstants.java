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

public final class LocationConstants {
	static final double DEG = 57.295779513082320876798154814105; // (180/PI)

	static final double E_R_WGS84 = 6378137.0;

	/**
	 * Equatorial radius of earth is required for distance computation.
	 */
	static final double EQUATORIALRADIUS = 6378137.0;

	/**
	 * The flattening factor of the earth's ellipsoid is required for distance
	 * computation.
	 */
	static final double INVERSEFLATTENING = 298.257223563;

	/**
	 * Polar radius of earth is required for distance computation.
	 */
	static final double POLARRADIUS = 6356752.3142;
	static final double RAD = 0.017453292519943295769236907684886; // (PI/180)

	static final double UTM_CURVATURE = 4207498.0030576494;

	static final double UTM_HOR = 0.0066943800667646578;

	static final double UTM_VER = 0.0067394968199360620;

	static final double UTMK_HEIGHT = 2000000.;

	static final double UTMK_LON = 127.5;

	static final double UTMK_SCALE = 0.9996;
	static final double UTMK_WIDTH = 1000000.;

}
