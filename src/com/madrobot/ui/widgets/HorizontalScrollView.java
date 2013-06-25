package com.madrobot.ui.widgets;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.Display;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.WindowManager;
import android.widget.Scroller;

import com.madrobot.R;

/**
 * A ViewGroup that arranges Views/ViewGroups horizontally.
 * <p>
 * <b>Styled Attributes</b>
 * <ul>
 * <li><b>pageWidth</b>(dip):</li> Width of each horizontal page in dip. If not specified, the value mentioned in the
 * layout_height is used.
 * <li><b>snapVelocity</b>(integer):The velocity at which a fling gesture will cause us to snap to the next page. The
 * default is 1000</li>
 * <li><b>screenAnimationDuration</b>(integer):Duration to animate each page.The default is 600ms.
 * </ul>
 * <br/>
 * <b>Example Usage:</b><br/>
 * 
 * <pre>
 * 			&lt;HorizontalScrollView  android:layout_width="fill_parent" android:layout_height="fill_parent" my_namespace:pageWidth="230dp">
 * 				<!--This is page1. Can be any View/viewgroup -->
 * 				&lt;LinearLayout android:id="@+id/page1 android:layout_width="fill_parent" android:layout_height="fill_parent">
 * 					.. contents of first page
 * 				&lt;/LinearLayout>
 * 				<!--This is page2. Can be any View/viewgroup -->
 * 				&lt;LinearLayout android:id="@+id/page2 android:layout_width="fill_parent" android:layout_height="fill_parent">
 * 					.. contents of second page
 * 				&lt;/LinearLayout>
 * 				<!--This is page3. Can be any View/viewgroup -->
 * 				&lt;LinearLayout android:id="@+id/page1 android:layout_width="fill_parent" android:layout_height="fill_parent">
 * 					.. contents of third page
 * 				&lt;/LinearLayout>
 * 			&lt;/HorizontalScrollView>
 * </pre>
 * 
 * </p>
 * 
 * @author elton.stephen.kent
 * 
 */
public class HorizontalScrollView extends ViewGroup {
	/**
	 * Implement to receive events on scroll position and page snaps.
	 */
	public static interface OnScrollListener {
		/**
		 * Receives the current scroll X value. This value will be adjusted to assume the left edge of the first page
		 * has a scroll position of 0. Note that values less than 0 and greater than the right edge of the last page are
		 * possible due to touch events scrolling beyond the edges.
		 * 
		 * @param scrollX
		 *            Scroll X value
		 */
		void onScroll(int scrollX);

		/**
		 * Invoked when scrolling is finished (settled on a page, centered).
		 * 
		 * @param currentPage
		 *            The current page
		 */
		void onViewScrollFinished(int currentPage);
	}

	

	private static final int INVALID_PAGE = -1;

	public static final int SPEC_UNDEFINED = -1;

	public static final String TAG = "HorizontalScrollView";
	private static final int TOUCH_STATE_HORIZONTAL_SCROLLING = 1;
	private final static int TOUCH_STATE_REST = 0;

	private final static int TOUCH_STATE_SCROLLING = 1;
	private static final int TOUCH_STATE_VERTICAL_SCROLLING = -1;

	private boolean mAllowLongPress;
	private int mCurrentPage;

	private boolean mFirstLayout = true;
	private float mLastMotionX;

	private float mLastMotionY;
	private int mLastSeenLayoutWidth = -1;

	private Set<OnScrollListener> mListeners = new HashSet<OnScrollListener>();
	private int mMaximumVelocity;

	private int mNextPage = INVALID_PAGE;

	private Scroller mScroller;

	private int mTouchSlop;

	private int mTouchState = TOUCH_STATE_REST;

	private VelocityTracker mVelocityTracker;

	private int pageAnimationDuration;

	private int pageWidthSpec, pageWidth;

	/**
	 * The velocity at which a fling gesture will cause us to snap to the next screen
	 */
	private int snapVelocity;

	/**
	 * Used to inflate the Workspace from XML.
	 * 
	 * @param context
	 *            The application's context.
	 * @param attrs
	 *            The attribtues set containing the Workspace's customization values.
	 */
	public HorizontalScrollView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	/**
	 * Used to inflate the Workspace from XML.
	 * 
	 * @param context
	 *            The application's context.
	 * @param attrs
	 *            The attribtues set containing the Workspace's customization values.
	 * @param defStyle
	 *            Unused.
	 */
	public HorizontalScrollView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.HorizontalScrollView);
		pageWidthSpec = a.getDimensionPixelSize(R.styleable.HorizontalScrollView_pageWidth, SPEC_UNDEFINED);
		snapVelocity = a.getInt(R.styleable.HorizontalScrollView_snapVelocity, 1000);
		pageAnimationDuration = a.getInt(R.styleable.HorizontalScrollView_pageAnimationDuration, 600);
		a.recycle();

		init();
	}

	@Override
	public void addFocusables(ArrayList<View> views, int direction) {
		getChildAt(mCurrentPage).addFocusables(views, direction);
		if (direction == View.FOCUS_LEFT) {
			if (mCurrentPage > 0) {
				getChildAt(mCurrentPage - 1).addFocusables(views, direction);
			}
		} else if (direction == View.FOCUS_RIGHT) {
			if (mCurrentPage < getChildCount() - 1) {
				getChildAt(mCurrentPage + 1).addFocusables(views, direction);
			}
		}
	}

	public void addOnScrollListener(OnScrollListener listener) {
		mListeners.add(listener);
	}

	/**
	 * @return True is long presses are still allowed for the current touch
	 */
	public boolean allowLongPress() {
		return mAllowLongPress;
	}

	private void checkStartScroll(float x, float y) {
		/*
		 * Locally do absolute value. mLastMotionX is set to the y value of the down event.
		 */
		final int xDiff = (int) Math.abs(x - mLastMotionX);
		final int yDiff = (int) Math.abs(y - mLastMotionY);

		boolean xMoved = xDiff > mTouchSlop;
		boolean yMoved = yDiff > mTouchSlop;

		if (xMoved || yMoved) {

			if (xMoved) {
				// Scroll if the user moved far enough along the X axis
				mTouchState = TOUCH_STATE_SCROLLING;
				enableChildrenCache();
			}
			// Either way, cancel any pending longpress
			if (mAllowLongPress) {
				mAllowLongPress = false;
				// Try canceling the long press. It could also have been scheduled
				// by a distant descendant, so use the mAllowLongPress flag to block
				// everything
				final View currentPage = getChildAt(mCurrentPage);
				currentPage.cancelLongPress();
			}
		}
	}

	private void clearChildrenCache() {
		setChildrenDrawnWithCacheEnabled(false);
	}

	@Override
	public void computeScroll() {
		if (mScroller.computeScrollOffset()) {
			scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
			postInvalidate();
			intimateScroll(mScroller.getCurrX(), "ComputeScroll");
		} else if (mNextPage != INVALID_PAGE) {
			mCurrentPage = mNextPage;
			mNextPage = INVALID_PAGE;
			clearChildrenCache();
			intimateNextPage(mCurrentPage, "ComputeScroll");
			// Notify observer about screen change
		}
	}

	@Override
	public boolean dispatchUnhandledMove(View focused, int direction) {
		if (direction == View.FOCUS_LEFT) {
			if (getCurrentPage() > 0) {
				snapToPage(getCurrentPage() - 1);
				return true;
			}
		} else if (direction == View.FOCUS_RIGHT) {
			if (getCurrentPage() < getChildCount() - 1) {
				snapToPage(getCurrentPage() + 1);
				return true;
			}
		}
		return super.dispatchUnhandledMove(focused, direction);
	}

	private void enableChildrenCache() {
		setChildrenDrawingCacheEnabled(true);
		setChildrenDrawnWithCacheEnabled(true);
	}

	/**
	 * Returns the index of the currently displayed page.
	 * 
	 * @return The zero based index of the currently displayed page.
	 */
	public int getCurrentPage() {
		return mCurrentPage;
	}

	/**
	 * Get the page at which the given view is present
	 * 
	 * @param v
	 * @return
	 */
	public int getPageForView(View v) {
		int result = -1;
		if (v != null) {
			ViewParent vp = v.getParent();
			int count = getChildCount();
			for (int i = 0; i < count; i++) {
				if (vp == getChildAt(i)) {
					return i;
				}
			}
		}
		return result;
	}

	/**
	 * Get the width of each page
	 * 
	 * @return
	 */
	public int getPageWidth() {
		return pageWidth;
	}

	/**
	 * Gets the value that getScrollX() should return if the specified page is the current page (and no other scrolling
	 * is occurring). Use this to pass a value to scrollTo(), for example.
	 * 
	 * @param whichPage
	 * @return
	 */
	private int getScrollXForPage(int whichPage) {
		return (whichPage * pageWidth) - pageWidthPadding();
	}

	/**
	 * Initializes various states for this workspace.
	 */
	private void init() {
		mScroller = new Scroller(getContext());
		mCurrentPage = 0;
		final ViewConfiguration configuration = ViewConfiguration.get(getContext());
		mTouchSlop = configuration.getScaledTouchSlop();
		mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();
	}

	private void intimateNextPage(int page, String from) {
		for (OnScrollListener mListener : mListeners) {
			mListener.onViewScrollFinished(page);
		}
	}

	private void intimateScroll(int scrollX, String from) {
		for (OnScrollListener mListener : mListeners) {
			mListener.onScroll(scrollX);
		}
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		// Log.d(TAG, "onInterceptTouchEvent::action=" + ev.getAction());

		/*
		 * This method JUST determines whether we want to intercept the motion. If we return true, onTouchEvent will be
		 * called and we do the actual scrolling there.
		 */

		/*
		 * Shortcut the most recurring case: the user is in the dragging state and he is moving his finger. We want to
		 * intercept this motion.
		 */
		final int action = ev.getAction();
		final float x = ev.getX();
		final float y = ev.getY();
		boolean intercept = false;

		switch (action) {
		case MotionEvent.ACTION_MOVE:
			/*
			 * If we're in a horizontal scroll event, take it (intercept further events). But if we're
			 * mid-vertical-scroll, don't even try; let the children deal with it. If we haven't found a scroll event
			 * yet, check for one.
			 */
			if (mTouchState == TOUCH_STATE_HORIZONTAL_SCROLLING) {
				/*
				 * We've already started a horizontal scroll; set intercept to true so we can take the remainder of all
				 * touch events in onTouchEvent.
				 */
				intercept = true;
			} else if (mTouchState == TOUCH_STATE_VERTICAL_SCROLLING) {
				// Let children handle the events for the duration of the scroll event.
				intercept = false;
			} else { // We haven't picked up a scroll event yet; check for one.

				/*
				 * If we detected a horizontal scroll event, start stealing touch events (mark as scrolling). Otherwise,
				 * see if we had a vertical scroll event -- if so, let the children handle it and don't look to
				 * intercept again until the motion is done.
				 */

				// final float x = ev.getX();
				final int xDiff = (int) Math.abs(x - mLastMotionX);
				boolean xMoved = xDiff > mTouchSlop;

				if (xMoved) {
					// Scroll if the user moved far enough along the X axis
					mTouchState = TOUCH_STATE_HORIZONTAL_SCROLLING;
					mLastMotionX = x;
				}

				// final float y = ev.getY();
				final int yDiff = (int) Math.abs(y - mLastMotionY);
				boolean yMoved = yDiff > mTouchSlop;

				if (yMoved) {
					mTouchState = TOUCH_STATE_VERTICAL_SCROLLING;
				}
			}
			break;
		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:
			// Release the drag.
			mTouchState = TOUCH_STATE_REST;
			break;
		case MotionEvent.ACTION_DOWN:
			/*
			 * No motion yet, but register the coordinates so we can check for intercept at the next MOVE event.
			 */
			mLastMotionY = ev.getY();
			mLastMotionX = ev.getX();
			break;
		default:
			break;
		}

		return intercept;
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		int childLeft = 0;

		final int count = getChildCount();
		for (int i = 0; i < count; i++) {
			final View child = getChildAt(i);
			if (child.getVisibility() != View.GONE) {
				final int childWidth = child.getMeasuredWidth();
				child.layout(childLeft, 0, childLeft + childWidth, child.getMeasuredHeight());
				childLeft += childWidth;
			}
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		final int width = MeasureSpec.getSize(widthMeasureSpec);
		pageWidth = pageWidthSpec == SPEC_UNDEFINED ? getMeasuredWidth() : pageWidthSpec;
		pageWidth = Math.min(pageWidth, getMeasuredWidth());

		final int count = getChildCount();
		for (int i = 0; i < count; i++) {
			getChildAt(i).measure(MeasureSpec.makeMeasureSpec(pageWidth, MeasureSpec.EXACTLY), heightMeasureSpec);
		}

		if (mFirstLayout) {
			scrollTo(getScrollXForPage(mCurrentPage), 0);
			mFirstLayout = false;
		} else if (width != mLastSeenLayoutWidth) { // Width has changed
			/*
			 * Recalculate the width and scroll to the right position to be sure we're in the right place in the event
			 * that we had a rotation that didn't result in an activity restart (code by aveyD). Without this you can
			 * end up between two pages after a rotation.
			 */
			Display display = ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE))
					.getDefaultDisplay();
			int displayWidth = display.getWidth();

			mNextPage = Math.max(0, Math.min(mCurrentPage, getChildCount() - 1));
			final int newX = mNextPage * displayWidth;
			final int delta = newX - getScrollX();
			mScroller.startScroll(getScrollX(), 0, delta, 0, 0);
		}

		mLastSeenLayoutWidth = width;
	}

	@Override
	protected boolean onRequestFocusInDescendants(int direction, Rect previouslyFocusedRect) {
		int focusablePage;
		if (mNextPage != INVALID_PAGE) {
			focusablePage = mNextPage;
		} else {
			focusablePage = mCurrentPage;
		}
		getChildAt(focusablePage).requestFocus(direction, previouslyFocusedRect);
		return false;
	}

	@Override
	protected void onRestoreInstanceState(Parcelable state) {
		SavedState savedState = (SavedState) state;
		super.onRestoreInstanceState(savedState.getSuperState());
		if (savedState.currentPage != INVALID_PAGE) {
			mCurrentPage = savedState.currentPage;
		}
	}

	@Override
	protected Parcelable onSaveInstanceState() {
		final SavedState state = new SavedState(super.onSaveInstanceState());
		state.currentPage = mCurrentPage;
		return state;
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if (mVelocityTracker == null) {
			mVelocityTracker = VelocityTracker.obtain();
		}
		mVelocityTracker.addMovement(ev);

		final int action = ev.getAction();
		final float x = ev.getX();
		final float y = ev.getY();

		switch (action) {
		case MotionEvent.ACTION_DOWN:

			/*
			 * If being flinged and user touches, stop the fling. isFinished will be false if being flinged.
			 */
			if (!mScroller.isFinished()) {
				mScroller.abortAnimation();
			}

			// Remember where the motion event started
			mLastMotionX = x;

			if (mScroller.isFinished()) {
				mTouchState = TOUCH_STATE_REST;
			} else {
				mTouchState = TOUCH_STATE_HORIZONTAL_SCROLLING;
			}
			break;
		case MotionEvent.ACTION_MOVE:
			final int xDiff = (int) Math.abs(x - mLastMotionX);
			boolean xMoved = xDiff > mTouchSlop;
			if (xMoved) {
				// Scroll if the user moved far enough along the X axis
				mTouchState = TOUCH_STATE_HORIZONTAL_SCROLLING;
			}

			if (mTouchState == TOUCH_STATE_REST) {
				checkStartScroll(x, y);
			}

			else if (mTouchState == TOUCH_STATE_HORIZONTAL_SCROLLING) {
				// Scroll to follow the motion event
				int deltaX = (int) (mLastMotionX - x);
				mLastMotionX = x;
				final int scrollX = getScrollX();

				if (deltaX < 0) {
					if (scrollX > 0) {
						scrollBy(Math.max(-scrollX, deltaX), 0);
					}
				}

				else if (getScrollX() < 0 || getScrollX() > getChildAt(getChildCount() - 1).getLeft()) {
					deltaX /= 2;
				}

				scrollBy(deltaX, 0);

			}

			break;
		case MotionEvent.ACTION_UP:
			if (mTouchState == TOUCH_STATE_HORIZONTAL_SCROLLING) {
				final VelocityTracker velocityTracker = mVelocityTracker;
				velocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
				int velocityX = (int) velocityTracker.getXVelocity();

				if (velocityX > snapVelocity && mCurrentPage > 0) {
					// Fling hard enough to move left
					snapToPage(mCurrentPage - 1);
				} else if (velocityX < -snapVelocity && mCurrentPage < getChildCount() - 1) {
					// Fling hard enough to move right
					snapToPage(mCurrentPage + 1);
				} else {
					snapToDestination();
				}

				if (mVelocityTracker != null) {
					mVelocityTracker.recycle();
					mVelocityTracker = null;
				}
			}
			mTouchState = TOUCH_STATE_REST;
			break;
		case MotionEvent.ACTION_CANCEL:
			mTouchState = TOUCH_STATE_REST;
		}

		return true;
	}

	private int pageWidthPadding() {
		return ((getMeasuredWidth() - pageWidth) / 2);
	}

	public void removeOnScrollListener(OnScrollListener listener) {
		mListeners.remove(listener);
	}

	@Override
	public boolean requestChildRectangleOnScreen(View child, Rect rectangle, boolean immediate) {
		int screen = indexOfChild(child);
		if (screen != mCurrentPage || !mScroller.isFinished()) {
			return true;
		}
		return false;
	}

	/**
	 * Sets the current page
	 * 
	 * @param currentPage
	 *            Zero based index of the pages in this layout
	 * @param animate
	 *            If true, the scrolling to the given page is animated
	 */
	public void setCurrentPage(int currentPage, boolean animate) {
		mCurrentPage = Math.max(0, Math.min(currentPage, getChildCount()));
		if (animate) {
			snapToPage(currentPage);
		} else {
			int scrollLength = getScrollXForPage(mCurrentPage);
			scrollTo(scrollLength, 0);
			invalidate();
			intimateScroll(scrollLength, "setCurrentPage");
		}

	}

	/**
	 * Set the width of each page
	 * 
	 * @param pageWidth
	 */
	public void setPageWidth(int pageWidth) {
		this.pageWidthSpec = pageWidth;
		requestLayout();
	}

	private void snapToDestination() {
		final int startX = getScrollXForPage(mCurrentPage);
		int whichPage = mCurrentPage;
		if (getScrollX() < startX - getWidth() / 8) {
			whichPage = Math.max(0, whichPage - 1);
		} else if (getScrollX() > startX + getWidth() / 8) {
			whichPage = Math.min(getChildCount() - 1, whichPage + 1);
		}

		snapToPage(whichPage);
	}

	private void snapToPage(int whichPage) {
		enableChildrenCache();

		boolean changingPages = whichPage != mCurrentPage;

		mNextPage = whichPage;

		View focusedChild = getFocusedChild();
		if (focusedChild != null && changingPages && focusedChild == getChildAt(mCurrentPage)) {
			focusedChild.clearFocus();
			getChildAt(mNextPage).requestFocus();
		}
		final int newX = getScrollXForPage(whichPage);
		final int delta = newX - getScrollX();
		mScroller.startScroll(getScrollX(), 0, delta, 0, pageAnimationDuration);
		invalidate();
	}

}
