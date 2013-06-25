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

/*
 * Copyright (C) 2004, 2005, 2006 Joe Walnes.
 * Copyright (C) 2006, 2007, 2009, 2011 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 03. April 2004 by Joe Walnes
 */
package com.madrobot.di.wizard.xml;

import com.madrobot.di.wizard.xml.converters.ConverterLookup;
import com.madrobot.di.wizard.xml.io.AbstractReader;
import com.madrobot.di.wizard.xml.io.HierarchicalStreamReader;
import com.madrobot.di.wizard.xml.io.path.Path;
import com.madrobot.di.wizard.xml.io.path.PathTracker;
import com.madrobot.di.wizard.xml.io.path.PathTrackingReader;

class ReferenceByXPathUnmarshaller extends AbstractReferenceUnmarshaller {

	protected boolean isNameEncoding;
	private PathTracker pathTracker = new PathTracker();

	ReferenceByXPathUnmarshaller(
			Object root,
			HierarchicalStreamReader reader,
			ConverterLookup converterLookup,
			Mapper mapper) {
		super(root, reader, converterLookup, mapper);
		this.reader = new PathTrackingReader(reader, pathTracker);
		isNameEncoding = reader.underlyingReader() instanceof AbstractReader;
	}

	@Override
	protected Object getCurrentReferenceKey() {
		return pathTracker.getPath();
	}

	@Override
	protected Object getReferenceKey(String reference) {
		final Path path = new Path(isNameEncoding ? ((AbstractReader) reader.underlyingReader()).decodeNode(reference)
				: reference);
		// We have absolute references, if path starts with '/'
		return reference.charAt(0) != '/' ? pathTracker.getPath().apply(path) : path;
	}

}
