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
package com.madrobot.beans;

public class ParameterDescriptor extends FeatureDescriptor {

	/**
	 * Public default constructor.
	 */
	public ParameterDescriptor() {
	}

	/**
	 * Package private dup constructor.
	 * This must isolate the new object from any changes to the old object.
	 */
	ParameterDescriptor(ParameterDescriptor old) {
		super(old);
	}

}
