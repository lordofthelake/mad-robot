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
package com.madrobot.graphics;

import android.graphics.Color;

/**
 * Color manipulation routines
 * <p>
 * Colors are represented in a hex triplet.
 * </p>
 * 
 */
public final class ColorUtils {

	/**
	 * Change the brightness of a color
	 * 
	 * @param color
	 * @param percent
	 *            to increase brightness. Negative value indicates decrease
	 *            brightness
	 * @return Hex color with the adjusted brightness
	 */
	public static int adjustBrightness(int color, int percent) {
		int nChange = 255 * percent / 100;
		int r = ((color & 0x00FF0000) >> 16) + nChange;
		int g = ((color & 0x0000FF00) >> 8) + nChange;
		int b = (color & 0x000000FF) + nChange;

		r = r < 0 ? 0 : (r > 255 ? 255 : r);
		g = g < 0 ? 0 : (g > 255 ? 255 : g);
		b = b < 0 ? 0 : (b > 255 ? 255 : b);

		return (r << 16) | (g << 8) | b;
	}

	/**
	 * Apply sepia to the given color
	 * 
	 * @param color
	 *            argb value
	 * @param depth
	 *            sepia depth , optimal is 20
	 * @return
	 */
	public static int applySepia(int color, int depth) {
		int a = (color >> 24) & 0xff;
		int r = (color >> 16) & 0xff;
		int g = (color >> 8) & 0xff;
		int b = color & 0xff;

		int gry = (r + g + b) / 3;
		r = g = b = gry;

		r = r + (depth * 2);
		g = g + depth;
		if (r > 255) {
			r = 255;
		}
		if (g > 255) {
			g = 255;
		}
		return toRGB(a, r, g, b);
	}

	/**
	 * Blend two colors
	 * 
	 * @param color1
	 * @param color2
	 * @param f
	 *            percentage
	 * @return Blended color
	 */
	public static int blend(int color1, int color2, float f) {
		return mix(color1 >>> 24, color2 >>> 24, f) << 24
				| mix((color1 >> 16) & 0xff, (color2 >> 16) & 0xff, f) << 16
				| mix((color1 >> 8) & 0xff, (color2 >> 8) & 0xff, f) << 8
				| mix(color1 & 0xff, color2 & 0xff, f);
	}

	/**
	 * Clear the transparency in a color
	 * 
	 * @param color
	 * @return
	 */
	public static int clearTransparency(int color) {
		return color & 0x00FFFFFF;
	}

	// /**
	// * Blend two colors.
	// *
	// * @param color1
	// * First color to blend.
	// * @param color2
	// * Second color to blend.
	// * @param ratio
	// * Blend ratio. 0.5 will give even blend, 1.0 will return
	// * color1, 0.0 will return color2 and so on.
	// * @return Blended color.
	// */
	// public static Color blend(Color color1, Color color2, float ratio) {
	// float r = (float) ratio;
	// float ir = (float) 1.0 - r;
	//
	// float rgb1[] = new float[3];
	// float rgb2[] = new float[3];
	//
	// color1.getColorComponents(rgb1);
	// color2.getColorComponents(rgb2);
	//
	// Color color = new Color(rgb1[0] * r + rgb2[0] * ir, rgb1[1] * r + rgb2[1]
	// * ir, rgb1[2] * r
	// + rgb2[2] * ir);
	//
	// return color;
	// }

	/**
	 * Convert color array to HSV color model
	 * 
	 * @param argb
	 */
	public static void convertToHSV(int[] argb) {
		for (int i = 0; i < argb.length; i++) {

			int nR = Color.red(argb[i]);
			int nG = Color.green(argb[i]);
			int nB = Color.blue(argb[i]);
			int nMax, nMid, nMin;
			int nHueOffset;
			// determine color order
			if ((nR > nG) && (nR > nB)) {
				// red is max
				nMax = nR;
				nHueOffset = 0;
				if (nG > nB) {
					nMid = nG;
					nMin = nB;
				} else {
					nMid = nB;
					nMin = nG;
				}
			} else if ((nG > nR) && (nG > nB)) {
				// green is max
				nMax = nG;
				nHueOffset = 80;
				if (nR > nB) {
					nMid = nR;
					nMin = nB;
				} else {
					nMid = nB;
					nMin = nR;
				}
			} else {
				// blue is max
				nMax = nB;
				nHueOffset = 160;
				if (nR > nG) {
					nMid = nR;
					nMin = nG;
				} else {
					nMid = nG;
					nMin = nR;
				}
			}
			// if the max value is Byte.MIN_VALUE the RGB value
			// = 0 so the HSV value = 0 and needs no change.
			if (nMax > Byte.MIN_VALUE) {
				if (nMax == nMin) {
					// color is gray. Hue, saturation are 0.
					argb[i] = Color.rgb(Byte.MIN_VALUE, Byte.MIN_VALUE,
							(byte) nMax);
				} else {
					// compute hue scaled from 0-240.
					int nHue = Math.min(239, nHueOffset + (40 * (nMid - nMin))
							/ (nMax - nMin));
					// compute saturation scaled from 0-255.
					int nSat = Math.min(255, (256 * (nMax - nMin))
							/ (nMax - Byte.MIN_VALUE));
					argb[i] = Color.rgb((byte) (nHue + Byte.MIN_VALUE),
							(byte) (nSat + Byte.MIN_VALUE), (byte) nMax);
				}
			}
		}
	}

	/**
	 * Convert a color to grayscale
	 * 
	 * @param color
	 * @return
	 */
	public static final int covertToGrayscale(int color) {
		int a = (color >> 24) & 0xff;
		int r = (color >> 16) & 0xff;
		int g = (color >> 8) & 0xff;
		int b = color & 0xff;
		int gray = (r + g + b) / 3;
		return (a << 24) | (gray << 16) | (gray << 8) | gray;
	}

	public static int getAlpha(int argb) {
		return Color.alpha(argb);
	}

	public static int getBlue(int argb) {
		return Color.blue(argb);
	}

	/**
	 * Get a brighter shade
	 * 
	 * @param color
	 * @return
	 */
	public static int getBrighterColor(int color) {
		int i = getRed(color);
		int j = getGreen(color);
		int k = getBlue(color);
		byte byte0 = 3;
		if ((i == 0) && (j == 0) && (k == 0)) {
			return toRGB(255, byte0, byte0, byte0);
		}
		if ((i > 0) && (i < byte0)) {
			i = byte0;
		}
		if ((j > 0) && (j < byte0)) {
			j = byte0;
		}
		if ((k > 0) && (k < byte0)) {
			k = byte0;
		}
		return toRGB(255, Math.min((int) (i / 0.69999999999999996D), 255), Math
				.min((int) (j / 0.69999999999999996D), 255), Math.min(
				(int) (k / 0.69999999999999996D), 255));

	}

	/**
	 * 
	 * @param color
	 * @param shades
	 *            The number of shades brighter
	 * @return
	 */
	public static int getBrighterColor(int color, int shades) {
		for (int i = 0; i < shades; i++) {
			color = getBrighterColor(color);
		}
		return color;
	}

	/**
	 * 
	 * @param color
	 * @return
	 */
	public static int getDarkerColor(int color) {
		return toRGB(255, Math.max(
				(int) (getRed(color) * 0.69999999999999996D), 0), Math.max(
				(int) (getGreen(color) * 0.69999999999999996D), 0), Math.max(
				(int) (getBlue(color) * 0.69999999999999996D), 0));

	}

	/**
	 * 
	 * @param color
	 * @param shades
	 *            The number of shades brighter
	 * @return
	 */
	public static int getDarkerColor(int color, int shades) {
		for (int i = 0; i < shades; i++) {
			color = getDarkerColor(color);
		}
		return color;
	}

	/**
	 * Get the distance between two colors
	 * 
	 * @param r1
	 *            Red component of the first color
	 * @param g1
	 *            Green component of the first color
	 * @param b1
	 *            Blue component of the first color
	 * @param r2
	 *            Red component of the second color
	 * @param g2
	 *            Green component of the second color
	 * @param b2
	 *            Blue component of the second color
	 * @return Distance bwetween colors
	 */
	public static double getDistance(int r1, int g1, int b1, int r2, int g2,
			int b2) {
		double a = r2 - r1;
		double b = g2 - g1;
		double c = b2 - b1;

		return Math.sqrt(a * a + b * b + c * c);
	}

	/**
	 * Creates a gradient of colors. This method is highly optimized and only
	 * uses bit-shifting and additions (no multitplication nor devision), but it
	 * will create a new integer array in each call.
	 * 
	 * @param startColor
	 *            the first color
	 * @param endColor
	 *            the last color
	 * @param steps
	 *            the number of colors in the gradient, when 2 is given, the
	 *            first one will be the startColor and the second one will the
	 *            endColor.
	 * @return an int array with the gradient.
	 * 
	 */
	public static final int[] getGradient(int startColor, int endColor,
			int steps) {
		int[] gradient = new int[steps];
		getGradient(startColor, endColor, gradient);
		return gradient;
	}

	/**
	 * Creates a gradient of colors. This method is highly optimized and only
	 * uses bit-shifting and additions (no multitplication nor devision).
	 * 
	 * @param startColor
	 *            The first color
	 * @param endColor
	 *            The last color
	 * @param gradient
	 *            The array in which the gradient colors are stored. length of
	 *            the array is the number of steps used in the gradient
	 * 
	 */
	public static final void getGradient(final int startColor,
			final int endColor, final int[] gradient) {
		int steps = gradient.length;
		if (steps == 0) {
			return;
		} else if (steps == 1) {
			gradient[0] = startColor;
			return;
		}
		int startAlpha = startColor >>> 24;
		int startRed = (startColor >>> 16) & 0x00FF;
		int startGreen = (startColor >>> 8) & 0x0000FF;
		int startBlue = startColor & 0x00000FF;

		final int endAlpha = endColor >>> 24;
		final int endRed = (endColor >>> 16) & 0x00FF;
		final int endGreen = (endColor >>> 8) & 0x0000FF;
		final int endBlue = endColor & 0x00000FF;

		final int stepAlpha = ((endAlpha - startAlpha) << 8) / (steps - 1);
		final int stepRed = ((endRed - startRed) << 8) / (steps - 1);
		final int stepGreen = ((endGreen - startGreen) << 8) / (steps - 1);
		final int stepBlue = ((endBlue - startBlue) << 8) / (steps - 1);
		startAlpha <<= 8;
		startRed <<= 8;
		startGreen <<= 8;
		startBlue <<= 8;

		gradient[0] = startColor;
		for (int i = 1; i < steps; i++) {
			startAlpha += stepAlpha;
			startRed += stepRed;
			startGreen += stepGreen;
			startBlue += stepBlue;

			gradient[i] = ((startAlpha << 16) & 0xFF000000)
					| ((startRed << 8) & 0x00FF0000)
					| (startGreen & 0x0000FF00) | (startBlue >>> 8);
			// | (( startBlue >>> 8) & 0x000000FF);
		}
	}

	public static int getGreen(final int argb) {
		return Color.green(argb);
	}

	/**
	 * Return the hex name of a specified color.
	 * 
	 * @param color
	 *            Color to get hex name of.
	 * @return Hex name of color: "rrggbb".
	 */
	public static String getHexName(int color) {
		int r = getRed(color);
		int g = getGreen(color);
		int b = getBlue(color);

		String rHex = Integer.toString(r, 16);
		String gHex = Integer.toString(g, 16);
		String bHex = Integer.toString(b, 16);

		return (rHex.length() == 2 ? "" + rHex : "0" + rHex)
				+ (gHex.length() == 2 ? "" + gHex : "0" + gHex)
				+ (bHex.length() == 2 ? "" + bHex : "0" + bHex);
	}

	public static int getRed(final int argb) {
		return Color.red(argb);
		// return argb >> 16 & 0xff;
	}

	/**
	 * Convert from HSB color to RGB
	 * 
	 * @param hue
	 * @param saturation
	 * @param brightness
	 * @return RGB value of given hue, saturation, brightness.
	 */
	public static int HSBtoRGB(final float hue, final float saturation,
			final float brightness) {
		int i = 0;
		int j = 0;
		int k = 0;
		if (saturation == 0.0F) {
			i = j = k = (int) (brightness * 255F + 0.5F);
		} else {
			float f3 = (hue - (float) Math.floor(hue)) * 6F;
			float f4 = f3 - (float) Math.floor(f3);
			float f5 = brightness * (1.0F - saturation);
			float f6 = brightness * (1.0F - saturation * f4);
			float f7 = brightness * (1.0F - saturation * (1.0F - f4));
			switch ((int) f3) {
			case 0: // '\0'
				i = (int) (brightness * 255F + 0.5F);
				j = (int) (f7 * 255F + 0.5F);
				k = (int) (f5 * 255F + 0.5F);
				break;

			case 1: // '\001'
				i = (int) (f6 * 255F + 0.5F);
				j = (int) (brightness * 255F + 0.5F);
				k = (int) (f5 * 255F + 0.5F);
				break;

			case 2: // '\002'
				i = (int) (f5 * 255F + 0.5F);
				j = (int) (brightness * 255F + 0.5F);
				k = (int) (f7 * 255F + 0.5F);
				break;

			case 3: // '\003'
				i = (int) (f5 * 255F + 0.5F);
				j = (int) (f6 * 255F + 0.5F);
				k = (int) (brightness * 255F + 0.5F);
				break;

			case 4: // '\004'
				i = (int) (f7 * 255F + 0.5F);
				j = (int) (f5 * 255F + 0.5F);
				k = (int) (brightness * 255F + 0.5F);
				break;

			case 5: // '\005'
				i = (int) (brightness * 255F + 0.5F);
				j = (int) (f5 * 255F + 0.5F);
				k = (int) (f6 * 255F + 0.5F);
				break;
			}
		}
		return 0xff000000 | i << 16 | j << 8 | k << 0;
	}

	/**
	 * Invert the color
	 * 
	 * @param color
	 * @return inverted color
	 */
	public static int invertColor(int color) {
		int a = (color >> 24) & 0xff;
		int r = (color >> 16) & 0xff;
		int g = (color >> 8) & 0xff;
		int b = color & 0xff;

		r = 255 - r;
		g = 255 - g;
		b = 255 - b;
		return toRGB(r, g, b, a);
	}

	/**
	 * Check if a color is more dark than light. Useful if an entity of this
	 * color is to be labeled: Use white label on a "dark" color and black label
	 * on a "light" color.
	 * 
	 * @param color
	 *            Color to check.
	 * @return True if this is a "dark" color, false otherwise.
	 */
	public static boolean isDark(int color) {
		int r = getRed(color) / 255;
		int g = getGreen(color) / 255;
		int b = getBlue(color) / 255;

		return isDark(r, g, b);
	}

	/**
	 * Check if a color is more dark than light. Useful if an entity of this
	 * color is to be labeled: Use white label on a "dark" color and black label
	 * on a "light" color.
	 * 
	 * @param r
	 *            ,g,b Color to check.
	 * @return True if this is a "dark" color, false otherwise.
	 */
	public static boolean isDark(int r, int g, int b) {
		// Measure distance to white and black respectively
		double dWhite = getDistance(r, g, b, 255, 255, 255);
		double dBlack = getDistance(r, g, b, 0, 0, 0);

		return dBlack < dWhite;
	}

	/**
	 * Color between two colors
	 * 
	 * @param color1
	 *            argb value of first color
	 * @param color2
	 *            argb value of second color
	 * @param prop
	 * @param max
	 * @return Middle color
	 */
	public static final int middleColor(int color1, int color2, int prop,
			int max) {
		int red = (((color1 >> 16) & 0xff) * prop + ((color2 >> 16) & 0xff)
				* (max - prop))
				/ max;
		int green = (((color1 >> 8) & 0xff) * prop + ((color2 >> 8) & 0xff)
				* (max - prop))
				/ max;
		int blue = (((color1 >> 0) & 0xff) * prop + ((color2 >> 0) & 0xff)
				* (max - prop))
				/ max;
		int color = red << 16 | green << 8 | blue;
		return color;
	}

	private static int mix(int a, int b, float f) {
		return (int) (a + (b - a) * f);
	}

	/**
	 * Posterize a given pixel
	 * 
	 * @param argb
	 *            color
	 * @param depth
	 *            of posterization
	 * @return
	 */
	public static int posterizePixel(int argb, int depth) {
		int noOfAreas = 256 / depth;
		int noOfValues = 256 / (depth - 1);

		int a = (argb >> 24) & 0xff;
		int r = (argb >> 16) & 0xff;
		int g = (argb >> 8) & 0xff;
		int b = argb & 0xff;

		int redArea = r / noOfAreas;
		r = noOfValues * redArea;

		int greenArea = g / noOfAreas;
		g = noOfValues * greenArea;

		int blue = b / noOfAreas;
		b = noOfValues * blue;

		return toRGB(a, r, g, b);
	}

	/**
	 * Convert from RGB to HSB color
	 * 
	 * @param r
	 * @param g
	 * @param b
	 * @param hsbvals
	 *            the array used to return the three HSB values, or null
	 * 
	 * @return a float array containing hue, saturation, brightness.
	 */
	public static float[] RGBtoHSB(int r, int g, int b, float hsbvals[]) {
		if (hsbvals == null) {
			hsbvals = new float[3];
		}
		int l = r <= g ? g : r;
		if (b > l) {
			l = b;
		}
		int i1 = r >= g ? g : r;
		if (b < i1) {
			i1 = b;
		}
		float f2 = l / 255F;
		float f1;
		if (l != 0) {
			f1 = (float) (l - i1) / (float) l;
		} else {
			f1 = 0.0F;
		}
		float f;
		if (f1 == 0.0F) {
			f = 0.0F;
		} else {
			float f3 = (float) (l - r) / (float) (l - i1);
			float f4 = (float) (l - g) / (float) (l - i1);
			float f5 = (float) (l - b) / (float) (l - i1);
			if (r == l) {
				f = f5 - f4;
			} else if (g == l) {
				f = (2.0F + f3) - f5;
			} else {
				f = (4F + f4) - f3;
			}
			f /= 6F;
			if (f < 0.0F) {
				f++;
			}
		}
		hsbvals[0] = f;
		hsbvals[1] = f1;
		hsbvals[2] = f2;
		return hsbvals;
	}

	/**
	 * set the luminosity of a ARGB value
	 * 
	 * @param color
	 *            argb value
	 * @param percent
	 *            percent always less than or equal to 100
	 * @return
	 */
	public static int setLuminosity(int color, int percent) {
		int a = (color >> 24) & 0xff;
		int r = (color >> 16) & 0xff;
		int g = (color >> 8) & 0xff;
		int b = color & 0xff;

		int t = (r * percent) / 100;
		r -= t;
		t = (g * percent) / 100;
		g -= t;
		t = (b * percent) / 100;
		b -= t;
		return toRGB(a, r, g, b);
	}

	/**
	 * Set the saturation for a given color
	 * <p>
	 * The saturation of a color is determined by a combination of light
	 * intensity and how much it is distributed across the spectrum of different
	 * wavelengths.<br/>
	 * <img src="../../../resources/saturation.png"> <small>Scale of saturation
	 * (0% at bottom).</small>
	 * </p>
	 * 
	 * @param color
	 *            argb value
	 * @param percent
	 *            percent always less than or equal to 100
	 * @return color with the set saturation level
	 */
	public static int setSaturation(int color, int percent) {
		int per = (128 * percent) / 100;
		int a = (color >> 24) & 0xff;
		int r = (color >> 16) & 0xff;
		int g = (color >> 8) & 0xff;
		int b = color & 0xff;
		r = (r + per) / 2;
		g = (g + per) / 2;
		b = (b + per) / 2;
		return toRGB(a, r, g, b);
	}

	/**
	 * 
	 * @param color
	 *            argb value
	 * @param level
	 *            0(fully transparent) to 255(fully opaque)
	 * @return
	 */
	public static int setTransparency(int color, int level) {
		level = (level << 24);
		return (color & 0x00ffffff) | level;
	}

	public static void setTransparency(int[] color, int level) {
		level = level << 24;
		for (int i = 0; i < color.length; i++) {
			color[i] = (color[i] & 0x00ffffff) | level;
		}
	}

	/**
	 * 
	 * @param alpha
	 * @param red
	 * @param green
	 * @param blue
	 * @return
	 */
	public static int toRGB(int alpha, int red, int green, int blue) {
		return (alpha & 0xff) << 24 | (red & 0xff) << 16 | (green & 0xff) << 8
				| (blue & 0xff) << 0;
	}

	private ColorUtils() {
	}

	// public static final int getR(int color) {
	// return ((color & 0x00ff0000) >> 16);
	// }
	//
	// public static final int getG(int color) {
	// return ((color & 0x0000ff00) >> 8);
	// }
	//
	// public static final int getB(int color) {
	// return ((color & 0x000000ff) >> 0);
	// }

	/**
	 * 
	 * @param color
	 * @return
	 */
	public int getTransparency(int color) {
		int i = getAlpha(color);
		if (i == 255) {
			return 1;
		}
		return i != 0 ? 3 : 2;
	}

	// public static int toARGB(int a, int r, int g, int b) {
	// return (a << 24) + (r << 16) + (g << 8) + b;
	// }
}
