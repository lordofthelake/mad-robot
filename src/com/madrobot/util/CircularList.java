package com.madrobot.util;

import java.util.ArrayList;

/**
 * This class implements a cyclic list based on a ArrayList.
 */

public class CircularList extends ArrayList {

	private int iCurrentIndex, iMaxIndex;

	// ////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructor. Arguments are just passed on to the superclass.
	 */

	public CircularList() {
		super();
		iCurrentIndex = -1;
		iMaxIndex = -1;
	}

	// ////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructor. Arguments are just passed on to the superclass.
	 * 
	 * @param initialCapacity
	 */

	public CircularList(int initialCapacity) {
		super(initialCapacity);
		iCurrentIndex = -1;
		iMaxIndex = -1;
	}

	// ////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructor. Arguments are just passed on to the superclass.
	 * 
	 * @param initialCapacity
	 * @param capacityIncrement
	 */

	public CircularList(int initialCapacity, int capacityIncrement) {
		super(initialCapacity);
		iCurrentIndex = -1;
		iMaxIndex = -1;
	}

	// ////////////////////////////////////////////////////////////////////////////

	/**
	 * Add an element to the list. Calls Vector.addElement().
	 * 
	 * @param obj
	 *            Object to add to the list.
	 */

	public final synchronized void addElem(Object obj) {
		iMaxIndex++;
		iCurrentIndex = iMaxIndex;
		add(obj);
	}

	// ////////////////////////////////////////////////////////////////////////////

	/**
	 * Return current element. If no element exists, return null.
	 */

	public final synchronized Object currElem() {
		if (iMaxIndex == -1)
			return null;
		else
			return get(iCurrentIndex);
	}

	// ////////////////////////////////////////////////////////////////////////////

	/**
	 * Return next element. If no element exists, return null. If at end of the
	 * list, return the first element.
	 */

	public final synchronized Object nextElem() {
		try {
			if (iMaxIndex == -1)
				return null;
			else if (iCurrentIndex < iMaxIndex)
				iCurrentIndex++;
			else
				iCurrentIndex = 0;
			return get(iCurrentIndex);
		} catch (Exception e) { // ArrayIndexOutOfBoundsException impossible
			return null;
		}
	}

	// ////////////////////////////////////////////////////////////////////////////

	/**
	 * Return previous element. If no element exists, return null. If at
	 * beginning of the list, return the last element.
	 */

	public final synchronized Object prevElem() {
		try {
			if (iMaxIndex == -1)
				return null;
			else if (iCurrentIndex > 0)
				iCurrentIndex--;
			else
				iCurrentIndex = iMaxIndex;
			return get(iCurrentIndex);
		} catch (Exception e) { // ArrayIndexOutOfBoundsException impossible
			return null;
		}
	}
}
