/*
 * Copyright (C) 2005, 2006 Joe Walnes.
 * Copyright (C) 2006, 2007, 2011 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 06. February 2005 by Joe Walnes
 */
package com.madrobot.util;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;


/**
 * List that allows items to be added with a priority that will affect the order in which they are later iterated over.
 * Objects with a high priority will appear before objects with a low priority in the list. If two objects of the same
 * priority are added to the list, the most recently added one will be iterated over first. Implementation uses a
 * TreeSet, which has a guaranteed add time of O(log(n)).
 * 
 * @author Joe Walnes
 * @author Guilherme Silveira
 */
public class PrioritizedList {

    private static class PrioritizedItem implements Comparable {

        final int id;
        final int priority;
        final Object value;

        public PrioritizedItem(Object value, int priority, int id) {
            this.value = value;
            this.priority = priority;
            this.id = id;
        }

        @Override
		public int compareTo(Object o) {
            PrioritizedItem other = (PrioritizedItem)o;
            if (this.priority != other.priority) {
                return (other.priority - this.priority);
            }
            return (other.id - this.id);
        }

        @Override
		public boolean equals(Object obj) {
        	if(obj==null)
        		return false;
            return this.id == ((PrioritizedItem)obj).id;
        }

    }

    private static class PrioritizedItemIterator implements Iterator {

        private Iterator iterator;

        public PrioritizedItemIterator(Iterator iterator) {
            this.iterator = iterator;
        }

        @Override
		public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
		public Object next() {
            return ((PrioritizedItem)iterator.next()).value;
        }

        @Override
		public void remove() {
            // call iterator.remove()?
            throw new UnsupportedOperationException();
        }

    }

    private int lastId = 0;

    private int lowestPriority = Integer.MAX_VALUE;

    private final Set set = new TreeSet();

    public void add(Object item, int priority) {
        if (this.lowestPriority > priority) {
            this.lowestPriority = priority;
        }
        this.set.add(new PrioritizedItem(item, priority, ++lastId));
    }

    public Iterator iterator() {
        return new PrioritizedItemIterator(this.set.iterator());
    }

}
