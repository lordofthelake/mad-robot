package com.madrobot.ui.widgets;

import com.madrobot.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;

/**
 * ListView that can be sorted using touch gestures.
 * 
 * <p>
 * <b>Important</b>
 * <ul>
 * <li>The adapter should be an instance of an array adapter.</li>
 * <li>List header and footer cannot be added for this ListView.</li>
 * </ul>
 * <table border="0" width="350" cellspacing="1" cellpadding="5">
 * <tr>
 * <th><b>Attribute</b></td>
 * <td width="50"><b>Type</b></td>
 * <td><b>Default</b></td>
 * <td><b>Description</b></td>
 * </tr>
 * 
 * <tr>
 * <td><code>normalHeight</code></td>
 * <td>dimension</td>
 * <td>65</td>
 * <td>Height of the view that can be dragged</td>
 * </tr>
 * 
 * <tr>
 * <td><code>expandedHeight</code></td>
 * <td>dimension</td>
 * <td>0</td>
 * <td>Expanded height</td>
 * </tr>
 * 
 * <tr>
 * <td><code>grabberView</code></td>
 * <td>reference to a view id</td>
 * <td>-1</td>
 * <td>Id of the view to be used for dragging the List Item. Eg:
 * <code> &lt;your_namespace>:grabberView="@id/myIcon"</code></td>
 * </tr>
 * 
 * <tr>
 * <td><code>draggingBackgroundDrawable</code></td>
 * <td>drawable</td>
 * <td>0xfffffff</td>
 * <td>Background to be set for a view that is being dragged for sorting</td>
 * </tr>
 * 
 * <tr>
 * <td><code>removeMode</code></td>
 * <td><code>none<br/>fling<br/>slide<br/>slideRight<br/>slideLeft<br/></code></td>
 * <td>none</td>
 * <td>The gesture which removes a list item in dragging mode.</td>
 * </tr>
 * 
 * </table>
 * </p>
 */
public class SortableListView extends ListView {
	/**
	 * Listener f
	 * 
	 * @author elton.stephen.kent
	 * 
	 */
	public interface DragListener {
		void onDragged(int from, int to);
	}

	/**
	 * A Drop listener informs when a item that needs to be sorted is dropped
	 * 
	 * @author elton.stephen.kent
	 * 
	 */
	public interface DropListener {
		/**
		 * Item dropped intimation
		 * 
		 * @param from
		 *            previous index of the item
		 * @param to
		 *            current index of the item
		 */
		void onDropped(int from, int to);
	}

	private class GestureListener extends GestureDetector.SimpleOnGestureListener {

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			if (mDragView != null) {
				if (velocityX > 1000) {
					Rect r = mTempRect;
					mDragView.getDrawingRect(r);
					if (e2.getX() > r.right * 2 / 3) {
						// fast fling right with release near the right edge of the screen
						stopDragging();
						if (mRemoveListener != null) {
							mRemoveListener.onRemoved(mFirstDragPos);
						}
						unExpandViews(true);
					}
				}
				// flinging while dragging should have no effect
				return true;
			}
			return false;
		}
	}

	/**
	 * A RemoveListener recieves notifications when an item is removed from the list
	 * 
	 * @author elton.stephen.kent
	 * 
	 */
	public interface RemoveListener {
		/**
		 * 
		 * @param which
		 *            index was removed.
		 */
		void onRemoved(int which);
	}

	/**
	 * Fling to remove an item from the list
	 */
	public static final int REMOVE_MODE_FLING = 0;
	/**
	 * Items cannote be removed from the list
	 */
	public static final int REMOVE_MODE_NONE = -1;
	/**
	 * Slide left to remove a list item
	 */
	public static final int REMOVE_MODE_SLIDE_LEFT = 2;
	/**
	 * Slide right to remove a list item
	 */
	public static final int REMOVE_MODE_SLIDE_RIGHT = 1;

	private Drawable dragBackgroundDrawable;// = 0x00000000;
	private int grabberId = -1;
	private int mCoordOffset; // the difference between screen coordinates and coordinates in this view
	private Bitmap mDragBitmap;
	private DragListener mDragListener;
	private int mDragPoint; // at what offset inside the item did the user grab it

	private int mDragPos; // which item is being dragged
	private ImageView mDragView;
	private DropListener mDropListener;
	private int mFirstDragPos; // where was the dragged item originally
	private GestureDetector mGestureDetector;
	private int mHeight;
	private int mItemHeightExpanded = -1;
	private int mItemHeightNormal = -1;
	private int mLowerBound;
	private RemoveListener mRemoveListener;
	private int mRemoveMode = -1;
	private Rect mTempRect = new Rect();

	private final int mTouchSlop;

	private int mUpperBound;

	private WindowManager mWindowManager;

	private WindowManager.LayoutParams mWindowParams;

	public SortableListView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public SortableListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
		mGestureDetector = new GestureDetector(getContext(), new GestureListener());

		if (attrs != null) {
			TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.SortableListView, 0, 0);

			mItemHeightNormal = a.getDimensionPixelSize(R.styleable.SortableListView_normalHeight, 64);
			mItemHeightExpanded = a.getDimensionPixelSize(R.styleable.SortableListView_expandedHeight, mItemHeightNormal);
			grabberId = a.getResourceId(R.styleable.SortableListView_grabberView, -1);
			dragBackgroundDrawable = a.getDrawable(R.styleable.SortableListView_draggingBackgroundDrawable);
			if (dragBackgroundDrawable == null) {
				dragBackgroundDrawable = new ColorDrawable(0x00000000);
			}

			mRemoveMode = a.getInt(R.styleable.SortableListView_removeMode, -1);
			a.recycle();
		}
	}

	@Override
	final public void addFooterView(View v) {
		if (mRemoveMode == REMOVE_MODE_SLIDE_LEFT || mRemoveMode == REMOVE_MODE_SLIDE_RIGHT) {
			throw new RuntimeException("Footers are not supported with TouchListView in conjunction with remove_mode");
		}
	}

	@Override
	final public void addFooterView(View v, Object data, boolean isSelectable) {
		if (mRemoveMode == REMOVE_MODE_SLIDE_LEFT || mRemoveMode == REMOVE_MODE_SLIDE_RIGHT) {
			throw new RuntimeException("Footers are not supported with TouchListView in conjunction with remove_mode");
		}
	}

	@Override
	final public void addHeaderView(View v) {
		throw new RuntimeException("Headers are not supported with TouchListView");
	}

	@Override
	final public void addHeaderView(View v, Object data, boolean isSelectable) {
		throw new RuntimeException("Headers are not supported with TouchListView");
	}

	private void adjustScrollBounds(int y) {
		if (y >= mHeight / 3) {
			mUpperBound = mHeight / 3;
		}
		if (y <= mHeight * 2 / 3) {
			mLowerBound = mHeight * 2 / 3;
		}
	}

	/*
	 * Adjust visibility and size to make it appear as though an item is being dragged around and other items are making
	 * room for it: If dropping the item would result in it still being in the same place, then make the dragged
	 * listitem's size normal, but make the item invisible. Otherwise, if the dragged listitem is still on screen, make
	 * it as small as possible and expand the item below the insert point. If the dragged item is not on screen, only
	 * expand the item below the current insertpoint.
	 */
	private void doExpansion() {
		int childnum = mDragPos - getFirstVisiblePosition();
		if (mDragPos > mFirstDragPos) {
			childnum++;
		}

		View first = getChildAt(mFirstDragPos - getFirstVisiblePosition());

		for (int i = 0;; i++) {
			View vv = getChildAt(i);
			if (vv == null) {
				break;
			}
			int height = mItemHeightNormal;
			int visibility = View.VISIBLE;
			if (vv.equals(first)) {
				// processing the item that is being dragged
				if (mDragPos == mFirstDragPos) {
					// hovering over the original location
					visibility = View.INVISIBLE;
				} else {
					// not hovering over it
					height = 1;
				}
			} else if (i == childnum) {
				if (mDragPos < getCount() - 1) {
					height = mItemHeightExpanded;
				}
			}

			if (isDraggableRow(vv)) {
				ViewGroup.LayoutParams params = vv.getLayoutParams();
				params.height = height;
				vv.setLayoutParams(params);
				vv.setVisibility(visibility);
			}
		}
		// Request re-layout since we changed the items layout
		// and not doing this would cause bogus hitbox calculation
		// in myPointToPosition
		layoutChildren();
	}

	private void dragView(int x, int y) {
		float alpha = 1.0f;
		int width = mDragView.getWidth();

		if (mRemoveMode == REMOVE_MODE_SLIDE_RIGHT) {
			if (x > width / 2) {
				alpha = ((float) (width - x)) / (width / 2);
			}
			mWindowParams.alpha = alpha;
		} else if (mRemoveMode == REMOVE_MODE_SLIDE_LEFT) {
			if (x < width / 2) {
				alpha = ((float) x) / (width / 2);
			}
			mWindowParams.alpha = alpha;
		}
		mWindowParams.y = y - mDragPoint + mCoordOffset;
		mWindowManager.updateViewLayout(mDragView, mWindowParams);
	}

	private int getItemForPosition(int y) {
		int adjustedy = y - mDragPoint - (mItemHeightNormal / 2);
		int pos = myPointToPosition(0, adjustedy);
		if (pos >= 0) {
			if (pos <= mFirstDragPos) {
				pos += 1;
			}
		} else if (adjustedy < 0) {
			pos = 0;
		}
		return pos;
	}

	protected boolean isDraggableRow(View view) {
		return (view.findViewById(grabberId) != null);
	}

	/*
	 * pointToPosition() doesn't consider invisible views, but we need to, so implement a slightly different version.
	 */
	private int myPointToPosition(int x, int y) {
		Rect frame = mTempRect;
		final int count = getChildCount();
		for (int i = count - 1; i >= 0; i--) {
			final View child = getChildAt(i);
			child.getHitRect(frame);
			if (frame.contains(x, y)) {
				return getFirstVisiblePosition() + i;
			}
		}
		return INVALID_POSITION;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		// if (mRemoveListener != null && mGestureDetector == null) {
		if (mRemoveMode == REMOVE_MODE_FLING) {
			mGestureDetector.onTouchEvent(ev);
		}
		// }
		// if (mDragListener != null || mDropListener != null) {
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			int x = (int) ev.getX();
			int y = (int) ev.getY();
			int itemnum = pointToPosition(x, y);
			if (itemnum == AdapterView.INVALID_POSITION) {
				break;
			}

			View item = getChildAt(itemnum - getFirstVisiblePosition());

			if (isDraggableRow(item)) {
				mDragPoint = y - item.getTop();
				mCoordOffset = ((int) ev.getRawY()) - y;
				View dragger = item.findViewById(grabberId);
				Rect r = mTempRect;
				// dragger.getDrawingRect(r);

				r.left = dragger.getLeft();
				r.right = dragger.getRight();
				r.top = dragger.getTop();
				r.bottom = dragger.getBottom();

				if ((r.left < x) && (x < r.right)) {
					item.setDrawingCacheEnabled(true);
					// Create a copy of the drawing cache so that it does not get recycled
					// by the framework when the list tries to clean up memory
					Bitmap bitmap = Bitmap.createBitmap(item.getDrawingCache());
					item.setDrawingCacheEnabled(false);

					Rect listBounds = new Rect();

					getGlobalVisibleRect(listBounds, null);

					startDragging(bitmap, listBounds.left, y);
					mDragPos = itemnum;
					mFirstDragPos = mDragPos;
					mHeight = getHeight();
					int touchSlop = mTouchSlop;
					mUpperBound = Math.min(y - touchSlop, mHeight / 3);
					mLowerBound = Math.max(y + touchSlop, mHeight * 2 / 3);
					return false;
				}

				mDragView = null;
			}

			break;
		}
		// }
		return super.onInterceptTouchEvent(ev);
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if (mGestureDetector != null) {
			mGestureDetector.onTouchEvent(ev);
		}
		if (mDragView != null) {
			int action = ev.getAction();
			switch (action) {
			case MotionEvent.ACTION_UP:
			case MotionEvent.ACTION_CANCEL:
				Rect r = mTempRect;
				mDragView.getDrawingRect(r);
				stopDragging();

				if (mRemoveMode == REMOVE_MODE_SLIDE_RIGHT && ev.getX() > r.left + (r.width() * 3 / 4)) {
					ArrayAdapter adapter = (ArrayAdapter) getAdapter();
					adapter.remove(adapter.getItem(mFirstDragPos));
					if (mRemoveListener != null) {
						mRemoveListener.onRemoved(mFirstDragPos);
					}
					unExpandViews(true);
				} else if (mRemoveMode == REMOVE_MODE_SLIDE_LEFT && ev.getX() < r.left + (r.width() / 4)) {
					ArrayAdapter adapter = (ArrayAdapter) getAdapter();
					adapter.remove(adapter.getItem(mFirstDragPos));
					if (mRemoveListener != null) {
						mRemoveListener.onRemoved(mFirstDragPos);
					}
					unExpandViews(true);
				} else {

					if (mDragPos >= 0 && mDragPos < getCount()) {
						ArrayAdapter adapter = (ArrayAdapter) getAdapter();
						Object toRemove = adapter.getItem(mFirstDragPos);
						adapter.remove(toRemove);
						adapter.insert(toRemove, mDragPos);
						if (mDropListener != null)
							mDropListener.onDropped(mFirstDragPos, mDragPos);
					}
					unExpandViews(false);
				}
				break;

			case MotionEvent.ACTION_DOWN:
			case MotionEvent.ACTION_MOVE:
				int x = (int) ev.getX();
				int y = (int) ev.getY();
				dragView(x, y);
				int itemnum = getItemForPosition(y);
				if (itemnum >= 0) {
					if (action == MotionEvent.ACTION_DOWN || itemnum != mDragPos) {
						if (mDragListener != null) {
							mDragListener.onDragged(mDragPos, itemnum);
						}
						mDragPos = itemnum;
						doExpansion();
					}
					int speed = 0;
					adjustScrollBounds(y);
					if (y > mLowerBound) {
						// scroll the list up a bit
						speed = y > (mHeight + mLowerBound) / 2 ? 16 : 4;
					} else if (y < mUpperBound) {
						// scroll the list down a bit
						speed = y < mUpperBound / 2 ? -16 : -4;
					}
					if (speed != 0) {
						int ref = pointToPosition(0, mHeight / 2);
						if (ref == AdapterView.INVALID_POSITION) {
							// we hit a divider or an invisible view, check somewhere else
							ref = pointToPosition(0, mHeight / 2 + getDividerHeight() + 64);
						}
						View v = getChildAt(ref - getFirstVisiblePosition());
						if (v != null) {
							int pos = v.getTop();
							setSelectionFromTop(ref, pos - speed);
						}
					}
				}
				break;
			}
			return true;
		}
		return super.onTouchEvent(ev);
	}

	/**
	 * The Adapter should be an instance of an ArrayAdapter.
	 * <p>
	 * Any other adapter will throw a runtime exception.
	 * </p>
	 */
	@Override
	public void setAdapter(ListAdapter adapter) {
		if (!(adapter instanceof ArrayAdapter)) {
			throw new RuntimeException("Adapter not an instance of ArrayAdapter");
		}
		super.setAdapter(adapter);
	}

	public void setDragListener(DragListener l) {
		mDragListener = l;
	}

	public void setDropListener(DropListener l) {
		mDropListener = l;
	}

	public void setRemoveListener(RemoveListener l) {
		mRemoveListener = l;
	}

	/**
	 * Set the gesture to remove an item from a list.
	 * 
	 * @param removeMode
	 *            Any of the predefined remove modes
	 * @see SortableListView#REMOVE_MODE_FLING
	 * @see SortableListView#REMOVE_MODE_NONE
	 * @see SortableListView#REMOVE_MODE_SLIDE_LEFT
	 * @see SortableListView#REMOVE_MODE_SLIDE_RIGHT
	 */
	public void setRemoveMode(int removeMode) {
		if (removeMode > 2 && removeMode < -1) {
			throw new IllegalArgumentException("Invalid remove mode specified");
		}
		mRemoveMode = removeMode;

	}

	private void startDragging(Bitmap bm, int x, int y) {
		stopDragging();

		mWindowParams = new WindowManager.LayoutParams();
		mWindowParams.gravity = Gravity.TOP | Gravity.LEFT;
		mWindowParams.x = x;
		mWindowParams.y = y - mDragPoint + mCoordOffset;

		mWindowParams.height = android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
		mWindowParams.width = android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
		mWindowParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
				| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
				| WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
		mWindowParams.format = PixelFormat.TRANSLUCENT;
		mWindowParams.windowAnimations = 0;

		ImageView v = new ImageView(getContext());
		// int backGroundColor = getContext().getResources().getColor(R.color.dragndrop_background);
		v.setBackgroundDrawable(dragBackgroundDrawable);
		v.setImageBitmap(bm);
		mDragBitmap = bm;

		mWindowManager = (WindowManager) getContext().getSystemService("window");
		mWindowManager.addView(v, mWindowParams);
		mDragView = v;
	}

	private void stopDragging() {
		if (mDragView != null) {
			WindowManager wm = (WindowManager) getContext().getSystemService("window");
			wm.removeView(mDragView);
			mDragView.setImageDrawable(null);
			mDragView = null;
		}
		if (mDragBitmap != null) {
			mDragBitmap.recycle();
			mDragBitmap = null;
		}
	}

	/*
	 * Restore size and visibility for all listitems
	 */
	private void unExpandViews(boolean deletion) {
		for (int i = 0;; i++) {
			View v = getChildAt(i);
			if (v == null) {
				if (deletion) {
					// HACK force update of mItemCount
					int position = getFirstVisiblePosition();
					int y = getChildAt(0).getTop();
					setAdapter(getAdapter());
					setSelectionFromTop(position, y);
					// end hack
				}
				layoutChildren(); // force children to be recreated where needed
				v = getChildAt(i);
				if (v == null) {
					break;
				}
			}

			if (isDraggableRow(v)) {
				ViewGroup.LayoutParams params = v.getLayoutParams();
				params.height = mItemHeightNormal;
				v.setLayoutParams(params);
				v.setVisibility(View.VISIBLE);
			}
		}
	}
}
