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
 * Allows you to perform layer based drawing on a canvas.
 * 
 * @author elton.kent
 * 
 */
public class CanvasLayer {
	private final Bitmap bitmap;
	private Paint bmPaint;
	/**
	 * Canvas to be used for drawing operations on this layer
	 */
	public final Canvas canvas;

	public CanvasLayer(int width, int height) {
		bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		canvas = new Canvas(bitmap);
		bmPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

	}

	/**
	 * The source canvas
	 * 
	 * @param src
	 *            The canvas to draw this layer onto.
	 */
	public void drawLayer(Canvas src) {
		drawLayer(src, 0, 0);
	}

	public void drawLayer(Canvas src, float x, float y) {
		src.drawBitmap(bitmap, x, y, bmPaint);
	}

}
