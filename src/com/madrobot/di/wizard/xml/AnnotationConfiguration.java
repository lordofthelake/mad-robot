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

package com.madrobot.di.wizard.xml;

/**
 * An interface for the configuration part of the AnnotationMapper.
 * 
 * @since 1.3
 */
interface AnnotationConfiguration {

	void autodetectAnnotations(boolean mode);

	void processAnnotations(Class[] types);

}
