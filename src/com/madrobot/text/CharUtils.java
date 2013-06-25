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
package com.madrobot.text;

public final class CharUtils {
	private static final byte[] CHAR_CLASSES = { 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15,
			15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 12, 23, 23, 23, 25, 23,
			23, 23, 20, 21, 23, 24, 23, 19, 23, 23, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 23, 23, 24, 24, 24, 23,
			23, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 20, 23, 21,
			26, 22, 26, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 20,
			24, 21, 24, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15,
			15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 12, 23, 25, 25, 25, 25, 27, 27, 26, 27, 2, 28,
			24, 16, 27, 26, 27, 24, 11, 11, 26, 2, 27, 23, 26, 11, 2, 29, 11, 11, 11, 23, 1, 1, 1, 1, 1, 1,
			1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 24, 1, 1, 1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 2,
			2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 24, 2, 2, 2, 2, 2, 2, 2, 2 };

	/**
	 * General category "Mc" in the Unicode specification.
	 */
	public static final byte COMBINING_SPACING_MARK = 8;

	/**
	 * General category "Pc" in the Unicode specification.
	 */
	public static final byte CONNECTOR_PUNCTUATION = 23;

	/**
	 * General category "Cc" in the Unicode specification
	 */
	public static final byte CONTROL = 15;

	/**
	 * General category "Sc" in the Unicode specification.
	 */
	public static final byte CURRENCY_SYMBOL = 26;

	/**
	 * General category "Pd" in the Unicode specification.
	 */
	public static final byte DASH_PUNCTUATION = 20;

	/**
	 * General category "Nd" in the Unicode specification.
	 */
	public static final byte DECIMAL_DIGIT_NUMBER = 9;

	/**
	 * General category "Me" in the Unicode specification.
	 */
	public static final byte ENCLOSING_MARK = 7;

	/**
	 * General category "Pe" in the Unicode specification.
	 */
	public static final byte END_PUNCTUATION = 22;

	/**
	 * General category "Cf" in the Unicode specification.
	 */
	public static final byte FORMAT = 16;

	/**
	 * General category "Nl" in the Unicode specification.
	 */
	public static final byte LETTER_NUMBER = 10;

	/**
	 * General category "Zl" in the Unicode specification.
	 */
	public static final byte LINE_SEPARATOR = 13;

	/**
	 * General category "Ll" in the Unicode specification.
	 */
	public static final byte LOWERCASE_LETTER = 2;

	/**
	 * General category "Sm" in the Unicode specification.
	 */
	public static final byte MATH_SYMBOL = 25;

	/**
	 * General category "Lm" in the Unicode specification.
	 */
	public static final byte MODIFIER_LETTER = 4;

	/**
	 * General category "Sk" in the Unicode specification.
	 */
	public static final byte MODIFIER_SYMBOL = 27;

	/**
	 * General category "Mn" in the Unicode specification
	 */
	public static final byte NON_SPACING_MARK = 6;
	/**
	 * General category "Lo" in the Unicode specification.
	 */
	public static final byte OTHER_LETTER = 5;
	/**
	 * General category "No" in the Unicode specification.
	 */
	public static final byte OTHER_NUMBER = 11;
	/**
	 * General category "Po" in the Unicode specification.
	 */
	public static final byte OTHER_PUNCTUATION = 24;
	/**
	 * General category "So" in the Unicode specification.
	 */
	public static final byte OTHER_SYMBOL = 28;
	/**
	 * General category "Zp" in the Unicode specification.
	 */
	public static final byte PARAGRAPH_SEPARATOR = 14;
	/**
	 * General category "Co" in the Unicode specification.
	 */
	public static final byte PRIVATE_USE = 18;
	/**
	 * General category "Zs" in the Unicode specification.
	 */
	public static final byte SPACE_SEPARATOR = 12;
	/**
	 * General category "Ps" in the Unicode specification.
	 */
	public static final byte START_PUNCTUATION = 21;
	/**
	 * General category "Cs" in the Unicode specification
	 */
	public static final byte SURROGATE = 19;
	/**
	 * General category "Lt" in the Unicode specification.
	 */
	public static final byte TITLECASE_LETTER = 3;
	/**
	 * General category "Cn" in the Unicode specification.
	 */
	public static final byte UNASSIGNED = 0;
	/**
	 * General category "Lu" in the Unicode specification.
	 */
	public static final byte UPPERCASE_LETTER = 1;

	/**
	 * Get the type of character
	 * 
	 * @param c
	 * @return character type
	 */
	public static byte getType(char c) {
		// #ifdef RE_UNICODE
		// # for (int i = 0; i < CHAR_CLASSES_SPACE_INDEX.length; i++) {
		// # int spaceIndex = CHAR_CLASSES_SPACE_INDEX[i];
		// # if (c - spaceIndex < 0) {
		// # return UNASSIGNED;
		// # }
		// # if (c - spaceIndex < CHAR_CLASSES[i].length) {
		// # return CHAR_CLASSES[i][c - spaceIndex];
		// # }
		// # }
		// #else
		if(c < CHAR_CLASSES.length){
			return CHAR_CLASSES[c];
		}
		// #endif
		return UNASSIGNED;
	}

	/**
	 * <p>
	 * Checks whether the character is ASCII 7 bit.
	 * </p>
	 * 
	 * <pre>
	 *   CharUtils.isAscii('a')  = true
	 *   CharUtils.isAscii('A')  = true
	 *   CharUtils.isAscii('3')  = true
	 *   CharUtils.isAscii('-')  = true
	 *   CharUtils.isAscii('\n') = true
	 *   CharUtils.isAscii('&amp;copy') = false
	 * </pre>
	 * 
	 * @param ch
	 *            the character to check
	 * @return true if less than 128
	 */
	public static boolean isAscii(char ch) {
		return (ch < 128);
	}

	/**
	 * <p>
	 * Checks whether the character is ASCII 7 bit control.
	 * </p>
	 * 
	 * <pre>
	 *   CharUtils.isAsciiControl('a')  = false
	 *   CharUtils.isAsciiControl('A')  = false
	 *   CharUtils.isAsciiControl('3')  = false
	 *   CharUtils.isAsciiControl('-')  = false
	 *   CharUtils.isAsciiControl('\n') = true
	 *   CharUtils.isAsciiControl('&amp;copy') = false
	 * </pre>
	 * 
	 * @param ch
	 *            the character to check
	 * @return true if less than 32 or equals 127
	 */
	public static boolean isAsciiControl(char ch) {
		return ((ch < 32) || (ch == 127));
	}

	/**
	 * <p>
	 * Checks whether the character is ASCII 7 bit printable.
	 * </p>
	 * 
	 * <pre>
	 *   CharUtils.isAsciiPrintable('a')  = true
	 *   CharUtils.isAsciiPrintable('A')  = true
	 *   CharUtils.isAsciiPrintable('3')  = true
	 *   CharUtils.isAsciiPrintable('-')  = true
	 *   CharUtils.isAsciiPrintable('\n') = false
	 *   CharUtils.isAsciiPrintable('&amp;copy') = false
	 * </pre>
	 * 
	 * @param ch
	 *            the character to check
	 * @return true if between 32 and 126 inclusive
	 */
	public static boolean isAsciiPrintable(char ch) {
		return ((ch >= 32) && (ch < 127));
	}

	/**
	 * Check if the given character is a digit
	 * 
	 * @param c
	 * @return true if <code>c</code> is a digit
	 */
	public static boolean isDigit(char c) {
		byte type = getType(c);
		return type == DECIMAL_DIGIT_NUMBER;
	}

	public static boolean isEnglishChar(char chr) {
		if(chr < 128
				&& (chr >= 'a' && chr <= 'z' || chr >= 'A' && chr <= 'Z' || chr >= '0' && chr <= '9' || chr == '+')){
			return true;
		} else{
			return false;
		}
	}

	public static boolean isISOControl(int i) {
		return ((i >= 0) && (i <= 31)) || ((i >= 127) && (i <= 159));
	}

	public static boolean isJavaIdentifierPart(char c) {
		return isJavaIdentifierStart(c) || Character.isDigit(c);
	}

	public static boolean isJavaIdentifierStart(char c) {
		byte type = getType(c);
		return isLetter(c) || (type == LETTER_NUMBER) || (c == '$') || (c == '_');
	}

	/**
	 * Check if the given character is an english alphabet
	 * 
	 * @param chr
	 * @return true, if the character is an english alphabet.
	 * 
	 */
	public static boolean isLetter(char c) {
		byte type = getType(c);
		return (type == LOWERCASE_LETTER) || (type == UPPERCASE_LETTER) || (type == TITLECASE_LETTER)
				|| (type == OTHER_LETTER);
	}

	public static boolean isLetterOrDigit(char c) {
		return isDigit(c) || isLetter(c);
	}

	public static boolean isSpaceChar(char c) {
		byte type = getType(c);
		return (type == SPACE_SEPARATOR) || (type == LINE_SEPARATOR) || (type == PARAGRAPH_SEPARATOR);
	}

	/**
	 * 
	 * @param c
	 *            <p>
	 *            A character is a Java whitespace character if and only if it
	 *            satisfies one of the following criteria:<br/>
	 *            <ul>
	 *            <li>It is a Unicode space character (SPACE_SEPARATOR,
	 *            LINE_SEPARATOR, or PARAGRAPH_SEPARATOR)<br/>
	 *            but is not also a non-breaking space ('\u00A0', '\u2007',
	 *            '\u202F')</li>.
	 *            <li>It is '\u0009', HORIZONTAL TABULATION.</li>
	 *            <li>It is ' ', LINE FEED.</li>
	 *            <li>It is '\u000B', VERTICAL TABULATION.</li>
	 *            <li>It is '\u000C', FORM FEED.</li>
	 *            <li>It is ' ', CARRIAGE RETURN.</li>
	 *            <li>It is '\u001C', FILE SEPARATOR.</li>
	 *            <li>It is '\u001D', GROUP SEPARATOR.</li>
	 *            <li>It is '\u001E', RECORD SEPARATOR.</li>
	 *            <li>It is '\u001F', UNIT SEPARATOR.</li>
	 *            </ul>
	 *            </p>
	 * 
	 * @return True if <code>c</code> is a whitespace char
	 */
	public static boolean isWhitespace(char c) {
		byte type = getType(c);
		return (((type == SPACE_SEPARATOR) || (type == LINE_SEPARATOR) || (type == PARAGRAPH_SEPARATOR)) && !((c == 0x00A0)
				|| (c == 0x2007) || (c == 0x202F)))
				|| (c == 0x0009)
				|| (c == 0x000A)
				|| (c == 0x000B)
				|| (c == 0x000C)
				|| (c == 0x000D)
				|| (c == 0x0009) || (c == 0x001C) || (c == 0x001D) || (c == 0x001E) || (c == 0x001F);
	}

	/**
	 * Returns the value obtained by reversing the order of the bytes in the
	 * specified char value.
	 * 
	 * @param c
	 * @return Character representing the reverse byte order of <code>c</code>
	 */
	public static char reverseBytes(char c) {
		return (char) ((c & 0xff00) >> 8 | c << 8);
	}

	/**
	 * Convert a digital value to hex
	 * 
	 * @param digitValue
	 * @return Character representing the given integer
	 */
	public static char toHexChar(int digitValue) {
		if(digitValue < 10){
			// Convert value 0-9 to char 0-9 hex char
			return (char) ('0' + digitValue);
		} else{
			// Convert value 10-15 to A-F hex char
			return (char) ('A' + (digitValue - 10));
		}
	}

	private CharUtils() {
	}

}
