package com.madrobot.ui.widgets;

import java.lang.reflect.Method;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import com.madrobot.R;

/**
 * ImageView that can zoomed with double tap, pinch and scroll gestures. Compatible with Android 2.1 onwards.
 * <p>
 * <table border="0" width="450" cellspacing="1">
 * <tr>
 * <th><b>Attribute</b></td>
 * <td><b>Type</b></td>
 * <td><b>Default</b></td>
 * <td><b>Description</b></td>
 * </tr>
 * <tr>
 * <td><code>maxZoomSteps</code></td>
 * <td>Integer</td>
 * <td>3</td>
 * <td>Number of steps the user can tap to zoom in/out</td>
 * </tr>
 * <tr>
 * <td><code>doubleTapToZoom</code></td>
 * <td>Boolean</td>
 * <td>true</td>
 * <td>Flag to enable zooming in/out when the ImageView is double tapped</td>
 * </tr>
 * <tr>
 * <td><code>minMaxZoom</code></td>
 * <td>float</td>
 * <td>2.0</td>
 * <td>Zoom ratio for small images</td>
 * </tr>
 * <tr>
 * <td><code>pinchToZoomMinDistance</code></td>
 * <td>float</td>
 * <td>1.0</td>
 * <td>Minimum distance for which the pinch zoom gesture should be recognised</td>
 * </tr>
 * 
 * <tr>
 * <td><code>angleTolerant</code></td>
 * <td>integer</td>
 * <td>50</td>
 * <td>Tolerance angle for pinch to zoom</td>
 * </tr>
 * </table>
 * </p>
 * 
 * @author elton.kent
 * 
 */
public class ZoomableImageView extends LinearLayout {

	private class GestureListener extends GestureDetector.SimpleOnGestureListener {
		// event when double tap occurs
		@Override
		public boolean onDoubleTap(MotionEvent e) {
			if (isImageValid() && doubleTapZooms) {
				doubleTapZoom(e.getX(), e.getY());
			}
			return true;
		}

		@Override
		public boolean onDown(MotionEvent e) {
			if (isImageValid()) {
				float posX = (e.getX() - mZoomInfo.screenStartX) * mZoomInfo.scaleWidth + mScrollRectX;
				float posY = (e.getY() - mZoomInfo.screenStartY) * mZoomInfo.scaleWidth + mScrollRectY;
				onImageClick(posX, posY);
			}
			return true;
		}

		// its important that small bitmal is loaded
		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
			if (isImageValid()) {
				scroll(distanceX, distanceY);
			}
			return true;
		}
	}

	private class ZoomInfo {
		public int currentHeight;
		public int currentWidth;
		// 100px and not 480px
		public int displayHeight;
		public int displayWidth; // for example if current zoom is 100px and layout width is 480px than screenWidth is
		public float scaleHeight;
		public float scaleWidth;
		public int screenStartX = 0;
									public int screenStartY = 0;
		public int zoomDisplayHeight;
		public int zoomDisplayWidth; // how much pixels of bitmap are drawn on screen
	}
	private static int distanceZoomMultiplier = 10;
	private int angleTolerant;
	private boolean doubleTapZooms;
	private int maxZoomSteps;
	private Bitmap mBitmap = null;

	private int mCurrentZoomInc = 1;
	private int mCurrentZoomStep = 1;
	private Rect mDstRect = new Rect();
	private Object[] mEmptyObjectArray = new Object[] {};
	private PointF mFirstFingerStartPoint;

	private GestureDetector mGestureDetector;
	private float minMaxZoom;
	private Object[] mInt1ObjectArray = new Object[] { 1 };

	private boolean mIsInChanging = false;
	private boolean mIsReflectionError = true;
	private boolean mIsTwoFinger = false;
	private int mMaxZoomWidth = 0;

	private Method mMethodGetX;

	private Method mMethodGetY;
	private Method mMethodPointerCount;
	private int mMinZoomWidth = 0;

	private Paint mPaint;
	private int mScrollRectX = 0; // current left location of scroll rect
	private int mScrollRectY = 0; // current top location of scroll rect
	private PointF mSecondFingerStartPoint;
	private Rect mSrcRect = new Rect();
	private ZoomInfo mZoomInfo = new ZoomInfo();

	private float pinchToZoomMinDistance; // must be greaterequal to 1

	public ZoomableImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initAttributes(context, attrs);
		// get 2.1+ methods by reflection
		try {
			Class<?> eventClass = MotionEvent.class;
			mMethodPointerCount = eventClass.getMethod("getPointerCount");
			mMethodGetX = eventClass.getMethod("getX", new Class[] { int.class });
			mMethodGetY = eventClass.getMethod("getY", new Class[] { int.class });
			mIsReflectionError = false;
		} catch (Exception e) {
			mIsReflectionError = true;
		}

		setOrientation(VERTICAL);
		// without those settings zoomed bitmap will be full of ugly squares
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setFilterBitmap(true);
		mPaint.setDither(true);

		// because we are overriding onDraw method
		this.setWillNotDraw(false);
		mGestureDetector = new GestureDetector(context, new GestureListener());
	}

	protected void calcSmoothZoom(int offset, ZoomInfo zoomInfo) {
		int newWidth = zoomInfo.currentWidth + offset;
		updateZoomInfo(newWidth, zoomInfo);

		// for now - reset double tap zoom variables to middle zoom step
		mCurrentZoomStep = (int) Math.ceil((double) maxZoomSteps / 2);
		mCurrentZoomInc = offset > 0 ? 1 : -1;
	}

	protected void calcStepZoom(int step, ZoomInfo zoomInfo) {
		if (step == 1) {
			updateZoomInfo(mMinZoomWidth, zoomInfo);
			return;
		} else if (step == maxZoomSteps) {
			updateZoomInfo(mMaxZoomWidth, zoomInfo);
			return;
		}
		float newWidth = (float) (mMaxZoomWidth - mMinZoomWidth) * (float) step / maxZoomSteps + mMinZoomWidth;
		updateZoomInfo(newWidth, zoomInfo);
	}

	protected void doubleTapZoom(float x, float y) {
		mCurrentZoomStep += mCurrentZoomInc;
		if (mCurrentZoomStep == maxZoomSteps || mCurrentZoomStep == 1) {
			mCurrentZoomInc = -mCurrentZoomInc;
		}

		zoomIt(false, mCurrentZoomStep, x, y);
	}

	// fix scroll x and y so user cant scroll outside the image
	protected void fixScrollXY() {
		if (mScrollRectX < 0) {
			mScrollRectX = 0;
		} else if (mScrollRectX + mZoomInfo.zoomDisplayWidth >= mBitmap.getWidth()) {
			mScrollRectX = mBitmap.getWidth() - mZoomInfo.zoomDisplayWidth;
		}

		if (mScrollRectY < 0) {
			mScrollRectY = 0;
		} else if (mScrollRectY + mZoomInfo.zoomDisplayHeight >= mBitmap.getHeight()) {
			mScrollRectY = mBitmap.getHeight() - mZoomInfo.zoomDisplayHeight;
		}
	}

	protected PointF getCenterVector(PointF v1, PointF v2) {
		PointF v = new PointF(v1.x + (v2.x - v1.x) / 2, v1.y + (v2.y - v1.y) / 2);
		return v;
	}

	protected PointF getDifVector(PointF v1, PointF v2) {
		return new PointF(v2.x - v1.x, v2.y - v1.y);
	}

	protected float getDistance(PointF a, PointF b) {
		double difx = a.x - b.x;
		double dify = a.y - b.y;
		double value = Math.sqrt(difx * difx + dify * dify);
		return (float) value;
	}

	protected float getVectorAngle(PointF v) {
		float norm = getVectorNorm(v);
		if (v.y == 0) {
			return v.x >= 0 ? 0 : -180;
		}

		double angle = Math.asin(Math.abs(v.y) / norm) * 180 / Math.PI;

		if (v.y < 0) {
			if (v.x > 0) {
				angle = 270 + angle;
			} else {
				angle = 180 + angle;
			}
		} else if (v.x < 0) {
			angle = 90 + angle;
		}

		return (float) angle;
	}

	protected float getVectorNorm(PointF v) {
		double value = Math.sqrt(v.x * v.x + v.y * v.y);
		return (float) value;
	}

	private void initAttributes(Context context, AttributeSet attrs) {
		TypedArray styledAttrs = context.obtainStyledAttributes(attrs, R.styleable.ZoomableImageView);
		maxZoomSteps = styledAttrs.getInt(R.styleable.ZoomableImageView_maxZoomSteps, 3);
		// workaround because maxZoomSteps is 0 based.
		maxZoomSteps++;
		doubleTapZooms = styledAttrs.getBoolean(R.styleable.ZoomableImageView_doubleTapToZoom, true);
		minMaxZoom = styledAttrs.getFloat(R.styleable.ZoomableImageView_minMaxZoom, 2.0f);
		pinchToZoomMinDistance = styledAttrs.getFloat(R.styleable.ZoomableImageView_pinchToZoomMinDistance, 1);
		angleTolerant = styledAttrs.getInt(R.styleable.ZoomableImageView_angleTolerant, 50);

	}

	protected boolean isImageValid() {
		return mBitmap != null && !mIsInChanging && getWidth() > 0;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		// bitmap not exist or component didnt recieved width yet
		if (mBitmap != null && this.getWidth() > 0) {
			mDstRect.set(mZoomInfo.screenStartX, mZoomInfo.screenStartY, mZoomInfo.displayWidth
					+ mZoomInfo.screenStartX, mZoomInfo.displayHeight + mZoomInfo.screenStartY);
			mSrcRect.set(mScrollRectX, mScrollRectY, mScrollRectX + mZoomInfo.zoomDisplayWidth, mScrollRectY
					+ mZoomInfo.zoomDisplayHeight);
			canvas.drawBitmap(mBitmap, mSrcRect, mDstRect, mPaint);
		}
	}

	// ment to be overriden - add some hotspots or simular
	protected void onImageClick(float posX, float posY) {
	}

	/**
	 * Overridden to restore instance state when device orientation changes. This method is called automatically if you
	 * assign an id to the RangeSeekBar widget using the {@link #setId(int)} method.
	 */
	@Override
	protected void onRestoreInstanceState(Parcelable parcel) {
		Bundle bundle = (Bundle) parcel;
		super.onRestoreInstanceState(bundle.getParcelable("SUPER"));
		mScrollRectX = bundle.getInt("SCROLLX");
		mScrollRectY = bundle.getInt("SCROLLY");
		mZoomInfo.currentHeight = bundle.getInt("ZI_CH");
		mZoomInfo.currentWidth = bundle.getInt("ZI_CW");
		mZoomInfo.displayHeight = bundle.getInt("ZI_DH");
		mZoomInfo.displayWidth = bundle.getInt("ZI_DW");
		mZoomInfo.scaleHeight = bundle.getFloat("ZI_SH");
		mZoomInfo.scaleWidth = bundle.getFloat("ZI_SW");
		mZoomInfo.screenStartX = bundle.getInt("ZI_SSX");
		mZoomInfo.screenStartY = bundle.getInt("ZI_SSY");
		mZoomInfo.zoomDisplayHeight = bundle.getInt("ZI_ZDH");
		mZoomInfo.zoomDisplayWidth = bundle.getInt("ZI_ZDW");

	}

	/**
	 * Overridden to save instance state when device orientation changes. This method is called automatically if you
	 * assign an id to the RangeSeekBar widget using the {@link #setId(int)} method. Other members of this class than
	 * the normalized min and max values don't need to be saved.
	 */
	@Override
	protected Parcelable onSaveInstanceState() {
		Bundle bundle = new Bundle();
		bundle.putParcelable("SUPER", super.onSaveInstanceState());
		bundle.putInt("SCROLLX", mScrollRectX);
		bundle.putInt("SCROLLY", mScrollRectY);
		bundle.putInt("ZI_CH", mZoomInfo.currentHeight);
		bundle.putInt("ZI_CW", mZoomInfo.currentWidth);
		bundle.putInt("ZI_DH", mZoomInfo.displayHeight);
		bundle.putInt("ZI_DW", mZoomInfo.displayWidth);
		bundle.putInt("ZI_SSX", mZoomInfo.screenStartX);
		bundle.putInt("ZI_SSY", mZoomInfo.screenStartY);
		bundle.putInt("ZI_ZDH", mZoomInfo.zoomDisplayHeight);
		bundle.putInt("ZI_ZDW", mZoomInfo.zoomDisplayWidth);
		bundle.putFloat("ZI_SH", mZoomInfo.scaleHeight);
		bundle.putFloat("ZI_SW", mZoomInfo.scaleWidth);
		return bundle;
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		if (mBitmap != null) {
			setImage(mBitmap, w);
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (mIsReflectionError) {
			return mGestureDetector.onTouchEvent(event);
		}

		boolean rv = true;
		// if (event.getPointerCount() == 2) // two fingers are active
		try {
			if ((Integer) mMethodPointerCount.invoke(event, mEmptyObjectArray) == 2) {
				PointF newFirstFingerPosition = new PointF(event.getX(), event.getY());
				// PointF newSecondFingerPosition = new PointF(event.getX(1), event.getY(1));
				PointF newSecondFingerPosition = new PointF((Float) mMethodGetX.invoke(event, mInt1ObjectArray),
						(Float) mMethodGetY.invoke(event, mInt1ObjectArray));

				if (mIsTwoFinger) {
					zoomIfPinch(newFirstFingerPosition, newSecondFingerPosition);
				} else {
					mFirstFingerStartPoint = newFirstFingerPosition;
					mSecondFingerStartPoint = newSecondFingerPosition;
					mIsTwoFinger = true;
				}
			} else {
				mIsTwoFinger = false;
				rv = mGestureDetector.onTouchEvent(event);
			}
		} catch (Exception e) {
			rv = mGestureDetector.onTouchEvent(event);
		}

		return rv;
	}

	protected void scroll(float distanceX, float distanceY) {
		mScrollRectX = mScrollRectX + (int) (distanceX * mZoomInfo.scaleWidth);
		mScrollRectY = mScrollRectY + (int) (distanceY * mZoomInfo.scaleHeight);
		fixScrollXY();
		ZoomableImageView.this.invalidate(); // force a redraw
	}

	public void setImage(Bitmap bitmap) {
		setImage(bitmap, this.getWidth());
	}

	// must call after layout is loaded and measured
	/**
	 * Set the bitmap to be used for this ImageView. Any previously set bitmap is recycled.
	 * 
	 * @param bitmap
	 * @param widthOfParent
	 */
	public void setImage(Bitmap bitmap, int widthOfParent) {
		// if(mBitmap!=null){
		// bitmap.recycle();
		// }
		mIsInChanging = true;
		mBitmap = bitmap;
		mMinZoomWidth = widthOfParent <= bitmap.getWidth() ? widthOfParent : bitmap.getWidth(); // try to fit to width
																								// if greater than width
		// if image to small make max zoom minMaxZoom(1.5) times larger than bitmap width
		mMaxZoomWidth = minMaxZoom * mMinZoomWidth > bitmap.getWidth() ? (int) (minMaxZoom * mMinZoomWidth) : bitmap
				.getWidth();
		mIsTwoFinger = false;
		mCurrentZoomStep = 1;
		mCurrentZoomInc = 1;
		mScrollRectY = mScrollRectX = 0;
		calcStepZoom(mCurrentZoomStep, mZoomInfo);
		setVisibility(true);
		ZoomableImageView.this.invalidate();
		mIsInChanging = false;
	}

	public void setVisibility(boolean isVisible) {
		this.setVisibility(isVisible ? View.VISIBLE : View.GONE);
	}

	protected void updateZoomInfo(float newWidth, ZoomInfo zoomInfo) {
		this.mIsInChanging = true;
		// fix maximal width
		if (newWidth < mMinZoomWidth) {
			newWidth = mMinZoomWidth;
		} else if (newWidth > mMaxZoomWidth) {
			newWidth = mMaxZoomWidth;
		}

		// calculate new height
		float newHeight = (float) mBitmap.getHeight() / mBitmap.getWidth() * newWidth;

		// width/height of current zoomed image
		zoomInfo.currentWidth = (int) newWidth;
		zoomInfo.currentHeight = (int) newHeight;
		// width/height of current "display"
		zoomInfo.displayWidth = this.getWidth() <= zoomInfo.currentWidth ? this.getWidth() : zoomInfo.currentWidth;
		zoomInfo.displayHeight = this.getHeight() <= zoomInfo.currentHeight ? this.getHeight() : zoomInfo.currentHeight;
		// ration of real image size and current image size
		zoomInfo.scaleWidth = (float) mBitmap.getWidth() / zoomInfo.currentWidth;
		zoomInfo.scaleHeight = (float) mBitmap.getHeight() / zoomInfo.currentHeight;
		// how much virtual bitmap pixels are shown on display
		zoomInfo.zoomDisplayWidth = (int) (zoomInfo.scaleWidth * zoomInfo.displayWidth);
		zoomInfo.zoomDisplayHeight = (int) (zoomInfo.scaleHeight * zoomInfo.displayHeight);

		// calculate start positions of drawing - if zoom to small for current
		zoomInfo.screenStartX = this.getWidth() <= zoomInfo.displayWidth ? 0
				: (int) (0.5f * (this.getWidth() - zoomInfo.displayWidth));
		zoomInfo.screenStartY = this.getHeight() <= zoomInfo.displayHeight ? 0
				: (int) (0.5f * (this.getHeight() - zoomInfo.displayHeight));
		this.mIsInChanging = false;
	}

	protected boolean zoomIfPinch(PointF newFirst, PointF newSecond) {

		PointF firstFingerDiff = getDifVector(mFirstFingerStartPoint, newFirst);
		PointF secondFingerDiff = getDifVector(mSecondFingerStartPoint, newSecond);
		float firstFingerDistance = getVectorNorm(firstFingerDiff);
		float secondFingerDistance = getVectorNorm(secondFingerDiff);
		int distance = (int) (firstFingerDistance + secondFingerDistance);

		if (distance < pinchToZoomMinDistance) {
			return false;
		}

		float angleDiff = Math.abs(getVectorAngle(firstFingerDiff) - getVectorAngle(secondFingerDiff));

		// if one finger didnt move at all we have pinch zoom also
		if ((angleDiff < 180 - angleTolerant || angleDiff > 180 + angleTolerant) && firstFingerDistance > 0.0f
				&& secondFingerDistance > 0.0f) {
			return false;
		}

		// point beetween two fingers at start - it will be center of new scroll position
		PointF center = getCenterVector(mFirstFingerStartPoint, mSecondFingerStartPoint);
		// difference from start and end points
		float startDiff = getDistance(mFirstFingerStartPoint, mSecondFingerStartPoint);
		float endDiff = getDistance(newFirst, newSecond);

		if (startDiff < endDiff) {
			// zooom in
			zoomIt(true, distance * distanceZoomMultiplier, center.x, center.y);
		} else {
			// zoom out
			zoomIt(true, -distance * distanceZoomMultiplier, center.x, center.y);
		}

		mFirstFingerStartPoint = newFirst;
		mSecondFingerStartPoint = newSecond;
		return true;
	}

	// how i fish java has delegates... i dont want to make class of everything
	// offsetOrZoomStep is offset if smooth or currentZoomStep otherwise
	protected void zoomIt(boolean isSmooth, int offsetOrZoomStep, float x, float y) {
		x -= mZoomInfo.screenStartX;
		y -= mZoomInfo.screenStartY;

		float posX = mScrollRectX + (x * mZoomInfo.scaleWidth);
		float posY = mScrollRectY + (y * mZoomInfo.scaleHeight);

		if (isSmooth) {
			calcSmoothZoom(offsetOrZoomStep, mZoomInfo); // new zoom data
		} else {
			calcStepZoom(offsetOrZoomStep, mZoomInfo); // new zoom data
		}

		mScrollRectX = (int) (posX - x * mZoomInfo.scaleWidth);
		mScrollRectY = (int) (posY - y * mZoomInfo.scaleHeight);
		fixScrollXY();

		ZoomableImageView.this.invalidate();
	}
}