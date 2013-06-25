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

package com.madrobot.di.wizard.xml.converters;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.security.auth.Subject;

import com.madrobot.di.wizard.xml.Mapper;
import com.madrobot.di.wizard.xml.io.HierarchicalStreamReader;
import com.madrobot.di.wizard.xml.io.HierarchicalStreamWriter;

/**
 * Converts a {@link Subject} instance. Note, that this Converter does only convert the contained Principals as it is
 * done by JDK serialization, but not any credentials. For other behaviour you can derive your own converter, overload
 * the appropriate methods and register it in the {@link com.madrobot.di.wizard.xml.XMLWizard}.
 * 
 * @since 1.1.3
 */
public class SubjectConverter extends AbstractCollectionConverter {

	public SubjectConverter(Mapper mapper) {
		super(mapper);
	}

	@Override
	public boolean canConvert(Class type) {
		return type.equals(Subject.class);
	}

	@Override
	public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
		Subject subject = (Subject) source;
		marshalPrincipals(subject.getPrincipals(), writer, context);
		marshalPublicCredentials(subject.getPublicCredentials(), writer, context);
		marshalPrivateCredentials(subject.getPrivateCredentials(), writer, context);
		marshalReadOnly(subject.isReadOnly(), writer);
	}

	protected void marshalPrincipals(Set principals, HierarchicalStreamWriter writer, MarshallingContext context) {
		writer.startNode("principals");
		for (final Iterator iter = principals.iterator(); iter.hasNext();) {
			final Object principal = iter.next(); // pre jdk 1.4 a Principal was also in javax.security
			writeItem(principal, context, writer);
		}
		writer.endNode();
	};

	protected void marshalPrivateCredentials(Set privCredentials, HierarchicalStreamWriter writer, MarshallingContext context) {
	};

	protected void marshalPublicCredentials(Set pubCredentials, HierarchicalStreamWriter writer, MarshallingContext context) {
	};

	protected void marshalReadOnly(boolean readOnly, HierarchicalStreamWriter writer) {
		writer.startNode("readOnly");
		writer.setValue(String.valueOf(readOnly));
		writer.endNode();
	};

	protected Set populateSet(HierarchicalStreamReader reader, UnmarshallingContext context) {
		Set set = new HashSet();
		reader.moveDown();
		while (reader.hasMoreChildren()) {
			reader.moveDown();
			Object elementl = readItem(reader, context, set);
			reader.moveUp();
			set.add(elementl);
		}
		reader.moveUp();
		return set;
	}

	@Override
	public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
		Set principals = unmarshalPrincipals(reader, context);
		Set publicCredentials = unmarshalPublicCredentials(reader, context);
		Set privateCredentials = unmarshalPrivateCredentials(reader, context);
		boolean readOnly = unmarshalReadOnly(reader);
		return new Subject(readOnly, principals, publicCredentials, privateCredentials);
	};

	protected Set unmarshalPrincipals(HierarchicalStreamReader reader, UnmarshallingContext context) {
		return populateSet(reader, context);
	};

	protected Set unmarshalPrivateCredentials(HierarchicalStreamReader reader, UnmarshallingContext context) {
		return Collections.EMPTY_SET;
	};

	protected Set unmarshalPublicCredentials(HierarchicalStreamReader reader, UnmarshallingContext context) {
		return Collections.EMPTY_SET;
	};

	protected boolean unmarshalReadOnly(HierarchicalStreamReader reader) {
		reader.moveDown();
		boolean readOnly = Boolean.getBoolean(reader.getValue());
		reader.moveUp();
		return readOnly;
	}
}
