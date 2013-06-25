/*******************************************************************************
 * Copyright (c) 2012 MadRobot.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the GNU Lesser Public License v2.1
 *  which accompanies this distribution, and is available at
 *  http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 *  
 *  Contributors:
 *  Elton Kent - initial API and implementation
 ******************************************************************************/
package com.madrobot.di.wizard.xml.converters;

import java.awt.font.TextAttribute;

/**
 * A converter for {@link TextAttribute} constants.
 * 
 * @since 1.2
 */
public class TextAttributeConverter extends AbstractAttributedCharacterIteratorAttributeConverter {

	/**
	 * Constructs a TextAttributeConverter.
	 * 
	 * @since 1.2.2
	 */
	public TextAttributeConverter() {
		super(TextAttribute.class);
	}
}
