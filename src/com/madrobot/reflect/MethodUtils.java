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

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * <p>
 * Utility reflection methods focused on methods, originally from Commons
 * BeanUtils. Differences from the BeanUtils version may be noted, especially
 * where similar functionality already existed within Lang.
 * </p>
 * 
 * <h3>Known Limitations</h3> <h4>Accessing Public Methods In A Default Access
 * Superclass</h4>
 * <p>
 * There is an issue when invoking public methods contained in a default access
 * superclass on JREs prior to 1.4. Reflection locates these methods fine and
 * correctly assigns them as public. However, an
 * <code>IllegalAccessException</code> is thrown if the method is invoked.
 * </p>
 * 
 * <p>
 * <code>MethodUtils</code> contains a workaround for this situation. It will
 * attempt to call <code>setAccessible</code> on this method. If this call
 * succeeds, then the method can be invoked as normal. This call will only
 * succeed when the application has sufficient security privileges. If this call
 * fails then the method may fail.
 * </p>
 * 
 */
public class MethodUtils {

	private static Map declaredMethodCache = Collections.synchronizedMap(new WeakHashMap());

	/**
	 * <p>
	 * Find an accessible method that matches the given name and has compatible
	 * parameters. Compatible parameters mean that every method parameter is
	 * assignable from the given parameters. In other words, it finds a method
	 * with the given name that will take the parameters given.
	 * <p>
	 * 
	 * <p>
	 * This method is used by
	 * {@link #invokeMethod(Object object, String methodName, Object[] args, Class[] parameterTypes)}.
	 * 
	 * <p>
	 * This method can match primitive parameter by passing in wrapper classes.
	 * For example, a <code>Boolean</code> will match a primitive
	 * <code>boolean</code> parameter.
	 * 
	 * @param cls
	 *            find method in this class
	 * @param methodName
	 *            find method with this name
	 * @param parameterTypes
	 *            find method with most compatible parameters
	 * @return The accessible method
	 */
	public static Method findAccessibleMethod(Class<?> cls, String methodName, Class<?>... parameterTypes) {
		try{
			Method method = cls.getMethod(methodName, parameterTypes);
			MemberUtils.setAccessibleWorkaround(method);
			return method;
		} catch(NoSuchMethodException e){ /* SWALLOW */
		}
		// search through all methods
		Method bestMatch = null;
		Method[] methods = cls.getMethods();
		for(int i = 0, size = methods.length; i < size; i++){
			if(methods[i].getName().equals(methodName)){
				// compare parameters
				if(ClassUtils.isAssignable(parameterTypes, methods[i].getParameterTypes(), true)){
					// get accessible version of method
					Method accessibleMethod = getAccessibleMethod(methods[i]);
					if(accessibleMethod != null){
						if(bestMatch == null
								|| MemberUtils.compareParameterTypes(accessibleMethod.getParameterTypes(),
										bestMatch.getParameterTypes(), parameterTypes) < 0){
							bestMatch = accessibleMethod;
						}
					}
				}
			}
		}
		if(bestMatch != null){
			MemberUtils.setAccessibleWorkaround(bestMatch);
		}
		return bestMatch;
	}

	/**
	 * 
	 *Find accessible method and include any inherited interfaces as well.
	 *<p>
	 * Its a little slow when compared to
	 * {@link MethodUtils#findAccessibleMethod(Class, String, Class...)}
	 *</p>
	 * 
	 * @param start
	 * @param methodName
	 * @param argCount
	 *            number of arguments
	 * @param argumentTypes
	 *            list of argument types. If null the method is determined based
	 *            on <code>argCount</code>
	 * @return
	 */
	public static Method findAccessibleMethodIncludeInterfaces(Class start, String methodName,
			int argCount, Class argumentTypes[]) {
		
		if(methodName == null){
			return null;
		}
		// For overriden methods we need to find the most derived version.
		// So we start with the given class and walk up the superclass chain.

		Method method = null;

		for(Class cl = start; cl != null; cl = cl.getSuperclass()){
			Method methods[] = MethodUtils.getPublicDeclaredMethods(cl);
			for(int i = 0; i < methods.length; i++){
				method = methods[i];
				if(method == null){
					continue;
				}

				// make sure method signature matches.
				Class params[] = method.getParameterTypes();
				if(method.getName().equals(methodName) && params.length == argCount){
					if(argumentTypes != null){
						boolean different = false;
						if(argCount > 0){
							for(int j = 0; j < argCount; j++){
								if(params[j] != argumentTypes[j]){
									different = true;
									continue;
								}
							}
							if(different){
								continue;
							}
						}
					}
					return method;
				}
			}
		}
		method = null;

		// Now check any inherited interfaces. This is necessary both when
		// the argument class is itself an interface, and when the argument
		// class is an abstract class.
		Class ifcs[] = start.getInterfaces();
		for(int i = 0; i < ifcs.length; i++){
			// Note: The original implementation had both methods calling
			// the 3 arg method. This is preserved but perhaps it should
			// pass the args array instead of null.
			method = findAccessibleMethodIncludeInterfaces(ifcs[i], methodName, argCount, null);
			if(method != null){
				break;
			}
		}
		return method;
	}

	/**
	 * Flush Method caches
	 */
	public static void flushCaches() {
		declaredMethodCache.clear();

	}

	/**
	 * <p>
	 * Return an accessible method (that is, one that can be invoked via
	 * reflection) with given name and parameters. If no such method can be
	 * found, return <code>null</code>. This is just a convenient wrapper for
	 * {@link #getAccessibleMethod(Method method)}.
	 * </p>
	 * 
	 * @param cls
	 *            get method from this class
	 * @param methodName
	 *            get method with this name
	 * @param parameterTypes
	 *            with these parameters types
	 * @return The accessible method
	 */
	public static Method getAccessibleMethod(Class<?> cls, String methodName, Class<?>... parameterTypes) {
		try{
			return getAccessibleMethod(cls.getMethod(methodName, parameterTypes));
		} catch(NoSuchMethodException e){
			return (null);
		}
	}

	/**
	 * <p>
	 * Return an accessible method (that is, one that can be invoked via
	 * reflection) that implements the specified Method. If no such method can
	 * be found, return <code>null</code>.
	 * </p>
	 * 
	 * @param method
	 *            The method that we wish to call
	 * @return The accessible method
	 */
	public static Method getAccessibleMethod(Method method) {
		if(!MemberUtils.isAccessible(method)){
			return null;
		}
		// If the declaring class is public, we are done
		Class<?> cls = method.getDeclaringClass();
		if(Modifier.isPublic(cls.getModifiers())){
			return method;
		}
		String methodName = method.getName();
		Class<?>[] parameterTypes = method.getParameterTypes();

		// Check the implemented interfaces and subinterfaces
		method = getAccessibleMethodFromInterfaceNest(cls, methodName, parameterTypes);

		// Check the superclass chain
		if(method == null){
			method = getAccessibleMethodFromSuperclass(cls, methodName, parameterTypes);
		}
		return method;
	}

	/**
	 * <p>
	 * Return an accessible method (that is, one that can be invoked via
	 * reflection) that implements the specified method, by scanning through all
	 * implemented interfaces and subinterfaces. If no such method can be found,
	 * return <code>null</code>.
	 * </p>
	 * 
	 * <p>
	 * There isn't any good reason why this method must be private. It is
	 * because there doesn't seem any reason why other classes should call this
	 * rather than the higher level methods.
	 * </p>
	 * 
	 * @param cls
	 *            Parent class for the interfaces to be checked
	 * @param methodName
	 *            Method name of the method we wish to call
	 * @param parameterTypes
	 *            The parameter type signatures
	 * @return the accessible method or <code>null</code> if not found
	 */
	private static Method getAccessibleMethodFromInterfaceNest(Class<?> cls, String methodName,
			Class<?>... parameterTypes) {
		Method method = null;

		// Search up the superclass chain
		for(; cls != null; cls = cls.getSuperclass()){

			// Check the implemented interfaces of the parent class
			Class<?>[] interfaces = cls.getInterfaces();
			for(int i = 0; i < interfaces.length; i++){
				// Is this interface public?
				if(!Modifier.isPublic(interfaces[i].getModifiers())){
					continue;
				}
				// Does the method exist on this interface?
				try{
					method = interfaces[i].getDeclaredMethod(methodName, parameterTypes);
				} catch(NoSuchMethodException e){
					/*
					 * Swallow, if no method is found after the loop then this
					 * method returns null.
					 */
				}
				if(method != null){
					break;
				}
				// Recursively check our parent interfaces
				method = getAccessibleMethodFromInterfaceNest(interfaces[i], methodName, parameterTypes);
				if(method != null){
					break;
				}
			}
		}
		return method;
	}

	/**
	 * <p>
	 * Return an accessible method (that is, one that can be invoked via
	 * reflection) by scanning through the superclasses. If no such method can
	 * be found, return <code>null</code>.
	 * </p>
	 * 
	 * @param cls
	 *            Class to be checked
	 * @param methodName
	 *            Method name of the method we wish to call
	 * @param parameterTypes
	 *            The parameter type signatures
	 * @return the accessible method or <code>null</code> if not found
	 */
	private static Method getAccessibleMethodFromSuperclass(Class<?> cls, String methodName,
			Class<?>... parameterTypes) {
		Class<?> parentClass = cls.getSuperclass();
		while(parentClass != null){
			if(Modifier.isPublic(parentClass.getModifiers())){
				try{
					return parentClass.getMethod(methodName, parameterTypes);
				} catch(NoSuchMethodException e){
					return null;
				}
			}
			parentClass = parentClass.getSuperclass();
		}
		return null;
	}

	/**
	 * Get the methods declared as public within the class
	 * 
	 * @param clz
	 *            class to find public methods
	 * @return
	 */
	public static synchronized Method[] getPublicDeclaredMethods(Class clz) {
		// Looking up Class.getDeclaredMethods is relatively expensive,
		// so we cache the results.
		Method[] result = null;
		// if(!ReflectUtil.isPackageAccessible(clz)){
		// return new Method[0];
		// }
		final Class fclz = clz;
		Reference ref = (Reference) declaredMethodCache.get(fclz);
		if(ref != null){
			result = (Method[]) ref.get();
			if(result != null){
				return result;
			}
		}

		// We have to raise privilege for getDeclaredMethods
		result = (Method[]) AccessController.doPrivileged(new PrivilegedAction() {
			@Override
			public Object run() {
				return fclz.getDeclaredMethods();
			}
		});

		// Null out any non-public methods.
		for(int i = 0; i < result.length; i++){
			Method method = result[i];
			int mods = method.getModifiers();
			if(!Modifier.isPublic(mods)){
				result[i] = null;
			}
		}
		// Add it to the cache.
		declaredMethodCache.put(fclz, new SoftReference(result));
		return result;
	}

	/**
	 * <p>
	 * Invoke a method whose parameter types match exactly the object types.
	 * </p>
	 * 
	 * <p>
	 * This uses reflection to invoke the method obtained from a call to
	 * <code>getAccessibleMethod()</code>.
	 * </p>
	 * 
	 * @param object
	 *            invoke method on this object
	 * @param methodName
	 *            get method with this name
	 * @param args
	 *            use these arguments - treat null as empty array
	 * @return The value returned by the invoked method
	 * 
	 * @throws NoSuchMethodException
	 *             if there is no such accessible method
	 * @throws InvocationTargetException
	 *             wraps an exception thrown by the
	 *             method invoked
	 * @throws IllegalAccessException
	 *             if the requested method is not accessible
	 *             via reflection
	 */
	public static Object invokeExactMethod(Object object, String methodName, Object... args)
			throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
		if(args == null){
			args = ArrayUtils.EMPTY_OBJECT_ARRAY;
		}
		int arguments = args.length;
		Class<?>[] parameterTypes = new Class[arguments];
		for(int i = 0; i < arguments; i++){
			parameterTypes[i] = args[i].getClass();
		}
		return invokeExactMethod(object, methodName, args, parameterTypes);
	}

	/**
	 * <p>
	 * Invoke a method whose parameter types match exactly the parameter types
	 * given.
	 * </p>
	 * 
	 * <p>
	 * This uses reflection to invoke the method obtained from a call to
	 * <code>getAccessibleMethod()</code>.
	 * </p>
	 * 
	 * @param object
	 *            invoke method on this object
	 * @param methodName
	 *            get method with this name
	 * @param args
	 *            use these arguments - treat null as empty array
	 * @param parameterTypes
	 *            match these parameters - treat null as empty array
	 * @return The value returned by the invoked method
	 * 
	 * @throws NoSuchMethodException
	 *             if there is no such accessible method
	 * @throws InvocationTargetException
	 *             wraps an exception thrown by the
	 *             method invoked
	 * @throws IllegalAccessException
	 *             if the requested method is not accessible
	 *             via reflection
	 */
	public static Object invokeExactMethod(Object object, String methodName, Object[] args,
			Class<?>[] parameterTypes) throws NoSuchMethodException, IllegalAccessException,
			InvocationTargetException {
		if(args == null){
			args = ArrayUtils.EMPTY_OBJECT_ARRAY;
		}
		if(parameterTypes == null){
			parameterTypes = ArrayUtils.EMPTY_CLASS_ARRAY;
		}
		Method method = getAccessibleMethod(object.getClass(), methodName, parameterTypes);
		if(method == null){
			throw new NoSuchMethodException("No such accessible method: " + methodName + "() on object: "
					+ object.getClass().getName());
		}
		return method.invoke(object, args);
	}

	/**
	 * <p>
	 * Invoke a static method whose parameter types match exactly the object
	 * types.
	 * </p>
	 * 
	 * <p>
	 * This uses reflection to invoke the method obtained from a call to
	 * {@link #getAccessibleMethod(Class, String, Class[])}.
	 * </p>
	 * 
	 * @param cls
	 *            invoke static method on this class
	 * @param methodName
	 *            get method with this name, Case sensitive.
	 * @param args
	 *            use these arguments - treat null as empty array
	 * @return The value returned by the invoked method
	 * 
	 * @throws NoSuchMethodException
	 *             if there is no such accessible method
	 * @throws InvocationTargetException
	 *             wraps an exception thrown by the
	 *             method invoked
	 * @throws IllegalAccessException
	 *             if the requested method is not accessible
	 *             via reflection
	 */
	public static Object invokeExactStaticMethod(Class<?> cls, String methodName, Object... args)
			throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
		if(args == null){
			args = ArrayUtils.EMPTY_OBJECT_ARRAY;
		}
		int arguments = args.length;
		Class<?>[] parameterTypes = new Class[arguments];
		for(int i = 0; i < arguments; i++){
			parameterTypes[i] = args[i].getClass();
		}
		return invokeExactStaticMethod(cls, methodName, args, parameterTypes);
	}

	/**
	 * <p>
	 * Invoke a static method whose parameter types match exactly the parameter
	 * types given.
	 * </p>
	 * 
	 * <p>
	 * This uses reflection to invoke the method obtained from a call to
	 * {@link #getAccessibleMethod(Class, String, Class[])}.
	 * </p>
	 * 
	 * @param cls
	 *            invoke static method on this class
	 * @param methodName
	 *            get method with this name
	 * @param args
	 *            use these arguments - treat null as empty array
	 * @param parameterTypes
	 *            match these parameters - treat null as empty array
	 * @return The value returned by the invoked method
	 * 
	 * @throws NoSuchMethodException
	 *             if there is no such accessible method
	 * @throws InvocationTargetException
	 *             wraps an exception thrown by the
	 *             method invoked
	 * @throws IllegalAccessException
	 *             if the requested method is not accessible
	 *             via reflection
	 */
	public static Object invokeExactStaticMethod(Class<?> cls, String methodName, Object[] args,
			Class<?>[] parameterTypes) throws NoSuchMethodException, IllegalAccessException,
			InvocationTargetException {
		if(args == null){
			args = ArrayUtils.EMPTY_OBJECT_ARRAY;
		}
		if(parameterTypes == null){
			parameterTypes = ArrayUtils.EMPTY_CLASS_ARRAY;
		}
		Method method = getAccessibleMethod(cls, methodName, parameterTypes);
		if(method == null){
			throw new NoSuchMethodException("No such accessible method: " + methodName + "() on class: "
					+ cls.getName());
		}
		return method.invoke(null, args);
	}

	/**
	 * <p>
	 * Invoke a named method whose parameter type matches the object type.
	 * </p>
	 * 
	 * <p>
	 * This method delegates the method search to
	 * {@link #findAccessibleMethod(Class, String, Class[])}.
	 * </p>
	 * 
	 * <p>
	 * This method supports calls to methods taking primitive parameters via
	 * passing in wrapping classes. So, for example, a <code>Boolean</code>
	 * object would match a <code>boolean</code> primitive.
	 * </p>
	 * 
	 * <p>
	 * This is a convenient wrapper for
	 * {@link #invokeMethod(Object object,String methodName, Object[] args, Class[] parameterTypes)}
	 * .
	 * </p>
	 * 
	 * @param object
	 *            invoke method on this object
	 * @param methodName
	 *            get method with this name
	 * @param args
	 *            use these arguments - treat null as empty array
	 * @return The value returned by the invoked method
	 * 
	 * @throws NoSuchMethodException
	 *             if there is no such accessible method
	 * @throws InvocationTargetException
	 *             wraps an exception thrown by the method invoked
	 * @throws IllegalAccessException
	 *             if the requested method is not accessible via reflection
	 */
	public static Object invokeMethod(Object object, String methodName, Object... args)
			throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
		if(args == null){
			args = ArrayUtils.EMPTY_OBJECT_ARRAY;
		}
		int arguments = args.length;
		Class<?>[] parameterTypes = new Class[arguments];
		for(int i = 0; i < arguments; i++){
			parameterTypes[i] = args[i].getClass();
		}
		return invokeMethod(object, methodName, args, parameterTypes);
	}

	/**
	 * <p>
	 * Invoke a named method whose parameter type matches the object type.
	 * </p>
	 * 
	 * <p>
	 * This method delegates the method search to
	 * {@link #findAccessibleMethod(Class, String, Class[])}.
	 * </p>
	 * 
	 * <p>
	 * This method supports calls to methods taking primitive parameters via
	 * passing in wrapping classes. So, for example, a <code>Boolean</code>
	 * object would match a <code>boolean</code> primitive.
	 * </p>
	 * 
	 * @param object
	 *            invoke method on this object
	 * @param methodName
	 *            get method with this name
	 * @param args
	 *            use these arguments - treat null as empty array
	 * @param parameterTypes
	 *            match these parameters - treat null as empty array
	 * @return The value returned by the invoked method
	 * 
	 * @throws NoSuchMethodException
	 *             if there is no such accessible method
	 * @throws InvocationTargetException
	 *             wraps an exception thrown by the method invoked
	 * @throws IllegalAccessException
	 *             if the requested method is not accessible via reflection
	 */
	public static Object invokeMethod(Object object, String methodName, Object[] args,
			Class<?>[] parameterTypes) throws NoSuchMethodException, IllegalAccessException,
			InvocationTargetException {
		if(parameterTypes == null){
			parameterTypes = ArrayUtils.EMPTY_CLASS_ARRAY;
		}
		if(args == null){
			args = ArrayUtils.EMPTY_OBJECT_ARRAY;
		}
		Method method = findAccessibleMethod(object.getClass(), methodName, parameterTypes);
		if(method == null){
			throw new NoSuchMethodException("No such accessible method: " + methodName + "() on object: "
					+ object.getClass().getName());
		}
		return method.invoke(object, args);
	}

	/**
	 * <p>
	 * Invoke a named static method whose parameter type matches the object
	 * type.
	 * </p>
	 * 
	 * <p>
	 * This method delegates the method search to
	 * {@link #findAccessibleMethod(Class, String, Class[])}.
	 * </p>
	 * 
	 * <p>
	 * This method supports calls to methods taking primitive parameters via
	 * passing in wrapping classes. So, for example, a <code>Boolean</code>
	 * class would match a <code>boolean</code> primitive.
	 * </p>
	 * 
	 * <p>
	 * This is a convenient wrapper for
	 * {@link #invokeStaticMethod(Class objectClass,String methodName,Object [] args,Class[] parameterTypes)}
	 * .
	 * </p>
	 * 
	 * @param cls
	 *            invoke static method on this class
	 * @param methodName
	 *            get method with this name, Case sensitive.
	 * @param args
	 *            use these arguments - treat null as empty array
	 * @return The value returned by the invoked method
	 * 
	 * @throws NoSuchMethodException
	 *             if there is no such accessible method
	 * @throws InvocationTargetException
	 *             wraps an exception thrown by the
	 *             method invoked
	 * @throws IllegalAccessException
	 *             if the requested method is not accessible
	 *             via reflection
	 */
	public static Object invokeStaticMethod(Class<?> cls, String methodName, Object... args)
			throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
		if(args == null){
			args = ArrayUtils.EMPTY_OBJECT_ARRAY;
		}
		int arguments = args.length;
		Class<?>[] parameterTypes = new Class[arguments];
		for(int i = 0; i < arguments; i++){
			parameterTypes[i] = args[i].getClass();
		}
		return invokeStaticMethod(cls, methodName, args, parameterTypes);
	}

	/**
	 * <p>
	 * Invoke a named static method whose parameter type matches the object
	 * type.
	 * </p>
	 * 
	 * <p>
	 * This method delegates the method search to
	 * {@link #findAccessibleMethod(Class, String, Class[])}.
	 * </p>
	 * 
	 * <p>
	 * This method supports calls to methods taking primitive parameters via
	 * passing in wrapping classes. So, for example, a <code>Boolean</code>
	 * class would match a <code>boolean</code> primitive.
	 * </p>
	 * 
	 * 
	 * @param cls
	 *            invoke static method on this class
	 * @param methodName
	 *            get method with this name, Case sensitive.
	 * @param args
	 *            use these arguments - treat null as empty array
	 * @param parameterTypes
	 *            match these parameters - treat null as empty array
	 * @return The value returned by the invoked method
	 * 
	 * @throws NoSuchMethodException
	 *             if there is no such accessible method
	 * @throws InvocationTargetException
	 *             wraps an exception thrown by the
	 *             method invoked
	 * @throws IllegalAccessException
	 *             if the requested method is not accessible
	 *             via reflection
	 */
	public static Object invokeStaticMethod(Class<?> cls, String methodName, Object[] args,
			Class<?>[] parameterTypes) throws NoSuchMethodException, IllegalAccessException,
			InvocationTargetException {
		if(parameterTypes == null){
			parameterTypes = ArrayUtils.EMPTY_CLASS_ARRAY;
		}
		if(args == null){
			args = ArrayUtils.EMPTY_OBJECT_ARRAY;
		}
		Method method = findAccessibleMethod(cls, methodName, parameterTypes);
		if(method == null){
			throw new NoSuchMethodException("No such accessible method: " + methodName + "() on class: "
					+ cls.getName());
		}
		return method.invoke(null, args);
	}

	/**
	 * Return true iff the given method throws the given exception
	 * 
	 * @param method The method that throws the exception
	 * @param exception
	 * @return
	 */
	public static boolean throwsException(Method method, Class exception) {
		Class exs[] = method.getExceptionTypes();
		for(int i = 0; i < exs.length; i++){
			if(exs[i] == exception){
				return true;
			}
		}
		return false;
	}

	/**
	 * <p>
	 * MethodUtils instances should NOT be constructed in standard programming.
	 * Instead, the class should be used as
	 * <code>MethodUtils.getAccessibleMethod(method)</code>.
	 * </p>
	 * 
	 * <p>
	 * This constructor is public to permit tools that require a JavaBean
	 * instance to operate.
	 * </p>
	 */
	private MethodUtils() {
		super();
	}
}
