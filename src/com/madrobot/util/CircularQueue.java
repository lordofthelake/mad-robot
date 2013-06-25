/*******************************************************************************
 * Copyright (c) 2011 MadRobot.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *  Elton Kent - initial API and implementation
 ******************************************************************************/
package com.madrobot.util;

import java.io.Serializable;
import java.util.AbstractList;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Queue;

/**
 * A unbounded circular queue based on array.
 * 
 */
public class CircularQueue<E> extends AbstractList<E> implements Queue<E>, Serializable {
    /** Minimal size of the underlying array */
    private static final int DEFAULT_CAPACITY = 4;

    /** The serialVersionUID : mandatory for serializable classes */
    private static final long serialVersionUID = 3993421269224511264L;

    /**
     * The capacity must be a power of 2.
     */
    private static int normalizeCapacity(int initialCapacity) {
        int actualCapacity = 1;
        
        while (actualCapacity < initialCapacity) {
            actualCapacity <<= 1;
            if (actualCapacity < 0) {
                actualCapacity = 1 << 30;
                break;
            }
        }
        return actualCapacity;
    }
    
    private int first = 0;
    private boolean full;
    /** The initial capacity of the list */
    private final int initialCapacity;
    // XXX: This volatile keyword here is a workaround for SUN Java Compiler bug,
    //      which produces buggy byte code.  I don't event know why adding a volatile
    //      fixes the problem.  Eclipse Java Compiler seems to produce correct byte code.
    private volatile Object[] items;
    private int last = 0;
    private int mask;

    private int shrinkThreshold;
    
    /**
     * Construct a new, empty queue.
     */
    public CircularQueue() {
        this(DEFAULT_CAPACITY);
    }

    public CircularQueue(int initialCapacity) {
        int actualCapacity = normalizeCapacity(initialCapacity);
        items = new Object[actualCapacity];
        mask = actualCapacity - 1;
        this.initialCapacity = actualCapacity;
        this.shrinkThreshold = 0;
    }

    @Override
    public boolean add(E o) {
        return offer(o);
    }

    @Override
    public void add(int idx, E o) {
        if (idx == size()) {
            offer(o);
            return;
        }

        checkIndex(idx);
        expandIfNeeded();

        int realIdx = getRealIndex(idx);

        // Make a room for a new element.
        if (first < last) {
            System
                    .arraycopy(items, realIdx, items, realIdx + 1, last
                            - realIdx);
        } else {
            if (realIdx >= first) {
                System.arraycopy(items, 0, items, 1, last);
                items[0] = items[items.length - 1];
                System.arraycopy(items, realIdx, items, realIdx + 1,
                        items.length - realIdx - 1);
            } else {
                System.arraycopy(items, realIdx, items, realIdx + 1, last
                        - realIdx);
            }
        }

        items[realIdx] = o;
        increaseSize();
    }

    /**
     * Returns the capacity of this queue.
     */
    public int capacity() {
        return items.length;
    }

    private void checkIndex(int idx) {
        if (idx < 0 || idx >= size()) {
            throw new IndexOutOfBoundsException(String.valueOf(idx));
        }
    }

    @Override
    public void clear() {
        if (!isEmpty()) {
            Arrays.fill(items, null);
            first = 0;
            last = 0;
            full = false;
            shrinkIfNeeded();
        }
    }

    private void decreaseSize() {
        first = (first + 1) & mask;
        full = false;
    }

    @Override
	public E element() {
        if (isEmpty()) {
            throw new NoSuchElementException();
        }
        return peek();
    }

    private void expandIfNeeded() {
        if (full) {
            // expand queue
            final int oldLen = items.length;
            final int newLen = oldLen << 1;
            Object[] tmp = new Object[newLen];
    
            if (first < last) {
                System.arraycopy(items, first, tmp, 0, last - first);
            } else {
                System.arraycopy(items, first, tmp, 0, oldLen - first);
                System.arraycopy(items, 0, tmp, oldLen - first, last);
            }
    
            first = 0;
            last = oldLen;
            items = tmp;
            mask = tmp.length - 1;
            if (newLen >>> 3 > initialCapacity) {
                shrinkThreshold = newLen >>> 3;
            }
        }
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public E get(int idx) {
        checkIndex(idx);
        return (E) items[getRealIndex(idx)];
    }

    private int getRealIndex(int idx) {
        return (first + idx) & mask;
    }

    private void increaseSize() {
        last = (last + 1) & mask;
        full = first == last;
    }

    @Override
    public boolean isEmpty() {
        return (first == last) && !full;
    }

    @Override
	public boolean offer(E item) {
        if (item == null) {
            throw new IllegalArgumentException("item");
        }
        
        expandIfNeeded();
        items[last] = item;
        increaseSize();
        return true;
    }

    @Override
	@SuppressWarnings("unchecked")
    public E peek() {
        if (isEmpty()) {
            return null;
        }

        return (E) items[first];
    }
    
    @Override
	@SuppressWarnings("unchecked")
    public E poll() {
        if (isEmpty()) {
            return null;
        }

        Object ret = items[first];
        items[first] = null;
        decreaseSize();
        
        if (first == last) {
            first = last = 0;
        }

        shrinkIfNeeded();
        return (E) ret;
    }

    @Override
	public E remove() {
        if (isEmpty()) {
            throw new NoSuchElementException();
        }
        return poll();
    }

    @SuppressWarnings("unchecked")
    @Override
    public E remove(int idx) {
        if (idx == 0) {
            return poll();
        }

        checkIndex(idx);

        int realIdx = getRealIndex(idx);
        Object removed = items[realIdx];

        // Remove a room for the removed element.
        if (first < last) {
            System.arraycopy(items, first, items, first + 1, realIdx - first);
        } else {
            if (realIdx >= first) {
                System.arraycopy(items, first, items, first + 1, realIdx
                        - first);
            } else {
                System.arraycopy(items, 0, items, 1, realIdx);
                items[0] = items[items.length - 1];
                System.arraycopy(items, first, items, first + 1, items.length
                        - first - 1);
            }
        }

        items[first] = null;
        decreaseSize();

        shrinkIfNeeded();
        return (E) removed;
    }

    @SuppressWarnings("unchecked")
    @Override
    public E set(int idx, E o) {
        checkIndex(idx);

        int realIdx = getRealIndex(idx);
        Object old = items[realIdx];
        items[realIdx] = o;
        return (E) old;
    }

    private void shrinkIfNeeded() {
        int size = size();
        if (size <= shrinkThreshold) {
            // shrink queue
            final int oldLen = items.length;
            int newLen = normalizeCapacity(size);
            if (size == newLen) {
                newLen <<= 1;
            }
            
            if (newLen >= oldLen) {
                return;
            }
            
            if (newLen < initialCapacity) {
                if (oldLen == initialCapacity) {
                    return;
                }

                newLen = initialCapacity;
            }
            
            Object[] tmp = new Object[newLen];
    
            // Copy only when there's something to copy.
            if (size > 0) {
                if (first < last) {
                    System.arraycopy(items, first, tmp, 0, last - first);
                } else {
                    System.arraycopy(items, first, tmp, 0, oldLen - first);
                    System.arraycopy(items, 0, tmp, oldLen - first, last);
                }
            }
    
            first = 0;
            last = size;
            items = tmp;
            mask = tmp.length - 1;
            shrinkThreshold = 0;
        }
    }

    @Override
    public int size() {
        if (full) {
            return capacity();
        }
        
        if (last >= first) {
            return last - first;
        }

        return last - first + capacity();
    }

    @Override
    public String toString() {
        return "first=" + first + ", last=" + last + ", size=" + size()
                + ", mask = " + mask;
    }
}
