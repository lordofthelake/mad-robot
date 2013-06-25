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
package com.madrobot.taskpool;

import java.util.concurrent.Callable;

/**
 * Contract for any class the needs to be run as a task.
 * <p>
 * A Sample Task that makes a Http request. It uses the {@code HttpTaskHelper}
 * to peform the Http operation <code>
 * <pre>
 * public class HttpTask implements ITask&lt;Container&gt; {
 * 
 * 	private HttpTaskHelper httpHelper;
 * 	private WebResponseNotificationListener listener;
 * 	private Context context;
 * 	// constructor that takes a simple listener to notify the caller about progress
 * 	public HttpRequest(WebResponseNotificationListener listener,Context context) {
 * 		this.listener = listener;
 * 		this.context=context;
 * 	}
 * 
 * 	//execute the task. the Type mentioned in the task is returned by this method.
 * 	&#064;Override
 * 	public Container call() throws Exception {
 * 		//Check if a carrier is available (WiFi,3G etc..)
 * 		if(CarrierHelper.getInstance(context).getCurrentCarrier()!=null){
 * 			HttpEntity httpEntity = httpServiceHelper.execute();
 * 			InputStream is = httpEntity.getContent();
 * 			//construct container from input stream
 * 			//..
 *  		//..
 *  		listener.notifyResponse(container);
 *  	}
 * 	}
 * 
 * 	&#064;Override
 * 	public void cancel() {
 * 
 * 	}
 * 
 * 	// called after the task is executed. The Type mentioned in the IServiceTask is sent here
 * 	&#064;Override
 * 	public void postExecute(Container container, Throwable throwable) {
 * 	}
 * 
 * 	//called before the task is executed.
 * 	&#064;Override
 * 	public void preExecute() throws Exception {
 * 		httpHelper = new HttpTaskHelper(new URI(&quot;http://www.google.com&quot;));
 * 	}
 * 
 * }
 * </pre>
 * </code>
 * </p>
 * 
 * @author Elton Kent
 * @param <T>
 *            The type task returns after execution. It can be {@code Void}
 */
public interface Task<T> extends Callable<T> {

	/**
	 * Cancel the execution of the task.
	 * <p>
	 * The implementing class should provide this functionality
	 * </p>
	 */
	void cancel();

	/**
	 * Called after the task is executed
	 * 
	 * @param t
	 * @param throwable
	 */
	void postExecute(T t, Throwable throwable);

	/**
	 * Called before the task is executed
	 * 
	 * @throws Exception
	 */
	void preExecute() throws Exception;
}
