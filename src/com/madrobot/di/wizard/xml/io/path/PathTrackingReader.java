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

package com.madrobot.di.wizard.xml.io.path;

import com.madrobot.di.wizard.xml.converters.ErrorWriter;
import com.madrobot.di.wizard.xml.io.HierarchicalStreamReader;
import com.madrobot.di.wizard.xml.io.ReaderWrapper;

/**
 * Wrapper for HierarchicalStreamReader that tracks the path (a subset of XPath) of the current node that is being read.
 * 
 * @see PathTracker
 * @see Path
 * 
 * @author Joe Walnes
 */
public class PathTrackingReader extends ReaderWrapper {

	private final PathTracker pathTracker;

	public PathTrackingReader(HierarchicalStreamReader reader, PathTracker pathTracker) {
		super(reader);
		this.pathTracker = pathTracker;
		pathTracker.pushElement(getNodeName());
	}

	@Override
	public void appendErrors(ErrorWriter errorWriter) {
		errorWriter.add("path", pathTracker.getPath().toString());
		super.appendErrors(errorWriter);
	}

	@Override
	public void moveDown() {
		super.moveDown();
		pathTracker.pushElement(getNodeName());
	}

	@Override
	public void moveUp() {
		super.moveUp();
		pathTracker.popElement();
	}

}
