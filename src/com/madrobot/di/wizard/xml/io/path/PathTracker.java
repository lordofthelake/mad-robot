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

package com.madrobot.di.wizard.xml.io.path;

import java.util.HashMap;
import java.util.Map;

/**
 * Maintains the current {@link Path} as a stream is moved through.
 * 
 * <p>
 * Can be linked to a {@link com.madrobot.di.wizard.xml.io.HierarchicalStreamWriter} or
 * {@link com.madrobot.di.wizard.xml.io.HierarchicalStreamReader} by wrapping them with a {@link PathTrackingWriter} or
 * {@link PathTrackingReader}.
 * </p>
 * 
 * <h3>Example</h3>
 * 
 * <pre>
 * PathTracker tracker = new PathTracker();
 * tracker.pushElement(&quot;table&quot;);
 * tracker.pushElement(&quot;tr&quot;);
 * tracker.pushElement(&quot;td&quot;);
 * tracker.pushElement(&quot;form&quot;);
 * tracker.popElement(&quot;form&quot;);
 * tracker.popElement(&quot;td&quot;);
 * tracker.pushElement(&quot;td&quot;);
 * tracker.pushElement(&quot;div&quot;);
 * 
 * Path path = tracker.getPath(); // returns &quot;/table/tr/td[2]/div&quot;
 * </pre>
 * 
 * @see Path
 * @see PathTrackingReader
 * @see PathTrackingWriter
 * 
 * @author Joe Walnes
 */
public class PathTracker {

	private int capacity;
	private Path currentPath;
	private Map[] indexMapStack;
	private String[] pathStack;

	private int pointer;

	public PathTracker() {
		this(16);
	}

	/**
	 * @param initialCapacity
	 *            Size of the initial stack of nodes (one level per depth in the tree). Note that this is only for
	 *            optimizations - the stack will resize itself if it exceeds its capacity. If in doubt, use the other
	 *            constructor.
	 */
	public PathTracker(int initialCapacity) {
		this.capacity = Math.max(1, initialCapacity);
		pathStack = new String[capacity];
		indexMapStack = new Map[capacity];
	}

	/**
	 * Get the depth of the stack.
	 * 
	 * @return the stack depth
	 * @since 1.4.2
	 */
	public int depth() {
		return pointer;
	}

	/**
	 * Current Path in stream.
	 */
	public Path getPath() {
		if (currentPath == null) {
			String[] chunks = new String[pointer + 1];
			chunks[0] = "";
			for (int i = -pointer; ++i <= 0;) {
				final String name = peekElement(i);
				chunks[i + pointer] = name;
			}
			currentPath = new Path(chunks);
		}
		return currentPath;
	}

	/**
	 * Get the last path element from the stack.
	 * 
	 * @return the name of the path element
	 * @since 1.4.2
	 */
	public String peekElement() {
		return peekElement(0);
	}

	/**
	 * Get a path element from the stack.
	 * 
	 * @param i
	 *            path index
	 * @return the name of the path element
	 * @since 1.4.2
	 * @throws ArrayIndexOutOfBoundsException
	 *             if the index is &gt;= 0 or &lt;= -depth()
	 */
	public String peekElement(int i) {
		if (i < -pointer || i > 0) {
			throw new ArrayIndexOutOfBoundsException(i);
		}
		int idx = pointer + i - 1;
		final String name;
		Integer integer = ((Integer) indexMapStack[idx].get(pathStack[idx]));
		int index = integer.intValue();
		if (index > 1) {
			StringBuffer chunk = new StringBuffer(pathStack[idx].length() + 6);
			chunk.append(pathStack[idx]).append('[').append(index).append(']');
			name = chunk.toString();
		} else {
			name = pathStack[idx];
		}
		return name;
	}

	/**
	 * Notify the tracker that the stream has moved out of an element.
	 */
	public void popElement() {
		indexMapStack[pointer] = null;
		pathStack[pointer] = null;
		currentPath = null;
		pointer--;
	}

	/**
	 * Notify the tracker that the stream has moved into a new element.
	 * 
	 * @param name
	 *            Name of the element
	 */
	public void pushElement(String name) {
		if (pointer + 1 >= capacity) {
			resizeStacks(capacity * 2);
		}
		pathStack[pointer] = name;
		Map indexMap = indexMapStack[pointer];
		if (indexMap == null) {
			indexMap = new HashMap();
			indexMapStack[pointer] = indexMap;
		}
		if (indexMap.containsKey(name)) {
			indexMap.put(name, new Integer(((Integer) indexMap.get(name)).intValue() + 1));
		} else {
			indexMap.put(name, new Integer(1));
		}
		pointer++;
		currentPath = null;
	}

	private void resizeStacks(int newCapacity) {
		String[] newPathStack = new String[newCapacity];
		Map[] newIndexMapStack = new Map[newCapacity];
		int min = Math.min(capacity, newCapacity);
		System.arraycopy(pathStack, 0, newPathStack, 0, min);
		System.arraycopy(indexMapStack, 0, newIndexMapStack, 0, min);
		pathStack = newPathStack;
		indexMapStack = newIndexMapStack;
		capacity = newCapacity;
	}
}
