package com.madrobot.device;

import java.lang.reflect.Method;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.IInterface;
import android.util.Log;

/**
 * Used to bind a service using code.
 * @author elton.stephen.kent
 *
 * @param <T>
 */
public class ServiceBinder<T extends IInterface> {

	private Context mContext;
	private Class<? extends IInterface> mInterfaceClass;
	private Runnable mOnServiceReady;
	private Class<? extends Service> mServiceClass;
	private ServiceConnection mServiceConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			try {
				Method asInterface = null;
				for(Class c : mInterfaceClass.getClasses()) {
					String className = c.getSimpleName();
					if(className.equals("Stub")) {
						asInterface = c.getMethod("asInterface", IBinder.class);
						break;
					}
				}
				
				mServiceInterface = (T)asInterface.invoke(null, service);
				if(mOnServiceReady != null) {
					mOnServiceReady.run();
				}
			
			}catch(Exception e) {
				Log.e(ServiceBinder.class.getName(), "Unable to bind to service", e);
			}
			
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			// TODO Auto-generated method stub
			
		}
		
	};
	
	private T mServiceInterface;
	
	public ServiceBinder(Context context, 
			Class<? extends Service> serviceClass, 
			Class<? extends IInterface> interfaceClass,
			Runnable onServiceReady) {
		mContext = context;
		mServiceClass = serviceClass;
		mInterfaceClass = interfaceClass;
		mOnServiceReady = onServiceReady;
		Intent serviceIntent = new Intent(context, mServiceClass);
		context.bindService(serviceIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
	}
	
	public T getServiceInterface() {
		return mServiceInterface;
	}
	
	public void unBind(){
		mContext.unbindService(mServiceConnection);
	}
}
