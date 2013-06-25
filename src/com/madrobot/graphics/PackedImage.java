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

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * <p>
 * An image that is compressed to occupy very little memory.
 * </p>
 * This image allows very images with low color counts to occupy as little as
 * one byte per pixel.<br/>
 * <b>Warning!: Drawing operation of a PackedImage is
 * slower than a regular Image </b>
 * <table border="0">
 * <tr>
 * <td><b>Regular Image</b></td>
 * <td><b>Packed Image</b></td>
 * </tr>
 * <tr>
 * <td>
 * <img src="../../../resources/before.png"></td>
 * <td><img src="../../../resources/packedimage.png"></td>
 * </tr>
 * </table>
 * 
 * The efficiency of alpha processing and rgb data extraction is platform
 * dependent
 * 
 * 
 */
public class PackedImage {

	private static boolean contains(int array[], int length, int value) {
		for(int iter = 0; iter < length; iter++){
			if(array[iter] == value){
				return true;
			}
		}
		return false;
	}

	/**
	 * Loads a packaged image that was stored in a stream using the toByteArray
	 * method
	 * 
	 * @param data
	 *            previously stored image data
	 * @return newly created packed image
	 */
	public static PackedImage load(byte data[]) {
		try{
			java.io.DataInputStream input = new java.io.DataInputStream(new java.io.ByteArrayInputStream(
					data));
			int width = input.readShort();
			int height = input.readShort();
			int[] palette = new int[input.readByte() & 0xff];
			for(int iter = 0; iter < palette.length; iter++){
				palette[iter] = input.readInt();
			}

			byte[] arr = new byte[width * height];
			input.readFully(arr);
			return new PackedImage(width, height, palette, arr);
		} catch(java.io.IOException e){
			e.printStackTrace();
			return null;
		}
	}

	public static PackedImage pack(Bitmap bitmap) {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		int rgb[] = BimapUtils.getPixels(bitmap);
		return pack(rgb, width, height);

	}

	/**
	 * Packs the source rgba image and returns null if it fails
	 * 
	 * @param rgb
	 *            array containing ARGB data
	 * @param width
	 *            width of the image in the rgb array
	 * @param height
	 *            height of the image
	 * @return a packed image or null
	 */
	public static PackedImage pack(int rgb[], int width, int height) {
		int arrayLength = width * height;
		int tempPalette[] = new int[256];
		int paletteLocation = 0;
		for(int iter = 0; iter < arrayLength; iter++){
			int current = rgb[iter];
			if(contains(tempPalette, paletteLocation, current)){
				continue;
			}
			if(paletteLocation > 255){
				return null;
			}
			tempPalette[paletteLocation] = current;
			paletteLocation++;
		}

		if(paletteLocation != tempPalette.length){
			int newArray[] = new int[paletteLocation];
			System.arraycopy(tempPalette, 0, newArray, 0, paletteLocation);
			tempPalette = newArray;
		}
		return new PackedImage(width, height, tempPalette, rgb);

	}

	private int height;

	private byte imageDataByte[];

	private int lineCache[];

	private int palette[];

	private int width;

	private PackedImage() {
	}

	private PackedImage(int width, int height, int palette[], byte data[]) {
		this.width = width;
		this.height = height;
		this.palette = palette;
		imageDataByte = data;
	}

	private PackedImage(int width, int height, int palette[], int rgb[]) {
		this.width = width;
		this.height = height;
		this.palette = palette;
		imageDataByte = new byte[width * height];
		for(int iter = 0; iter < imageDataByte.length; iter++){
			imageDataByte[iter] = (byte) paletteOffset(rgb[iter]);
		}
		if(lineCache == null){
			System.out.println("lineCache is null");
		}
		System.out.println("[PackedImage]Packing done! rgb data len " + imageDataByte.length + " bytes");
	}

	public void drawImage(Canvas canvas, int x, int y) {
		if((lineCache == null) || (lineCache.length < width * 3)){
			lineCache = new int[width * 3];
		}
		int clipY = canvas.getClipBounds().top;
		int clipBottomY = canvas.getClipBounds().height() + clipY;
		int firstLine = 0;
		int lastLine = height;
		if(clipY > y){
			firstLine = clipY - y;
		}
		if(clipBottomY < y + height){
			lastLine = clipBottomY - y;
		}
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		for(int line = firstLine; line < lastLine; line += 3){
			int currentPos = line * width;
			int rowsToDraw = Math.min(3, height - line);
			int amount = width * rowsToDraw;
			for(int position = 0; position < amount; position++){
				int i = imageDataByte[position + currentPos] & 0xff;
				lineCache[position] = palette[i];
			}
			canvas.drawBitmap(lineCache, 0, width, x, y + line, width, rowsToDraw, true, paint);
		}

	}

	public int getHeight() {
		return height;
	}

	public int[] getRGB() {
		int rgb[] = new int[width * height];
		for(int iter = 0; iter < rgb.length; iter++){
			int i = imageDataByte[iter] & 0xff;
			rgb[iter] = palette[i];
		}
		return rgb;
	}

	/**
	 * 
	 * @return The size of the image in bytes
	 */
	public long getSize() {
		return imageDataByte.length;
	}

	public int getWidth() {
		return width;
	}

	private int paletteOffset(int rgb) {
		for(int iter = 0; iter < palette.length; iter++){
			if(rgb == palette[iter]){
				return iter;
			}
		}
		throw new IllegalStateException("Invalid palette request in paletteOffset");
	}

	/**
	 * This method allows us to store a package image into a persistant stream
	 * easily thus allowing us to store the image in RMS.
	 * 
	 * @return a byte array that can be loaded using the load method
	 */
	public byte[] toByteArray() {
		try{
			java.io.ByteArrayOutputStream array = new java.io.ByteArrayOutputStream();
			java.io.DataOutputStream out = new java.io.DataOutputStream(array);
			out.writeShort(width);
			out.writeShort(height);
			out.writeByte(palette.length);
			for(int iter = 0; iter < palette.length; iter++){
				out.writeInt(palette[iter]);
			}

			out.write(imageDataByte);
			out.close();
			return array.toByteArray();
		} catch(java.io.IOException e){
			e.printStackTrace();
			return null;
		}
	}
}
