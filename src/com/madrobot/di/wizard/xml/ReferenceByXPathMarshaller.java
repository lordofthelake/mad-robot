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

import com.madrobot.di.wizard.xml.converters.ConverterLookup;
import com.madrobot.di.wizard.xml.io.HierarchicalStreamWriter;
import com.madrobot.di.wizard.xml.io.path.Path;

class ReferenceByXPathMarshaller extends AbstractReferenceMarshaller {

	private final int mode;

	ReferenceByXPathMarshaller(HierarchicalStreamWriter writer, ConverterLookup converterLookup, Mapper mapper, int mode) {
		super(writer, converterLookup, mapper);
		this.mode = mode;
	}

	@Override
	protected String createReference(Path currentPath, Object existingReferenceKey) {
		Path existingPath = (Path) existingReferenceKey;
		Path referencePath = (mode & ReferenceByXPathMarshallingStrategy.ABSOLUTE) > 0 ? existingPath : currentPath
				.relativeTo(existingPath);
		return (mode & ReferenceByXPathMarshallingStrategy.SINGLE_NODE) > 0 ? referencePath.explicit() : referencePath
				.toString();
	}

	@Override
	protected Object createReferenceKey(Path currentPath, Object item) {
		return currentPath;
	}

	@Override
	protected void fireValidReference(Object referenceKey) {
		// nothing to do
	}
}
