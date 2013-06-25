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

class ReferenceByIdMarshaller extends AbstractReferenceMarshaller {

	public static interface IDGenerator {
		String next(Object item);
	}

	private final IDGenerator idGenerator;

	ReferenceByIdMarshaller(HierarchicalStreamWriter writer, ConverterLookup converterLookup, Mapper mapper) {
		this(writer, converterLookup, mapper, new SequenceGenerator(1));
	}

	ReferenceByIdMarshaller(
			HierarchicalStreamWriter writer,
			ConverterLookup converterLookup,
			Mapper mapper,
			IDGenerator idGenerator) {
		super(writer, converterLookup, mapper);
		this.idGenerator = idGenerator;
	}

	@Override
	protected String createReference(Path currentPath, Object existingReferenceKey) {
		return existingReferenceKey.toString();
	}

	@Override
	protected Object createReferenceKey(Path currentPath, Object item) {
		return idGenerator.next(item);
	}

	@Override
	protected void fireValidReference(Object referenceKey) {
		String attributeName = getMapper().aliasForSystemAttribute("id");
		if (attributeName != null) {
			writer.addAttribute(attributeName, referenceKey.toString());
		}
	}
}
