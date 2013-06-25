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

import java.beans.PropertyChangeEvent;

/*
 * A "PropertyChange" event gets fired whenever a bean changes a "bound"
 * property. You can register a PropertyChangeListener with a source
 * bean so as to be notified of any bound property updates.
 */

public interface PropertyChangeListener extends java.util.EventListener {

	/**
	 * This method gets called when a bound property is changed.
	 * 
	 * @param evt
	 *            A PropertyChangeEvent object describing the event source
	 *            and the property that has changed.
	 */

	void propertyChange(PropertyChangeEvent evt);

}
