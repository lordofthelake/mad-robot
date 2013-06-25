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
package com.madrobot.net.client.util;

import java.io.Serializable;
import java.util.EventListener;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 */

public class ListenerList implements Serializable, Iterable<EventListener>
{
    /**
	 * Provide a brief description of serialVersionUID.
	 * Specify the purpose of this field.
	 *
	 */
	private static final long serialVersionUID = -6684541015292922675L;
	private final CopyOnWriteArrayList<EventListener> __listeners;

    public ListenerList()
    {
        __listeners = new CopyOnWriteArrayList<EventListener>();
    }

    public void addListener(EventListener listener)
    {
            __listeners.add(listener);
    }

    public int getListenerCount()
    {
        return __listeners.size();
    }

    /**
     * Return an {@link Iterator} for the {@link EventListener} instances
     * 
     * @since 2.0
     * TODO Check that this is a good defensive strategy
     */
    @Override
	public Iterator<EventListener> iterator() {
            return __listeners.iterator();
    }
    
    public  void removeListener(EventListener listener)
    {
            __listeners.remove(listener);
    }

}
