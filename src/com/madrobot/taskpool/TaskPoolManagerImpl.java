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

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.concurrent.FutureTask;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public final class TaskPoolManagerImpl implements TaskPoolManager {

	// ////////////////////////////////////////////////////////////
	// Helper classes
	/**
	 * Subclass of FutureTask . Helper class to manage the active task
	 * 
	 * @see FutureTask
	 */
	public class ManagedServiceTask<T> extends FutureTask<T> {

		/**
		 * Field to hold the submitted active task
		 * 
		 */
		private final Task<T> task;
		private Throwable throwable;

		/**
		 * default constructor to hold the submitted active task
		 * 
		 * @param task
		 *            active task
		 */
		public ManagedServiceTask(Task<T> task) {
			super(task);
			this.task = task;
		}

		/**
		 * Attempts to cancel execution of this task. If this attempt will
		 * successful, and this task has not started when cancel is called, this
		 * task should never run.
		 */
		@Override
		public boolean cancel(boolean mayInterruptIfRunning) {
			boolean cancelSuccess = false;
			try{
				task.cancel();
			} finally{
				cancelSuccess = super.cancel(mayInterruptIfRunning);
				done();
			}
			return cancelSuccess;
		}

		/**
		 * Called the when the task get done
		 */
		@Override
		protected void done() {
			removeFromActiveTasks();
			super.done();
		}

		/**
		 * Called before postExecute
		 */
		public void postExecute() {

			try{
				if(!isCancelled()){
					task.postExecute(get(), throwable);
				}
			} catch(Throwable e){
				task.postExecute(null, e);
				cancel(true);
			} finally{
				done();
			}
		}

		/**
		 * Called after the preExecute
		 */
		public void preExecute() {
			try{
				task.preExecute();
			} catch(Throwable e){
				this.throwable = e;
				cancel(true);
			}
		}

		/**
		 * Access point to remove the active task
		 */
		private void removeFromActiveTasks() {
			listLock.lock();
			try{
				activeTasks.remove(this);
			} finally{
				listLock.unlock();
			}
		}
	}

	/**
	 * Field to hold the service manager singleton instance
	 */
	private static TaskPoolManager serviceManager;
	private static String TAG = "LIB:TaskManager";
	// ////////////////////////////////////////////////////////////
	// Public methods
	/**
	 * The IServiceManager API is the primary access point to initialize service
	 * handler. Only static access is followed, since only one instance of the
	 * service handler will be created
	 * 
	 * @see #initializeServiceHandler()
	 * 
	 * @return return shared instance of {@link IServiceHandler}
	 */
	public static synchronized TaskPoolManager getTaskPool() {
		if(serviceManager == null){
			serviceManager = new TaskPoolManagerImpl();
		}
		return serviceManager;
	}

	/**
	 * Field to hold the active task
	 */
	private final List<ManagedServiceTask<?>> activeTasks = new ArrayList<ManagedServiceTask<?>>();

	private boolean isRunning = true;

	// ////////////////////////////////////////////////////////////
	// Fields
	/**
	 * Field to block the operation
	 */
	private final Lock listLock = new ReentrantLock();

	/**
	 * Field to hold the thread pool executor instance
	 */
	private TaskPool sessionThreadPool;

	// ////////////////////////////////////////////////////////////
	// Private methods
	/**
	 * private constructor to achieve the singleton
	 * 
	 * @see #initializeServiceHandler()
	 */
	private TaskPoolManagerImpl() {
		initializeServiceHandler();
	}

	/**
	 * Add the submitted task to the active task
	 * 
	 * @param managedTask
	 *            future taks
	 */
	private <T> void addToActiveTasks(ManagedServiceTask<T> managedTask) {
		listLock.lock();
		try{
			activeTasks.add(managedTask);
		} finally{
			listLock.unlock();
		}
	}

	/**
	 * Base implementation of super{@link #cancelAllTasks()}
	 * 
	 * @see super{@link #cancelAllTasks()}
	 */
	@Override
	public void cancelAllTasks() {
		listLock.lock();

		try{

			sessionThreadPool.purge();
			for(ManagedServiceTask<?> t : activeTasks){
				t.cancel(true);
			}
			activeTasks.clear();
			sessionThreadPool.purge();
		} catch(ConcurrentModificationException e){

		} finally{
			listLock.unlock();
		}
	}

	/**
	 * Initialize the thread pool
	 * 
	 * @see TaskPool
	 */
	private void initializeServiceHandler() {
		sessionThreadPool = new TaskPool();
	}

	/**
	 * Base implementation of super{@link #shutdown()}
	 * 
	 * @see super{@link #shutdown()}
	 */
	@Override
	public void shutdown() {
		isRunning = false;
		sessionThreadPool.shutdownNow();
		TaskPoolManagerImpl.serviceManager = null;
	}

	/**
	 * Base implementation of super{@link #submit(Task)}
	 * 
	 * @see super{@link #submit(Task)}
	 */
	@Override
	public <T> void submit(Task<T> task) {
		if(isRunning){
			ManagedServiceTask<T> managedTask = new ManagedServiceTask<T>(task);
			addToActiveTasks(managedTask);
			sessionThreadPool.execute(managedTask);
		}
	}
}
