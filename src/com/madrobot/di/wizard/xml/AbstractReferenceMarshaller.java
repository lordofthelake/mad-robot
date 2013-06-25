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

import java.util.Iterator;

import com.madrobot.beans.ObjectIdDictionary;
import com.madrobot.di.wizard.xml.converters.ConversionException;
import com.madrobot.di.wizard.xml.converters.Converter;
import com.madrobot.di.wizard.xml.converters.ConverterLookup;
import com.madrobot.di.wizard.xml.converters.MarshallingContext;
import com.madrobot.di.wizard.xml.io.HierarchicalStreamWriter;
import com.madrobot.di.wizard.xml.io.path.Path;
import com.madrobot.di.wizard.xml.io.path.PathTracker;
import com.madrobot.di.wizard.xml.io.path.PathTrackingWriter;

/**
 * Abstract base class for a TreeMarshaller, that can build references.
 * 
 * 
 * @since 1.2
 */
abstract class AbstractReferenceMarshaller extends TreeMarshaller implements MarshallingContext {

	private static class Id {
		private Object item;
		private Path path;

		public Id(Object item, Path path) {
			this.item = item;
			this.path = path;
		}

		protected Object getItem() {
			return this.item;
		}

		protected Path getPath() {
			return this.path;
		}
	}
	public static class ReferencedImplicitElementException extends ConversionException {
		public ReferencedImplicitElementException(final Object item, final Path path) {
			super("Cannot reference implicit element");
			add("implicit-element", item.toString());
			add("referencing-element", path.toString());
		}
	}
	private ObjectIdDictionary implicitElements = new ObjectIdDictionary();
	private Path lastPath;

	private PathTracker pathTracker = new PathTracker();

	private ObjectIdDictionary references = new ObjectIdDictionary();

	AbstractReferenceMarshaller(HierarchicalStreamWriter writer, ConverterLookup converterLookup, Mapper mapper) {
		super(writer, converterLookup, mapper);
		this.writer = new PathTrackingWriter(writer, pathTracker);
	}

	@Override
	public void convert(Object item, Converter converter) {
		if (getMapper().isImmutableValueType(item.getClass())) {
			// strings, ints, dates, etc... don't bother using references.
			converter.marshal(item, writer, this);
		} else {
			final Path currentPath = pathTracker.getPath();
			Id existingReference = (Id) references.lookupId(item);
			if (existingReference != null && existingReference.getPath() != currentPath) {
				String attributeName = getMapper().aliasForSystemAttribute("reference");
				if (attributeName != null) {
					writer.addAttribute(attributeName, createReference(currentPath, existingReference.getItem()));
				}
			} else {
				final Object newReferenceKey = existingReference == null ? createReferenceKey(currentPath, item)
						: existingReference.getItem();
				if (lastPath == null || !currentPath.isAncestor(lastPath)) {
					fireValidReference(newReferenceKey);
					lastPath = currentPath;
					references.associateId(item, new Id(newReferenceKey, currentPath));
				}
				converter.marshal(item, writer, new ReferencingMarshallingContext() {

					@Override
					public void convertAnother(Object nextItem) {
						AbstractReferenceMarshaller.this.convertAnother(nextItem);
					}

					@Override
					public void convertAnother(Object nextItem, Converter converter) {
						AbstractReferenceMarshaller.this.convertAnother(nextItem, converter);
					}

					/**
					 * @deprecated As of 1.4.2
					 */
					@Deprecated
					@Override
					public Path currentPath() {
						return pathTracker.getPath();
					}

					@Override
					public Object get(Object key) {
						return AbstractReferenceMarshaller.this.get(key);
					}

					@Override
					public Iterator keys() {
						return AbstractReferenceMarshaller.this.keys();
					}

					@Override
					public Object lookupReference(Object item) {
						Id id = (Id) references.lookupId(item);
						return id.getItem();
					}

					@Override
					public void put(Object key, Object value) {
						AbstractReferenceMarshaller.this.put(key, value);
					}

					@Override
					public void registerImplicit(Object item) {
						if (implicitElements.containsId(item)) {
							throw new ReferencedImplicitElementException(item, currentPath);
						}
						implicitElements.associateId(item, newReferenceKey);
					}

					@Override
					public void replace(Object original, Object replacement) {
						references.associateId(replacement, new Id(newReferenceKey, currentPath));
					}
				});
			}
		}
	}

	protected abstract String createReference(Path currentPath, Object existingReferenceKey);

	protected abstract Object createReferenceKey(Path currentPath, Object item);

	protected abstract void fireValidReference(Object referenceKey);
}
