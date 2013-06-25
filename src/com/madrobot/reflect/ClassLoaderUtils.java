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
package com.madrobot.reflect;

/**
 * A class to centralize the class loader management code.
 */
public class ClassLoaderUtils {

	/**
	 * Get the loader for the given class.
	 * 
	 * @param clazz
	 *            the class to retrieve the loader for
	 * @return the class loader that loaded the provided class
	 */
	public static ClassLoader getClassLoader(Class clazz) {
		ClassLoader callersLoader = clazz.getClassLoader();
		if(callersLoader == null){
			callersLoader = ClassLoader.getSystemClassLoader();
		}
		return callersLoader;
	}

	/**
	 * Return the class loader to be used for instantiating application objects
	 * when required. This is determined based upon the following rules:
	 * <ul>
	 * <li>The specified class loader, if any</li>
	 * <li>The thread context class loader, if it exists and
	 * <code>useContextClassLoader</code> is true</li>
	 * <li>The class loader used to load the calling class.
	 * <li>The System class loader.
	 * </ul>
	 */
	public static ClassLoader getClassLoader(ClassLoader specifiedLoader, boolean useContextClassLoader,
			Class callingClass) {
		if(specifiedLoader != null){
			return specifiedLoader;
		}
		if(useContextClassLoader){
			ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
			if(classLoader != null){
				return classLoader;
			}
		}
		return getClassLoader(callingClass);
	}

	/**
	 * Return the class loader to be used for instantiating application objects
	 * when a context class loader is not specified. This is determined based
	 * upon the following rules:
	 * <ul>
	 * <li>The specified class loader, if any</li>
	 * <li>The class loader used to load the calling class.
	 * <li>The System class loader.
	 * </ul>
	 */
	public static ClassLoader getClassLoader(ClassLoader specifiedLoader, Class callingClass) {
		if(specifiedLoader != null){
			return specifiedLoader;
		}
		return getClassLoader(callingClass);
	}

	/**
	 * Loads the given class using the current Thread's context class loader
	 * first
	 * otherwise use the class loader which loaded this class.
	 * 
	 * @param className
	 *            The class to be loaded
	 * @param callingClass
	 *            The class which is calling this method
	 * @return The Loaded class
	 */
	public static Class loadClass(String className, Class callingClass) throws ClassNotFoundException {
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		if(loader == null){
			return getClassLoader(callingClass).loadClass(className);
		} else{
			return loader.loadClass(className);
		}
	}

	/**
	 * Loads the given class using:
	 * <ol>
	 * <li>the specified classloader,</li>
	 * <li>the current Thread's context class loader first, if asked</li>
	 * <li>otherwise use the class loader which loaded this class.</li>
	 * </ol>
	 */
	public static Class loadClass(String className, ClassLoader specifiedLoader, boolean useContextLoader,
			Class callingClass) throws ClassNotFoundException {
		Class clazz = null;
		if(specifiedLoader != null){
			try{
				clazz = specifiedLoader.loadClass(className);
			} catch(ClassNotFoundException e){
				e.printStackTrace();
			}
		}
		if(clazz == null && useContextLoader){
			ClassLoader contextLoader = Thread.currentThread().getContextClassLoader();
			if(contextLoader != null){
				try{
					clazz = contextLoader.loadClass(className);
				} catch(ClassNotFoundException e){
					e.printStackTrace();
				}
			}
		}
		if(clazz == null){
			ClassLoader loader = getClassLoader(callingClass);
			clazz = loader.loadClass(className);
		}
		return clazz;
	}
}
