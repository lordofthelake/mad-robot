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
 * Contains sensor related information
 *<p>
 *{@link DeviceUtils#getSensorInfo(android.content.Context)}
 *</p>
 */
public class SensorInfo {
	private boolean hasAccelerometer;
	private boolean hasGyroscope;
	private boolean hasLightSensor;
	private boolean hasMagneticSensor;
	private boolean hasOrientationSensor;
	private boolean hasPressureSensor;
	private boolean hasProximitySensor;
	private boolean hasTemperatureSensor;

	public boolean hasAccelerometer() {
		return hasAccelerometer;
	}

	public boolean hasGyroscope() {
		return hasGyroscope;
	}

	public boolean hasLightSensor() {
		return hasLightSensor;
	}

	public boolean hasMagneticSensor() {
		return hasMagneticSensor;
	}

	public boolean hasOrientationSensor() {
		return hasOrientationSensor;
	}

	public boolean hasPressureSensor() {
		return hasPressureSensor;
	}

	public boolean hasProximitySensor() {
		return hasProximitySensor;
	}

	public boolean hasTemperatureSensor() {
		return hasTemperatureSensor;
	}

	void setHasAccelerometer(boolean hasAccelerometer) {
		this.hasAccelerometer = hasAccelerometer;
	}

	void setHasGyroscope(boolean hasGyroscope) {
		this.hasGyroscope = hasGyroscope;
	}

	void setHasLightSensor(boolean hasLightSensor) {
		this.hasLightSensor = hasLightSensor;
	}

	void setHasMagneticSensor(boolean hasMagneticSensor) {
		this.hasMagneticSensor = hasMagneticSensor;
	}

	void setHasOrientationSensor(boolean hasOrientationSensor) {
		this.hasOrientationSensor = hasOrientationSensor;
	}

	void setHasPressureSensor(boolean hasPressureSensor) {
		this.hasPressureSensor = hasPressureSensor;
	}

	void setHasProximitySensor(boolean hasProximitySensor) {
		this.hasProximitySensor = hasProximitySensor;
	}

	void setHasTemperatureSensor(boolean hasTemperatureSensor) {
		this.hasTemperatureSensor = hasTemperatureSensor;
	}

}
