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
package com.madrobot.tasks;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.HttpEntity;

import com.madrobot.net.HttpTaskHelper;

/**
 * Async task to download a list of bitmaps
 *<p>
 * This task takes a list of java.net.URI instances that point to bitmaps. The
 * bitmaps are downloaded(using Http GET) sequentially and sent to the UI using
 * the {@link TaskNotifier#onSuccess(DataResponse)} method .<br/>
 * <b>Usage</b>
 * 
 * <pre>
 * URI[] urls = new URI[] { URI.create(&quot;http://www.foo.com/image1.png&quot;),
 * 		URI.create(&quot;http://www.foo.com/image2.png&quot;) };
 * 
 * BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
 * 
 * Object[] param = new Object[2];
 * param[0] = urls;
 * param[1] = bitmapOptions;
 * new DownloadBitmapTask(this, this).execute(param);
 * </pre>
 * 
 * </p>
 * 
 */
public class DownloadBitmapTask extends AbstractTask {

	public DownloadBitmapTask(Context context, TaskNotifier notifier) {
		super(context, notifier);
	}

	@Override
	protected Object doInBackground(Object... params) {
		/*sending*/
		taskStarted();
		URI[] uri = (URI[]) params[0];
		BitmapFactory.Options opt = (Options) params[1];
		for(int i = 0; i < uri.length; i++){
			DataResponse response = new DataResponse();
			response.setResponseId(i);
			HttpTaskHelper helper = new HttpTaskHelper(uri[i]);
			try{
				Log.d("BitmapTask","Downloading Bitmap->"+uri[i]);
				HttpEntity entity = helper.execute();
				InputStream is = entity.getContent();
				Bitmap bitmap = BitmapFactory.decodeStream(is, null, opt);
				response.setData(bitmap);
				response.setResponseStatus(1);
				Log.d("BitmapTask","Complete!");
				publishProgress(response);
			} catch(IOException e){
				response.setResponseStatus(-1);
				response.setT(e);
				e.printStackTrace();
			} catch(URISyntaxException e){
				response.setResponseStatus(-1);
				response.setT(e);
				e.printStackTrace();
			}
		}
		return null;

	}


	@Override
	protected void onPostExecute(Object result) {
		notifier.onTaskCompleted();
	}

}
