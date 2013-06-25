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

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.madrobot.taskpool.TaskPoolManagerImpl.ManagedServiceTask;

/**
 * 
 * A TaskThreadPool that executes each submitted task using one of possibly
 * several pooled threads.
 * 
 */
class TaskPool extends ThreadPoolExecutor {
//	public static final int QUEUE_SIZE = 250;
//	public static final int MAX_THREADS_COUNT = 2;

	private static LinkedBlockingQueue<Runnable> blockingQueue = new LinkedBlockingQueue<Runnable>(250);
	private static final long serialVersionUID = 1L;

	private static ThreadFactory threadFactory = new ThreadFactory() {

		private AtomicInteger atomicInteger = new AtomicInteger(1);

		@Override
		public Thread newThread(Runnable runnable) {
			String threadName = "SessionThread=" + atomicInteger.getAndIncrement();
			return new Thread(runnable, threadName);
		}
	};

	/**
	 * Creates a new TaskThreadPool with the initial parameters.
	 */
	protected TaskPool() {
		super(TaskPoolConstants.MAX_THREADS_COUNT, TaskPoolConstants.MAX_THREADS_COUNT, 0L, TimeUnit.MILLISECONDS, blockingQueue, threadFactory);
	}

	/**
	 * Method invoked upon completion of execution of the given Runnable. This
	 * method is invoked by the thread that executed the task. If non-null, the
	 * Throwable is the uncaught exception that caused execution to terminate
	 * abruptly.
	 * 
	 * @param thread
	 *            - the thread that will run task r.
	 * @param runnable
	 *            - the task that will be executed.
	 */
	@Override
	protected void afterExecute(Runnable runnable, Throwable throwable) {
		ManagedServiceTask<?> serviceTask = (ManagedServiceTask<?>) runnable;
		serviceTask.postExecute();
	}

	/**
	 * Method invoked prior to executing the given Runnable in the given thread.
	 * This method is invoked by thread t that will execute task r, and may be
	 * used to re-initialize ThreadLocals, or to perform logging.
	 * 
	 * @param thread
	 *            - the thread that will run task r.
	 * @param runnable
	 *            - the task that will be executed.
	 */
	@Override
	protected void beforeExecute(Thread thread, Runnable runnable) {
		ManagedServiceTask<?> serviceTask = (ManagedServiceTask<?>) runnable;
		serviceTask.preExecute();
	}
}
