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

package com.madrobot.di.wizard.xml.io;

/**
 * Base class to make it easy to create wrappers (decorators) for HierarchicalStreamWriter.
 * 
 * @author Joe Walnes
 */
public abstract class WriterWrapper implements ExtendedHierarchicalStreamWriter {

	protected HierarchicalStreamWriter wrapped;

	protected WriterWrapper(HierarchicalStreamWriter wrapped) {
		this.wrapped = wrapped;
	}

	@Override
	public void addAttribute(String key, String value) {
		wrapped.addAttribute(key, value);
	}

	@Override
	public void close() {
		wrapped.close();
	}

	@Override
	public void endNode() {
		wrapped.endNode();
	}

	@Override
	public void flush() {
		wrapped.flush();
	}

	@Override
	public void setValue(String text) {
		wrapped.setValue(text);
	}

	@Override
	public void startNode(String name) {
		wrapped.startNode(name);
	}

	@Override
	public void startNode(String name, Class clazz) {

		((ExtendedHierarchicalStreamWriter) wrapped).startNode(name, clazz);
	}

	@Override
	public HierarchicalStreamWriter underlyingWriter() {
		return wrapped.underlyingWriter();
	}

}
