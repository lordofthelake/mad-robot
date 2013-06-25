package com.madrobot.location;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;

public class LocationService extends Service {

	private Location mBestLocation;

	private LocationListener mLocationListener = new LocationListener() {

		@Override
		public void onLocationChanged(Location location) {
			if(LocationUtils.isBetterLocation(location, mBestLocation)){
				mBestLocation = location;
			}

		}

		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub

		}
	};
	private LocationManager mLocationManager;


	ILocationService.Stub mService = new ILocationService.Stub() {

		@Override
		public Location getBestLocation() throws RemoteException {
			return mBestLocation;
		}

	};

	@Override
	public IBinder onBind(Intent arg0) {
		return mService;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		mBestLocation = null; //mLocationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
		if(mBestLocation == null) {
			mBestLocation = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		}
		if(mBestLocation == null) {
			mBestLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		}
		mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, mLocationListener);
		mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mLocationListener);
		//mLocationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 0, 0, mLocationListener);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mLocationManager.removeUpdates(mLocationListener);
	}


	}
