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
//
//package com.ek.android.ui;
//
//import java.util.HashMap;
//import java.util.Map;
//
//import android.R;
//import android.content.Context;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.graphics.Canvas;
//import android.graphics.Matrix;
//import android.graphics.Paint;
//import android.graphics.Rect;
//import android.graphics.Typeface;
//import android.util.AttributeSet;
//import android.widget.TabWidget;
//
///**
// * CustomTabWidget
// */
//public class CustomTabWidget extends TabWidget {
//	/**
//	 * tab names are stored in this array
//	 */
//	private String[] myTabs;
//	/**
//	 * bitmaps for drawing the tabs
//	 */
//	private Bitmap tabSelected;
//
//	private Bitmap newPostImg;
//
//	/**
//	 * Divider image used to saperate the tab and the below layout.
//	 */
//	private Bitmap divider;
//	/**
//	 * Application context to sync with app
//	 */
//	private Context tabContext;
//	/**
//	 * width of single tab
//	 */
//	private int tabWidth;
//	/**
//	 * screen width
//	 */
//	private int screenWidth;
//	/**
//	 * fontsize that is used for drawing the titles of the tab.
//	 */
//	private int fontSize;
//	/**
//	 * tabTypeface is used to create font style and font family name.
//	 */
//	private Typeface tabTypeface;
//	/**
//	 * The Tab item that is highlighted by default is the first tab item.
//	 */
//	private int currentTab = 0;
//	/**
//	 * Color of the Text in the tabWidget.
//	 */
//	private int textColor;
//
//	/**
//	 * Constants used for applying Padding at the left of the TAB widget.
//	 */
//	private final int TAB_PADDING_LEFT = 6;
//	/**
//	 * Constants used for applying Padding at the top of the TAB widget.
//	 */
//	private final int TAB_PADDING_TOP = 5;
//	/**
//	 * Constants used for applying Padding at the bottom of the TAB widget.
//	 */
//	private final int TAB_PADDING_BOTTOM = 4;
//	/**
//	 * Constants used to define the divider image width,for the TAB widget.
//	 */
//	private final int DIVIDER_WIDTH = 2;
//	/**
//	 * Constants used for applying Padding for the TAB widget.This constant
//	 * value is used to define the height of the divider image.
//	 */
//	private final int DIVIDER_HEIGHT = 20;
//	/**
//	 * Constants used for applying Padding for the TAB widget.This constant
//	 * value is used to set top padding for divider image.
//	 */
//	private final int DIVIDER_PADDING_TOP = 25;
//
//	private Map<Integer, Boolean> newPostMap = null;
//
//	/**
//	 * Simple Constructor for the Widget
//	 * 
//	 * @param context
//	 *            App Context
//	 */
//	public CustomTabWidget(Context context) {
//		super(context);
//		tabContext = context;
//	}
//
//	/**
//	 * Constructor for the tabWidget
//	 * 
//	 * @param context
//	 *            App Context
//	 * @param attrs
//	 *            styles for the widgets
//	 */
//	public CustomTabWidget(Context context, AttributeSet attrs) {
//		super(context, attrs);
//		tabContext = context;
//		init();
//	}
//
//	public void setNewPostIcon(int tabIndex, boolean value) {
//		if(newPostMap != null){
//			newPostMap.put(tabIndex, value);
//		}
//	}
//
//	/**
//	 * Constructor for the tabWidget
//	 * 
//	 * @param context
//	 *            App Context
//	 * @param attrs
//	 *            styles for the widgets
//	 * @param defStyle
//	 *            styles
//	 */
//	public CustomTabWidget(Context context, AttributeSet attrs, int defStyle) {
//		super(context, attrs, defStyle);
//		tabContext = context;
//		init();
//	}
//
//	/**
//	 * initialize the bitmaps from the res folder.
//	 */
//	private void init() {
//		tabSelected = BitmapFactory.decodeResource(tabContext.getResources(), R.drawable.greentab_modified);
//		divider = BitmapFactory.decodeResource(tabContext.getResources(), R.drawable.divider);
//
//		Bitmap bmp = BitmapFactory.decodeResource(tabContext.getResources(), R.drawable.new_post);
//		newPostImg = scaleNIcon(bmp, 25, 25);// Bitmap.createBitmap(bmp, 0,0,
//		// 25, 25);
//
//	}
//
//	/**
//	 * Scales the N icon as per resolution.
//	 * 
//	 * @param originalBmp
//	 * @param newWidth
//	 * @param newHeight
//	 * @return
//	 */
//	public Bitmap scaleNIcon(Bitmap originalBmp, int newWidth, int newHeight) {
//		Bitmap scaledBmp = null;
//
//		int width = originalBmp.getWidth();
//		int height = originalBmp.getHeight();
//		newWidth = getScaledSize(newWidth);
//		newHeight = getScaledSize(newHeight);
//		float scaleWidth = ((float) newWidth) / width;
//		float scaleHeight = ((float) newHeight) / height;
//		Matrix matrix = new Matrix();
//		matrix.postScale(scaleWidth, scaleHeight);
//		scaledBmp = Bitmap.createBitmap(originalBmp, 0, 0, width, height, matrix, true);
//
//		return scaledBmp;
//	}
//
//	/**
//	 * sets the tab strings. for displaying on tabs.
//	 * 
//	 * @param tabLabels
//	 *            array of string
//	 */
//	public void setTabLabels(String[] tabLabels) {
//		myTabs = tabLabels;
//
//		newPostMap = new HashMap<Integer, Boolean>();
//		if(tabLabels != null){
//			for(int i = 0; i != tabLabels.length; ++i){
//				newPostMap.put(i, false);
//			}
//		}
//	}
//
//	/**
//	 * when the tabs selection is changed , then this method is called from the
//	 * parent shows the index of the currently selected tab.
//	 * 
//	 * @see android.widget.TabWidget#setCurrentTab(int)
//	 */
//	@Override
//	public void setCurrentTab(int index) {
//		// TODO Auto-generated method stub
//		currentTab = index;
//		super.setCurrentTab(index);
//	}
//
//	/**
//	 * returns the scaled size for different resolutions.
//	 * 
//	 * @param fntSize
//	 * @return
//	 */
//	int getScaledSize(int fntSize) {
//		int density = getResources().getDisplayMetrics().densityDpi;
//		if(160 == density){
//			int newSize = (int) (fntSize / (1.5));
//			return newSize;
//		} else if(density == 120){
//			int newSize = (fntSize / (2));
//			return newSize;
//		}
//		return fntSize;
//	}
//
//	/**
//	 * This may be overridden by derived classes to gain control just before its
//	 * children are drawn (but after its own view has been drawn). tabs and
//	 * background are drawn.
//	 * 
//	 * @see android.widget.TabWidget#dispatchDraw(android.graphics.Canvas)
//	 */
//	@Override
//	public void dispatchDraw(Canvas canvas) {
//		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
//		Rect divRect = new Rect(0, 0, 0, 0);
//
//		// screenWidth = canvas.getWidth();
//		screenWidth = canvas.getWidth() - (getScaledSize(6) * 2); // Wing
//
//		tabWidth = screenWidth / (myTabs != null ? myTabs.length : 1);
//		divRect.top = divRect.top + getScaledSize(DIVIDER_PADDING_TOP);
//		divRect.bottom = divRect.top + getScaledSize(DIVIDER_HEIGHT);
//		int newFontSize = getScaledSize(fontSize);
//		paint.setTextSize(newFontSize);
//		paint.setTypeface(tabTypeface);
//
//		if((myTabs != null) && (myTabs.length > 0)){
//
//			for(int i = 0; i < myTabs.length; i++){
//				Rect dest = new Rect(0, 0, tabWidth, getHeight() + 1);
//
//				// dest.left = i * tabWidth;
//				dest.left = i * tabWidth + getScaledSize(TAB_PADDING_LEFT); // Wing
//
//				dest.right = dest.left + tabWidth;
//
//				if(i == currentTab){
//					dest.top = dest.top + getScaledSize(TAB_PADDING_TOP);
//					// dest.left = dest.left + getScaledSize(TAB_PADDING_LEFT);
//					// Wing
//					if(i != 0){
//						dest.left = dest.left + getScaledSize(TAB_PADDING_LEFT) - getScaledSize(8);
//					}
//
//					dest.bottom = dest.bottom - getScaledSize(TAB_PADDING_BOTTOM);
//					canvas.drawBitmap(tabSelected, null, dest, paint);
//					paint.setColor(textColor);
//
//					if((newPostMap != null) && (newPostMap.get(currentTab) != null)
//							&& (newPostMap.get(currentTab) == true)){
//						int left = dest.left + dest.width() - 30;
//						int top = dest.top - getScaledSize(TAB_PADDING_TOP);
//
//						canvas.drawBitmap(newPostImg, left, top, paint);
//					}
//				} else{
//					if((newPostMap != null) && (newPostMap.get(i) != null) && (newPostMap.get(i) == true)){
//						int left = dest.left + dest.width() - 30;
//						int top = dest.top;
//						canvas.drawBitmap(newPostImg, left, top, paint);
//					}
//
//					if(i != 0){
//						dest.left = dest.left + getScaledSize(TAB_PADDING_LEFT) - getScaledSize(8);
//					}
//				}
//
//				// draw text as its present
//				if(myTabs[i].length() > 0){
//					Rect textRect = new Rect(0, 0, 0, 0);
//
//					// paint.getTextBounds(myTabs[i], 0, myTabs[i].length(),
//					// textRect);
//					paint.getTextBounds(myTabs[i], 0, myTabs[i].length(), textRect);// Wing
//
//					// int textStartX = (tabWidth - textRect.width()) >> 1;
//					int textStartX = dest.left + (tabWidth / 2) - (textRect.width() / 2); // Wing
//
//					int textHeight = (int) paint.getTextSize();
//					int textStartY = (getHeight() - textHeight) >> 1;
//					if(textRect.top < 0){
//						textStartY += Math.abs(textRect.top);
//					}
//
//					paint.setColor(textColor);
//					// canvas.drawText(myTabs[i], dest.left -
//					// getScaledSize(TAB_PADDING_LEFT) + textStartX, textStartY,
//					// paint);
//					canvas.drawText(myTabs[i], textStartX, textStartY, paint); // Wing
//					// Wing
//					// Draw town group name text
//					/**
//					 * if(i != currentTab){
//					 * // non-selected tab
//					 * canvas.drawText(myTabs[i], textStartX, textStartY,
//					 * paint);
//					 * } else{
//					 * // selected tab
//					 * canvas.drawText(myTabs[i], textStartX, textStartY +
//					 * getScaledSize(6), paint);
//					 * }
//					 **/
//
//					// Code of drawing the divider .....
//					// considering the current tab we need to draw the divider
//					// accordingly
//					//
//					if(i != currentTab){
//						// check for the
//						if((i < currentTab) && ((i + 1) != currentTab)){
//
//							// place the divider in correct position.
//							// divRect.left = (i + 1) * tabWidth;
//							divRect.left = (i + 1) * tabWidth + getScaledSize(8); // Wing
//
//							// divider width is 2 pixel
//							divRect.right = divRect.left + getScaledSize(DIVIDER_WIDTH);
//							canvas.drawBitmap(divider, null, divRect, paint);
//						} else if(((i > currentTab)) && ((i - 1) != currentTab)){
//
//							// divRect.left = i * tabWidth;
//							divRect.left = i * tabWidth + getScaledSize(8); // Wing
//
//							// divider width is 2 pixel
//							divRect.right = divRect.left + getScaledSize(DIVIDER_WIDTH);
//							canvas.drawBitmap(divider, null, divRect, paint);
//						}
//					}
//				}
//			}
//		}
//	}
//
//	/**
//	 * 
//	 * This method is used to set the font size and style for the custom tab
//	 * widget.
//	 * 
//	 * @param fontSize
//	 *            the size of the font.
//	 * @param familyName
//	 *            font family name
//	 * @param style
//	 *            font style.
//	 */
//	public void setFont(int fontSize, String familyName, int style) {
//		tabTypeface = Typeface.create(familyName, style);
//		this.fontSize = fontSize;
//
//	}
//
//	/**
//	 * 
//	 * This method is used to set the text color for the custom tab widget.
//	 * 
//	 * @param color
//	 *            the color code for the text.
//	 */
//	public void setTextColor(int color) {
//		textColor = color;
//	}
//}
