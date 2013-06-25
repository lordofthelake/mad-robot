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

package com.madrobot.di.wizard.xml.io.xml;

import java.util.Iterator;

import com.madrobot.di.wizard.xml.io.AbstractReader;
import com.madrobot.di.wizard.xml.io.AttributeNameIterator;
import com.madrobot.di.wizard.xml.io.NameCoder;
import com.madrobot.util.FastStack;

/**
 * Base class that contains common functionality across HierarchicalStreamReader implementations that need to read from
 * a pull parser.
 * 
 */
public abstract class AbstractPullReader extends AbstractReader {

	private static class Event {
		int type;
		String value;
	}
	protected static final int COMMENT = 4;
	protected static final int END_NODE = 2;
	protected static final int OTHER = 0;
	protected static final int START_NODE = 1;

	protected static final int TEXT = 3;
	private final FastStack elementStack = new FastStack(16);

	private final FastStack lookahead = new FastStack(4);
	private final FastStack lookback = new FastStack(4);
	private boolean marked;

	private final FastStack pool = new FastStack(16);

	/**
	 * @since 1.4
	 */
	protected AbstractPullReader(NameCoder nameCoder) {
		super(nameCoder);
	}

	/**
	 * @since 1.2
	 * @deprecated As of 1.4 use {@link AbstractPullReader#AbstractPullReader(NameCoder)} instead
	 */
	@Deprecated
	protected AbstractPullReader(XmlFriendlyNameCoder replacer) {
		this((NameCoder) replacer);
	}

	@Override
	public Iterator getAttributeNames() {
		return new AttributeNameIterator(this);
	}

	@Override
	public String getNodeName() {
		return decodeNode((String) elementStack.peek());
	}

	@Override
	public String getValue() {
		// we should collapse together any text which
		// contains comments

		// lets only use a string buffer when we get 2 strings
		// to avoid copying strings
		String last = null;
		StringBuffer buffer = null;

		mark();
		Event event = readEvent();
		while (true) {
			if (event.type == TEXT) {
				String text = event.value;
				if (text != null && text.length() > 0) {
					if (last == null) {
						last = text;
					} else {
						if (buffer == null) {
							buffer = new StringBuffer(last);
						}
						buffer.append(text);
					}
				}
			} else if (event.type != COMMENT) {
				break;
			}
			event = readEvent();
		}
		reset();
		if (buffer != null) {
			return buffer.toString();
		} else {
			return (last == null) ? "" : last;
		}
	}

	@Override
	public boolean hasMoreChildren() {
		mark();
		while (true) {
			switch (readEvent().type) {
			case START_NODE:
				reset();
				return true;
			case END_NODE:
				reset();
				return false;
			default:
				continue;
			}
		}
	}

	public void mark() {
		marked = true;
	}

	private void move() {
		final Event event = readEvent();
		pool.push(event);
		switch (event.type) {
		case START_NODE:
			elementStack.push(pullElementName());
			break;
		case END_NODE:
			elementStack.pop();
			break;
		}
	}

	@Override
	public void moveDown() {
		int currentDepth = elementStack.size();
		while (elementStack.size() <= currentDepth) {
			move();
			if (elementStack.size() < currentDepth) {
				throw new RuntimeException(); // sanity check
			}
		}
	}

	@Override
	public void moveUp() {
		int currentDepth = elementStack.size();
		while (elementStack.size() >= currentDepth) {
			move();
		}
	}

	@Override
	public String peekNextChild() {
		mark();
		while (true) {
			Event ev = readEvent();
			switch (ev.type) {
			case START_NODE:
				reset();
				return ev.value;
			case END_NODE:
				reset();
				return null;
			default:
				continue;
			}
		}
	}

	/**
	 * Pull the name of the current element from the stream.
	 */
	protected abstract String pullElementName();

	/**
	 * Pull the next event from the stream.
	 * 
	 * <p>
	 * This MUST return {@link #START_NODE}, {@link #END_NODE}, {@link #TEXT}, {@link #COMMENT}, {@link #OTHER} or throw
	 * {@link com.madrobot.di.wizard.xml.io.StreamException}.
	 * </p>
	 * 
	 * <p>
	 * The underlying pull parser will most likely return its own event types. These must be mapped to the appropriate
	 * events.
	 * </p>
	 */
	protected abstract int pullNextEvent();

	/**
	 * Pull the contents of the current text node from the stream.
	 */
	protected abstract String pullText();

	private Event readEvent() {
		if (marked) {
			if (lookback.hasStuff()) {
				return (Event) lookahead.push(lookback.pop());
			} else {
				return (Event) lookahead.push(readRealEvent());
			}
		} else {
			if (lookback.hasStuff()) {
				return (Event) lookback.pop();
			} else {
				return readRealEvent();
			}
		}
	}

	private Event readRealEvent() {
		Event event = pool.hasStuff() ? (Event) pool.pop() : new Event();
		event.type = pullNextEvent();
		if (event.type == TEXT) {
			event.value = pullText();
		} else if (event.type == START_NODE) {
			event.value = pullElementName();
		} else {
			event.value = null;
		}
		return event;
	}

	public void reset() {
		while (lookahead.hasStuff()) {
			lookback.push(lookahead.pop());
		}
		marked = false;
	}
}
