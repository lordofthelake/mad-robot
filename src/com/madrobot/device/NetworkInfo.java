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
 * Contains network related information.
 * <p>
 * 
 * {@link DeviceUtils#getNetworkInfo(android.content.Context)}
 * </p>
 */
public final class NetworkInfo {

	private int dataState;

	private boolean hasTelephoneSupport;
	private boolean isRoaming;
	private int networkType;
	private String operatorName;
	private int phoneType;
	private String voicemailNumber;
	NetworkInfo() {
	}
	
	/**
	 * 
	 * 
	 * @return TelephonyManager.DATA_XXX
	 */
	public int getDataState() {
		return dataState;
	}

	/**
	 * Get the network type currently in use
	 * 
	 * @return TelephonyManager.NETWORKTYPE_XXX
	 */
	public int getNetworkType() {
		return networkType;
	}

	/**
	 * Returns the operator name
	 * 
	 * @return
	 */
	public String getOperatorName() {
		return operatorName;
	}

	public int getPhoneType() {
		return phoneType;
	}

	/**
	 * Returns the voice mail number
	 * 
	 * @return
	 */
	public String getVoicemailNumber() {
		return voicemailNumber;
	}

	/**
	 * Returns true if the device has telephony support
	 * @return
	 */
	public boolean hasTelephoneSupport() {
		return hasTelephoneSupport;
	}

	/**
	 * 
	 * 
	 * @return True if the network is in roaming state
	 */
	public boolean isRoaming() {
		return isRoaming;
	}

	void setDataState(int dataState) {
		this.dataState = dataState;
	}

	void setHasTelephoneSupport(boolean hasTelephoneSupport) {
		this.hasTelephoneSupport = hasTelephoneSupport;
	}

	void setNetworkType(int networkType) {
		this.networkType = networkType;
	}

	void setOperatorName(String operatorName) {
		this.operatorName = operatorName;
	}

	/**
	 * Get the phone type
	 * 
	 * 
	 * @return TelephonyManager.PHONETYPE_XXX , 0 if phone type cannot be
	 *         established.
	 */
	void setPhoneType(int phoneType) {
		this.phoneType = phoneType;
	}

	void setRoaming(boolean isRoaming) {
		this.isRoaming = isRoaming;
	}

	void setVoicemailNumber(String voicemailNumber) {
		this.voicemailNumber = voicemailNumber;
	}

}
