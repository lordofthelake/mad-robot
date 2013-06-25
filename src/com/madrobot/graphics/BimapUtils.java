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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;

import com.madrobot.io.IOUtils;
import com.madrobot.math.MathUtils;

public final class BimapUtils {

	private static final int bottom = 8 * 1024;

	public static void addBitmapLayer(Bitmap src, Bitmap layer, int left,
			int top) throws IllegalArgumentException {
		if (!src.isMutable()) {
			throw new IllegalArgumentException("Bitmap is not mutable");
		}
		Canvas canvas = new Canvas(src);
		canvas.drawBitmap(layer, left, top, null);
	}

	private static Bitmap applyFilter(Bitmap bitmap, int value,
			byte[][] filter, Bitmap.Config outputConfig) {
		int[] argbData = getPixels(bitmap);
		applyFilter(filter, value, argbData, bitmap.getWidth(),
				bitmap.getHeight());
		return Bitmap.createBitmap(argbData, bitmap.getWidth(),
				bitmap.getHeight(), outputConfig);

	}

	/**
	 * Performs a convolution of an image with a given matrix.
	 * 
	 * @param filterMatrix
	 *            a matrix, which should have odd rows an colums (not
	 *            neccessarily a square). The matrix is used for a 2-dimensional
	 *            convolution. Negative values are possible.
	 * 
	 * @param brightness
	 *            you can vary the brightness of the image measured in percent.
	 *            Note that the algorithm tries to keep the original brightness
	 *            as far as is possible.
	 * 
	 * @param argbData
	 *            the image (RGB+transparency)
	 * 
	 * @param width
	 *            of the given Image
	 * 
	 * @param height
	 *            of the given Image Be aware that the computation time depends
	 *            on the size of the matrix.
	 * @throws IllegalArgumentException
	 *             if the filter matrix length is an even number
	 */
	private final static void applyFilter(byte[][] filterMatrix,
			int brightness, int[] argbData, int width, int height) {
		// ############ tested by $t3p#3n on 29-july-08 #################//
		int COLOR_BIT_MASK = 0x000000FF;
		// check whether the matrix is ok
		if ((filterMatrix.length % 2 != 1) || (filterMatrix[0].length % 2 != 1)) {
			throw new IllegalArgumentException();
		}

		int fhRadius = filterMatrix.length / 2 + 1;
		int fwRadius = filterMatrix[0].length / 2 + 1;
		int currentPixel = 0;
		int newTran, newRed, newGreen, newBlue;

		// compute the brightness
		int divisor = 0;
		for (int fCol, fRow = 0; fRow < filterMatrix.length; fRow++) {
			for (fCol = 0; fCol < filterMatrix[0].length; fCol++) {
				divisor += filterMatrix[fRow][fCol];
			}
		}
		// TODO: if (divisor==0), because of negativ matrixvalues
		if (divisor == 0) {
			return; // no brightness
		}

		// copy the neccessary imagedata into a small buffer
		int[] tmpRect = new int[width * (filterMatrix.length)];
		System.arraycopy(argbData, 0, tmpRect, 0, width * (filterMatrix.length));

		for (int fCol, fRow, col, row = fhRadius - 1; row + fhRadius < height + 1; row++) {
			for (col = fwRadius - 1; col + fwRadius < width + 1; col++) {

				// perform the convolution
				newTran = 0;
				newRed = 0;
				newGreen = 0;
				newBlue = 0;

				for (fRow = 0; fRow < filterMatrix.length; fRow++) {

					for (fCol = 0; fCol < filterMatrix[0].length; fCol++) {

						// take the Data from the little buffer and skale the
						// color
						currentPixel = tmpRect[fRow * width + col + fCol
								- fwRadius + 1];
						if (((currentPixel >>> 24) & COLOR_BIT_MASK) != 0) {
							newTran += filterMatrix[fRow][fCol]
									* ((currentPixel >>> 24) & COLOR_BIT_MASK);
							newRed += filterMatrix[fRow][fCol]
									* ((currentPixel >>> 16) & COLOR_BIT_MASK);
							newGreen += filterMatrix[fRow][fCol]
									* ((currentPixel >>> 8) & COLOR_BIT_MASK);
							newBlue += filterMatrix[fRow][fCol]
									* (currentPixel & COLOR_BIT_MASK);
						}

					}
				}

				// calculate the color
				newTran = newTran * brightness / 100 / divisor;
				newRed = newRed * brightness / 100 / divisor;
				newGreen = newGreen * brightness / 100 / divisor;
				newBlue = newBlue * brightness / 100 / divisor;

				newTran = Math.max(0, Math.min(255, newTran));
				newRed = Math.max(0, Math.min(255, newRed));
				newGreen = Math.max(0, Math.min(255, newGreen));
				newBlue = Math.max(0, Math.min(255, newBlue));
				argbData[(row) * width + col] = (newTran << 24 | newRed << 16
						| newGreen << 8 | newBlue);

			}

			// shift the buffer if we are not near the end
			if (row + fhRadius != height) {
				System.arraycopy(tmpRect, width, tmpRect, 0, width
						* (filterMatrix.length - 1)); // shift
				// it
				// back
				System.arraycopy(argbData, width * (row + fhRadius), tmpRect,
						width * (filterMatrix.length - 1), width); // add
				// new
				// data
			}
		}
		// return Image.createRGBImage(argbData, width, height, true);
	}

	/**
	 * Apply sepia filters
	 * 
	 * @param bitmap
	 * @param depth
	 *            Sepia depth. values between 1-100 provide an optimal output.
	 * @param outputConfig
	 *            Bitmap configuration of the output bitmap
	 * @return
	 */
	public static final Bitmap applySepia(Bitmap bitmap, int depth,
			Bitmap.Config outputConfig) {
		/* Tested Apr 27 2011 */
		int[] argb = getPixels(bitmap);
		for (int i = 0; i < argb.length; i++) {
			argb[i] = ColorUtils.applySepia(argb[i], depth);
		}
		return Bitmap.createBitmap(argb, bitmap.getWidth(), bitmap.getHeight(),
				outputConfig);
	}

	/**
	 * Convert an image into a 2D array of pixels.
	 * 
	 * @param image
	 * @return
	 * @throws NullPointerException
	 *             if the image is null
	 */
	public static final int[][] convertTo2D(Bitmap bitmap) {
		if (bitmap == null) {
			throw new NullPointerException("Image is null");
		}
		int w = bitmap.getWidth();
		int h = bitmap.getHeight();
		int[] argb = getPixels(bitmap);
		int[][] twoD = new int[h][w];
		convertTo2D(argb, w, h, twoD);
		return twoD;
	}

	/**
	 * 
	 * @param argb
	 *            data
	 * @param srcWidth
	 *            argb image width
	 * @param srcHeight
	 *            argb image height
	 * @param dest
	 *            the converted array int[argb.getHeight()][argb.getWidth()];
	 */
	public static final void convertTo2D(int[] argb, int srcWidth,
			int srcHeight, int[][] dest) {
		for (int i = 0; i < srcHeight; i++) {
			for (int j = 0; j < srcWidth; j++) {
				dest[i][j] = argb[i * srcWidth + j];
			}
		}
	}

	public static Bitmap createBitmap(InputStream is) {
		Bitmap bm = BitmapFactory.decodeStream(is);
		return bm;
	}

	public static Bitmap createBitmapFromAsset(Context context, String name) {
		try {
			return createBitmap(context.getAssets().open(name));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Create a reflection of an image
	 * <p>
	 * Reflection with <code>bgColor=0xffffff</code> and
	 * <code> reflectionHeight=20</code>
	 * <table border="0">
	 * <tr>
	 * <td>
	 * <img src="../../../resources/gray.png"></td>
	 * <td><img src="../../../resources/reflection.png"></td>
	 * </tr>
	 * </table>
	 * </p>
	 * 
	 * @param image
	 *            source image
	 * @param reflectionHeight
	 *            height of the reflection
	 * @param outputConfig
	 *            Bitmap configuration of the output bitmap
	 * 
	 * @return Image
	 */
	public static final Bitmap createReflection(final Bitmap image,
			final int reflectionHeight, Bitmap.Config outputConfig) {
		/* Tested Apr 27 2011 */
		int w = image.getWidth();
		int h = image.getHeight();

		final Bitmap reflectedImage = Bitmap.createBitmap(w, h
				+ reflectionHeight, outputConfig);
		reflectedImage.setDensity(image.getDensity());
		Canvas canvas = new Canvas(reflectedImage);

		Paint bitmapPaint2 = new Paint();
		bitmapPaint2.setAntiAlias(true);

		canvas.drawBitmap(image, 0, 0, bitmapPaint2);

		int[] rgba = new int[w];
		int currentY = -1;

		for (int i = 0; i < reflectionHeight; i++) {
			int y = (h - 1) - (i * h / reflectionHeight);

			if (y != currentY) {
				image.getPixels(rgba, 0, w, 0, y, w, 1);
			}

			int alpha = 0xff - (i * 0xff / reflectionHeight);

			for (int j = 0; j < w; j++) {
				int origAlpha = (rgba[j] >> 24);
				int newAlpha = (alpha & origAlpha) * alpha / 0xff;

				rgba[j] = (rgba[j] & 0x00ffffff);
				rgba[j] = (rgba[j] | (newAlpha << 24));
			}
			canvas.drawBitmap(rgba, 0, w, 0, h + i, w, 1, true, bitmapPaint2);
		}
		return reflectedImage;
	}

	/**
	 * The weights specified are scaled so that the image average brightness
	 * should not change after the halftoning.
	 * 
	 * @param n0
	 *            the weight for pixel (r,c+1)
	 * @param n1
	 *            the weight for pixel (r,c+2)
	 * @param n2
	 *            the weight for pixel (r,c+3)
	 * @param n3
	 *            the weight for pixel (r,c+4)
	 * @param n4
	 *            the weight for pixel (r,c+5)
	 */
	public static final Bitmap deBlurHorizontalHalftone(Bitmap bitmap, int n0,
			int n1, int n2, int n3, int n4, Bitmap.Config outputConfig) {
		int sum = n0 + n1 + n2 + n3 + n4;
		n0 = 8 * n0 / sum;
		n1 = 8 * n1 / sum;
		n2 = 8 * n2 / sum;
		n3 = 8 * n3 / sum;
		n4 = 8 * n4 / sum;
		// integer division may make the sum not quite 8. correct this
		// in the weight for pixel (r,c+1)
		n0 = 8 - (n0 + n1 + n2 + n3 + n4);

		int[] bData = getPixels(bitmap);
		for (int i = 0; i < bitmap.getHeight(); i++) {
			int nRow = i * bitmap.getWidth();
			for (int j = 0; j < bitmap.getWidth(); j++) {
				int bVal = bData[nRow + j];
				int bNewVal;
				if (bVal >= 0) {
					bNewVal = Integer.MAX_VALUE;
				} else {
					bNewVal = Integer.MIN_VALUE;
				}
				int nDiff = bVal - bNewVal;
				if (j < bitmap.getWidth() - 1) {
					bData[nRow + j + 1] = Math.max(
							Integer.MIN_VALUE,
							Math.min(Integer.MAX_VALUE, bData[nRow + j + 1]
									+ n0 * nDiff / 8));
				}
				if (j < bitmap.getWidth() - 2) {
					bData[nRow + j + 2] = Math.max(
							Integer.MIN_VALUE,
							Math.min(Integer.MAX_VALUE, bData[nRow + j + 2]
									+ n1 * nDiff / 8));

				}
				if (j < bitmap.getWidth() - 3) {
					bData[nRow + j + 3] = Math.max(
							Integer.MIN_VALUE,
							Math.min(Integer.MAX_VALUE, bData[nRow + j + 3]
									+ n2 * nDiff / 8));

				}
				if (j < bitmap.getWidth() - 4) {
					bData[nRow + j + 4] = Math.max(
							Integer.MIN_VALUE,
							Math.min(Integer.MAX_VALUE, bData[nRow + j + 4]
									+ n3 * nDiff / 8));

				}
				if (j < bitmap.getWidth() - 5) {
					bData[nRow + j + 5] = Math.max(
							Integer.MIN_VALUE,
							Math.min(Integer.MAX_VALUE, bData[nRow + j + 5]
									+ n4 * nDiff / 8));

				}
			}
		}
		return Bitmap.createBitmap(bData, bitmap.getWidth(),
				bitmap.getHeight(), outputConfig);

	}

	/**
	 * Decrease the color depth of the given bitmap
	 * 
	 * @param pixel
	 * @param bitOffset
	 * @return
	 */
	public static Bitmap decreaseColorDepth(final Bitmap bitmap,
			final int bitOffset, Bitmap.Config config) {
		int A, R, G, B;
		int[] pixels = getPixels(bitmap);
		for (int i = 0; i < pixels.length; i++) {
			A = Color.alpha(pixels[i]);
			R = Color.red(pixels[i]);
			G = Color.green(pixels[i]);
			B = Color.blue(pixels[i]);
			// round-off color offset
			R = ((R + (bitOffset / 2)) - ((R + (bitOffset / 2)) % bitOffset) - 1);
			if (R < 0) {
				R = 0;
			}
			G = ((G + (bitOffset / 2)) - ((G + (bitOffset / 2)) % bitOffset) - 1);
			if (G < 0) {
				G = 0;
			}
			B = ((B + (bitOffset / 2)) - ((B + (bitOffset / 2)) % bitOffset) - 1);
			if (B < 0) {
				B = 0;
			}

			pixels[i] = Color.argb(A, R, G, B);
		}
		return Bitmap.createBitmap(pixels, bitmap.getWidth(),
				bitmap.getHeight(), config);
	}

	/**
	 * Apply Gaussian blur Filter to the given image data
	 * <p>
	 * <table border="0">
	 * <tr>
	 * <td><b>Before</b></td>
	 * <td><b>After</b></td>
	 * </tr>
	 * <tr>
	 * <td>
	 * <img src="../../../resources/before.png"></td>
	 * <td><img src="../../../resources/gaussian.png"></td>
	 * </tr>
	 * </table>
	 * </p>
	 * 
	 * @param bitmap
	 * @param brightness
	 *            of the result. Optimum values are within 200
	 * @param outputConfig
	 *            Bitmap configuration of the output bitmap
	 */
	public static final Bitmap doGaussianBlurFilter(Bitmap bitmap,
			int brightness, Bitmap.Config outputConfig) {
		byte[][] filter = { { 1, 2, 1 }, { 2, 4, 2 }, { 1, 2, 1 } };
		return applyFilter(bitmap, brightness, filter, outputConfig);
	}

	/**
	 * Converts
	 * 
	 * @param src
	 * @param saturation
	 *            Grayscale saturation level
	 * @param outputConfig
	 *            Bitmap configuration of the output bitmap
	 * @return
	 */
	public static final Bitmap doGrayscaleFilter(Bitmap src, int saturation,
			Bitmap.Config outputConfig) {
		int[] rgbInput = getPixels(src);
		int[] rgbOutput = new int[src.getWidth() * src.getHeight()];

		int alpha, red, green, blue;
		int output_red, output_green, output_blue;

		// We will use the standard NTSC color quotiens, multiplied by 1024
		// in order to be able to use integer-only math throughout the code.
		int RW = 306; // 0.299 * 1024
		int RG = 601; // 0.587 * 1024
		int RB = 117; // 0.114 * 1024

		// Define and calculate matrix quotients
		final int a, b, c, d, e, f, g, h, i;
		a = (1024 - saturation) * RW + saturation * 1024;
		b = (1024 - saturation) * RW;
		c = (1024 - saturation) * RW;
		d = (1024 - saturation) * RG;
		e = (1024 - saturation) * RG + saturation * 1024;
		f = (1024 - saturation) * RG;
		g = (1024 - saturation) * RB;
		h = (1024 - saturation) * RB;
		i = (1024 - saturation) * RB + saturation * 1024;

		int pixel = 0;
		for (int p = 0; p < rgbOutput.length; p++) {
			pixel = rgbInput[p];
			alpha = (0xFF000000 & pixel);
			red = (0x00FF & (pixel >> 16));
			green = (0x0000FF & (pixel >> 8));
			blue = pixel & (0x000000FF);

			// Matrix multiplication
			output_red = ((a * red + d * green + g * blue) >> 4) & 0x00FF0000;
			output_green = ((b * red + e * green + h * blue) >> 12) & 0x0000FF00;
			output_blue = (c * red + f * green + i * blue) >> 20;

			rgbOutput[p] = alpha | output_red | output_green | output_blue;
		}
		return getBitmap(rgbOutput, src.getWidth(), src.getHeight(),
				outputConfig);
	}

	/**
	 * Generates only the border pixels of the given image
	 * 
	 * @param bitmap
	 * 
	 * @param outputConfig
	 *            Bitmap configuration of the output bitmap
	 */
	public static Bitmap doImageRim(Bitmap bitmap, Bitmap.Config outputConfig) {
		byte[][] filter = { { 0, -1, 0 }, { -1, 5, -1 }, { 0, -1, 0 } };
		return applyFilter(bitmap, 0, filter, outputConfig);
	}

	/**
	 * Apply image blur filter on the given image data
	 * <p>
	 * <table border="0">
	 * 
	 * <tr>
	 * <td><b>Before</b></td>
	 * <td><b>After</b></td>
	 * </tr>
	 * <tr>
	 * <td>
	 * <img src="../../../resources/before.png"></td>
	 * <td><img src="../../../resources/blur.png"></td>
	 * </tr>
	 * </table>
	 * </p>
	 * 
	 * @param argbData
	 *            of the image
	 * 
	 * @param width
	 *            of the image
	 * @param height
	 *            of the image
	 */
	public static Bitmap doSimpleBlur(Bitmap bitmap, Bitmap.Config outputConfig) {
		byte[][] filter = { { -1, -1, -1 }, { -1, 0, -1 }, { -1, -1, -1 } };
		return applyFilter(bitmap, 100, filter, outputConfig);
	}

	/**
	 * Apply emboss filter to the given image data
	 * <p>
	 * <table border="0">
	 * <tr>
	 * <td><b>Before</b></td>
	 * <td><b>After</b></td>
	 * </tr>
	 * <tr>
	 * <td>
	 * <img src="../../../resources/before.png"></td>
	 * <td><img src="../../../resources/emboss.png"></td>
	 * </tr>
	 * </table>
	 * </p>
	 * 
	 * @param bitmap
	 *            of the image
	 * @param outputConfig
	 *            Bitmap configuration of the output bitmap
	 * @return
	 */
	public static Bitmap emboss(Bitmap bitmap, Bitmap.Config outputConfig) {
		byte[][] filter = { { -2, 0, 0 }, { 0, 1, 0 }, { 0, 0, 2 } };
		return applyFilter(bitmap, 100, filter, outputConfig);
	}

	public static Bitmap getBitmap(InputStream is) throws IOException {
		byte[] data = IOUtils.toByteArray(is);
		return BitmapFactory.decodeByteArray(data, 0, data.length);
	}

	public static Bitmap getBitmap(int[] pixels, int width, int height,
			Bitmap.Config config) {
		return Bitmap.createBitmap(pixels, width, height, config);
	}

	/**
	 * Get the size of the given bitmap in KB
	 * 
	 * @param bitmap
	 * @param bitsPerPixel
	 * @return
	 */
	public static long getBitmapSize(Bitmap bitmap, int bitsPerPixel) {
		long top = bitmap.getHeight() * bitmap.getWidth() * bitsPerPixel;
		return top / bottom;
	}

	/**
	 * Get the pixels for the given bitmap
	 * 
	 * @param bitmap
	 * @return
	 */
	public static int[] getPixels(Bitmap bitmap) {
		int w = bitmap.getWidth();
		int h = bitmap.getHeight();
		int[] pixels = new int[w * h];
		bitmap.getPixels(pixels, 0, w, 0, 0, w, h);
		return pixels;

	}

	/**
	 * Returns the new height for the given degree. The new heigth symbols the
	 * heigth for an rotated rgb data array with the same degree rotation.
	 * 
	 * @param degree
	 *            the degree value of the rotation.
	 * @param width
	 *            the width of the rgb source.
	 * @param heigth
	 *            the heigth of the rgb source
	 * @return the new height of the rgb data.
	 */
	public static int getRotatedHeight(int degree, int width, int heigth,
			double angle) {
		double degreeCos = Math.cos(Math.PI * angle / 180);
		double degreeSin = Math.sin(Math.PI * angle / 180);
		if (degree == -90 || degree == 90 || degree == 270 || degree == -270) {
			return width;
		} else if (degree == 360 || degree == 180 || degree == 0) {
			return heigth;
		}
		long pointY1 = MathUtils.round(0 * degreeSin + 0 * degreeCos);
		long pointY2 = MathUtils.round(width * degreeSin + 0 * degreeCos);
		long pointY3 = MathUtils.round(0 * degreeSin + heigth * degreeCos);
		long pointY4 = MathUtils.round(width * degreeSin + heigth * degreeCos);
		long minY = pointY1;
		if (pointY2 < minY) {
			minY = pointY2;
		}
		if (pointY3 < minY) {
			minY = pointY3;
		}
		if (pointY4 < minY) {
			minY = pointY4;
		}
		long maxY = pointY1;
		if (pointY2 > maxY) {
			maxY = pointY2;
		}
		if (pointY3 > maxY) {
			maxY = pointY3;
		}
		if (pointY4 > maxY) {
			maxY = pointY4;
		}
		return (int) (maxY - minY);
	}

	/**
	 * Returns the new width for the given degree. The new width symbols the
	 * width for an rotated rgb data array with the same degree rotation.
	 * 
	 * @param degree
	 *            the degree value of the rotation.
	 * @param width
	 *            the width of the rgb source.
	 * @param heigth
	 *            the heigth of the rgb source
	 * @return the new width of the rgb data.
	 */
	public static int getRotatedWidth(int degree, int width, int heigth,
			double angle) {
		double degreeCos = Math.cos(Math.PI * angle / 180);
		double degreeSin = Math.sin(Math.PI * angle / 180);
		if (degree == -90 || degree == 90 || degree == 270 || degree == -270) {
			return heigth;
		} else if (degree == 360 || degree == 180 || degree == 0) {
			return width;
		}
		long pointX1 = 0; // MathUtil.round(0 * degreeCos - 0 * degreeSin);
		long pointX2 = MathUtils.round(width * degreeCos); // MathUtil.round(width
		// * degreeCos - 0
		// *degreeSin);
		long pointX3 = MathUtils.round(-heigth * degreeSin); // MathUtil.round(0
		// *degreeCos -
		// heigth
		// *degreeSin);
		long pointX4 = MathUtils.round(width * degreeCos - heigth * degreeSin);
		long minX = pointX1;
		if (pointX2 < minX) {
			minX = pointX2;
		}
		if (pointX3 < minX) {
			minX = pointX3;
		}
		if (pointX4 < minX) {
			minX = pointX4;
		}
		long maxX = pointX1;
		if (pointX2 > maxX) {
			maxX = pointX2;
		}
		if (pointX3 > maxX) {
			maxX = pointX3;
		}
		if (pointX4 > maxX) {
			maxX = pointX4;
		}
		return (int) (maxX - minX);
	}

	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int pixels) {
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);
		final float roundPx = pixels;

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);

		return output;
	}

	/**
	 * Convert a bitmap to a series of bitmap tiles
	 * 
	 * @param bitmap
	 *            to extract the tiles from
	 * @param number
	 *            number of tiles to extract.
	 * @param tileWidth
	 * @param tileHeight
	 * @param fromHorizontal
	 *            if true, extract tiles horizontally from the image. Else
	 *            extract vertically.
	 * @return
	 */
	public static Bitmap[] getTiles(Bitmap bitmap, int number, int tileWidth,
			int tileHeight, boolean fromHorizontal) {
		Bitmap[] tiles = new Bitmap[number];
		for (int i = 0; i < number; i++) {
			tiles[i] = Bitmap.createBitmap(bitmap, fromHorizontal ? i
					* tileWidth : 0, fromHorizontal ? 0 : i * tileHeight,
					tileWidth, tileHeight);
		}
		return tiles;
	}

	/**
	 * Invert the bitmap's colors.
	 * 
	 * @param bitmap
	 * @param outputConfig
	 *            Bitmap configuration of the output bitmap
	 * @return
	 */
	public static final Bitmap invert(Bitmap bitmap, Bitmap.Config outputConfig) {
		/* Tested Apr 27 2011 */
		int[] argb = getPixels(bitmap);
		for (int i = 0; i < argb.length; i++) {
			argb[i] = ColorUtils.invertColor(argb[i]);
		}
		return Bitmap.createBitmap(argb, bitmap.getWidth(), bitmap.getHeight(),
				outputConfig);
	}

	/**
	 * Poseterize the given bitmap
	 * 
	 * @param bitmap
	 * @param depth
	 *            Posterization depth
	 * @param outputConfig
	 *            Bitmap configuration of the output bitmap
	 * @return
	 */
	public static final Bitmap posterize(Bitmap bitmap, int depth,
			Bitmap.Config outputConfig) {
		int[] argb = getPixels(bitmap);
		for (int i = 0; i < argb.length; i++) {
			argb[i] = ColorUtils.posterizePixel(argb[i], depth);
		}
		return Bitmap.createBitmap(argb, bitmap.getWidth(), bitmap.getHeight(),
				outputConfig);
	}

	/**
	 * Read the given file as a bitmap
	 * 
	 * @param file
	 *            file to create a bitmap from
	 */
	public static Bitmap readBitmapFromFS(File file) {
		final BitmapFactory.Options option = new Options();
		option.inSampleSize = 1;
		Bitmap image = null;
		try {
			image = BitmapFactory.decodeStream(new FileInputStream(file), null,
					option);
			Thread.sleep(4);
		} catch (Exception e) {
			Log.e("Blog/DM", "Execption : " + e);
			e.printStackTrace();
		}
		return image;
	}

	/**
	 * Saturate the given bitmap
	 * 
	 * @param bitmap
	 * @param percent
	 * @param outputConfig
	 * @return
	 */
	public static final Bitmap saturate(Bitmap bitmap, int percent,
			Bitmap.Config outputConfig) {
		int[] argb = getPixels(bitmap);
		for (int i = 0; i < argb.length; i++) {
			argb[i] = ColorUtils.setSaturation(argb[i], percent);
		}
		return Bitmap.createBitmap(argb, bitmap.getWidth(), bitmap.getHeight(),
				outputConfig);
	}

	/**
	 * Scales an rgb data unproportional in every new size you want bigger or
	 * smaller than the given original.
	 * 
	 * @param opacity
	 *            the maximum alpha value, 255 (0xFF) means fully opaque, 0
	 *            fully transparent. Lower alpha values will be preserved.
	 * @param rgbData
	 *            the original rgbdata
	 * @param newWidth
	 *            the new width for the new rgbdata
	 * @param newHeight
	 *            the new height for the new rgbdata
	 * @param oldWidth
	 *            the width from the oiginal rgbdata
	 * @param oldHeight
	 *            the height from the oiginal rgbdata
	 * @param newRgbData
	 *            the new rgbdata has to be initialised
	 */
	public static void scale(int opacity, int[] rgbData, int newWidth,
			int newHeight, int oldWidth, int oldHeight, int[] newRgbData) {
		int currentX = 0, currentY = 0;
		int oldLenght = rgbData.length;
		int newLength = newRgbData.length;
		int targetArrayIndex;
		int verticalShrinkFactorPercent = ((newHeight * 100) / oldHeight);
		int horizontalScaleFactorPercent = ((newWidth * 100) / oldWidth);
		int alpha = (opacity << 24); // is now 0xAA000000
		for (int i = 0; i < newLength; i++) {
			currentX = (currentX + 1) % newWidth;
			if (currentX == 0) {
				currentY++;
			}
			targetArrayIndex = ((currentX * 100) / horizontalScaleFactorPercent)
					+ (oldWidth * ((currentY * 100) / verticalShrinkFactorPercent));
			if (targetArrayIndex >= oldLenght) {
				targetArrayIndex = oldLenght - 1;
			}
			if (targetArrayIndex < 0) {
				targetArrayIndex = 0;
			}
			int pixel = rgbData[targetArrayIndex];
			if (opacity != 255) {
				int pixelAlpha = (pixel & 0xff000000) >>> 24;
				if (pixelAlpha > opacity) {
					pixel = (pixel & 0x00ffffff) | alpha;
				}
			}
			newRgbData[i] = pixel;
		}
	}

	/**
	 * Scales an rgb data unproportional in every new size you want bigger or
	 * smaller than the given original.
	 * 
	 * @param rgbData
	 *            the original rgbdata
	 * @param newWidth
	 *            the new width for the new rgbdata
	 * @param newHeight
	 *            the new height for the new rgbdata
	 * @param oldWidth
	 *            the width from the oiginal rgbdata
	 * @param oldHeight
	 *            the height from the oiginal rgbdata
	 * @param newRgbData
	 *            the new rgbdata has to be initialised
	 */
	public static void scale(int[] rgbData, int newWidth, int newHeight,
			int oldWidth, int oldHeight, int[] newRgbData) {

		int x, y, dy;
		int srcOffset;
		int destOffset;

		// Calculate the pixel ratio ( << 10 )
		final int pixelRatioWidth = (1024 * oldWidth) / newWidth;
		final int pixelRatioHeight = (1024 * oldHeight) / newHeight;

		y = 0;
		destOffset = 0;
		while (y < newHeight) {
			dy = ((pixelRatioHeight * y) >> 10) * oldWidth;
			srcOffset = 0;

			x = 0;
			while (x < newWidth) {
				newRgbData[destOffset + x] = rgbData[dy + (srcOffset >> 10)];
				srcOffset += pixelRatioWidth;
				x++;
			}

			destOffset += newWidth;
			y++;
		}
	}

	/**
	 * Returns the one scaled Pixel for the given new heigth and width.
	 * 
	 * @param oldLength
	 *            length of the rgb data source.
	 * @param oldWidth
	 *            the old width of the rgb data.
	 * @param oldHeigth
	 *            the old heigth of the rgb data.
	 * @param newWidth
	 *            the new width of the rgb data.
	 * @param newHeigth
	 *            the new heigth of the rgb data.
	 * @param currentX
	 *            the x position of the pixel to be scaled.
	 * @param currentY
	 *            the y position of the pixel to be scaled.
	 * @return position of the scaled pixel in the old rgb data array.
	 */
	public static int scaledPixel(int oldLength, int oldWidth, int oldHeigth,
			int newWidth, int newHeigth, int currentX, int currentY) {
		int targetArrayIndex;
		int verticalShrinkFactorPercent = ((newHeigth * 100) / oldHeigth);
		int horizontalScaleFactorPercent = ((newWidth * 100) / oldWidth);
		targetArrayIndex = ((currentX * 100) / horizontalScaleFactorPercent)
				+ (oldWidth * ((currentY * 100) / verticalShrinkFactorPercent));
		if (targetArrayIndex >= oldLength) {
			targetArrayIndex = oldLength - 1;
		}
		if (targetArrayIndex < 0) {
			targetArrayIndex = 0;
		}
		return targetArrayIndex;
	}

	/**
	 * Scales an image to fit into the specified available width and height
	 * while maintaining the ratio.
	 * 
	 * @param source
	 *            the source bitmap
	 * @param availableWidth
	 *            the new width for the new rgbdata
	 * @param availableHeight
	 *            the new height for the new rgbdata
	 * @param outputConfig
	 *            Bitmap configuration of the output bitmap
	 * @return the resulting image
	 */
	public static Bitmap scaleToFit(Bitmap source, int availableWidth,
			int availableHeight, Bitmap.Config outputConfig) {
		int sourceWidth = source.getWidth();
		int sourceHeight = source.getHeight();

		if (sourceWidth > availableWidth || sourceHeight > availableHeight) {
			int[] rgbData = getPixels(source);

			double availableRatio = (double) availableWidth
					/ (double) availableHeight;
			double sourceRatio = (double) sourceWidth / (double) sourceHeight;

			int targetWidth = availableWidth;
			int targetHeight = availableHeight;

			if (availableRatio < sourceRatio) {
				targetHeight = (int) (targetWidth / sourceRatio);
			} else {
				targetWidth = (int) (targetHeight * sourceRatio);
			}

			int[] newRgbData = new int[targetWidth * targetHeight];
			scale(rgbData, targetWidth, targetHeight, sourceWidth,
					sourceHeight, newRgbData);
			return getBitmap(newRgbData, targetWidth, targetHeight,
					outputConfig);
		}

		return source;
	}

	/**
	 * Set the transparency of an image
	 * 
	 * @param bitmap
	 * @param level
	 *            between 0 and 256. 0 indicates fully transparent and 256
	 *            indicates its fully opaque
	 * @param outputConfig
	 * @return
	 */
	public static final Bitmap setTransparency(final Bitmap bitmap, int level,
			Bitmap.Config outputConfig) {
		int[] argb = getPixels(bitmap);
		ColorUtils.setTransparency(argb, level);
		return Bitmap.createBitmap(argb, bitmap.getWidth(), bitmap.getHeight(),
				outputConfig);
	}

	/**
	 * Stretches the rgb data horizontal to the given left and rigth stretch
	 * factor and returns the new rgb data array.
	 * 
	 * @param argbArray
	 *            the rgb data to be stretched.
	 * @param leftStrechFactor
	 *            the stretch factor of the left.
	 * @param rightStrechFactor
	 *            the stretch factor of the rigth.
	 * @param width
	 *            the source width of the rgb data.
	 * @param heigth
	 *            the source heigth of the rgb data.
	 * @return stretched rgb data array.
	 */
	public static int[] stretchHorizontal(int[] argbArray,
			int leftStrechFactor, int rightStrechFactor, int width, int heigth) {
		int newHeigthLeft = (heigth * leftStrechFactor) / 100;
		int newHeigthRight = (heigth * rightStrechFactor) / 100;
		int procentualScalingWidth;
		int biggerHeigth;
		if (newHeigthLeft < newHeigthRight) {
			procentualScalingWidth = (newHeigthRight - newHeigthLeft) / width;
			biggerHeigth = newHeigthRight;
		} else {
			procentualScalingWidth = (newHeigthLeft - newHeigthRight) / width;
			biggerHeigth = newHeigthLeft;
		}
		int[] newArgbArray = new int[biggerHeigth * width];
		return stretchHorizontal(argbArray, newHeigthLeft, newHeigthRight,
				biggerHeigth, width, heigth, procentualScalingWidth,
				newArgbArray);
	}

	/**
	 * Stretches the rgb data horizontal to the given left and heigth width and
	 * returns the new rgb data array.
	 * 
	 * 
	 * @param argbArray
	 *            the rgb data to be stretched.
	 * @param newLeftHeigth
	 *            the new left heigth of the rgb data.
	 * @param newRigthHeigth
	 *            the new rigth heigth of the rgb data.
	 * @param biggerHeigth
	 *            the bigger heigth of left and rigth heigth.
	 * @param width
	 *            the source width of the rgb data.
	 * @param heigth
	 *            the source heigth of the rgb data.
	 * @param procentualScalingHeight
	 *            the procentual scaling heigth(biggerHeigth -
	 *            smallerSmaller)/widthOfTheOriginalImage).
	 * @param newArgbArray
	 *            the new rgb data where the changes getting in.
	 * @return return the filled newArgbArray with stretched changes.
	 */
	public static int[] stretchHorizontal(int[] argbArray, int newLeftHeigth,
			int newRigthHeigth, int biggerHeigth, int width, int heigth,
			int procentualScalingHeight, int[] newArgbArray) {
		if (procentualScalingHeight == 0) {
			procentualScalingHeight++;
		}
		int length = newArgbArray.length;
		int oldLength = argbArray.length;
		// x and y position int the new array
		int idX = 0, idY = 0;
		// x and y position of the old array
		int x = 0, y = 0;
		// position in the new array
		int whereIamAt = 0;
		// Heighth for goal
		int newHeigth = newLeftHeigth;
		// start Heigth to goal
		int startColumn = (biggerHeigth - newHeigth) / 2;
		int endColumn = biggerHeigth - ((biggerHeigth - newHeigth) / 2);

		for (int i = 0; i < length; i++) {

			if (startColumn <= idY && endColumn >= idY) {
				newArgbArray[whereIamAt] = argbArray[scaledPixel(oldLength,
						width, heigth, width, newHeigth, x, y)];
				y = (y + 1) % newHeigth;
			} else {
				newArgbArray[whereIamAt] = 000000;
			}
			idY = (idY + 1) % (biggerHeigth);
			whereIamAt = idX + (idY * width);
			if (idY == 0) {
				idX++;
				x++;
				y = 0;
				if (newLeftHeigth < newRigthHeigth) {
					newHeigth += procentualScalingHeight;
				} else if (newLeftHeigth > newRigthHeigth) {
					newHeigth -= procentualScalingHeight;
				}
				startColumn = (biggerHeigth - newHeigth) / 2;
				endColumn = biggerHeigth - ((biggerHeigth - newHeigth) / 2);
			}
		}
		return newArgbArray;
	}

	/**
	 * Stretches the rgb data vertical to the given top and bottom stretch
	 * factor and returns the new rgb data array.
	 * 
	 * @param argbArray
	 *            the rgb data to be stretched.
	 * @param topStrechFactor
	 *            the stretch factor of the top.
	 * @param bottomStrechFactor
	 *            the stretch factor of the bottom.
	 * @param width
	 *            the source width of the rgb data.
	 * @param heigth
	 *            the source heigth of the rgb data.
	 * @return stretched rgb data array.
	 */
	public static int[] stretchVertical(int[] argbArray, int topStrechFactor,
			int bottomStrechFactor, int width, int heigth) {
		int newWidthTop = (width * topStrechFactor) / 100;
		int newWidthBottom = (width * bottomStrechFactor) / 100;
		int procentualScalingHeight;
		int biggerWidth;
		if (newWidthTop < newWidthBottom) {
			procentualScalingHeight = (newWidthBottom - newWidthTop) / heigth;
			biggerWidth = newWidthBottom;
		} else {
			procentualScalingHeight = (newWidthTop - newWidthBottom) / heigth;
			biggerWidth = newWidthTop;
		}
		int[] newArgbArray = new int[biggerWidth * heigth];
		return stretchVertical(argbArray, newWidthTop, newWidthBottom,
				biggerWidth, width, heigth, procentualScalingHeight,
				newArgbArray);
	}

	/**
	 * Stretches the rgb data vertical to the given top and bottom width and
	 * returns the new rgb data array.
	 * 
	 * 
	 * @param argbArray
	 *            the rgb data to be stretched.
	 * @param newWidthTop
	 *            the new top width of the rgb data.
	 * @param newWidthBottom
	 *            the new bottom width of the rgb data.
	 * @param biggerWidth
	 *            the bigger width of top and bottom width.
	 * @param width
	 *            the source width of the rgb data.
	 * @param heigth
	 *            the source heigth of the rgb data.
	 * @param procentualScalingHeight
	 *            the procentual scaling heigth(biggerWidth -
	 *            smallerWidth)/heigthOfTheOriginalImage).
	 * @param newArgbArray
	 *            the new rgb data where the changes getting in.
	 * @return return filled the newArgbArray with stretched changes.
	 */
	public static int[] stretchVertical(int[] argbArray, int newWidthTop,
			int newWidthBottom, int biggerWidth, int width, int heigth,
			int procentualScalingHeight, int[] newArgbArray) {
		if (procentualScalingHeight == 0) {
			procentualScalingHeight++;
		}
		int length = newArgbArray.length;
		int oldLength = argbArray.length;
		int insideCurrentY = 0;
		int insideCurrentX = 0, outsideCurrentX = 0;
		int sum1 = (biggerWidth - newWidthTop) / 2;
		int sum2 = biggerWidth - ((biggerWidth - newWidthTop) / 2);
		for (int i = 0; i < length; i++) {

			outsideCurrentX = (outsideCurrentX + 1) % biggerWidth;
			if (outsideCurrentX == 0) {
				if (newWidthTop < newWidthBottom) {
					newWidthTop += procentualScalingHeight;
					sum1 = (biggerWidth - newWidthTop) / 2;
					sum2 = biggerWidth - ((biggerWidth - newWidthTop) / 2);
				} else if (newWidthTop > newWidthBottom) {
					newWidthTop -= procentualScalingHeight;
					sum1 = (biggerWidth - newWidthTop) / 2;
					sum2 = biggerWidth - ((biggerWidth - newWidthTop) / 2);
				}
				insideCurrentY++;
				insideCurrentX = 0;
			}
			if (outsideCurrentX >= sum1 && outsideCurrentX < sum2) {
				insideCurrentX = (insideCurrentX + 1) % newWidthTop;
				newArgbArray[i] = argbArray[scaledPixel(oldLength, width,
						heigth, newWidthTop, heigth, insideCurrentX,
						insideCurrentY)];
			} else {
				newArgbArray[i] = 000000;
			}
		}
		return newArgbArray;
	}

	/**
	 * Tint the given bitmap
	 * 
	 * @param bitmap
	 * @param tintDegree
	 *            degree to tint the bitmap
	 * @param config
	 *            for the output bitmap
	 * @return
	 */
	public static Bitmap tint(Bitmap bitmap, int tintDegree,
			Bitmap.Config config) {
		int pich = bitmap.getHeight();
		int picw = bitmap.getWidth();
		int[] pix = getPixels(bitmap);
		int RY, BY, RYY, GYY, BYY, R, G, B, Y;
		double angle = (3.14159d * tintDegree) / 180.0d;
		int S = (int) (256.0d * Math.sin(angle));
		int C = (int) (256.0d * Math.cos(angle));

		for (int i = 0; i < pix.length; i++) {
			int r = (pix[i] >> 16) & 0xff;
			int g = (pix[i] >> 8) & 0xff;
			int b = pix[i] & 0xff;
			RY = (70 * r - 59 * g - 11 * b) / 100;
			// GY = (-30 * r + 41 * g - 11 * b) / 100;
			BY = (-30 * r - 59 * g + 89 * b) / 100;
			Y = (30 * r + 59 * g + 11 * b) / 100;
			RYY = (S * BY + C * RY) / 256;
			BYY = (C * BY - S * RY) / 256;
			GYY = (-51 * RYY - 19 * BYY) / 100;
			R = Y + RYY;
			R = (R < 0) ? 0 : ((R > 255) ? 255 : R);
			G = Y + GYY;
			G = (G < 0) ? 0 : ((G > 255) ? 255 : G);
			B = Y + BYY;
			B = (B < 0) ? 0 : ((B > 255) ? 255 : B);
			pix[i] = 0xff000000 | (R << 16) | (G << 8) | B;
		}
		// for (int y = 0; y < pich; y++)
		// for (int x = 0; x < picw; x++) {
		// int index = y * picw + x;
		//
		// }

		return Bitmap.createBitmap(pix, picw, pich, config);

	}

	// /**
	// *
	// * @param image
	// * @param xOffset
	// * of the shadow
	// * @param yOffset
	// * of the shadow
	// * @param size
	// * @param innerColor
	// * of the shadow
	// * @param outerColor
	// * of the shadow
	// * @return
	// */
	// public static final Bitmap dropShadow(Bitmap bitmap, int xOffset, int
	// yOffset, int size,
	// int innerColor, int outerColor) {
	// int w = bitmap.getWidth();
	// int h = bitmap.getHeight();
	// int[] data = getPixels(bitmap);
	// dropShadow(data, w, h, xOffset, yOffset, size, innerColor, outerColor);
	// return Bitmap.createBitmap(data, bitmap.getWidth(), bitmap.getHeight(),
	// Bitmap.Config.ARGB_8888);
	// }
	//
	// /**
	// * <p>
	// * Paints a dropshadow behind a given ARGB-Array, whereas you are able to
	// * specify the shadows inner and outer color.
	// * </p>
	// * <p>
	// * Note that the dropshadow just works for fully opaque pixels and that it
	// * needs a transparent margin to draw the shadow.
	// * </p>
	// * <p>
	// * Choosing the same inner and outer color and varying the transparency is
	// * recommended. Dropshadow just works for fully opaque pixels.
	// * </p>
	// *
	// * @param argbData
	// * of the image
	// * @param width
	// * of the image
	// * @param height
	// * of the image
	// * @param xOffset
	// * use this for finetuning the shadow's horizontal position.
	// * Negative values move the shadow to the left.
	// * @param yOffset
	// * use this for finetuning the shadow's vertical position.
	// * Negative values move the shadow to the top.
	// * @param size
	// * use this for finetuning the shadows radius.
	// * @param innerColor
	// * the inner color of the shadow, which should be less opaque
	// * than the text.
	// * @param outerColor
	// * the outer color of the shadow, which should be less than
	// * opaque the inner color.
	// */
	// private final static void dropShadow(int[] argbData, int width, int
	// height, int xOffset, int yOffset,
	// int size, int innerColor, int outerColor) {
	// // additional Margin for the image because of the shadow
	// int iLeft = size - xOffset < 0 ? 0 : size - xOffset;
	// int iRight = size + xOffset < 0 ? 0 : size + xOffset;
	// int iTop = size - yOffset < 0 ? 0 : size - yOffset;
	// int iBottom = size + yOffset < 0 ? 0 : size + yOffset;
	//
	// // set colors
	// int[] gradient = ColorUtil.getGradient(innerColor, outerColor, size);
	//
	// // walk over the text and look for non-transparent Pixels
	// for(int ix = -size + 1; ix < size; ix++){
	// for(int iy = -size + 1; iy < size; iy++){
	//
	// int r = (Math.abs(ix) + Math.abs(iy)) / 2;
	// // }
	// if(r < size){
	// int gColor = gradient[r];
	//
	// for(int col = iLeft, row; col < width/* +iLeft */- iRight; col++){
	// for(row = iTop; row < height - iBottom/* +iTop */- 1; row++){
	//
	// // draw if an opaque pixel is found and the
	// // destination is less opaque then the shadow
	// if(argbData[row * (width /* + size2 */) + col] >>> 24 == 0xFF
	// && argbData[(row + yOffset + iy) * (width /*
	// * size2
	// */) + col + xOffset + ix] >>> 24 < gColor >>> 24){
	// argbData[(row + yOffset + iy) * (width /*
	// * +
	// * size2
	// */) + col + xOffset + ix] = gColor;
	// }
	// }
	// }
	// }
	// }
	// }
	// }

	public static void writeBitmapToFile(Bitmap bitmap, File file)
			throws IOException {
		Bitmap.CompressFormat format = (file.getPath().endsWith("jpg") || file
				.getPath().endsWith("JPG")) ? CompressFormat.JPEG
				: CompressFormat.PNG;
		final FileOutputStream fos = new FileOutputStream(file);
		bitmap.compress(format, 100, fos);
		fos.close();
	}
}
