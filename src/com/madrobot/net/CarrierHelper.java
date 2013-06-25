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
package com.madrobot.net;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiManager;

/**
 * 
 * Utility to find out the current carrier type used by the device. (i.e) Wifi
 * or 3G
 * <p>
 * It implements a dynamic broadcast listener that receives carrier information
 * as and when it changes.<br/>
 * Usage:</br>
 * 
 * <pre>
 * android.content.Context context= //current app context
 * CarrierHelper carrier=CarrierHelper.getInstance(context);
 * CarrierHelper.Carrier current=carrier.getCurrentCarrier();
 * if(current!=null){
 *  //network operations can be performed.
 * }
 * </pre>
 * 
 * </p>
 * 
 * @author Elton Kent
 */
public final class CarrierHelper {
	/**
	 * Represents the Current Carrier
	 */
	public enum Carrier {
		BEARER_3G,
		BEARER_WIFI;
	}

	/**
	 * Holds the CarrierHelper object instance for accessing the bearer Handler
	 * API
	 */
	private static CarrierHelper carrierHandler;

	/**
	 * The BearerHandler is the primary access point to initialize bearer
	 * handler. Only static access is followed, since only one instance of the
	 * service handler will be created
	 * 
	 * @param context
	 *            application context
	 * 
	 * @return return shared instance of {@link CarrierHelper}
	 */
	public static synchronized CarrierHelper getInstance(Context context) {

		try{
			if(carrierHandler == null){
				carrierHandler = new CarrierHelper();
				carrierHandler.init(context);
				return carrierHandler;
			}
			return carrierHandler;
		} catch(Exception e){

			carrierHandler = null;
			return null;
		}
	}

	/**
	 * Holds the availableBearers
	 */
	private Set<Carrier> availableBearer;
	/**
	 * Represents the connection manager for context
	 */
	private ConnectivityManager connectivityManager;
	/**
	 * Represents the application context
	 */
	private Context context;

	/**
	 * Holds the current bearer
	 */
	private Carrier currentBearer;

	/**
	 * The BroadcastReceiver is registered by BearerHandler as a broadcast
	 * receiver to be invoked by the system when the network changes was happen
	 * like wifi state changed ,supplicant state, background setting changed and
	 * network changed are available. NetworkChangeRecevier gets the callback
	 * via onReceive().
	 */
	private BroadcastReceiver networkChangeRecevier = new BroadcastReceiver() {

		/**
		 * <p>
		 * If no connectivity is available then this receiver will update the
		 * status in bearer handler or if any network is changed it will update
		 * the status in bearer handler based on the available networks
		 * </p>
		 * 
		 * <p>
		 * It gets the new wifi state from the intent and check if wifi is
		 * disabled or unknown state ,if its disabled then its update the status
		 * in BearerHandler
		 * </p>
		 * 
		 * <p>
		 * It gets the new supplicant state and check if wifi is
		 * disconnected,its Disconnected then its update the status in
		 * BearerHandler
		 * </p>
		 */
		@Override
		public void onReceive(Context context, Intent intent) {

			final boolean noConnectivity = intent.getBooleanExtra(
					ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);

			if(noConnectivity){
				noConnectivity();
			} else{
				final NetworkInfo networkInfo = (NetworkInfo) intent
						.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);

				if(networkInfo != null){
					if((networkInfo.getType() == ConnectivityManager.TYPE_WIFI) && networkInfo.isConnected()){
						enableWifi();
					} else if((networkInfo.getType() == ConnectivityManager.TYPE_MOBILE)
							&& networkInfo.isConnected()){
						enabled3G();
					}
				}
			}

			final int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);

			if(wifiState == 0){
				final SupplicantState supplicantState = intent
						.getParcelableExtra(WifiManager.EXTRA_NEW_STATE);

				if((supplicantState != null) && (supplicantState == SupplicantState.DISCONNECTED)){
					setWifiDisabled();
				}
			} else{
				if((wifiState == WifiManager.WIFI_STATE_DISABLED)
						|| (wifiState == WifiManager.WIFI_STATE_UNKNOWN)){
					setWifiDisabled();
				} else if(wifiState == WifiManager.WIFI_STATE_ENABLED){
					enableWifi();
				}
			}
		}
	};

	private CarrierHelper() {

	}

	/**
	 * Enable the 3g.Called from the broad cost receiver
	 * 
	 */
	public void enabled3G() {
		this.getAvailableCarrier().add(Carrier.BEARER_3G);
		this.setCurrentCarrier(Carrier.BEARER_3G);
	}

	/**
	 * Enable the wifi.Called from the broad cost receiver
	 * 
	 */
	public void enableWifi() {
		this.getAvailableCarrier().add(Carrier.BEARER_WIFI);
		this.setCurrentCarrier(Carrier.BEARER_WIFI);
	}

	/**
	 * Used to access the Available bearer
	 * 
	 */
	public Set<Carrier> getAvailableCarrier() {
		return availableBearer;
	}

	/**
	 * Used to get the current bearer
	 * 
	 */
	public Carrier getCurrentCarrier() {
		return currentBearer;
	}

	/**
	 * Hold the responsibility of adding the available bearer and register the
	 * broadcast receiver
	 * 
	 * @param context
	 *            application context
	 */
	private void init(Context context) {

		this.context = context;
		this.availableBearer = Collections.synchronizedSet(new HashSet<Carrier>(Carrier.values().length));
		connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

		if(isWifiAvailable()){
			this.getAvailableCarrier().add(Carrier.BEARER_WIFI);
			this.setCurrentCarrier(Carrier.BEARER_WIFI);
		}

		if(is3GAvailable()){
			this.getAvailableCarrier().add(Carrier.BEARER_3G);
			if(this.getCurrentCarrier() == null){
				this.setCurrentCarrier(Carrier.BEARER_3G);
			}
		}

		this.registerNetWorkChangeMonitorRecevier();
	}

	/**
	 * Return true if 3g is connected otherwise false
	 * 
	 * @return true 3g is connected , otherwise false
	 */
	private boolean is3GAvailable() {
		NetworkInfo networkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

		if((networkInfo != null) && networkInfo.isConnected()){
			return true;
		}
		return false;
	}

	/**
	 * Return true if wifi is connected otherwise false
	 * 
	 * @return true wifi is connected , otherwise false
	 */
	private boolean isWifiAvailable() {

		NetworkInfo networkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

		if((networkInfo != null) && networkInfo.isConnected()){
			return true;
		}
		return false;
	}

	/**
	 * Called when the no connectivity is available
	 */
	public void noConnectivity() {
		this.getAvailableCarrier().clear();
		this.setCurrentCarrier(null);
	}

	/**
	 * Register the broad cost receiver if no bearer is available
	 * 
	 */
	private void registerNetWorkChangeMonitorRecevier() {
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		intentFilter.addAction(ConnectivityManager.ACTION_BACKGROUND_DATA_SETTING_CHANGED);
		intentFilter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
		intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);

		this.context.registerReceiver(this.networkChangeRecevier, intentFilter);
	}

	/**
	 * Used to change the current bearer
	 * 
	 */
	private void setCurrentCarrier(Carrier currentBearer) {
		this.currentBearer = currentBearer;
	}

	/**
	 * Disable the wifi connection and change current bearer into 3G if 3G is
	 * available. Called from the broad cost receiver
	 * 
	 */
	public synchronized void setWifiDisabled() {
		this.getAvailableCarrier().remove(Carrier.BEARER_WIFI);

		boolean mobileAvailable = false;
		for(Carrier bearer : this.getAvailableCarrier()){
			if(bearer == Carrier.BEARER_3G){
				mobileAvailable = true;
				break;
			}
		}

		if(mobileAvailable){
			this.setCurrentCarrier(Carrier.BEARER_3G);
		} else{
			this.setCurrentCarrier(null);
		}
	}

	/**
	 * Hold the responsibility of unregister all the receiver and clean the
	 * bearer handler
	 * 
	 */
	public void shutdownBearerHandler() {
		if(context != null){
			context.unregisterReceiver(networkChangeRecevier);
		}
		CarrierHelper.carrierHandler = null;
	}
}
