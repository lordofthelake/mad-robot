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

public final class DeviceInfo {
	private String deviceBrand;

	private String deviceModel;
	private String firmwareVersion;
	private String kernelVersion;
	private String manufacturer;
	DeviceInfo() {
	}

	public String getDeviceBrand() {
		return deviceBrand;
	}

	public String getDeviceModel() {
		return deviceModel;
	}

	public String getFirmwareVersion() {
		return firmwareVersion;
	}

	public String getKernelVersion() {
		return kernelVersion;
	}

	public String getManufacturer() {
		return manufacturer;
	}

	void setDeviceBrand(String deviceBrand) {
		this.deviceBrand = deviceBrand;
	}

	void setDeviceModel(String deviceModel) {
		this.deviceModel = deviceModel;
	}

	void setFirmwareVersion(String firmwareVersion) {
		this.firmwareVersion = firmwareVersion;
	}

	void setKernelVersion(String kernelVersion) {
		this.kernelVersion = kernelVersion;
	}

	void setManufacturer(String manufacturer) {
		this.manufacturer = manufacturer;
	}

}
