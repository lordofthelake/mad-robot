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

import static java.lang.Math.cos;
import static java.lang.Math.pow;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;
import static java.lang.Math.tan;

import java.util.List;

import android.location.Location;

public final class LocationUtils {

	/**
	 * The multiplication factor to convert from double to int.
	 */
	private static final double FACTOR_DOUBLE_TO_INT = 1000000;

	/**
	 * The largest possible latitude value.
	 */
	public static final double LATITUDE_MAX = 90;

	/**
	 * The smallest possible latitude value.
	 */
	public static final double LATITUDE_MIN = -90;

	/**
	 * The largest possible longitude value.
	 */
	public static final double LONGITUDE_MAX = 180;

	/**
	 * The smallest possible longitude value.
	 */
	public static final double LONGITUDE_MIN = -180;

	private static double curvature(double e1f, double e2f, double e3f, double e4f, double p) {
		return e1f * p - e2f * sin(2. * p) + e3f * sin(4. * p) - e4f * sin(6. * p);
	}

	private static double E1F(double d) {
		return (1.0 - d / 4. - 3. * d * d / 64. - 5. * d * d * d / 256.);
	}

	private static double E2F(double d) {
		return (3. * d / 8. + 3. * d * d / 32. + 45. * d * d * d / 1024.);
	}

	private static double E3F(double d) {
		return (15. * d * d / 256. + 45. * d * d * d / 1024.);
	}

	private static double E4F(double d) {
		return (35. * d * d * d * d / 3072.);
	}

	/**
	 * Get the GPS bearing for given GPS points
	 * 
	 * @param lon1
	 * @param lat1
	 * @param lon2
	 * @param lat2
	 * @return
	 */
	public static double getBearing(double lon1, double lat1, double lon2, double lat2) {
		double dLon = Math.toRadians((lon2 - lon1));
		double y = Math.sin(dLon) * Math.cos(lat2);
		double x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1) * Math.cos(lat2) * Math.cos(dLon);
		return Math.toDegrees(Math.atan2(y, x));
	}

	/**
	 * Converts degree representation to NW,N,S etc.
	 * 
	 * @param degrees
	 * @return
	 */
	public static String getDirection(float degrees) {

		if ((degrees >= 11.25) && (degrees <= 33.75)) {
			return "NNE";
		} else if ((degrees > 33.75) && (degrees <= 56.25)) {
			return "NE";
		} else if ((degrees > 56.25) && (degrees <= 78.75)) {
			return "ENE";
		} else if ((degrees > 78.75) && (degrees <= 101.25)) {
			return "E";
		} else if ((degrees > 101.25) && (degrees <= 123.75)) {
			return "ESE";
		} else if ((degrees > 123.75) && (degrees <= 146.25)) {
			return "SE";
		} else if ((degrees > 146.25) && (degrees <= 168.75)) {
			return "SSE";
		} else if ((degrees > 168.75) && (degrees <= 191.25)) {
			return "S";
		} else if ((degrees > 191.25) && (degrees <= 213.75)) {
			return "SSW";
		} else if ((degrees > 213.75) && (degrees <= 236.25)) {
			return "SW";
		} else if ((degrees > 236.25) && (degrees <= 258.75)) {
			return "WSW";
		} else if ((degrees > 258.75) && (degrees <= 281.25)) {
			return "W";
		} else if ((degrees > 281.25) && (degrees <= 303.75)) {
			return "WNW";
		} else if ((degrees > 303.75) && (degrees <= 326.25)) {
			return "NW";
		} else if ((degrees > 326.2) && (degrees <= 348.75)) {
			return "NNW";
		} else if ((degrees > 348.75) && (degrees < 11.25)) {
			return "N";
		}
		return "U/K";
	}

	/**
	 * Convert UTM coordinates to GPS coordinates
	 * 
	 * @param utm
	 * @return
	 */
	public static GPSCoordinate getGPSCoordinate(UTMCoordinate utm) {
		int x = (utm.getX() - 340000000) / 10;
		int y = (utm.getY() - 130000000) / 10;

		double M0 = LocationConstants.UTM_CURVATURE;
		double M = M0 + ((y - LocationConstants.UTMK_HEIGHT) / LocationConstants.UTMK_SCALE);
		double u1 = M / (LocationConstants.E_R_WGS84 * E1F(LocationConstants.UTM_HOR));

		double temp = sqrt(1. - LocationConstants.UTM_HOR);
		double e1 = (1 - temp) / (1 + temp);

		double v1 = (((3. * e1) / 2.) - ((27. * pow(e1, 3.)) / 32.)) * sin(2. * u1);
		double v2 = (((21. * pow(e1, 2.)) / 16.) - ((55. * pow(e1, 4.)) / 32.)) * sin(4. * u1);
		double v3 = ((151. * pow(e1, 3.)) / 96.) * sin(6. * u1);
		double v4 = ((1097. * pow(e1, 4.)) / 512.) * sin(8. * u1);

		double q1 = u1 + v1 + v2 + v3 + v4;

		double sq1 = sin(q1);
		double cq1 = cos(q1);
		double tq1 = tan(q1);

		double N1 = LocationConstants.E_R_WGS84 / sqrt(1. - LocationConstants.UTM_HOR * (sq1 * sq1));
		double R1 = (N1 * (1. - LocationConstants.UTM_HOR)) / (1. - LocationConstants.UTM_HOR * (sq1 * sq1));
		double C1 = LocationConstants.UTM_VER * (cq1 * cq1);
		double T1 = tq1 * tq1;
		double D = (x - LocationConstants.UTMK_WIDTH) / (N1 * LocationConstants.UTMK_SCALE);

		double dLat = q1
				- ((N1 * tq1) / R1)
				* ((D * D) / 2. - pow(D, 4.) / 24.
						* (5. + 3. * T1 + 10. * C1 - 4. * (C1 * C1) - 9. * LocationConstants.UTM_VER) + pow(D, 6.)
						/ 720.
						* (61. + 90. * T1 + 298. * C1 + 45. * (T1 * T1) - 252. * LocationConstants.UTM_VER - 3. * (C1 * C1)));
		double dLong = LocationConstants.UTMK_LON
				* LocationConstants.RAD
				+ (1. / cq1)
				* (D - pow(D, 3.) / 6. * (1. + (2. * T1) + C1) + pow(D, 5.)
						/ 120.
						* (5. - (2. * C1) + (28. * T1) - (3. * (C1 * C1)) + (8. * LocationConstants.UTM_VER) + (24 * (T1 * T1))));

		dLat *= LocationConstants.DEG;
		dLong *= LocationConstants.DEG;

		return new GPSCoordinate(dLong, dLat);
	}

	public static GPSCoordinate getMidPoint(double lon1, double lat1, double lon2, double lat2) {
		// double dLat = Math.toRadians((lat2 - lat1));
		double dLon = Math.toRadians((lon2 - lon1));
		double Bx = Math.cos(lat2) * Math.cos(dLon);
		double By = Math.cos(lat2) * Math.sin(dLon);
		double lat3 = Math.atan2(Math.sin(lat1) + Math.sin(lat2),
				Math.sqrt((Math.cos(lat1) + Bx) * (Math.cos(lat1) + Bx) + By * By));
		double lon3 = lon1 + Math.atan2(By, Math.cos(lat1) + Bx);
		return new GPSCoordinate(lat3, lon3);
	}

	/**
	 * Converts GPS coordinates to UTM
	 * 
	 * @param longitude
	 *            Longitude
	 * @param latitude
	 *            Latitude
	 * @return The converted output
	 */
	public static UTMCoordinate getUTMCoordinate(GPSCoordinate coordinate) {
		double lat = coordinate.getLatitude();
		double lon = coordinate.getLongitude();

		lat *= LocationConstants.RAD;
		lon *= LocationConstants.RAD;

		double slat = sin(lat);
		double clat = cos(lat);
		double tlat = tan(lat);

		double T = tlat * tlat;
		double C = LocationConstants.UTM_HOR / (1.0 - LocationConstants.UTM_HOR) * (clat * clat);
		double A = (lon - LocationConstants.UTMK_LON * LocationConstants.RAD) * clat;
		double N = LocationConstants.E_R_WGS84 / sqrt(1.0 - (LocationConstants.UTM_HOR * slat * slat));
		double M = LocationConstants.E_R_WGS84
				* curvature(E1F(LocationConstants.UTM_HOR), E2F(LocationConstants.UTM_HOR),
						E3F(LocationConstants.UTM_HOR), E4F(LocationConstants.UTM_HOR), lat);

		double M0 = LocationConstants.UTM_CURVATURE;

		double x = LocationConstants.UTMK_SCALE
				* N
				* (A + A * A * A / 6. * (1. - T + C) + A * A * A * A * A / 120.
						* (5. - 18. * T + T * T + 72. * C - 58. * LocationConstants.UTM_VER))
				+ LocationConstants.UTMK_WIDTH;
		double y = LocationConstants.UTMK_SCALE
				* (M - M0 + N * tlat * A * A / 2. + A * A * A * A / 24. * (5. - T + 9. * C + 4. * C * C) + A * A * A
						* A * A * A / 720. * (61. - 58. * T + T * T + 600. * C - 330. * LocationConstants.UTM_VER))
				+ LocationConstants.UTMK_HEIGHT;

		x = x * 10. + 340000000;
		y = y * 10. + 130000000;

		return new UTMCoordinate((int) x, (int) y);
	}

	/**
	 * Calculate the spherical distance between two GeoCoordinates in meters using the Haversine formula.
	 * 
	 * This calculation is done using the assumption, that the earth is a sphere, it is not though. If you need a higher
	 * precision and can afford a longer execution time you might want to use vincentyDistance
	 * 
	 * @param lon1
	 *            longitude of first coordinate
	 * @param lat1
	 *            latitude of first coordinate
	 * @param lon2
	 *            longitude of second coordinate
	 * @param lat2
	 *            latitude of second coordinate
	 * 
	 * @return distance in meters as a double
	 * @throws IllegalArgumentException
	 *             if one of the arguments is null
	 */
	public static double haversineDistance(double lon1, double lat1, double lon2, double lat2) {
		double dLat = Math.toRadians(lat2 - lat1);
		double dLon = Math.toRadians(lon2 - lon1);
		double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(Math.toRadians(lat1))
				* Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2) * Math.sin(dLon / 2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

		return c * LocationConstants.EQUATORIALRADIUS;
	}

	/**
	 * Converts a coordinate from microdegrees to degrees.
	 * 
	 * @param coordinate
	 *            the coordinate in microdegrees.
	 * @return the coordinate in degrees.
	 */
	public static double intToDouble(int coordinate) {
		return coordinate / FACTOR_DOUBLE_TO_INT;
	}

	/**
	 * Determines whether one Location reading is better than the current Location fix
	 * 
	 * @param location
	 *            The new Location that you want to evaluate
	 * @param currentBestLocation
	 *            The Location fix, to which you want to compare the new one
	 * @return true of {@code location} is better that {@code currentBestLocation}
	 */
	public static boolean isBetterLocation(Location location, Location currentBestLocation) {
		 final int TWO_MINUTES = 1000 * 60 * 2;
		if (currentBestLocation == null) {
			// A new location is always better than no location
			return true;
		}

		// Check whether the new location fix is newer or older
		long timeDelta = location.getTime() - currentBestLocation.getTime();
		boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
		boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
		boolean isNewer = timeDelta > 0;

		// If it's been more than two minutes since the current location, use the new location
		// because the user has likely moved
		if (isSignificantlyNewer) {
			return true;
			// If the new location is more than two minutes older, it must be worse
		} else if (isSignificantlyOlder) {
			return false;
		}

		// Check whether the new location fix is more or less accurate
		int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
		boolean isLessAccurate = accuracyDelta > 0;
		boolean isMoreAccurate = accuracyDelta < 0;
		boolean isSignificantlyLessAccurate = accuracyDelta > 200;

		// Check if the old and new location are from the same provider
		boolean isFromSameProvider = isSameProvider(location.getProvider(), currentBestLocation.getProvider());

		// Determine location quality using a combination of timeliness and accuracy
		if (isMoreAccurate) {
			return true;
		} else if (isNewer && !isLessAccurate) {
			return true;
		} else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
			return true;
		}
		return false;
	}


	/** Checks whether two providers are the same */
	private static boolean isSameProvider(String provider1, String provider2) {
		if (provider1 == null) {
			return provider2 == null;
		}
		return provider1.equals(provider2);
	}

	/**
	 * Calculate the amount of degrees of latitude for a given distance in meters.
	 * 
	 * @param meters
	 *            distance in meters
	 * @return latitude degrees
	 */
	public static double latitudeDistance(int meters) {
		return (meters * 360) / (2 * Math.PI * LocationConstants.EQUATORIALRADIUS);
	}

	/**
	 * Calculate the spherical distance between two points using the Spherical law of Cosines formula.
	 * <p>
	 * Its a more accurate than the haversine distance and gives an accuracy of upto 1 metre.
	 * </p>
	 * 
	 * @param lon1
	 * @param lat1
	 * @param lon2
	 * @param lat2
	 * @return Distance in Km
	 */
	public static double lawOfCosineDistance(double lon1, double lat1, double lon2, double lat2) {
		return Math.acos(Math.sin(lat1) * Math.sin(lat2) + Math.cos(lat1) * Math.cos(lat2) * Math.cos(lon2 - lon1)) * 6371;
	}

	/**
	 * Calculate the amount of degrees of longitude for a given distance in meters.
	 * 
	 * @param meters
	 *            distance in meters
	 * @param latitude
	 *            the latitude at which the calculation should be performed
	 * @return longitude degrees
	 */
	public static double longitudeDistance(int meters, double latitude) {
		return (meters * 360) / (2 * Math.PI * LocationConstants.EQUATORIALRADIUS * Math.cos(Math.toRadians(latitude)));
	}

	/**
	 * Calculate the amount of degrees of longitude for a given distance in meters.
	 * 
	 * @param meters
	 *            distance in meters
	 * @param latitude
	 *            the latitude at which the calculation should be performed
	 * @return longitude degrees
	 */
	public static double longitudeDistance(int meters, int latitude) {
		return (meters * 360)
				/ (2 * Math.PI * LocationConstants.EQUATORIALRADIUS * Math.cos(Math.toRadians(intToDouble(latitude))));
	}

	/**
	 * Convert the list of coordinates to GPX format.
	 * 
	 * @param g
	 *            coordinates
	 * @return Coordinates in GPX format
	 */
	public static String toGPX(final List<GPSCoordinate> g) {
		StringBuilder sb = new StringBuilder();
		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\" ?>").append("\n");
		sb.append(
				"<gpx xmlns=\"http://www.topografix.com/GPX/1/1\" creator=\"byHand\" "
						+ "version=\"1.1\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "
						+ "xsi:schemaLocation=\"http://www.topografix.com/GPX/1/1 "
						+ "http://www.topografix.com/GPX/1/1/gpx.xsd\">").append("\n");
		for (GPSCoordinate c : g) {
			sb.append("\t<wpt ").append("lat=\"").append(c.getLatitude()).append("\" ");
			sb.append("lon=\"").append(c.getLongitude()).append("\"/>");
			sb.append("\n");
		}
		sb.append("</gpx>");

		return sb.toString();
	}

	/**
	 * Checks the given latitude value and throws an exception if the value is out of range.
	 * 
	 * @param lat
	 *            the latitude value that should be checked.
	 * @return the latitude value.
	 * @throws IllegalArgumentException
	 *             if the latitude value is < LATITUDE_MIN or > LATITUDE_MAX.
	 */
	public static double validateLatitude(double lat) {
		if (lat < LATITUDE_MIN) {
			throw new IllegalArgumentException("invalid latitude value: " + lat);
		} else if (lat > LATITUDE_MAX) {
			throw new IllegalArgumentException("invalid latitude value: " + lat);
		} else {
			return lat;
		}
	}

	/**
	 * @param location
	 * @param currentBestLocation
	 */

	/**
	 * Checks the given longitude value and throws an exception if the value is out of range.
	 * 
	 * @param lon
	 *            the longitude value that should be checked.
	 * @return the longitude value.
	 * @throws IllegalArgumentException
	 *             if the longitude value is < LONGITUDE_MIN or > LONGITUDE_MAX.
	 */
	public static double validateLongitude(double lon) {
		if (lon < LONGITUDE_MIN) {
			throw new IllegalArgumentException("invalid longitude value: " + lon);
		} else if (lon > LONGITUDE_MAX) {
			throw new IllegalArgumentException("invalid longitude value: " + lon);
		} else {
			return lon;
		}
	}

	/**
	 * Calculates geodetic distance between two GeoCoordinates using Vincenty inverse formula for ellipsoids. This is
	 * very accurate but consumes more resources and time than the sphericalDistance method
	 * 
	 * Adaptation of Chriss Veness' JavaScript Code on http://www.movable-type.co.uk/scripts/latlong-vincenty.html
	 * 
	 * Paper: Vincenty inverse formula - T Vincenty, "Direct and Inverse Solutions of Geodesics on the Ellipsoid with
	 * application of nested equations", Survey Review, vol XXII no 176, 1975
	 * (http://www.ngs.noaa.gov/PUBS_LIB/inverse.pdf)
	 * 
	 * @param gc1
	 *            first GeoCoordinate
	 * @param gc2
	 *            second GeoCoordinate
	 * 
	 * @return distance in meters between points as a double
	 */
	public static double vincentyDistance(GPSCoordinate gc1, GPSCoordinate gc2) {
		double f = 1 / LocationConstants.INVERSEFLATTENING;
		double L = Math.toRadians(gc2.getLongitude() - gc1.getLongitude());
		double U1 = Math.atan((1 - f) * Math.tan(Math.toRadians(gc1.getLatitude())));
		double U2 = Math.atan((1 - f) * Math.tan(Math.toRadians(gc2.getLatitude())));
		double sinU1 = Math.sin(U1), cosU1 = Math.cos(U1);
		double sinU2 = Math.sin(U2), cosU2 = Math.cos(U2);

		double lambda = L, lambdaP, iterLimit = 100;

		double cosSqAlpha = 0, sinSigma = 0, cosSigma = 0, cos2SigmaM = 0, sigma = 0, sinLambda = 0, sinAlpha = 0, cosLambda = 0;
		do {
			sinLambda = Math.sin(lambda);
			cosLambda = Math.cos(lambda);
			sinSigma = Math.sqrt((cosU2 * sinLambda) * (cosU2 * sinLambda)
					+ (cosU1 * sinU2 - sinU1 * cosU2 * cosLambda) * (cosU1 * sinU2 - sinU1 * cosU2 * cosLambda));
			if (sinSigma == 0) {
				return 0; // co-incident points
			}
			cosSigma = sinU1 * sinU2 + cosU1 * cosU2 * cosLambda;
			sigma = Math.atan2(sinSigma, cosSigma);
			sinAlpha = cosU1 * cosU2 * sinLambda / sinSigma;
			cosSqAlpha = 1 - sinAlpha * sinAlpha;
			if (cosSqAlpha != 0) {
				cos2SigmaM = cosSigma - 2 * sinU1 * sinU2 / cosSqAlpha;
			} else {
				cos2SigmaM = 0;
			}
			double C = f / 16 * cosSqAlpha * (4 + f * (4 - 3 * cosSqAlpha));
			lambdaP = lambda;
			lambda = L + (1 - C) * f * sinAlpha
					* (sigma + C * sinSigma * (cos2SigmaM + C * cosSigma * (-1 + 2 * cos2SigmaM * cos2SigmaM)));
		} while ((Math.abs(lambda - lambdaP) > 1e-12) && (--iterLimit > 0));

		if (iterLimit == 0) {
			return 0; // formula failed to converge
		}

		double uSq = cosSqAlpha
				* (Math.pow(LocationConstants.EQUATORIALRADIUS, 2) - Math.pow(LocationConstants.POLARRADIUS, 2))
				/ Math.pow(LocationConstants.POLARRADIUS, 2);
		double A = 1 + uSq / 16384 * (4096 + uSq * (-768 + uSq * (320 - 175 * uSq)));
		double B = uSq / 1024 * (256 + uSq * (-128 + uSq * (74 - 47 * uSq)));
		double deltaSigma = B
				* sinSigma
				* (cos2SigmaM + B
						/ 4
						* (cosSigma * (-1 + 2 * cos2SigmaM * cos2SigmaM) - B / 6 * cos2SigmaM
								* (-3 + 4 * sinSigma * sinSigma) * (-3 + 4 * cos2SigmaM * cos2SigmaM)));
		double s = LocationConstants.POLARRADIUS * A * (sigma - deltaSigma);

		return s;
	}
}
