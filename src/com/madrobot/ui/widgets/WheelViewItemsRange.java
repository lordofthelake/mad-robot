package com.madrobot.ui.widgets;

/**
 * Range for visible items.
 */
class WheelViewItemsRange {
	// Items count
	private int count;

	// First item number
	private int first;

	/**
	 * Default constructor. Creates an empty range
	 */
	WheelViewItemsRange() {
		this(0, 0);
	}

	/**
	 * Constructor
	 * 
	 * @param first
	 *            the number of first item
	 * @param count
	 *            the count of items
	 */
	WheelViewItemsRange(int first, int count) {
		this.first = first;
		this.count = count;
	}

	/**
	 * Tests whether item is contained by range
	 * 
	 * @param index
	 *            the item number
	 * @return true if item is contained
	 */
	boolean contains(int index) {
		return index >= getFirst() && index <= getLast();
	}

	/**
	 * Get items count
	 * 
	 * @return the count of items
	 */
	int getCount() {
		return count;
	}

	/**
	 * Gets number of first item
	 * 
	 * @return the number of the first item
	 */
	int getFirst() {
		return first;
	}

	/**
	 * Gets number of last item
	 * 
	 * @return the number of last item
	 */
	int getLast() {
		return getFirst() + getCount() - 1;
	}
}