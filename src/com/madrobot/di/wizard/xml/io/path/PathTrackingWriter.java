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

import com.madrobot.di.wizard.xml.io.AbstractWriter;
import com.madrobot.di.wizard.xml.io.HierarchicalStreamWriter;
import com.madrobot.di.wizard.xml.io.WriterWrapper;

/**
 * Wrapper for HierarchicalStreamWriter that tracks the path (a subset of XPath) of the current node that is being
 * written.
 * 
 * @see PathTracker
 * @see Path
 * 
 * @author Joe Walnes
 */
public class PathTrackingWriter extends WriterWrapper {

	private final boolean isNameEncoding;
	private final PathTracker pathTracker;

	public PathTrackingWriter(HierarchicalStreamWriter writer, PathTracker pathTracker) {
		super(writer);
		this.isNameEncoding = writer.underlyingWriter() instanceof AbstractWriter;
		this.pathTracker = pathTracker;
	}

	@Override
	public void endNode() {
		super.endNode();
		pathTracker.popElement();
	}

	@Override
	public void startNode(String name) {
		pathTracker.pushElement(isNameEncoding ? ((AbstractWriter) wrapped.underlyingWriter()).encodeNode(name) : name);
		super.startNode(name);
	}

	@Override
	public void startNode(String name, Class clazz) {
		pathTracker.pushElement(isNameEncoding ? ((AbstractWriter) wrapped.underlyingWriter()).encodeNode(name) : name);
		super.startNode(name, clazz);
	}
}
