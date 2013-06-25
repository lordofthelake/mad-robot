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
import com.madrobot.di.wizard.xml.io.HierarchicalStreamReader;

class ReferenceByIdUnmarshaller extends AbstractReferenceUnmarshaller {

	public ReferenceByIdUnmarshaller(
			Object root,
			HierarchicalStreamReader reader,
			ConverterLookup converterLookup,
			Mapper mapper) {
		super(root, reader, converterLookup, mapper);
	}

	@Override
	protected Object getCurrentReferenceKey() {
		String attributeName = getMapper().aliasForSystemAttribute("id");
		return attributeName == null ? null : reader.getAttribute(attributeName);
	}

	@Override
	protected Object getReferenceKey(String reference) {
		return reference;
	}
}
