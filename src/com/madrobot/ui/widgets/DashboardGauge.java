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
package com.madrobot.ui.widgets;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LightingColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;

public final class DashboardGauge extends CustomWidget {

	private static final int centerDegree = 40; // the one in the top center (12


	// scale configuration
	private static final int totalNicks = 100;
	private static final float degreesPerNick = 360.0f / totalNicks;
	private static final int maxDegrees = 110;
	// o'clock)
	private static final int minDegrees = -30;

	private static final String TAG = DashboardGauge.class.getSimpleName();
	private Bitmap background; // holds the cached static part
	private Paint backgroundPaint;
	// end drawing tools

	private Paint facePaint;
	private RectF faceRect;

	private Bitmap faceTexture;
	private float handAcceleration = 0.0f;

	// hand dynamics -- all are angular expressed in F degrees
	private boolean handInitialized = false;
	private Paint handPaint;
	private Path handPath;
	private float handPosition = centerDegree;

	private Paint handScrewPaint;
	private float handTarget = centerDegree;
	private float handVelocity = 0.0f;

	private long lastHandMoveTime = -1L;

	private Bitmap logo;

	private Matrix logoMatrix;
	private Paint logoPaint;
	private float logoScale;
	private Paint rimCirclePaint;
	private Paint rimPaint;

	// drawing tools
	private RectF rimRect;
	private Paint rimShadowPaint;
	private Paint scalePaint;
	private RectF scaleRect;
	private String title = "";
	private Paint titlePaint;

	private Path titlePath;

	public DashboardGauge(Context context) {
		super(context);
	}

	public DashboardGauge(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public DashboardGauge(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	private int chooseDimension(int mode, int size) {
		if(mode == MeasureSpec.AT_MOST || mode == MeasureSpec.EXACTLY){
			return size;
		} else{ // (mode == MeasureSpec.UNSPECIFIED)
			return getPreferredSize();
		}
	}

	

	private float degreeToAngle(float degree) {
		return (degree - centerDegree) / 2.0f * degreesPerNick;
	}

	private void drawBackground(Canvas canvas) {
		if(background == null){
			Log.w(TAG, "Background not created");
		} else{
			canvas.drawBitmap(background, 0, 0, backgroundPaint);
		}
	}

	private void drawFace(Canvas canvas) {
		canvas.drawOval(faceRect, facePaint);
		// draw the inner rim circle
		canvas.drawOval(faceRect, rimCirclePaint);
		// draw the rim shadow inside the face
		canvas.drawOval(faceRect, rimShadowPaint);
	}

	

	private void drawHand(Canvas canvas) {
		if(handInitialized){
			float handAngle = degreeToAngle(handPosition);
			canvas.save(Canvas.MATRIX_SAVE_FLAG);
			canvas.rotate(handAngle, 0.5f, 0.5f);
			canvas.drawPath(handPath, handPaint);
			canvas.restore();

			canvas.drawCircle(0.5f, 0.5f, 0.01f, handScrewPaint);
		}
	}

	private void drawLogo(Canvas canvas) {
		canvas.save(Canvas.MATRIX_SAVE_FLAG);
		canvas.translate(0.5f - logo.getWidth() * logoScale / 2.0f, 0.5f - logo.getHeight() * logoScale
				/ 2.0f);

		int color = 0x00000000;
		float position = getRelativeTemperaturePosition();
		if(position < 0){
			color |= (int) ((0xf0) * -position); // blue
		} else{
			color |= ((int) ((0xf0) * position)) << 16; // red
		}
		// Log.d(TAG, "*** " + Integer.toHexString(color));
		LightingColorFilter logoFilter = new LightingColorFilter(0xff008822, color);
		logoPaint.setColorFilter(logoFilter);
		if(logo != null)
			canvas.drawBitmap(logo, logoMatrix, logoPaint);
		canvas.restore();
	}

	private void drawRim(Canvas canvas) {
		// first, draw the metallic body
		canvas.drawOval(rimRect, rimPaint);
		// now the outer rim circle
		canvas.drawOval(rimRect, rimCirclePaint);
	}

	private void drawScale(Canvas canvas) {
		canvas.drawOval(scaleRect, scalePaint);
		canvas.save(Canvas.MATRIX_SAVE_FLAG);
		for(int i = 0; i < totalNicks; ++i){
			float y1 = scaleRect.top;
			float y2 = y1 - 0.020f;

			canvas.drawLine(0.5f, y1, 0.5f, y2, scalePaint);

			if(i % 5 == 0){
				int value = nickToDegree(i);

				if(value >= minDegrees && value <= maxDegrees){
					String valueString = Integer.toString(value);
					canvas.drawText(valueString, 0.5f, y2 - 0.015f, scalePaint);
				}
			}
			canvas.rotate(degreesPerNick, 0.5f, 0.5f);
		}
		canvas.restore();
	}

	private void drawTitle(Canvas canvas) {
		String title = getTitle();
		canvas.drawTextOnPath(title, titlePath, 0.0f, 0.0f, titlePaint);
	}

	// in case there is no size specified
	private int getPreferredSize() {
		return 300;
	}

	private float getRelativeTemperaturePosition() {
		if(handPosition < centerDegree){
			return -(centerDegree - handPosition) / (centerDegree - minDegrees);
		} else{
			return (handPosition - centerDegree) / (maxDegrees - centerDegree);
		}
	}

	public String getTitle() {
		return title;
	}

	private boolean handNeedsToMove() {
		return Math.abs(handPosition - handTarget) > 0.01f;
	}

	private void moveHand() {
		if(!handNeedsToMove()){
			return;
		}

		if(lastHandMoveTime != -1L){
			long currentTime = System.currentTimeMillis();
			float delta = (currentTime - lastHandMoveTime) / 1000.0f;

			float direction = Math.signum(handVelocity);
			if(Math.abs(handVelocity) < 90.0f){
				handAcceleration = 5.0f * (handTarget - handPosition);
			} else{
				handAcceleration = 0.0f;
			}
			handPosition += handVelocity * delta;
			handVelocity += handAcceleration * delta;
			if((handTarget - handPosition) * direction < 0.01f * direction){
				handPosition = handTarget;
				handVelocity = 0.0f;
				handAcceleration = 0.0f;
				lastHandMoveTime = -1L;
			} else{
				lastHandMoveTime = System.currentTimeMillis();
			}
			invalidate();
		} else{
			lastHandMoveTime = System.currentTimeMillis();
			moveHand();
		}
	}

	private int nickToDegree(int nick) {
		int rawDegree = ((nick < totalNicks / 2) ? nick : (nick - totalNicks)) * 2;
		int shiftedDegree = rawDegree + centerDegree;
		return shiftedDegree;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		drawBackground(canvas);

		float scale = getWidth();
		canvas.save(Canvas.MATRIX_SAVE_FLAG);
		canvas.scale(scale, scale);

		drawLogo(canvas);
		drawHand(canvas);

		canvas.restore();

		if(handNeedsToMove()){
			moveHand();
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		Log.d(TAG, "Width spec: " + MeasureSpec.toString(widthMeasureSpec));
		Log.d(TAG, "Height spec: " + MeasureSpec.toString(heightMeasureSpec));

		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);

		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);

		int chosenWidth = chooseDimension(widthMode, widthSize);
		int chosenHeight = chooseDimension(heightMode, heightSize);

		int chosenDimension = Math.min(chosenWidth, chosenHeight);

		setMeasuredDimension(chosenDimension, chosenDimension);
	}

	@Override
	protected void onRestoreInstanceState(Parcelable state) {
		Bundle bundle = (Bundle) state;
		Parcelable superState = bundle.getParcelable("superState");
		super.onRestoreInstanceState(superState);

		handInitialized = bundle.getBoolean("handInitialized");
		handPosition = bundle.getFloat("handPosition");
		handTarget = bundle.getFloat("handTarget");
		handVelocity = bundle.getFloat("handVelocity");
		handAcceleration = bundle.getFloat("handAcceleration");
		lastHandMoveTime = bundle.getLong("lastHandMoveTime");
	}

	@Override
	protected Parcelable onSaveInstanceState() {
		Parcelable superState = super.onSaveInstanceState();

		Bundle state = new Bundle();
		state.putParcelable("superState", superState);
		state.putBoolean("handInitialized", handInitialized);
		state.putFloat("handPosition", handPosition);
		state.putFloat("handTarget", handTarget);
		state.putFloat("handVelocity", handVelocity);
		state.putFloat("handAcceleration", handAcceleration);
		state.putLong("lastHandMoveTime", lastHandMoveTime);
		return state;
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		Log.d(TAG, "Size changed to " + w + "x" + h);

		regenerateBackground();
	}

	private void regenerateBackground() {
		// free the old bitmap
		if(background != null){
			background.recycle();
		}

		background = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
		Canvas backgroundCanvas = new Canvas(background);
		float scale = getWidth();
		backgroundCanvas.scale(scale, scale);

		drawRim(backgroundCanvas);
		drawFace(backgroundCanvas);
		drawScale(backgroundCanvas);
		drawTitle(backgroundCanvas);
	}

	public void setHandTarget(float value) {
		if(value < minDegrees){
			value = minDegrees;
		} else if(value > maxDegrees){
			value = maxDegrees;
		}
		handTarget = value;
		handInitialized = true;
		invalidate();
	}

	public void setScaleLogo(Bitmap logo) {
		this.logo = logo;
		logoMatrix = new Matrix();
		logoScale = (1.0f / logo.getWidth()) * 0.3f;
		logoMatrix.setScale(logoScale, logoScale);
		invalidate();
	}

	public void setScaleTexture(Bitmap texture) {
		this.faceTexture = texture;
		BitmapShader paperShader = new BitmapShader(faceTexture, Shader.TileMode.MIRROR,
				Shader.TileMode.MIRROR);
		Matrix paperMatrix = new Matrix();
		paperMatrix.setScale(1.0f / faceTexture.getWidth(), 1.0f / faceTexture.getHeight());
		paperShader.setLocalMatrix(paperMatrix);
		facePaint.setFilterBitmap(true);
		facePaint.setShader(paperShader);
		invalidate();
	}

	public void setTitle(String title) {
		this.title = title;
		invalidate();
	}

	@Override
	protected void setupDrawingTools() {
		rimRect = new RectF(0.1f, 0.1f, 0.9f, 0.9f);

		// the linear gradient is a bit skewed for realism
		rimPaint = new Paint();
		rimPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
		rimPaint.setShader(new LinearGradient(0.40f, 0.0f, 0.60f, 1.0f, Color.rgb(0xf0, 0xf5, 0xf0), Color
				.rgb(0x30, 0x31, 0x30), Shader.TileMode.CLAMP));

		rimCirclePaint = new Paint();
		rimCirclePaint.setAntiAlias(true);
		rimCirclePaint.setStyle(Paint.Style.STROKE);
		rimCirclePaint.setColor(Color.argb(0x4f, 0x33, 0x36, 0x33));
		rimCirclePaint.setStrokeWidth(0.005f);

		float rimSize = 0.02f;
		faceRect = new RectF();
		faceRect.set(rimRect.left + rimSize, rimRect.top + rimSize, rimRect.right - rimSize, rimRect.bottom
				- rimSize);


		facePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		facePaint.setStyle(Paint.Style.FILL);
		
		rimShadowPaint = new Paint();
		rimShadowPaint.setShader(new RadialGradient(0.5f, 0.5f, faceRect.width() / 2.0f, new int[] {
				0x00000000, 0x00000500, 0x50000500 }, new float[] { 0.96f, 0.96f, 0.99f },
				Shader.TileMode.MIRROR));
		rimShadowPaint.setStyle(Paint.Style.FILL);

		scalePaint = new Paint();
		scalePaint.setStyle(Paint.Style.STROKE);
		scalePaint.setColor(0x9f004d0f);
		scalePaint.setStrokeWidth(0.005f);
		scalePaint.setAntiAlias(true);

		scalePaint.setTextSize(0.045f);
		scalePaint.setTypeface(Typeface.SANS_SERIF);
		scalePaint.setTextScaleX(0.8f);
		scalePaint.setTextAlign(Paint.Align.CENTER);

		float scalePosition = 0.10f;
		scaleRect = new RectF();
		scaleRect.set(faceRect.left + scalePosition, faceRect.top + scalePosition, faceRect.right
				- scalePosition, faceRect.bottom - scalePosition);

		titlePaint = new Paint();
		titlePaint.setColor(0xaf946109);
		titlePaint.setAntiAlias(true);
		titlePaint.setTypeface(Typeface.DEFAULT_BOLD);
		titlePaint.setTextAlign(Paint.Align.CENTER);
		titlePaint.setTextSize(0.05f);
		titlePaint.setTextScaleX(0.8f);

		titlePath = new Path();
		titlePath.addArc(new RectF(0.24f, 0.24f, 0.76f, 0.76f), -180.0f, -180.0f);

		logoPaint = new Paint();
		logoPaint.setFilterBitmap(true);

		handPaint = new Paint();
		handPaint.setAntiAlias(true);
		handPaint.setColor(0xff392f2c);
		handPaint.setShadowLayer(0.01f, -0.005f, -0.005f, 0x7f000000);
		handPaint.setStyle(Paint.Style.FILL);

		handPath = new Path();
		handPath.moveTo(0.5f, 0.5f + 0.2f);
		handPath.lineTo(0.5f - 0.010f, 0.5f + 0.2f - 0.007f);
		handPath.lineTo(0.5f - 0.002f, 0.5f - 0.32f);
		handPath.lineTo(0.5f + 0.002f, 0.5f - 0.32f);
		handPath.lineTo(0.5f + 0.010f, 0.5f + 0.2f - 0.007f);
		handPath.lineTo(0.5f, 0.5f + 0.2f);
		handPath.addCircle(0.5f, 0.5f, 0.025f, Path.Direction.CW);

		handScrewPaint = new Paint();
		handScrewPaint.setAntiAlias(true);
		handScrewPaint.setColor(0xff493f3c);
		handScrewPaint.setStyle(Paint.Style.FILL);

		backgroundPaint = new Paint();
		backgroundPaint.setFilterBitmap(true);
	}
}
