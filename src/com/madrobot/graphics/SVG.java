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
package com.madrobot.graphics;

import android.graphics.Picture;
import android.graphics.RectF;
import android.graphics.drawable.PictureDrawable;

/**
 * Describes a vector Picture object, and optionally its bounds.
 *
 */
public class SVG {

    /**
     * These are the bounds for the SVG specified as a hidden "bounds" layer in the SVG.
     */
    private RectF bounds;

    /**
     * These are the estimated bounds of the SVG computed from the SVG elements while parsing.
     * Note that this could be null if there was a failure to compute limits (ie. an empty SVG).
     */
    private RectF limits = null;

    /**
     * The parsed Picture object.
     */
    private Picture picture;

    /**
     * Construct a new SVG.
     * @param picture the parsed picture object.
     * @param bounds the bounds computed from the "bounds" layer in the SVG.
     */
    SVG(Picture picture, RectF bounds) {
        this.picture = picture;
        this.bounds = bounds;
    }

    /**
     * Create a picture drawable from the SVG.
     * @return the PictureDrawable.
     */
    public PictureDrawable createPictureDrawable() {
        return new PictureDrawable(picture);
//        return new PictureDrawable(picture) {
//            @Override
//            public int getIntrinsicWidth() {
//                if (bounds != null) {
//                    return (int) bounds.width();
//                } else if (limits != null) {
//                    return (int) limits.width();
//                } else {
//                    return -1;
//                }
//            }
//
//            @Override
//            public int getIntrinsicHeight() {
//                if (bounds != null) {
//                    return (int) bounds.height();
//                } else if (limits != null) {
//                    return (int) limits.height();
//                } else {
//                    return -1;
//                }
//            }
//        };
    }

    /**
     * Gets the bounding rectangle for the SVG, if one was specified.
     * @return rectangle representing the bounds.
     */
    public RectF getBounds() {
        return bounds;
    }

    /**
     * Gets the bounding rectangle for the SVG that was computed upon parsing. It may not be entirely accurate for certain curves or transformations, but is often better than nothing.
     * @return rectangle representing the computed bounds.
     */
    public RectF getLimits() {
        return limits;
    }

    /**
     * Get the parsed SVG picture data.
     * @return the picture.
     */
    public Picture getPicture() {
        return picture;
    }

    /**
     * Set the limits of the SVG, which are the estimated bounds computed by the parser.
     * @param limits the bounds computed while parsing the SVG, may not be entirely accurate.
     */
    void setLimits(RectF limits) {
        this.limits = limits;
    }
}
