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
package com.madrobot.device;

/**
 * Contains information for a given display
 * <p>
 * {@link DeviceUtils#getDisplayInfo(android.view.Display, android.content.Context)}
 * </p>
 */
public final class DisplayInfo {

	public static final int SCREENTYPE_FULL_WIDTH = 30;
	public static final int SCREENTYPE_NORMAL = 10;
	public static final int SCREENTYPE_WIDE = 20;
	private double diagonalSizeInInch;
	private int heightInPixels;
	private float horizontalDensity;
	private boolean isTouchEnabled;
	private float logicalDensity;
	private float logicalDPI;
	private float refreshRate;

	private float scaledDensity;

	private int screenType;

	private float verticalDensity;

	private int widthInPixels;
	DisplayInfo() {
	}
	public double getDiagonalSizeInInch() {
		return diagonalSizeInInch;
	}

	public int getHeightInPixels() {
		return heightInPixels;
	}

	public float getHorizontalDensity() {
		return horizontalDensity;
	}

	public float getLogicalDensity() {
		return logicalDensity;
	}

	public float getLogicalDPI() {
		return logicalDPI;
	}

	public float getRefreshRate() {
		return refreshRate;
	}

	public float getScaledDensity() {
		return scaledDensity;
	}

	/**
	 * 
	 *
	 * @return {@link DisplayInfo#SCREENTYPE_XXX}
	 */
	public int getScreenType() {
		return screenType;
	}

	public float getVerticalDensity() {
		return verticalDensity;
	}

	public int getWidthInPixels() {
		return widthInPixels;
	}

	public boolean isTouchEnabled() {
		return isTouchEnabled;
	}

	void setDiagonalSizeInInch(double diagonalSizeInInch) {
		this.diagonalSizeInInch = diagonalSizeInInch;
	}

	void setHeightInPixels(int heightInPixels) {
		this.heightInPixels = heightInPixels;
	}

	void setHorizontalDensity(float horizontalDensity) {
		this.horizontalDensity = horizontalDensity;
	}

	void setLogicalDensity(float logicalDensity) {
		this.logicalDensity = logicalDensity;
	}

	void setLogicalDPI(float logicalDPI) {
		this.logicalDPI = logicalDPI;
	}

	void setRefreshRate(float refreshRate) {
		this.refreshRate = refreshRate;
	}

	void setScaledDensity(float scaledDensity) {
		this.scaledDensity = scaledDensity;
	}

	void setScreenType(int screenType) {
		this.screenType = screenType;
	}

	void setTouchEnabled(boolean isTouchEnabled) {
		this.isTouchEnabled = isTouchEnabled;
	}

	void setVerticalDensity(float verticalDensity) {
		this.verticalDensity = verticalDensity;
	}

	void setWidthInPixels(int widthInPixels) {
		this.widthInPixels = widthInPixels;
	}
}
