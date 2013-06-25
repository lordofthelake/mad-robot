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

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * @since 1.2
 */
public class BinaryStreamWriter implements ExtendedHierarchicalStreamWriter {

	private class IdRegistry {

		private Map ids = new HashMap();
		private long nextId = 0;

		public long getId(String value) {
			Long id = (Long) ids.get(value);
			if (id == null) {
				id = new Long(++nextId);
				ids.put(value, id);
				write(new Token.MapIdToValue(id.longValue(), value));
			}
			return id.longValue();
		}

	}
	private final IdRegistry idRegistry = new IdRegistry();
	private final DataOutputStream out;

	private final Token.Formatter tokenFormatter = new Token.Formatter();

	public BinaryStreamWriter(OutputStream outputStream) {
		out = new DataOutputStream(outputStream);
	}

	@Override
	public void addAttribute(String name, String value) {
		write(new Token.Attribute(idRegistry.getId(name), value));
	}

	@Override
	public void close() {
		try {
			out.close();
		} catch (IOException e) {
			throw new StreamException(e);
		}
	}

	@Override
	public void endNode() {
		write(new Token.EndNode());
	}

	@Override
	public void flush() {
		try {
			out.flush();
		} catch (IOException e) {
			throw new StreamException(e);
		}
	}

	@Override
	public void setValue(String text) {
		write(new Token.Value(text));
	}

	@Override
	public void startNode(String name) {
		write(new Token.StartNode(idRegistry.getId(name)));
	}

	@Override
	public void startNode(String name, Class clazz) {
		startNode(name);
	}

	@Override
	public HierarchicalStreamWriter underlyingWriter() {
		return this;
	}

	private void write(Token token) {
		try {
			tokenFormatter.write(out, token);
		} catch (IOException e) {
			throw new StreamException(e);
		}
	}
}
