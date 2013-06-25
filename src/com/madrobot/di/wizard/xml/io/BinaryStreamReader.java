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

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.madrobot.di.wizard.xml.converters.ErrorWriter;

/**
 * A HierarchicalStreamReader that reads from a binary stream created by {@link BinaryStreamWriter}.
 * <p/>
 * <p>
 * This produces
 * 
 * @see BinaryStreamReader
 * @since 1.2
 */
public class BinaryStreamReader implements ExtendedHierarchicalStreamReader {

	private static class IdRegistry {

		private Map map = new HashMap();

		public String get(long id) {
			String result = (String) map.get(new Long(id));
			if (result == null) {
				throw new StreamException("Unknown ID : " + id);
			} else {
				return result;
			}
		}

		public void put(long id, String value) {
			map.put(new Long(id), value);
		}
	}
	private final ReaderDepthState depthState = new ReaderDepthState();
	private final IdRegistry idRegistry = new IdRegistry();

	private final DataInputStream in;
	private Token pushback;

	private final Token.Formatter tokenFormatter = new Token.Formatter();

	public BinaryStreamReader(InputStream inputStream) {
		in = new DataInputStream(inputStream);
		moveDown();
	}

	@Override
	public void appendErrors(ErrorWriter errorWriter) {
		// TODO: When things go bad, it would be good to know where!
	}

	@Override
	public void close() {
		try {
			in.close();
		} catch (IOException e) {
			throw new StreamException(e);
		}
	}

	@Override
	public String getAttribute(int index) {
		return depthState.getAttribute(index);
	}

	@Override
	public String getAttribute(String name) {
		return depthState.getAttribute(name);
	}

	@Override
	public int getAttributeCount() {
		return depthState.getAttributeCount();
	}

	@Override
	public String getAttributeName(int index) {
		return depthState.getAttributeName(index);
	}

	@Override
	public Iterator getAttributeNames() {
		return depthState.getAttributeNames();
	}

	@Override
	public String getNodeName() {
		return depthState.getName();
	}

	@Override
	public String getValue() {
		return depthState.getValue();
	}

	@Override
	public boolean hasMoreChildren() {
		return depthState.hasMoreChildren();
	}

	@Override
	public void moveDown() {
		depthState.push();
		Token firstToken = readToken();
		switch (firstToken.getType()) {
		case Token.TYPE_START_NODE:
			depthState.setName(idRegistry.get(firstToken.getId()));
			break;
		default:
			throw new StreamException("Expected StartNode");
		}
		while (true) {
			Token nextToken = readToken();
			switch (nextToken.getType()) {
			case Token.TYPE_ATTRIBUTE:
				depthState.addAttribute(idRegistry.get(nextToken.getId()), nextToken.getValue());
				break;
			case Token.TYPE_VALUE:
				depthState.setValue(nextToken.getValue());
				break;
			case Token.TYPE_END_NODE:
				depthState.setHasMoreChildren(false);
				pushBack(nextToken);
				return;
			case Token.TYPE_START_NODE:
				depthState.setHasMoreChildren(true);
				pushBack(nextToken);
				return;
			default:
				throw new StreamException("Unexpected token " + nextToken);
			}
		}
	}

	@Override
	public void moveUp() {
		depthState.pop();
		// We're done with this depth. Skip over all tokens until we get to the end.
		int depth = 0;
		slurp: while (true) {
			Token nextToken = readToken();
			switch (nextToken.getType()) {
			case Token.TYPE_END_NODE:
				if (depth == 0) {
					break slurp;
				} else {
					depth--;
				}
				break;
			case Token.TYPE_START_NODE:
				depth++;
				break;
			default:
				// Ignore other tokens
			}
		}
		// Peek ahead to determine if there are any more kids at this level.
		Token nextToken = readToken();
		switch (nextToken.getType()) {
		case Token.TYPE_END_NODE:
			depthState.setHasMoreChildren(false);
			break;
		case Token.TYPE_START_NODE:
			depthState.setHasMoreChildren(true);
			break;
		default:
			throw new StreamException("Unexpected token " + nextToken);
		}
		pushBack(nextToken);
	}

	@Override
	public String peekNextChild() {
		if (depthState.hasMoreChildren()) {
			return idRegistry.get(pushback.getId());
		}
		return null;
	}

	public void pushBack(Token token) {
		if (pushback == null) {
			pushback = token;
		} else {
			// If this happens, I've messed up :( -joe.
			throw new Error("Cannot push more than one token back");
		}
	}

	private Token readToken() {
		if (pushback == null) {
			try {
				Token token = tokenFormatter.read(in);
				switch (token.getType()) {
				case Token.TYPE_MAP_ID_TO_VALUE:
					idRegistry.put(token.getId(), token.getValue());
					return readToken(); // Next one please.
				default:
					return token;
				}
			} catch (IOException e) {
				throw new StreamException(e);
			}
		} else {
			Token result = pushback;
			pushback = null;
			return result;
		}
	}

	@Override
	public HierarchicalStreamReader underlyingReader() {
		return this;
	}

}
