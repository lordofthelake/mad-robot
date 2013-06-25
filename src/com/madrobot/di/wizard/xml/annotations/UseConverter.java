/*******************************************************************************
 * Copyright (c) 2012 MadRobot.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the GNU Lesser Public License v2.1
 *  which accompanies this distribution, and is available at
 *  http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 *  
 *  Contributors:
 *  Elton Kent - initial API and implementation
 ******************************************************************************/
package com.madrobot.di.wizard.xml.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.madrobot.di.wizard.xml.XMLWizard;
import com.madrobot.di.wizard.xml.converters.ConverterMatcher;

/**
 * Annotation to declare a converter. The annotation supports additionally the injection of various constructor
 * arguments provided by XMLWizard:
 * <ul>
 * <li>{@link com.madrobot.di.wizard.xml.Mapper}: The current mapper chain of the XMLWizard instance.</li>
 * <li>{@link ClassLoader}: The class loader used by the XMLWizard instance to deserialize the objects.</li>
 * <li>{@link com.madrobot.di.wizard.xml.converters.ReflectionProvider}: The reflection provider used by the reflection
 * based converters of the current XMLWizard instance.</li>
 * <li>{@link com.madrobot.di.wizard.xml.converters.ConverterLookup}: The lookup for converters handling a special type.
 * </li>
 * <li>{@link com.madrobot.di.wizard.xml.core.JVM}: Utility e.g. to load classes.</li>
 * <li>All elements provided with the individual arrays of this annotation.</li>
 * <li>{@link Class}: The type of the element where the annotation is declared. Note, that this argument is not
 * supported when using {@link com.madrobot.di.wizard.xml.annotations.Converters}.</li>
 * </ul>
 * <p>
 * Note, the annotation matches a {@link ConverterMatcher}.
 * {@link com.madrobot.di.wizard.xml.converters.ConverterMatcher} as well as
 * {@link com.madrobot.di.wizard.xml.converters.SingleValueConverter} extend this interface. The
 * {@link com.madrobot.di.wizard.xml.AnnotationMapper} can only handle these two <strong>known</strong> types.
 * </p>
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.FIELD })
@Documented
public @interface UseConverter {
	boolean[] booleans() default {};

	byte[] bytes() default {};

	char[] chars() default {};

	double[] doubles() default {};

	float[] floats() default {};

	int[] ints() default {};

	long[] longs() default {};

	int priority() default XMLWizard.PRIORITY_NORMAL;

	short[] shorts() default {};

	String[] strings() default {};

	Class<?>[] types() default {};

	Class<? extends ConverterMatcher> value();
}
