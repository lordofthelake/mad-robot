package com.madrobot.ui.widgets;

import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Interpolator;
import android.widget.LinearLayout;

import com.madrobot.R;
import com.madrobot.ui.adapters.WheelViewAdapter;

/**
 * iPhone style wheel based selector
 * <p>
 * The following are the WheelView attributes (attrs.xml)
 * <table border="0" width="250" cellspacing="1">
 * <tr>
 * <th><b>Attribute</b></td>
 * <td><b>Type</b></td>
 * <td><b>Default</b></td>
 * <td><b>Description</b></td>
 * </tr>
 * <tr>
 * <td><code>isCyclic</code></td>
 * <td>Boolean</td>
 * <td>false</td>
 * <td>If true, the wheelview's adapter is cyclic. (Snap's back to the first item after the last)</td>
 * </tr>
 * 
 * <tr>
 * <td><code>topDrawable</code></td>
 * <td>Drawable</td>
 * <td>Black shadow</td>
 * <td>
 * <p>
 * Drawable to be used as a top shadow.Can be set to not be rendered using the <code>drawTopDrawable</code> attribute
 * </p>
 * </td>
 * </tr>
 * <tr>
 * <td><code>bottomDrawable</code></td>
 * <td>Drawable</td>
 * <td>Black shadow</td>
 * <td>
 * <p>
 * Drawable to be used as a bottom shadow.Can be set to not be rendered using the <code>drawBottomDrawable</code>
 * attribute
 * </p>
 * </td>
 * </tr>
 * <tr>
 * <td><code>selectorDrawable</code></td>
 * <td>Drawable</td>
 * <td>Semi-transparent box</td>
 * <td>
 * <p>
 * Drawable used as the wheel's selector.Can be set not to be rendered using the <code>drawSelector</code>
 * </p>
 * </td>
 * </tr>
 * <tr>
 * <td><code>drawTopDrawable</code></td>
 * <td>Boolean</td>
 * <td>false</td>
 * <td>If true, the top shadow drawable is not rendered</td>
 * </tr>
 * <tr>
 * <td><code>drawBottomDrawable</code></td>
 * <td>Boolean</td>
 * <td>false</td>
 * <td>If true, the bottom shadow drawable is not rendered</td>
 * </tr>
 * 
 * <tr>
 * <td><code>drawSelector</code></td>
 * <td>Boolean</td>
 * <td>false</td>
 * <td>If true,the selector is not rendered</td>
 * </tr>
 * </table>
 * <br/>
 * <b>Using the WheelView</b><br/>
 * A background needs to be set for the WheelView if the transparent look is not desired.
 * <code>
 * 		&lt;com.madrobot.ui.widgets.WheelView 
 * 					android:layout_width="wrap_content" 
 * 					android:layout_height="wrap_content"
 * 					android:background="#fff"
 * 					{your_namespace}:isCyclic="true">
 * </code><br/>
 * <b>Creating a WheelView Adapter</b></br>
 * </p>
 * 
 * @author elton.kent
 * 
 */
public class WheelView extends View {

	/**
	 * Wheel changed listener interface.
	 * <p>
	 * The onChanged() method is called whenever current wheel positions is changed:
	 * <li>New Wheel position is set
	 * <li>Wheel view is scrolled
	 */
	public interface OnWheelChangedListener {
		/**
		 * Callback method to be invoked when current item changed
		 * 
		 * @param wheel
		 *            the wheel view whose state has changed
		 * @param oldValue
		 *            the old value of current item
		 * @param newValue
		 *            the new value of current item
		 */
		void onChanged(WheelView wheel, int oldValue, int newValue);
	}

	/**
	 * Wheel clicked listener interface.
	 * <p>
	 * The onItemClicked() method is called whenever a wheel item is clicked
	 * <li>New Wheel position is set
	 * <li>Wheel view is scrolled
	 */
	public interface OnWheelClickedListener {
		/**
		 * Callback method to be invoked when current item clicked
		 * 
		 * @param wheel
		 *            the wheel view
		 * @param itemIndex
		 *            the index of clicked item
		 */
		void onItemClicked(WheelView wheel, int itemIndex);
	}

	/**
	 * Wheel scrolled listener interface.
	 */
	public interface OnWheelScrollListener {
		/**
		 * Callback method to be invoked when scrolling ended.
		 * 
		 * @param wheel
		 *            the wheel view whose state has changed.
		 */
		void onScrollingFinished(WheelView wheel);

		/**
		 * Callback method to be invoked when scrolling started.
		 * 
		 * @param wheel
		 *            the wheel view whose state has changed.
		 */
		void onScrollingStarted(WheelView wheel);
	}

	/** Default count of visible items */
	private static final int DEF_VISIBLE_ITEMS = 5;

	/** Top and bottom items offset (to hide that) */
	private static final int ITEM_OFFSET_PERCENT = 10;

	/** Left and right padding value */
	private static final int PADDING = 10;
	/** Top and bottom shadows colors */
	private static final int[] SHADOWS_COLORS = new int[] { 0xFF111111, 0x00AAAAAA, 0x00AAAAAA };

	private Drawable bottomDrawable;
	private boolean canDrawBottomDrawable = true;
	private boolean canDrawSelector = true;

	private boolean canDrawTopDrawable = true;
	// Listeners
	private List<OnWheelChangedListener> changingListeners = new LinkedList<OnWheelChangedListener>();
	private List<OnWheelClickedListener> clickingListeners = new LinkedList<OnWheelClickedListener>();

	// Wheel Values
	private int currentItem = 0;

	// Adapter listener
	private DataSetObserver dataObserver = new DataSetObserver() {
		@Override
		public void onChanged() {
			invalidateWheel(false);
		}

		@Override
		public void onInvalidated() {
			invalidateWheel(true);
		}
	};

	// The number of first item in layout
	private int firstItem;

	// Cyclic
	private boolean isCyclic = false;
	private boolean isScrollingPerformed;
	// Item height
	private int itemHeight = 0;
	// Items layout
	private LinearLayout itemsLayout;

	/**
	 * Constructor
	 */
	// public WheelView(Context context) {
	// super(context);
	// initData(context);
	// }

	// Recycle
	private WheelViewRecycler recycle = new WheelViewRecycler(this);

	// Scrolling
	private WheelViewScroller scroller;

	// Scrolling listener
	WheelViewScroller.ScrollingListener scrollingListener = new WheelViewScroller.ScrollingListener() {
		@Override
		public void onFinished() {
			if (isScrollingPerformed) {
				notifyScrollingListenersAboutEnd();
				isScrollingPerformed = false;
			}

			scrollingOffset = 0;
			invalidate();
		}

		@Override
		public void onJustify() {
			if (Math.abs(scrollingOffset) > WheelViewScroller.MIN_DELTA_FOR_SCROLLING) {
				scroller.scroll(scrollingOffset, 0);
			}
		}

		@Override
		public void onScroll(int distance) {
			doScroll(distance);

			int height = getHeight();
			if (scrollingOffset > height) {
				scrollingOffset = height;
				scroller.stopScrolling();
			} else if (scrollingOffset < -height) {
				scrollingOffset = -height;
				scroller.stopScrolling();
			}
		}

		@Override
		public void onStarted() {
			isScrollingPerformed = true;
			notifyScrollingListenersAboutStart();
		}
	};

	private List<OnWheelScrollListener> scrollingListeners = new LinkedList<OnWheelScrollListener>();

	private int scrollingOffset;

	// Center Line
	private Drawable selectorDrawable;

	// Shadows drawables
	private Drawable topDrawable;

	// View adapter
	private WheelViewAdapter viewAdapter;

	// Count of visible items
	private int visibleItems = DEF_VISIBLE_ITEMS;

	/**
	 * Constructor
	 */
	public WheelView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initAttributes(context, attrs);
		initData(context);
	}

	/**
	 * Constructor
	 */
	public WheelView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initAttributes(context, attrs);
		initData(context);
	}

	/**
	 * Adds wheel changing listener
	 * 
	 * @param listener
	 *            the listener
	 */
	public void addChangingListener(OnWheelChangedListener listener) {
		changingListeners.add(listener);
	}

	/**
	 * Adds wheel clicking listener
	 * 
	 * @param listener
	 *            the listener
	 */
	public void addClickingListener(OnWheelClickedListener listener) {
		clickingListeners.add(listener);
	}

	/**
	 * Adds wheel scrolling listener
	 * 
	 * @param listener
	 *            the listener
	 */
	public void addScrollingListener(OnWheelScrollListener listener) {
		scrollingListeners.add(listener);
	}

	/**
	 * Adds view for item to items layout
	 * 
	 * @param index
	 *            the item index
	 * @param first
	 *            the flag indicates if view should be first
	 * @return true if corresponding item exists and is added
	 */
	private boolean addViewItem(int index, boolean first) {
		View view = getItemView(index);
		if (view != null) {
			if (first) {
				itemsLayout.addView(view, 0);
			} else {
				itemsLayout.addView(view);
			}

			return true;
		}

		return false;
	}

	/**
	 * Builds view for measuring
	 */
	private void buildViewForMeasuring() {
		// clear all items
		if (itemsLayout != null) {
			recycle.recycleItems(itemsLayout, firstItem, new WheelViewItemsRange());
		} else {
			createItemsLayout();
		}

		// add views
		int addItems = visibleItems / 2;
		for (int i = currentItem + addItems; i >= currentItem - addItems; i--) {
			if (addViewItem(i, true)) {
				firstItem = i;
			}
		}
	}

	/**
	 * Calculates control width and creates text layouts
	 * 
	 * @param widthSize
	 *            the input layout width
	 * @param mode
	 *            the layout mode
	 * @return the calculated control width
	 */
	private int calculateLayoutWidth(int widthSize, int mode) {

		itemsLayout.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		itemsLayout.measure(MeasureSpec.makeMeasureSpec(widthSize, MeasureSpec.UNSPECIFIED),
				MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
		int width = itemsLayout.getMeasuredWidth();

		if (mode == MeasureSpec.EXACTLY) {
			width = widthSize;
		} else {
			width += 2 * PADDING;

			// Check against our minimum width
			width = Math.max(width, getSuggestedMinimumWidth());

			if (mode == MeasureSpec.AT_MOST && widthSize < width) {
				width = widthSize;
			}
		}

		itemsLayout.measure(MeasureSpec.makeMeasureSpec(width - 2 * PADDING, MeasureSpec.EXACTLY),
				MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));

		return width;
	}

	/**
	 * Creates item layouts if necessary
	 */
	private void createItemsLayout() {
		if (itemsLayout == null) {
			itemsLayout = new LinearLayout(getContext());
			itemsLayout.setOrientation(LinearLayout.VERTICAL);
		}
	}

	/**
	 * Scrolls the wheel
	 * 
	 * @param delta
	 *            the scrolling value
	 */
	private void doScroll(int delta) {
		scrollingOffset += delta;

		int itemHeight = getItemHeight();
		int count = scrollingOffset / itemHeight;

		int pos = currentItem - count;
		int itemCount = viewAdapter.getItemsCount();

		int fixPos = scrollingOffset % itemHeight;
		if (Math.abs(fixPos) <= itemHeight / 2) {
			fixPos = 0;
		}
		if (isCyclic && itemCount > 0) {
			if (fixPos > 0) {
				pos--;
				count++;
			} else if (fixPos < 0) {
				pos++;
				count--;
			}
			// fix position by rotating
			while (pos < 0) {
				pos += itemCount;
			}
			pos %= itemCount;
		} else {
			//
			if (pos < 0) {
				count = currentItem;
				pos = 0;
			} else if (pos >= itemCount) {
				count = currentItem - itemCount + 1;
				pos = itemCount - 1;
			} else if (pos > 0 && fixPos > 0) {
				pos--;
				count++;
			} else if (pos < itemCount - 1 && fixPos < 0) {
				pos++;
				count--;
			}
		}

		int offset = scrollingOffset;
		if (pos != currentItem) {
			setCurrentItem(pos, false);
		} else {
			invalidate();
		}

		// update offset
		scrollingOffset = offset - count * itemHeight;
		if (scrollingOffset > getHeight()) {
			scrollingOffset = scrollingOffset % getHeight() + getHeight();
		}
	}

	/**
	 * Draws items
	 * 
	 * @param canvas
	 *            the canvas for drawing
	 */
	private void drawItems(Canvas canvas) {
		canvas.save();

		int top = (currentItem - firstItem) * getItemHeight() + (getItemHeight() - getHeight()) / 2;
		canvas.translate(PADDING, -top + scrollingOffset);

		itemsLayout.draw(canvas);

		canvas.restore();
	}

	/**
	 * Draws rect for current value
	 * 
	 * @param canvas
	 *            the canvas for drawing
	 */
	private void drawSelector(Canvas canvas) {
		int center = getHeight() / 2;
		int offset = (int) (getItemHeight() / 2 * 1.2);
		selectorDrawable.setBounds(0, center - offset, getWidth(), center + offset);
		selectorDrawable.draw(canvas);
	}

	/**
	 * Draws shadows on top and bottom of control
	 * 
	 * @param canvas
	 *            the canvas for drawing
	 */
	private void drawTopBottomDrawable(Canvas canvas) {
		int height = (int) (1.5 * getItemHeight());
		if (canDrawTopDrawable) {
			topDrawable.setBounds(0, 0, getWidth(), height);
			topDrawable.draw(canvas);
		}
		if (canDrawBottomDrawable) {
			bottomDrawable.setBounds(0, getHeight() - height, getWidth(), getHeight() + 1);
			bottomDrawable.draw(canvas);
		}
	}

	/**
	 * Gets current value
	 * 
	 * @return the current value
	 */
	public int getCurrentItem() {
		return currentItem;
	}

	/**
	 * Calculates desired height for layout
	 * 
	 * @param layout
	 *            the source layout
	 * @return the desired layout height
	 */
	private int getDesiredHeight(LinearLayout layout) {
		if (layout != null && layout.getChildAt(0) != null) {
			itemHeight = layout.getChildAt(0).getMeasuredHeight();
		}

		int desired = itemHeight * visibleItems - itemHeight * ITEM_OFFSET_PERCENT / 50;

		return Math.max(desired, getSuggestedMinimumHeight());
	}

	/**
	 * Returns height of wheel item
	 * 
	 * @return the item height
	 */
	private int getItemHeight() {
		if (itemHeight != 0) {
			return itemHeight;
		}

		if (itemsLayout != null && itemsLayout.getChildAt(0) != null) {
			itemHeight = itemsLayout.getChildAt(0).getHeight();
			return itemHeight;
		}

		return getHeight() / visibleItems;
	}

	/**
	 * Calculates range for wheel items
	 * 
	 * @return the items range
	 */
	private WheelViewItemsRange getItemsRange() {
		if (getItemHeight() == 0) {
			return null;
		}

		int first = currentItem;
		int count = 1;

		while (count * getItemHeight() < getHeight()) {
			first--;
			count += 2; // top + bottom items
		}

		if (scrollingOffset != 0) {
			if (scrollingOffset > 0) {
				first--;
			}
			count++;

			// process empty items above the first or below the second
			int emptyItems = scrollingOffset / getItemHeight();
			first -= emptyItems;
			count += Math.asin(emptyItems);
		}
		return new WheelViewItemsRange(first, count);
	}

	/**
	 * Returns view for specified item
	 * 
	 * @param index
	 *            the item index
	 * @return item view or empty view if index is out of bounds
	 */
	private View getItemView(int index) {
		if (viewAdapter == null || viewAdapter.getItemsCount() == 0) {
			return null;
		}
		int count = viewAdapter.getItemsCount();
		if (!isValidItemIndex(index)) {
			return viewAdapter.getEmptyItem(recycle.getEmptyItem(), itemsLayout);
		} else {
			while (index < 0) {
				index = count + index;
			}
		}

		index %= count;
		return viewAdapter.getItem(index, recycle.getItem(), itemsLayout);
	}

	/**
	 * Gets view adapter
	 * 
	 * @return the view adapter
	 */
	public WheelViewAdapter getViewAdapter() {
		return viewAdapter;
	}

	/**
	 * Gets count of visible items
	 * 
	 * @return the count of visible items
	 */
	public int getVisibleItems() {
		return visibleItems;
	}

	private void initAttributes(Context context, AttributeSet attrs) {
		TypedArray styledAttrs = context.obtainStyledAttributes(attrs, R.styleable.WheelView);
		isCyclic = styledAttrs.getBoolean(R.styleable.WheelView_isCyclic, false);
		canDrawTopDrawable = styledAttrs.getBoolean(R.styleable.WheelView_drawTopDrawable, true);
		canDrawBottomDrawable = styledAttrs.getBoolean(R.styleable.WheelView_drawBottomDrawable, true);
		canDrawSelector = styledAttrs.getBoolean(R.styleable.WheelView_drawSelector, true);
		topDrawable = styledAttrs.getDrawable(R.styleable.WheelView_topDrawable);
		if (topDrawable == null) {
			topDrawable = new GradientDrawable(Orientation.TOP_BOTTOM, SHADOWS_COLORS);
		}
		bottomDrawable = styledAttrs.getDrawable(R.styleable.WheelView_bottomDrawable);
		if (bottomDrawable == null) {
			bottomDrawable = new GradientDrawable(Orientation.BOTTOM_TOP, SHADOWS_COLORS);
		}

		selectorDrawable = styledAttrs.getDrawable(R.styleable.WheelView_selectorDrawable);
		if (selectorDrawable == null) {
			selectorDrawable = new GradientDrawable(Orientation.BOTTOM_TOP, new int[] { 0x70222222, 0x70222222,
					0x70EEEEEE });
		}
	}

	/**
	 * Initializes class data
	 * 
	 * @param context
	 *            the context
	 */
	private void initData(Context context) {
		scroller = new WheelViewScroller(getContext(), scrollingListener);
	}

	/**
	 * Invalidates wheel
	 * 
	 * @param clearCaches
	 *            if true then cached views will be clear
	 */
	public void invalidateWheel(boolean clearCaches) {
		if (clearCaches) {
			recycle.clearAll();
			if (itemsLayout != null) {
				itemsLayout.removeAllViews();
			}
			scrollingOffset = 0;
		} else if (itemsLayout != null) {
			// cache all items
			recycle.recycleItems(itemsLayout, firstItem, new WheelViewItemsRange());
		}

		invalidate();
	}

	/**
	 * Tests if wheel is cyclic. That means before the 1st item there is shown the last one
	 * 
	 * @return true if wheel is cyclic
	 */
	public boolean isCyclic() {
		return isCyclic;
	}

	/**
	 * Checks whether intem index is valid
	 * 
	 * @param index
	 *            the item index
	 * @return true if item index is not out of bounds or the wheel is cyclic
	 */
	private boolean isValidItemIndex(int index) {
		return viewAdapter != null && viewAdapter.getItemsCount() > 0
				&& (isCyclic || index >= 0 && index < viewAdapter.getItemsCount());
	}

	/**
	 * Sets layouts width and height
	 * 
	 * @param width
	 *            the layout width
	 * @param height
	 *            the layout height
	 */
	private void layout(int width, int height) {
		int itemsWidth = width - 2 * PADDING;

		itemsLayout.layout(0, 0, itemsWidth, height);
	}

	/**
	 * Notifies changing listeners
	 * 
	 * @param oldValue
	 *            the old wheel value
	 * @param newValue
	 *            the new wheel value
	 */
	protected void notifyChangingListeners(int oldValue, int newValue) {
		for (OnWheelChangedListener listener : changingListeners) {
			listener.onChanged(this, oldValue, newValue);
		}
	}

	/**
	 * Notifies listeners about clicking
	 */
	protected void notifyClickListenersAboutClick(int item) {
		for (OnWheelClickedListener listener : clickingListeners) {
			listener.onItemClicked(this, item);
		}
	}

	/**
	 * Notifies listeners about ending scrolling
	 */
	protected void notifyScrollingListenersAboutEnd() {
		for (OnWheelScrollListener listener : scrollingListeners) {
			listener.onScrollingFinished(this);
		}
	}

	/**
	 * Notifies listeners about starting scrolling
	 */
	protected void notifyScrollingListenersAboutStart() {
		for (OnWheelScrollListener listener : scrollingListeners) {
			listener.onScrollingStarted(this);
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		if (viewAdapter != null && viewAdapter.getItemsCount() > 0) {
			updateView();

			drawItems(canvas);
			if (canDrawSelector)
				drawSelector(canvas);
		}

		drawTopBottomDrawable(canvas);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		layout(r - l, b - t);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);

		buildViewForMeasuring();

		int width = calculateLayoutWidth(widthSize, widthMode);

		int height;
		if (heightMode == MeasureSpec.EXACTLY) {
			height = heightSize;
		} else {
			height = getDesiredHeight(itemsLayout);

			if (heightMode == MeasureSpec.AT_MOST) {
				height = Math.min(height, heightSize);
			}
		}

		setMeasuredDimension(width, height);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (!isEnabled() || getViewAdapter() == null) {
			return true;
		}

		switch (event.getAction()) {
		case MotionEvent.ACTION_MOVE:
			if (getParent() != null) {
				getParent().requestDisallowInterceptTouchEvent(true);
			}
			break;

		case MotionEvent.ACTION_UP:
			if (!isScrollingPerformed) {
				int distance = (int) event.getY() - getHeight() / 2;
				if (distance > 0) {
					distance += getItemHeight() / 2;
				} else {
					distance -= getItemHeight() / 2;
				}
				int items = distance / getItemHeight();
				if (items != 0 && isValidItemIndex(currentItem + items)) {
					notifyClickListenersAboutClick(currentItem + items);
				}
			}
			break;
		}

		return scroller.onTouchEvent(event);
	}

	/**
	 * Rebuilds wheel items if necessary. Caches all unused items.
	 * 
	 * @return true if items are rebuilt
	 */
	private boolean rebuildItems() {
		boolean updated = false;
		WheelViewItemsRange range = getItemsRange();
		if (itemsLayout != null) {
			int first = recycle.recycleItems(itemsLayout, firstItem, range);
			updated = firstItem != first;
			firstItem = first;
		} else {
			createItemsLayout();
			updated = true;
		}

		if (!updated) {
			updated = firstItem != range.getFirst() || itemsLayout.getChildCount() != range.getCount();
		}

		if (firstItem > range.getFirst() && firstItem <= range.getLast()) {
			for (int i = firstItem - 1; i >= range.getFirst(); i--) {
				if (!addViewItem(i, true)) {
					break;
				}
				firstItem = i;
			}
		} else {
			firstItem = range.getFirst();
		}

		int first = firstItem;
		for (int i = itemsLayout.getChildCount(); i < range.getCount(); i++) {
			if (!addViewItem(firstItem + i, false) && itemsLayout.getChildCount() == 0) {
				first++;
			}
		}
		firstItem = first;

		return updated;
	}

	/**
	 * Removes wheel changing listener
	 * 
	 * @param listener
	 *            the listener
	 */
	public void removeChangingListener(OnWheelChangedListener listener) {
		changingListeners.remove(listener);
	}

	/**
	 * Removes wheel clicking listener
	 * 
	 * @param listener
	 *            the listener
	 */
	public void removeClickingListener(OnWheelClickedListener listener) {
		clickingListeners.remove(listener);
	}

	/**
	 * Removes wheel scrolling listener
	 * 
	 * @param listener
	 *            the listener
	 */
	public void removeScrollingListener(OnWheelScrollListener listener) {
		scrollingListeners.remove(listener);
	}

	/**
	 * Scroll the wheel
	 * 
	 * @param itemsToSkip
	 *            items to scroll
	 * @param time
	 *            scrolling duration
	 */
	public void scroll(int itemsToScroll, int time) {
		int distance = itemsToScroll * getItemHeight() - scrollingOffset;
		scroller.scroll(distance, time);
	}

	/**
	 * Sets the current item w/o animation. Does nothing when index is wrong.
	 * 
	 * @param index
	 *            the item index
	 */
	public void setCurrentItem(int index) {
		setCurrentItem(index, false);
	}

	/**
	 * Sets the current item. Does nothing when index is wrong.
	 * 
	 * @param index
	 *            the item index
	 * @param animated
	 *            the animation flag
	 */
	public void setCurrentItem(int index, boolean animated) {
		if (viewAdapter == null || viewAdapter.getItemsCount() == 0) {
			return; // throw?
		}

		int itemCount = viewAdapter.getItemsCount();
		if (index < 0 || index >= itemCount) {
			if (isCyclic) {
				while (index < 0) {
					index += itemCount;
				}
				index %= itemCount;
			} else {
				return; // throw?
			}
		}
		if (index != currentItem) {
			if (animated) {
				int itemsToScroll = index - currentItem;
				if (isCyclic) {
					int scroll = itemCount + Math.min(index, currentItem) - Math.max(index, currentItem);
					if (scroll < Math.abs(itemsToScroll)) {
						itemsToScroll = itemsToScroll < 0 ? scroll : -scroll;
					}
				}
				scroll(itemsToScroll, 0);
			} else {
				scrollingOffset = 0;

				int old = currentItem;
				currentItem = index;

				notifyChangingListeners(old, currentItem);

				invalidate();
			}
		}
	}

	/**
	 * Set wheel cyclic flag
	 * 
	 * @param isCyclic
	 *            the flag to set
	 */
	public void setCyclic(boolean isCyclic) {
		this.isCyclic = isCyclic;
		invalidateWheel(false);
	}

	/**
	 * Set the the specified scrolling interpolator
	 * 
	 * @param interpolator
	 *            the interpolator
	 */
	public void setInterpolator(Interpolator interpolator) {
		scroller.setInterpolator(interpolator);
	}

	/**
	 * Sets view adapter. Usually new adapters contain different views, so it needs to rebuild view by calling
	 * measure().
	 * 
	 * @param viewAdapter
	 *            the view adapter
	 */
	public void setViewAdapter(WheelViewAdapter viewAdapter) {
		if (this.viewAdapter != null) {
			this.viewAdapter.unregisterDataSetObserver(dataObserver);
		}
		this.viewAdapter = viewAdapter;
		if (this.viewAdapter != null) {
			this.viewAdapter.registerDataSetObserver(dataObserver);
		}

		invalidateWheel(true);
	}

	/**
	 * Sets the desired count of visible items. Actual amount of visible items depends on wheel layout parameters. To
	 * apply changes and rebuild view call measure().
	 * 
	 * @param count
	 *            the desired count for visible items
	 */
	public void setVisibleItems(int count) {
		visibleItems = count;
	}

	/**
	 * Stops scrolling
	 */
	public void stopScrolling() {
		scroller.stopScrolling();
	}

	/**
	 * Updates view. Rebuilds items and label if necessary, recalculate items sizes.
	 */
	private void updateView() {
		if (rebuildItems()) {
			calculateLayoutWidth(getWidth(), MeasureSpec.EXACTLY);
			layout(getWidth(), getHeight());
		}
	}
}
