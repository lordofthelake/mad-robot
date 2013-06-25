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
package com.madrobot.graphics.threeD;

import android.graphics.Canvas;
import android.graphics.Paint;

import com.madrobot.graphics.ColorUtils;

/**
 * Helper class to draw 3D primitives on a 2D surface
 * Graphics3DUtil.java
 * 
 */
public class Graphics3DUtil {

	/**
	 * Draw a 3D rectangle
	 * <p>
	 * <table>
	 * <tr>
	 * <td><img src="../../../resources/3drect.png"></td>
	 * </tr>
	 * <tr>
	 * <td>
	 * <code>highLightColor</code>=0xADAAAD<br/>
	 * <code>shadowColor</code>=0xff0000 <br/>
	 * <code>isRaised</code>=true<br/>
	 * <code>lineWidth</code>=15<br/>
	 * <td>
	 * </tr>
	 * </table>
	 * </p>
	 * 
	 * @param canvas
	 *            Graphics context
	 * @param highLightColor
	 * @param shadowColor
	 * @param lineWidth
	 *            of rectangle side
	 * @param isRaised
	 *            raised or lowered 3d rectangle
	 * @param x
	 *            coordinate of 3D rect
	 * @param y
	 *            coordiate of 3D rect
	 * @param width
	 *            of the 3D rect
	 * @param height
	 *            of the 3D rect
	 */
	public final static void draw3DRect(final Canvas canvas, int highLightColor, int shadowColor,
			final boolean isRaised, int lineWidth, float x, float y, final int width, final int height) {
		Paint paint = new Paint();
		paint.setAntiAlias(true);

		float x2 = x + width - 1;
		float y2 = y + height - 1;
		int BEVEL_LOWERED = 0x2000;
		int ETCHED_IN = 0x8000;
		int BEVEL_RAISED = 0x1000;
		int nLowered = BEVEL_LOWERED | ETCHED_IN;
		int nBevel = BEVEL_LOWERED | BEVEL_RAISED;
		boolean bLowered = (isRaised ? 0x1000 : 0x2000 & nLowered) != 0;
		boolean bBevelled = (isRaised ? 0x1000 : 0x2000 & nBevel) != 0;
		int nHalfWidth = lineWidth / 2;

		while(lineWidth > 0){
			paint.setColor(bLowered ? shadowColor : highLightColor);

			canvas.drawLine(x, y, x, y2, paint);
			canvas.drawLine(x, y, x2 - 1, y, paint);
			paint.setColor(bLowered ? highLightColor : shadowColor);
			canvas.drawLine(x2, y, x2, y2, paint);
			canvas.drawLine(x + 1, y2, x2, y2, paint);

			x++;
			y++;
			x2--;
			y2--;
			lineWidth--;
			if(bBevelled){
				// soften the bevel by slightly fading the colors
				highLightColor = ColorUtils.adjustBrightness(highLightColor, 10);
				shadowColor = ColorUtils.adjustBrightness(shadowColor, 10);
			} else if(lineWidth == nHalfWidth){
				int tmp = highLightColor;

				highLightColor = shadowColor;
				shadowColor = tmp;
			}
		}
	}
}
