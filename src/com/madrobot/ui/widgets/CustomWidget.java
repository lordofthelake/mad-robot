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
import android.util.AttributeSet;
import android.view.View;

public abstract class CustomWidget extends View {

	public CustomWidget(Context context) {
		super(context);
		setupDrawingTools();
	}

	public CustomWidget(Context context, AttributeSet attrs) {
		super(context, attrs);
		setupDrawingTools();
	}

	public CustomWidget(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setupDrawingTools();
	}

	protected abstract void setupDrawingTools();

}
