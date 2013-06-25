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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.io.ObjectStreamConstants;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import com.madrobot.di.wizard.xml.converters.ObjectAccessException;
import com.madrobot.di.wizard.xml.core.TypedNull;
import com.madrobot.text.StringUtils;

/**
 * <p>
 * Operates on classes without using reflection.
 * </p>
 * 
 * <p>
 * This class handles invalid <code>null</code> inputs as best it can. Each
 * method documents its behaviour in more detail.
 * </p>
 * 
 * <p>
 * The notion of a <code>canonical name</code> includes the human readable name
 * for the type, for example <code>int[]</code>. The non-canonical method
 * variants work with the JVM names, such as <code>[I</code>.
 * </p>
 */
public class ClassUtils {

	private static class TypedValue {
        final Class type;
        final Object value;

        public TypedValue(final Class type, final Object value) {
            super();
            this.type = type;
            this.value = value;
        }

        @Override
		public String toString()
        {
                return type.getName() + ":" + value;
        }
    }

	/**
	 * Maps a primitive class name to its corresponding abbreviation used in
	 * array class names.
	 */
	private static final Map<String, String> abbreviationMap = new HashMap<String, String>();

	/**
	 * <p>
	 * The inner class separator character: <code>'$' == {@value}</code>.
	 * </p>
	 */
	public static final char INNER_CLASS_SEPARATOR_CHAR = '$';
	/**
	 * <p>
	 * The inner class separator String: <code>"$"</code>.
	 * </p>
	 */
	public static final String INNER_CLASS_SEPARATOR = String
			.valueOf(INNER_CLASS_SEPARATOR_CHAR);

	/**
	 * <p>
	 * The package separator character: <code>'&#x2e;' == {@value}</code>.
	 * </p>
	 */
	public static final char PACKAGE_SEPARATOR_CHAR = '.';

	/**
	 * <p>
	 * The package separator String: <code>"&#x2e;"</code>.
	 * </p>
	 */
	public static final String PACKAGE_SEPARATOR = String
			.valueOf(PACKAGE_SEPARATOR_CHAR);

	/**
	 * Maps primitive <code>Class</code>es to their corresponding wrapper
	 * <code>Class</code>.
	 */
	private static final Map<Class<?>, Class<?>> primitiveWrapperMap = new HashMap<Class<?>, Class<?>>();
	/**
	 * Maps an abbreviation used in array class names to corresponding primitive
	 * class name.
	 */
	private static final Map<String, String> reverseAbbreviationMap = new HashMap<String, String>();

	/**
	 * Maps wrapper <code>Class</code>es to their corresponding primitive types.
	 */
	private static final Map<Class<?>, Class<?>> wrapperPrimitiveMap = new HashMap<Class<?>, Class<?>>();

	static {
		primitiveWrapperMap.put(Boolean.TYPE, Boolean.class);
		primitiveWrapperMap.put(Byte.TYPE, Byte.class);
		primitiveWrapperMap.put(Character.TYPE, Character.class);
		primitiveWrapperMap.put(Short.TYPE, Short.class);
		primitiveWrapperMap.put(Integer.TYPE, Integer.class);
		primitiveWrapperMap.put(Long.TYPE, Long.class);
		primitiveWrapperMap.put(Double.TYPE, Double.class);
		primitiveWrapperMap.put(Float.TYPE, Float.class);
		primitiveWrapperMap.put(Void.TYPE, Void.TYPE);
	}

	static {
		for (Class<?> primitiveClass : primitiveWrapperMap.keySet()) {
			Class<?> wrapperClass = primitiveWrapperMap.get(primitiveClass);
			if (!primitiveClass.equals(wrapperClass)) {
				wrapperPrimitiveMap.put(wrapperClass, primitiveClass);
			}
		}
	}

	/**
	 * Feed abbreviation maps
	 */
	static {
		addAbbreviation("int", "I");
		addAbbreviation("boolean", "Z");
		addAbbreviation("float", "F");
		addAbbreviation("long", "J");
		addAbbreviation("short", "S");
		addAbbreviation("byte", "B");
		addAbbreviation("double", "D");
		addAbbreviation("char", "C");
	}

	/**
	 * Add primitive type abbreviation to maps of abbreviations.
	 * 
	 * @param primitive
	 *            Canonical name of primitive type
	 * @param abbreviation
	 *            Corresponding abbreviation of primitive type
	 */
	private static void addAbbreviation(String primitive, String abbreviation) {
		abbreviationMap.put(primitive, abbreviation);
		reverseAbbreviationMap.put(abbreviation, primitive);
	}

	/**
	 * <p>
	 * Given a <code>List</code> of <code>Class</code> objects, this method
	 * converts them into class names.
	 * </p>
	 * 
	 * <p>
	 * A new <code>List</code> is returned. <code>null</code> objects will be
	 * copied into the returned list as <code>null</code>.
	 * </p>
	 * 
	 * @param classes
	 *            the classes to change
	 * @return a <code>List</code> of class names corresponding to the Class
	 *         objects, <code>null</code> if null input
	 * @throws ClassCastException
	 *             if <code>classes</code> contains a non-<code>Class</code>
	 *             entry
	 */
	public static List<String> convertClassesToClassNames(List<Class<?>> classes) {
		if (classes == null) {
			return null;
		}
		List<String> classNames = new ArrayList<String>(classes.size());
		for (Class<?> cls : classes) {
			if (cls == null) {
				classNames.add(null);
			} else {
				classNames.add(cls.getName());
			}
		}
		return classNames;
	}

	// Convert list
	// ----------------------------------------------------------------------
	/**
	 * <p>
	 * Given a <code>List</code> of class names, this method converts them into
	 * classes.
	 * </p>
	 * 
	 * <p>
	 * A new <code>List</code> is returned. If the class name cannot be found,
	 * <code>null</code> is stored in the <code>List</code>. If the class name
	 * in the <code>List</code> is <code>null</code>, <code>null</code> is
	 * stored in the output <code>List</code>.
	 * </p>
	 * 
	 * @param classNames
	 *            the classNames to change
	 * @return a <code>List</code> of Class objects corresponding to the class
	 *         names, <code>null</code> if null input
	 * @throws ClassCastException
	 *             if classNames contains a non String entry
	 */
	public static List<Class<?>> convertClassNamesToClasses(
			List<String> classNames) {
		if (classNames == null) {
			return null;
		}
		List<Class<?>> classes = new ArrayList<Class<?>>(classNames.size());
		for (String className : classNames) {
			try {
				classes.add(Class.forName(className));
			} catch (Exception ex) {
				classes.add(null);
			}
		}
		return classes;
	}
	
    /**
	 * <p>
	 * Gets a <code>List</code> of all interfaces implemented by the given class
	 * and its superclasses.
	 * </p>
	 * 
	 * <p>
	 * The order is determined by looking through each interface in turn as
	 * declared in the source file and following its hierarchy up. Then each
	 * superclass is considered in the same way. Later duplicates are ignored,
	 * so the order is maintained.
	 * </p>
	 * 
	 * @param cls
	 *            the class to look up, may be <code>null</code>
	 * @return the <code>List</code> of interfaces in order, <code>null</code>
	 *         if null input
	 */
	public static List<Class<?>> getAllInterfaces(Class<?> cls) {
		if (cls == null) {
			return null;
		}

		LinkedHashSet<Class<?>> interfacesFound = new LinkedHashSet<Class<?>>();
		getAllInterfaces(cls, interfacesFound);

		return new ArrayList<Class<?>>(interfacesFound);
	}
    
    /**
	 * Get the interfaces for the specified class.
	 * 
	 * @param cls
	 *            the class to look up, may be <code>null</code>
	 * @param interfacesFound
	 *            the <code>Set</code> of interfaces for the class
	 */
	private static void getAllInterfaces(Class<?> cls,
			HashSet<Class<?>> interfacesFound) {
		while (cls != null) {
			Class<?>[] interfaces = cls.getInterfaces();

			for (Class<?> i : interfaces) {
				if (interfacesFound.add(i)) {
					getAllInterfaces(i, interfacesFound);
				}
			}

			cls = cls.getSuperclass();
		}
	}

    // Superclasses/Superinterfaces
	// ----------------------------------------------------------------------
	/**
	 * <p>
	 * Gets a <code>List</code> of superclasses for the given class.
	 * </p>
	 * 
	 * @param cls
	 *            the class to look up, may be <code>null</code>
	 * @return the <code>List</code> of superclasses in order going up from this
	 *         one <code>null</code> if null input
	 */
	public static List<Class<?>> getAllSuperclasses(Class<?> cls) {
		if (cls == null) {
			return null;
		}
		List<Class<?>> classes = new ArrayList<Class<?>>();
		Class<?> superclass = cls.getSuperclass();
		while (superclass != null) {
			classes.add(superclass);
			superclass = superclass.getSuperclass();
		}
		return classes;
	}

	/**
	 * <p>
	 * Converts a given name of class into canonical format. If name of class is
	 * not a name of array class it returns unchanged name.
	 * </p>
	 * <p>
	 * Example:
	 * <ul>
	 * <li><code>getCanonicalName("[I") = "int[]"</code></li>
	 * <li>
	 * <code>getCanonicalName("[Ljava.lang.String;") = "java.lang.String[]"</code>
	 * </li>
	 * <li>
	 * <code>getCanonicalName("java.lang.String") = "java.lang.String"</code></li>
	 * </ul>
	 * </p>
	 * 
	 * @param className
	 *            the name of class
	 * @return canonical form of class name
	 * @since 2.4
	 */
	private static String getCanonicalName(String className) {
		className = StringUtils.deleteWhitespace(className);
		if (className == null) {
			return null;
		} else {
			int dim = 0;
			while (className.startsWith("[")) {
				dim++;
				className = className.substring(1);
			}
			if (dim < 1) {
				return className;
			} else {
				if (className.startsWith("L")) {
					className = className.substring(1,
							className.endsWith(";") ? className.length() - 1
									: className.length());
				} else {
					if (className.length() > 0) {
						className = reverseAbbreviationMap.get(className
								.substring(0, 1));
					}
				}
				StringBuilder canonicalClassNameBuffer = new StringBuilder(
						className);
				for (int i = 0; i < dim; i++) {
					canonicalClassNameBuffer.append("[]");
				}
				return canonicalClassNameBuffer.toString();
			}
		}
	}

	/**
	 * Returns the (initialized) class represented by <code>className</code>
	 * using the <code>classLoader</code>. This implementation supports the
	 * syntaxes "<code>java.util.Map.Entry[]</code>", "
	 * <code>java.util.Map$Entry[]</code>", "
	 * <code>[Ljava.util.Map.Entry;</code>", and "
	 * <code>[Ljava.util.Map$Entry;</code>".
	 * 
	 * @param classLoader
	 *            the class loader to use to load the class
	 * @param className
	 *            the class name
	 * @return the class represented by <code>className</code> using the
	 *         <code>classLoader</code>
	 * @throws ClassNotFoundException
	 *             if the class is not found
	 */
	public static Class<?> getClass(ClassLoader classLoader, String className)
			throws ClassNotFoundException {
		return getClass(classLoader, className, true);
	}

	// Class loading
	// ----------------------------------------------------------------------
	/**
	 * Returns the class represented by <code>className</code> using the
	 * <code>classLoader</code>. This implementation supports the syntaxes "
	 * <code>java.util.Map.Entry[]</code>", "<code>java.util.Map$Entry[]</code>
	 * ", "<code>[Ljava.util.Map.Entry;</code>", and "
	 * <code>[Ljava.util.Map$Entry;</code>".
	 * 
	 * @param classLoader
	 *            the class loader to use to load the class
	 * @param className
	 *            the class name
	 * @param initialize
	 *            whether the class must be initialized
	 * @return the class represented by <code>className</code> using the
	 *         <code>classLoader</code>
	 * @throws ClassNotFoundException
	 *             if the class is not found
	 */
	public static Class<?> getClass(ClassLoader classLoader, String className,
			boolean initialize) throws ClassNotFoundException {
		try {
			Class<?> clazz;
			if (abbreviationMap.containsKey(className)) {
				String clsName = "[" + abbreviationMap.get(className);
				clazz = Class.forName(clsName, initialize, classLoader)
						.getComponentType();
			} else {
				clazz = Class.forName(toCanonicalName(className), initialize,
						classLoader);
			}
			return clazz;
		} catch (ClassNotFoundException ex) {
			// allow path separators (.) as inner class name separators
			int lastDotIndex = className.lastIndexOf(PACKAGE_SEPARATOR_CHAR);

			if (lastDotIndex != -1) {
				try {
					return getClass(
							classLoader,
							className.substring(0, lastDotIndex)
									+ INNER_CLASS_SEPARATOR_CHAR
									+ className.substring(lastDotIndex + 1),
							initialize);
				} catch (ClassNotFoundException ex2) {
				}
			}

			throw ex;
		}
	}

	/**
	 * Returns the (initialized) class represented by <code>className</code>
	 * using the current thread's context class loader. This implementation
	 * supports the syntaxes "<code>java.util.Map.Entry[]</code>", "
	 * <code>java.util.Map$Entry[]</code>", "
	 * <code>[Ljava.util.Map.Entry;</code>", and "
	 * <code>[Ljava.util.Map$Entry;</code>".
	 * 
	 * @param className
	 *            the class name
	 * @return the class represented by <code>className</code> using the current
	 *         thread's context class loader
	 * @throws ClassNotFoundException
	 *             if the class is not found
	 */
	public static Class<?> getClass(String className)
			throws ClassNotFoundException {
		return getClass(className, true);
	}

	/**
	 * Returns the class represented by <code>className</code> using the current
	 * thread's context class loader. This implementation supports the syntaxes
	 * "<code>java.util.Map.Entry[]</code>", "
	 * <code>java.util.Map$Entry[]</code>", "<code>[Ljava.util.Map.Entry;</code>
	 * ", and " <code>[Ljava.util.Map$Entry;</code>".
	 * 
	 * @param className
	 *            the class name
	 * @param initialize
	 *            whether the class must be initialized
	 * @return the class represented by <code>className</code> using the current
	 *         thread's context class loader
	 * @throws ClassNotFoundException
	 *             if the class is not found
	 */
	public static Class<?> getClass(String className, boolean initialize)
			throws ClassNotFoundException {
		ClassLoader contextCL = Thread.currentThread().getContextClassLoader();
		ClassLoader loader = contextCL == null ? ClassUtils.class
				.getClassLoader() : contextCL;
		return getClass(loader, className, initialize);
	}

	/**
	 * <p>
	 * Gets the package name from the canonical name of a <code>Class</code>.
	 * </p>
	 * 
	 * @param cls
	 *            the class to get the package name for, may be
	 *            <code>null</code>.
	 * @return the package name or an empty string
	 * @since 2.4
	 */
	public static String getPackageCanonicalName(Class<?> cls) {
		if (cls == null) {
			return StringUtils.EMPTY;
		}
		return getPackageCanonicalName(cls.getName());
	}

	// Package name
	// ----------------------------------------------------------------------
	/**
	 * <p>
	 * Gets the package name from the canonical name of an <code>Object</code>.
	 * </p>
	 * 
	 * @param object
	 *            the class to get the package name for, may be null
	 * @param valueIfNull
	 *            the value to return if null
	 * @return the package name of the object, or the null value
	 * @since 2.4
	 */
	public static String getPackageCanonicalName(Object object,
			String valueIfNull) {
		if (object == null) {
			return valueIfNull;
		}
		return getPackageCanonicalName(object.getClass().getName());
	}

	/**
	 * <p>
	 * Gets the package name from the canonical name.
	 * </p>
	 * 
	 * <p>
	 * The string passed in is assumed to be a canonical name - it is not
	 * checked.
	 * </p>
	 * <p>
	 * If the class is unpackaged, return an empty string.
	 * </p>
	 * 
	 * @param canonicalName
	 *            the canonical name to get the package name for, may be
	 *            <code>null</code>
	 * @return the package name or an empty string
	 * @since 2.4
	 */
	public static String getPackageCanonicalName(String canonicalName) {
		return ClassUtils.getPackageName(getCanonicalName(canonicalName));
	}

	/**
	 * <p>
	 * Gets the package name of a <code>Class</code>.
	 * </p>
	 * 
	 * @param cls
	 *            the class to get the package name for, may be
	 *            <code>null</code>.
	 * @return the package name or an empty string
	 */
	public static String getPackageName(Class<?> cls) {
		if (cls == null) {
			return StringUtils.EMPTY;
		}
		return getPackageName(cls.getName());
	}

	// Package name
	// ----------------------------------------------------------------------
	/**
	 * <p>
	 * Gets the package name of an <code>Object</code>.
	 * </p>
	 * 
	 * @param object
	 *            the class to get the package name for, may be null
	 * @param valueIfNull
	 *            the value to return if null
	 * @return the package name of the object, or the null value
	 */
	public static String getPackageName(Object object, String valueIfNull) {
		if (object == null) {
			return valueIfNull;
		}
		return getPackageName(object.getClass());
	}

	/**
	 * <p>
	 * Gets the package name from a <code>String</code>.
	 * </p>
	 * 
	 * <p>
	 * The string passed in is assumed to be a class name - it is not checked.
	 * </p>
	 * <p>
	 * If the class is unpackaged, return an empty string.
	 * </p>
	 * 
	 * @param className
	 *            the className to get the package name for, may be
	 *            <code>null</code>
	 * @return the package name or an empty string
	 */
	public static String getPackageName(String className) {
		if (className == null || className.length() == 0) {
			return StringUtils.EMPTY;
		}

		// Strip array encoding
		while (className.charAt(0) == '[') {
			className = className.substring(1);
		}
		// Strip Object type encoding
		if (className.charAt(0) == 'L'
				&& className.charAt(className.length() - 1) == ';') {
			className = className.substring(1);
		}

		int i = className.lastIndexOf(PACKAGE_SEPARATOR_CHAR);
		if (i == -1) {
			return StringUtils.EMPTY;
		}
		return className.substring(0, i);
	}

	// Public method
	// ----------------------------------------------------------------------
	/**
	 * <p>
	 * Returns the desired Method much like <code>Class.getMethod</code>,
	 * however it ensures that the returned Method is from a public class or
	 * interface and not from an anonymous inner class. This means that the
	 * Method is invokable and doesn't fall foul of Java bug <a
	 * href="http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4071957"
	 * >4071957</a>).
	 * 
	 * <code><pre>Set set = Collections.unmodifiableSet(...);
	 *  Method method = ClassUtils.getPublicMethod(set.getClass(), "isEmpty",  new Class[0]);
	 *  Object result = method.invoke(set, new Object[]);</pre></code>
	 * </p>
	 * 
	 * @param cls
	 *            the class to check, not null
	 * @param methodName
	 *            the name of the method
	 * @param parameterTypes
	 *            the list of parameters
	 * @return the method
	 * @throws NullPointerException
	 *             if the class is null
	 * @throws SecurityException
	 *             if a a security violation occured
	 * @throws NoSuchMethodException
	 *             if the method is not found in the given class or if the
	 *             metothod doen't conform with the requirements
	 */
	public static Method getPublicMethod(Class<?> cls, String methodName,
			Class<?> parameterTypes[]) throws SecurityException,
			NoSuchMethodException {

		Method declaredMethod = cls.getMethod(methodName, parameterTypes);
		if (Modifier
				.isPublic(declaredMethod.getDeclaringClass().getModifiers())) {
			return declaredMethod;
		}

		List<Class<?>> candidateClasses = new ArrayList<Class<?>>();
		candidateClasses.addAll(getAllInterfaces(cls));
		candidateClasses.addAll(getAllSuperclasses(cls));

		for (Class<?> candidateClass : candidateClasses) {
			if (!Modifier.isPublic(candidateClass.getModifiers())) {
				continue;
			}
			Method candidateMethod;
			try {
				candidateMethod = candidateClass.getMethod(methodName,
						parameterTypes);
			} catch (NoSuchMethodException ex) {
				continue;
			}
			if (Modifier.isPublic(candidateMethod.getDeclaringClass()
					.getModifiers())) {
				return candidateMethod;
			}
		}

		throw new NoSuchMethodException("Can't find a public method for "
				+ methodName + " " + ArrayUtils.toString(parameterTypes));
	}

	/**
	 * <p>
	 * Gets the canonical name minus the package name from a <code>Class</code>.
	 * </p>
	 * 
	 * @param cls
	 *            the class to get the short name for.
	 * @return the canonical name without the package name or an empty string
	 * @since 2.4
	 */
	public static String getShortCanonicalName(Class<?> cls) {
		if (cls == null) {
			return StringUtils.EMPTY;
		}
		return getShortCanonicalName(cls.getName());
	}

	// Short canonical name
	// ----------------------------------------------------------------------
	/**
	 * <p>
	 * Gets the canonical name minus the package name for an <code>Object</code>
	 * .
	 * </p>
	 * 
	 * @param object
	 *            the class to get the short name for, may be null
	 * @param valueIfNull
	 *            the value to return if null
	 * @return the canonical name of the object without the package name, or the
	 *         null value
	 * @since 2.4
	 */
	public static String getShortCanonicalName(Object object, String valueIfNull) {
		if (object == null) {
			return valueIfNull;
		}
		return getShortCanonicalName(object.getClass().getName());
	}

	/**
	 * <p>
	 * Gets the canonical name minus the package name from a String.
	 * </p>
	 * 
	 * <p>
	 * The string passed in is assumed to be a canonical name - it is not
	 * checked.
	 * </p>
	 * 
	 * @param canonicalName
	 *            the class name to get the short name for
	 * @return the canonical name of the class without the package name or an
	 *         empty string
	 * @since 2.4
	 */
	public static String getShortCanonicalName(String canonicalName) {
		return ClassUtils.getShortClassName(getCanonicalName(canonicalName));
	}

	/**
	 * <p>
	 * Gets the class name minus the package name from a <code>Class</code>.
	 * </p>
	 * 
	 * @param cls
	 *            the class to get the short name for.
	 * @return the class name without the package name or an empty string
	 */
	public static String getShortClassName(Class<?> cls) {
		if (cls == null) {
			return StringUtils.EMPTY;
		}
		return getShortClassName(cls.getName());
	}

	// Short class name
	// ----------------------------------------------------------------------
	/**
	 * <p>
	 * Gets the class name minus the package name for an <code>Object</code>.
	 * </p>
	 * 
	 * @param object
	 *            the class to get the short name for, may be null
	 * @param valueIfNull
	 *            the value to return if null
	 * @return the class name of the object without the package name, or the
	 *         null value
	 */
	public static String getShortClassName(Object object, String valueIfNull) {
		if (object == null) {
			return valueIfNull;
		}
		return getShortClassName(object.getClass());
	}

	/**
	 * <p>
	 * Gets the class name minus the package name from a String.
	 * </p>
	 * 
	 * <p>
	 * The string passed in is assumed to be a class name - it is not checked.
	 * </p>
	 * 
	 * @param className
	 *            the className to get the short name for
	 * @return the class name of the class without the package name or an empty
	 *         string
	 */
	public static String getShortClassName(String className) {
		if (className == null) {
			return StringUtils.EMPTY;
		}
		if (className.length() == 0) {
			return StringUtils.EMPTY;
		}

		StringBuilder arrayPrefix = new StringBuilder();

		// Handle array encoding
		if (className.startsWith("[")) {
			while (className.charAt(0) == '[') {
				className = className.substring(1);
				arrayPrefix.append("[]");
			}
			// Strip Object type encoding
			if (className.charAt(0) == 'L'
					&& className.charAt(className.length() - 1) == ';') {
				className = className.substring(1, className.length() - 1);
			}
		}

		if (reverseAbbreviationMap.containsKey(className)) {
			className = reverseAbbreviationMap.get(className);
		}

		int lastDotIdx = className.lastIndexOf(PACKAGE_SEPARATOR_CHAR);
		int innerIdx = className.indexOf(INNER_CLASS_SEPARATOR_CHAR,
				lastDotIdx == -1 ? 0 : lastDotIdx + 1);
		String out = className.substring(lastDotIdx + 1);
		if (innerIdx != -1) {
			out = out.replace(INNER_CLASS_SEPARATOR_CHAR,
					PACKAGE_SEPARATOR_CHAR);
		}
		return out + arrayPrefix;
	}

	/**
	 * Try to create an instance of a named class. First try the classloader of
	 * "sibling", then try the system classloader then the class loader of the
	 * current Thread.
	 * 
	 * @param sibling
	 * @param className
	 * @return
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 */
	public static Object instantiate(Class sibling, String className)
			throws InstantiationException, IllegalAccessException,
			ClassNotFoundException {
		// First check with sibling's classloader (if any).
		ClassLoader cl = sibling.getClassLoader();
		if (cl != null) {
			try {
				Class cls = cl.loadClass(className);
				return cls.newInstance();
			} catch (Exception ex) {
				// Just drop through and try the system classloader.
			}
		}

		// Now try the system classloader.
		try {
			cl = ClassLoader.getSystemClassLoader();
			if (cl != null) {
				Class cls = cl.loadClass(className);
				return cls.newInstance();
			}
		} catch (Exception ex) {
			// We're not allowed to access the system class loader or
			// the class creation failed.
			// Drop through.
		}

		// Use the classloader from the current Thread.
		cl = Thread.currentThread().getContextClassLoader();
		Class cls = cl.loadClass(className);
		return cls.newInstance();
	}

	private static <T> T instantiateUsingSerialization(final Class<T> type) {
		try {
			Map serializedDataCache = new WeakHashMap();
			synchronized (serializedDataCache) {
				byte[] data = (byte[]) serializedDataCache.get(type);
				if (data == null) {
					ByteArrayOutputStream bytes = new ByteArrayOutputStream();
					DataOutputStream stream = new DataOutputStream(bytes);
					stream.writeShort(ObjectStreamConstants.STREAM_MAGIC);
					stream.writeShort(ObjectStreamConstants.STREAM_VERSION);
					stream.writeByte(ObjectStreamConstants.TC_OBJECT);
					stream.writeByte(ObjectStreamConstants.TC_CLASSDESC);
					stream.writeUTF(type.getName());
					stream.writeLong(ObjectStreamClass.lookup(type)
							.getSerialVersionUID());
					stream.writeByte(2); // classDescFlags (2 = Serializable)
					stream.writeShort(0); // field count
					stream.writeByte(ObjectStreamConstants.TC_ENDBLOCKDATA);
					stream.writeByte(ObjectStreamConstants.TC_NULL);
					data = bytes.toByteArray();
					serializedDataCache.put(type, data);
				}

				ObjectInputStream in = new ObjectInputStream(
						new ByteArrayInputStream(data)) {
					@Override
					protected Class resolveClass(ObjectStreamClass desc)
							throws IOException, ClassNotFoundException {
						return Class.forName(desc.getName(), false,
								type.getClassLoader());
					}
				};
				return (T) in.readObject();
			}
		} catch (IOException e) {
			throw new ObjectAccessException("Cannot create " + type.getName()
					+ " by JDK serialization", e);
		} catch (ClassNotFoundException e) {
			throw new ObjectAccessException("Cannot find class "
					+ e.getMessage(), e);
		}
	}

	/**
	 * <p>
	 * Checks if one <code>Class</code> can be assigned to a variable of another
	 * <code>Class</code>.
	 * </p>
	 * 
	 * <p>
	 * Unlike the {@link Class#isAssignableFrom(java.lang.Class)} method, this
	 * method takes into account widenings of primitive classes and
	 * <code>null</code>s.
	 * </p>
	 * 
	 * <p>
	 * Primitive widenings allow an int to be assigned to a long, float or
	 * double. This method returns the correct result for these cases.
	 * </p>
	 * 
	 * <p>
	 * <code>Null</code> may be assigned to any reference type. This method will
	 * return <code>true</code> if <code>null</code> is passed in and the
	 * toClass is non-primitive.
	 * </p>
	 * 
	 * <p>
	 * Specifically, this method tests whether the type represented by the
	 * specified <code>Class</code> parameter can be converted to the type
	 * represented by this <code>Class</code> object via an identity conversion
	 * widening primitive or widening reference conversion. See
	 * <em><a href="http://java.sun.com/docs/books/jls/">The Java Language Specification</a></em>
	 * , sections 5.1.1, 5.1.2 and 5.1.4 for details.
	 * </p>
	 * 
	 * <p>
	 * <strong>Since Lang 3.0,</strong> this method will default behavior for
	 * calculating assignability between primitive and wrapper types
	 * <em>corresponding
	 * to the running Java version</em>; i.e. autoboxing will be the default
	 * behavior in VMs running Java versions >= 1.5.
	 * </p>
	 * 
	 * @param cls
	 *            the Class to check, may be null
	 * @param toClass
	 *            the Class to try to assign into, returns false if null
	 * @return <code>true</code> if assignment possible
	 */
	public static boolean isAssignable(Class<?> cls, Class<?> toClass) {
		return isAssignable(cls, toClass, true);
	}

	/**
	 * <p>
	 * Checks if one <code>Class</code> can be assigned to a variable of another
	 * <code>Class</code>.
	 * </p>
	 * 
	 * <p>
	 * Unlike the {@link Class#isAssignableFrom(java.lang.Class)} method, this
	 * method takes into account widenings of primitive classes and
	 * <code>null</code>s.
	 * </p>
	 * 
	 * <p>
	 * Primitive widenings allow an int to be assigned to a long, float or
	 * double. This method returns the correct result for these cases.
	 * </p>
	 * 
	 * <p>
	 * <code>Null</code> may be assigned to any reference type. This method will
	 * return <code>true</code> if <code>null</code> is passed in and the
	 * toClass is non-primitive.
	 * </p>
	 * 
	 * <p>
	 * Specifically, this method tests whether the type represented by the
	 * specified <code>Class</code> parameter can be converted to the type
	 * represented by this <code>Class</code> object via an identity conversion
	 * widening primitive or widening reference conversion. See
	 * <em><a href="http://java.sun.com/docs/books/jls/">The Java Language Specification</a></em>
	 * , sections 5.1.1, 5.1.2 and 5.1.4 for details.
	 * </p>
	 * 
	 * @param cls
	 *            the Class to check, may be null
	 * @param toClass
	 *            the Class to try to assign into, returns false if null
	 * @param autoboxing
	 *            whether to use implicit autoboxing/unboxing between primitives
	 *            and wrappers
	 * @return <code>true</code> if assignment possible
	 */
	public static boolean isAssignable(Class<?> cls, Class<?> toClass,
			boolean autoboxing) {
		if (toClass == null) {
			return false;
		}
		// have to check for null, as isAssignableFrom doesn't
		if (cls == null) {
			return !(toClass.isPrimitive());
		}
		// autoboxing:
		if (autoboxing) {
			if (cls.isPrimitive() && !toClass.isPrimitive()) {
				cls = primitiveToWrapper(cls);
				if (cls == null) {
					return false;
				}
			}
			if (toClass.isPrimitive() && !cls.isPrimitive()) {
				cls = wrapperToPrimitive(cls);
				if (cls == null) {
					return false;
				}
			}
		}
		if (cls.equals(toClass)) {
			return true;
		}
		if (cls.isPrimitive()) {
			if (toClass.isPrimitive() == false) {
				return false;
			}
			if (Integer.TYPE.equals(cls)) {
				return Long.TYPE.equals(toClass) || Float.TYPE.equals(toClass)
						|| Double.TYPE.equals(toClass);
			}
			if (Long.TYPE.equals(cls)) {
				return Float.TYPE.equals(toClass)
						|| Double.TYPE.equals(toClass);
			}
			if (Boolean.TYPE.equals(cls)) {
				return false;
			}
			if (Double.TYPE.equals(cls)) {
				return false;
			}
			if (Float.TYPE.equals(cls)) {
				return Double.TYPE.equals(toClass);
			}
			if (Character.TYPE.equals(cls)) {
				return Integer.TYPE.equals(toClass)
						|| Long.TYPE.equals(toClass)
						|| Float.TYPE.equals(toClass)
						|| Double.TYPE.equals(toClass);
			}
			if (Short.TYPE.equals(cls)) {
				return Integer.TYPE.equals(toClass)
						|| Long.TYPE.equals(toClass)
						|| Float.TYPE.equals(toClass)
						|| Double.TYPE.equals(toClass);
			}
			if (Byte.TYPE.equals(cls)) {
				return Short.TYPE.equals(toClass)
						|| Integer.TYPE.equals(toClass)
						|| Long.TYPE.equals(toClass)
						|| Float.TYPE.equals(toClass)
						|| Double.TYPE.equals(toClass);
			}
			// should never get here
			return false;
		}
		return toClass.isAssignableFrom(cls);
	}

	// Is assignable
	// ----------------------------------------------------------------------
	/**
	 * <p>
	 * Checks if an array of Classes can be assigned to another array of
	 * Classes.
	 * </p>
	 * 
	 * <p>
	 * This method calls {@link #isAssignable(Class, Class) isAssignable} for
	 * each Class pair in the input arrays. It can be used to check if a set of
	 * arguments (the first parameter) are suitably compatible with a set of
	 * method parameter types (the second parameter).
	 * </p>
	 * 
	 * <p>
	 * Unlike the {@link Class#isAssignableFrom(java.lang.Class)} method, this
	 * method takes into account widenings of primitive classes and
	 * <code>null</code>s.
	 * </p>
	 * 
	 * <p>
	 * Primitive widenings allow an int to be assigned to a <code>long</code>,
	 * <code>float</code> or <code>double</code>. This method returns the
	 * correct result for these cases.
	 * </p>
	 * 
	 * <p>
	 * <code>Null</code> may be assigned to any reference type. This method will
	 * return <code>true</code> if <code>null</code> is passed in and the
	 * toClass is non-primitive.
	 * </p>
	 * 
	 * <p>
	 * Specifically, this method tests whether the type represented by the
	 * specified <code>Class</code> parameter can be converted to the type
	 * represented by this <code>Class</code> object via an identity conversion
	 * widening primitive or widening reference conversion. See
	 * <em><a href="http://java.sun.com/docs/books/jls/">The Java Language Specification</a></em>
	 * , sections 5.1.1, 5.1.2 and 5.1.4 for details.
	 * </p>
	 * 
	 * <p>
	 * <strong>Since Lang 3.0,</strong> this method will default behavior for
	 * calculating assignability between primitive and wrapper types
	 * <em>corresponding
	 * to the running Java version</em>; i.e. autoboxing will be the default
	 * behavior in VMs running Java versions >= 1.5.
	 * </p>
	 * 
	 * @param classArray
	 *            the array of Classes to check, may be <code>null</code>
	 * @param toClassArray
	 *            the array of Classes to try to assign into, may be
	 *            <code>null</code>
	 * @return <code>true</code> if assignment possible
	 */
	public static boolean isAssignable(Class<?>[] classArray,
			Class<?>[] toClassArray) {
		return isAssignable(classArray, toClassArray, true);
	}

	/**
	 * <p>
	 * Checks if an array of Classes can be assigned to another array of
	 * Classes.
	 * </p>
	 * 
	 * <p>
	 * This method calls {@link #isAssignable(Class, Class) isAssignable} for
	 * each Class pair in the input arrays. It can be used to check if a set of
	 * arguments (the first parameter) are suitably compatible with a set of
	 * method parameter types (the second parameter).
	 * </p>
	 * 
	 * <p>
	 * Unlike the {@link Class#isAssignableFrom(java.lang.Class)} method, this
	 * method takes into account widenings of primitive classes and
	 * <code>null</code>s.
	 * </p>
	 * 
	 * <p>
	 * Primitive widenings allow an int to be assigned to a <code>long</code>,
	 * <code>float</code> or <code>double</code>. This method returns the
	 * correct result for these cases.
	 * </p>
	 * 
	 * <p>
	 * <code>Null</code> may be assigned to any reference type. This method will
	 * return <code>true</code> if <code>null</code> is passed in and the
	 * toClass is non-primitive.
	 * </p>
	 * 
	 * <p>
	 * Specifically, this method tests whether the type represented by the
	 * specified <code>Class</code> parameter can be converted to the type
	 * represented by this <code>Class</code> object via an identity conversion
	 * widening primitive or widening reference conversion. See
	 * <em><a href="http://java.sun.com/docs/books/jls/">The Java Language Specification</a></em>
	 * , sections 5.1.1, 5.1.2 and 5.1.4 for details.
	 * </p>
	 * 
	 * @param classArray
	 *            the array of Classes to check, may be <code>null</code>
	 * @param toClassArray
	 *            the array of Classes to try to assign into, may be
	 *            <code>null</code>
	 * @param autoboxing
	 *            whether to use implicit autoboxing/unboxing between primitives
	 *            and wrappers
	 * @return <code>true</code> if assignment possible
	 */
	public static boolean isAssignable(Class<?>[] classArray,
			Class<?>[] toClassArray, boolean autoboxing) {
		if (ArrayUtils.isSameLength(classArray, toClassArray) == false) {
			return false;
		}
		if (classArray == null) {
			classArray = ArrayUtils.EMPTY_CLASS_ARRAY;
		}
		if (toClassArray == null) {
			toClassArray = ArrayUtils.EMPTY_CLASS_ARRAY;
		}
		for (int i = 0; i < classArray.length; i++) {
			if (isAssignable(classArray[i], toClassArray[i], autoboxing) == false) {
				return false;
			}
		}
		return true;
	}

	// Inner class
	// ----------------------------------------------------------------------
	/**
	 * <p>
	 * Is the specified class an inner class or static nested class.
	 * </p>
	 * 
	 * @param cls
	 *            the class to check, may be null
	 * @return <code>true</code> if the class is an inner or static nested
	 *         class, false if not or <code>null</code>
	 */
	public static boolean isInnerClass(Class<?> cls) {
		if (cls == null) {
			return false;
		}
		return cls.getName().indexOf(INNER_CLASS_SEPARATOR_CHAR) >= 0;
	}

	/**
	 * Return true if class a is either equivalent to class b, or if class a is
	 * a subclass of class b, i.e. if a either "extends" or "implements" b. Note
	 * tht either or both "Class" objects may represent interfaces.
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	public static boolean isSubclass(Class a, Class b) {
		// We rely on the fact that for any given java class or
		// primtitive type there is a unqiue Class object, so
		// we can use object equivalence in the comparisons.
		if (a == b) {
			return true;
		}
		if (a == null || b == null) {
			return false;
		}
		for (Class x = a; x != null; x = x.getSuperclass()) {
			if (x == b) {
				return true;
			}
			if (b.isInterface()) {
				Class interfaces[] = x.getInterfaces();
				for (int i = 0; i < interfaces.length; i++) {
					if (isSubclass(interfaces[i], b)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
     * Create an instance with dependency injection. The given dependencies are used to match the parameters of the
     * constructors of the type. Constructors with most parameters are examined first. A parameter type sequence
     * matching the sequence of the dependencies' types match first. Otherwise all the types of the dependencies must
     * match one of the the parameters although no dependency is used twice. Use a {@link TypedNull} instance to inject
     * <code>null</code> as parameter.
     * 
     * @param type the type to create an instance of
     * @param dependencies the possible dependencies
     * @return the instantiated object
     * @throws ObjectAccessException if no instance can be generated
     * @since 1.2.2
     */
    public static Object newInstance(final Class type, final Object[] dependencies) {
        return newInstance(type, dependencies, new BitSet());
    }

	/**
     * Create an instance with dependency injection. The given dependencies are used to match the parameters of the
     * constructors of the type. Constructors with most parameters are examined first. A parameter type sequence
     * matching the sequence of the dependencies' types match first. Otherwise all the types of the dependencies must
     * match one of the the parameters although no dependency is used twice. Use a {@link TypedNull} instance to inject
     * <code>null</code> as parameter.
     * 
     * @param type the type to create an instance of
     * @param dependencies the possible dependencies
     * @param usedDependencies bit mask set by the method for all used dependencies (may be <code>null</code>)
     * @return the instantiated object
     * @throws ObjectAccessException if no instance can be generated
     * @since 1.4
     */
    public static Object newInstance(final Class type, final Object[] dependencies, final BitSet usedDependencies) {
        Constructor bestMatchingCtor = null;
        final List matchingDependencies = new ArrayList();

        if (dependencies != null && dependencies.length > 0) {
            // sort available ctors according their arity
            final Constructor[] ctors = type.getConstructors();
            if (ctors.length > 1) {
                Arrays.sort(ctors, new Comparator() {
                    @Override
					public int compare(final Object o1, final Object o2) {
                        return ((Constructor)o2).getParameterTypes().length
                            - ((Constructor)o1).getParameterTypes().length;
                    }
                });
            }

            final TypedValue[] typedDependencies = new TypedValue[dependencies.length];
            for (int i = 0; i < dependencies.length; i++ ) {
                Object dependency = dependencies[i];
                Class depType = dependency.getClass();
                if (depType.isPrimitive()) {
                    depType = PrimitiveUtils.box(depType);
                } else if (depType == TypedNull.class) {
                    depType = ((TypedNull)dependency).getType();
                    dependency = null;
                }

                typedDependencies[i] = new TypedValue(depType, dependency);
            }

            Constructor possibleCtor = null;
            int arity = Integer.MAX_VALUE;
            for (int i = 0; bestMatchingCtor == null && i < ctors.length; i++ ) {
                final Constructor constructor = ctors[i];
                final Class[] parameterTypes = constructor.getParameterTypes();
                if (parameterTypes.length > dependencies.length) {
                    continue;
                } else if (parameterTypes.length == 0) {
                    if (possibleCtor == null) {
                        bestMatchingCtor = constructor;
                    }
                    break;
                }
                if (arity > parameterTypes.length) {
                    if (possibleCtor != null) {
                        bestMatchingCtor = possibleCtor;
                        continue;
                    }
                    arity = parameterTypes.length;
                }

                for (int j = 0; j < parameterTypes.length; j++ ) {
                    if (parameterTypes[j].isPrimitive()) {
                        parameterTypes[j] = PrimitiveUtils.box(parameterTypes[j]);
                    }
                }

                // first approach: test the ctor params against the dependencies in the sequence
                // of the parameter
                // declaration
                matchingDependencies.clear();
                for (int j = usedDependencies.length(); j-- > 0;) {
                    usedDependencies.clear(j); // JDK 1.3, BitSet.clear() is JDK 1.4
                }
                for (int j = 0, k = 0; j < parameterTypes.length
                    && parameterTypes.length + k - j <= typedDependencies.length; k++ ) {
                    if (parameterTypes[j].isAssignableFrom(typedDependencies[k].type)) {
                        matchingDependencies.add(typedDependencies[k].value);
                        usedDependencies.set(k);
                        if ( ++j == parameterTypes.length) {
                            bestMatchingCtor = constructor;
                            break;
                        }
                    }
                }

                if (bestMatchingCtor == null && possibleCtor == null) {
                    possibleCtor = constructor; // assumption

                    // try to match all dependencies in the sequence of the parameter
                    // declaration
                    final TypedValue[] deps = new TypedValue[typedDependencies.length];
                    System.arraycopy(typedDependencies, 0, deps, 0, deps.length);
                    matchingDependencies.clear();
                    for (int j = usedDependencies.length(); j-- > 0;) {
                        usedDependencies.clear(j); // JDK 1.3, BitSet.clear() is JDK 1.4
                    }
                    for (int j = 0; j < parameterTypes.length; j++ ) {
                        int assignable = -1;
                        for (int k = 0; k < deps.length; k++ ) {
                            if (deps[k] == null) {
                                continue;
                            }
                            if (deps[k].type == parameterTypes[j]) {
                                assignable = k;
                                // optimal match
                                break;
                            } else if (parameterTypes[j].isAssignableFrom(deps[k].type)) {
                                // use most specific type
                                if (assignable < 0
                                    || (deps[assignable].type != deps[k].type && deps[assignable].type
                                        .isAssignableFrom(deps[k].type))) {
                                    assignable = k;
                                }
                            }
                        }

                        if (assignable >= 0) {
                            matchingDependencies.add(deps[assignable].value);
                            usedDependencies.set(assignable);
                            deps[assignable] = null; // do not match same dep twice
                        } else {
                            possibleCtor = null;
                            break;
                        }
                    }
                }
            }

            if (bestMatchingCtor == null) {
                if (possibleCtor == null) {
                    for (int j = usedDependencies.length(); j-- > 0;) {
                        usedDependencies.clear(j); // JDK 1.3, BitSet.clear() is JDK 1.4
                    }
                    throw new ObjectAccessException("Cannot construct "
                        + type.getName()
                        + ", none of the dependencies match any constructor's parameters");
                } else {
                    bestMatchingCtor = possibleCtor;
                }
            }
        }

        try {
            final Object instance;
            if (bestMatchingCtor == null) {
                instance = type.newInstance();
            } else {
                instance = bestMatchingCtor.newInstance(matchingDependencies.toArray());
            }
            return instance;
        } catch (final InstantiationException e) {
            throw new ObjectAccessException("Cannot construct " + type.getName(), e);
        } catch (final IllegalAccessException e) {
            throw new ObjectAccessException("Cannot construct " + type.getName(), e);
        } catch (final InvocationTargetException e) {
            throw new ObjectAccessException("Cannot construct " + type.getName(), e);
        }
    }

	/**
	 * Factory method that returns a new instance of the given Class. This is
	 * called at the start of the bean creation process and may be overridden to
	 * provide custom behavior like returning a cached bean instance.
	 * <p>
	 * A more refined class instance creation is defined in
	 * {@link ClassUtils#instantiate(Class, String)}
	 * </p>
	 * 
	 * @param <T>
	 *            The type of object to create
	 * @param c
	 *            The Class to create an object from.
	 * @return A newly created object of the Class.
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	public static <T> T newInstance(Class<T> type) {
		try {
			Constructor[] constructors = type.getDeclaredConstructors();
			for (int i = 0; i < constructors.length; i++) {
				final Constructor constructor = constructors[i];
				if (constructor.getParameterTypes().length == 0) {
					if (!constructor.isAccessible()) {
						constructor.setAccessible(true);
					}
					return (T) constructor.newInstance(new Object[0]);
				}
			}
			if (Serializable.class.isAssignableFrom(type)) {
				return instantiateUsingSerialization(type);
			} else {
				throw new ObjectAccessException("Cannot construct "
						+ type.getName()
						+ " as it does not have a no-args constructor");
			}
		} catch (InstantiationException e) {
			throw new ObjectAccessException("Cannot construct "
					+ type.getName(), e);
		} catch (IllegalAccessException e) {
			throw new ObjectAccessException("Cannot construct "
					+ type.getName(), e);
		} catch (InvocationTargetException e) {
			if (e.getTargetException() instanceof RuntimeException) {
				throw (RuntimeException) e.getTargetException();
			} else if (e.getTargetException() instanceof Error) {
				throw (Error) e.getTargetException();
			} else {
				throw new ObjectAccessException("Constructor for "
						+ type.getName() + " threw an exception",
						e.getTargetException());
			}
		}
	}

	/**
	 * <p>
	 * Converts the specified array of primitive Class objects to an array of
	 * its corresponding wrapper Class objects.
	 * </p>
	 * 
	 * @param classes
	 *            the class array to convert, may be null or empty
	 * @return an array which contains for each given class, the wrapper class
	 *         or the original class if class is not a primitive.
	 *         <code>null</code> if null input. Empty array if an empty array
	 *         passed in.
	 * @since 2.1
	 */
	public static Class<?>[] primitivesToWrappers(Class<?>[] classes) {
		if (classes == null) {
			return null;
		}

		if (classes.length == 0) {
			return classes;
		}

		Class<?>[] convertedClasses = new Class[classes.length];
		for (int i = 0; i < classes.length; i++) {
			convertedClasses[i] = primitiveToWrapper(classes[i]);
		}
		return convertedClasses;
	}

	/**
	 * <p>
	 * Converts the specified primitive Class object to its corresponding
	 * wrapper Class object.
	 * </p>
	 * 
	 * <p>
	 * NOTE: From v2.2, this method handles <code>Void.TYPE</code>, returning
	 * <code>Void.TYPE</code>.
	 * </p>
	 * 
	 * @param cls
	 *            the class to convert, may be null
	 * @return the wrapper class for <code>cls</code> or <code>cls</code> if
	 *         <code>cls</code> is not a primitive. <code>null</code> if null
	 *         input.
	 * @since 2.1
	 */
	public static Class<?> primitiveToWrapper(Class<?> cls) {
		Class<?> convertedClass = cls;
		if (cls != null && cls.isPrimitive()) {
			convertedClass = primitiveWrapperMap.get(cls);
		}
		return convertedClass;
	}

	// ----------------------------------------------------------------------
	/**
	 * Converts a class name to a JLS style class name.
	 * 
	 * @param className
	 *            the class name
	 * @return the converted name
	 */
	private static String toCanonicalName(String className) {
		className = StringUtils.deleteWhitespace(className);
		if (className == null) {
			throw new NullPointerException("className must not be null.");
		} else if (className.endsWith("[]")) {
			StringBuilder classNameBuffer = new StringBuilder();
			while (className.endsWith("[]")) {
				className = className.substring(0, className.length() - 2);
				classNameBuffer.append("[");
			}
			String abbreviation = abbreviationMap.get(className);
			if (abbreviation != null) {
				classNameBuffer.append(abbreviation);
			} else {
				classNameBuffer.append("L").append(className).append(";");
			}
			className = classNameBuffer.toString();
		}
		return className;
	}

	/**
	 * <p>
	 * Converts an array of <code>Object</code> in to an array of
	 * <code>Class</code> objects. If any of these objects is null, a null
	 * element will be inserted into the array.
	 * </p>
	 * 
	 * <p>
	 * This method returns <code>null</code> for a <code>null</code> input
	 * array.
	 * </p>
	 * 
	 * @param array
	 *            an <code>Object</code> array
	 * @return a <code>Class</code> array, <code>null</code> if null array input
	 * @since 2.4
	 */
	public static Class<?>[] toClass(Object[] array) {
		if (array == null) {
			return null;
		} else if (array.length == 0) {
			return ArrayUtils.EMPTY_CLASS_ARRAY;
		}
		Class<?>[] classes = new Class[array.length];
		for (int i = 0; i < array.length; i++) {
			classes[i] = array[i] == null ? null : array[i].getClass();
		}
		return classes;
	}

	/**
	 * <p>
	 * Converts the specified array of wrapper Class objects to an array of its
	 * corresponding primitive Class objects.
	 * </p>
	 * 
	 * <p>
	 * This method invokes <code>wrapperToPrimitive()</code> for each element of
	 * the passed in array.
	 * </p>
	 * 
	 * @param classes
	 *            the class array to convert, may be null or empty
	 * @return an array which contains for each given class, the primitive class
	 *         or <b>null</b> if the original class is not a wrapper class.
	 *         <code>null</code> if null input. Empty array if an empty array
	 *         passed in.
	 * @see #wrapperToPrimitive(Class)
	 * @since 2.4
	 */
	public static Class<?>[] wrappersToPrimitives(Class<?>[] classes) {
		if (classes == null) {
			return null;
		}

		if (classes.length == 0) {
			return classes;
		}

		Class<?>[] convertedClasses = new Class[classes.length];
		for (int i = 0; i < classes.length; i++) {
			convertedClasses[i] = wrapperToPrimitive(classes[i]);
		}
		return convertedClasses;
	}

	/**
	 * <p>
	 * Converts the specified wrapper class to its corresponding primitive
	 * class.
	 * </p>
	 * 
	 * <p>
	 * This method is the counter part of <code>primitiveToWrapper()</code>. If
	 * the passed in class is a wrapper class for a primitive type, this
	 * primitive type will be returned (e.g. <code>Integer.TYPE</code> for
	 * <code>Integer.class</code>). For other classes, or if the parameter is
	 * <b>null</b>, the return value is <b>null</b>.
	 * </p>
	 * 
	 * @param cls
	 *            the class to convert, may be <b>null</b>
	 * @return the corresponding primitive type if <code>cls</code> is a wrapper
	 *         class, <b>null</b> otherwise
	 * @see #primitiveToWrapper(Class)
	 * @since 2.4
	 */
	public static Class<?> wrapperToPrimitive(Class<?> cls) {
		return wrapperPrimitiveMap.get(cls);
	}

	/**
	 * <p>
	 * ClassUtils instances should NOT be constructed in standard programming.
	 * Instead, the class should be used as
	 * <code>ClassUtils.getShortClassName(cls)</code>.
	 * </p>
	 * 
	 * <p>
	 * This constructor is public to permit tools that require a JavaBean
	 * instance to operate.
	 * </p>
	 */
	private ClassUtils() {
		super();
	}
}
