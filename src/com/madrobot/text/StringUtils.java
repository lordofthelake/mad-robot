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

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

/**
 * String manipulation utilities
 * 
 * 
 */
public final class StringUtils {

	/**
	 * The empty String {@code ""}.
	 * 
	 * @since 2.0
	 */
	public static final String EMPTY = "";
	/**
	 * <p>
	 * The maximum size to which the padding constant(s) can expand.
	 * </p>
	 */
	private static final int PAD_LIMIT = 8192;

	/**
	 * Space character.
	 */
	private final static char SPACE = 32;

	/**
	 * Check if the given text contains alphabets A-Z or a-z or empty space
	 * 
	 * @param string
	 * @return true if the text contains alphabets
	 */
	public static boolean containsAlphabets(String string) {
		for (int i = 0; i < string.length(); i++) {
			if (CharUtils.isLetter(string.charAt(i))) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Check if the given text contains numbers
	 * 
	 * @param string
	 * @return true if the text contains numbers
	 */
	public static boolean containsNumbers(String string) {
		for (int i = 0; i < string.length(); i++) {
			if (CharUtils.isDigit(string.charAt(i))) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Utility method to take a string and convert it to normal Java variable
	 * name capitalization. This normally means converting the first character
	 * from upper case to lower case, but in the (unusual) special case when
	 * there is more than one character and both the first and second characters
	 * are upper case, we leave it alone.
	 * <p>
	 * Thus "FooBah" becomes "fooBah" and "X" becomes "x", but "URL" stays as
	 * "URL".
	 * 
	 * @param name
	 *            The string to be decapitalized.
	 * @return The decapitalized version of the string.
	 */
	public static String decapitalize(String name) {
		if (name == null || name.length() == 0) {
			return name;
		}
		if (name.length() > 1 && Character.isUpperCase(name.charAt(1))
				&& Character.isUpperCase(name.charAt(0))) {
			return name;
		}
		char chars[] = name.toCharArray();
		chars[0] = Character.toLowerCase(chars[0]);
		return new String(chars);
	}

	/**
	 * 
	 * @param base
	 *            String to compare
	 * @param end
	 *            Ending String
	 * @return <code>True</code> if <code>base</code> ends with <code>end</code>
	 */
	// public static boolean endsWithIgnoreCase(CharSequence base, String end) {
	// if(base.length() < end.length()){
	// return false;
	// }
	// return base.regionMatches(true, base.length() - end.length(), end, 0,
	// end.length());
	// }

	/**
	 * <p>
	 * Returns either the passed in String, or if the String is
	 * <code>null</code>, an empty String ("").
	 * </p>
	 * 
	 * <pre>
	 * StringUtils.defaultString(null)  = ""
	 * StringUtils.defaultString("")    = ""
	 * StringUtils.defaultString("bat") = "bat"
	 * </pre>
	 * 
	 * @see String#valueOf(Object)
	 * @param str
	 *            the String to check, may be null
	 * @return the passed in String, or the empty String if it was
	 *         <code>null</code>
	 */
	public static String defaultString(String str) {
		return str == null ? EMPTY : str;
	}

	/**
	 * <p>
	 * Deletes all whitespaces from a String as defined by
	 * {@link Character#isWhitespace(char)}.
	 * </p>
	 * 
	 * <pre>
	 * StringUtils.deleteWhitespace(null)         = null
	 * StringUtils.deleteWhitespace("")           = ""
	 * StringUtils.deleteWhitespace("abc")        = "abc"
	 * StringUtils.deleteWhitespace("   ab  c  ") = "abc"
	 * </pre>
	 * 
	 * @param str
	 *            the String to delete whitespace from, may be null
	 * @return the String without whitespaces, <code>null</code> if null String
	 *         input
	 */
	public static String deleteWhitespace(String str) {
		if (isBlank(str)) {
			return str;
		}
		int sz = str.length();
		char[] chs = new char[sz];
		int count = 0;
		for (int i = 0; i < sz; i++) {
			if (!Character.isWhitespace(str.charAt(i))) {
				chs[count++] = str.charAt(i);
			}
		}
		if (count == sz) {
			return str;
		}
		return new String(chs, 0, count);
	}

	/**
	 * Encodes the string in the string buffer
	 * <p>
	 * Encodes a string so that it is suitable for transmission over HTTP
	 * </p>
	 * 
	 * @param string
	 * @return the encoded string
	 */
	public final static String encodeString(StringBuilder string) {
		StringBuilder encodedUrl = new StringBuilder(); // Encoded URL

		int len = string.length();
		// Encode each URL character
		final String UNRESERVED = "-_.!~*'()\"";
		for (int i = 0; i < len; i++) {
			char c = string.charAt(i); // Get next character
			if (((c >= '0') && (c <= '9')) || ((c >= 'a') && (c <= 'z'))
					|| ((c >= 'A') && (c <= 'Z'))) {
				// Alphanumeric characters require no encoding, append as is
				encodedUrl.append(c);
			} else {
				int imark = UNRESERVED.indexOf(c);
				if (imark >= 0) {
					// Unreserved punctuation marks and symbols require
					// no encoding, append as is
					encodedUrl.append(c);
				} else {
					// Encode all other characters to Hex, using the format
					// "%XX",
					// where XX are the hex digits
					encodedUrl.append('%'); // Add % character
					// Encode the character's high-order nibble to Hex
					encodedUrl.append(CharUtils.toHexChar((c & 0xF0) >> 4));
					// Encode the character's low-order nibble to Hex
					encodedUrl.append(CharUtils.toHexChar(c & 0x0F));
				}
			}
		}
		System.out.println("Encoded string " + encodedUrl.toString());
		return encodedUrl.toString(); // Return encoded URL
	}

	/**
	 * Checks if the two strings are equal irrespective of the case
	 * <p>
	 * For cdlc1.0 based devices
	 * </p>
	 * 
	 * @param str1
	 * @param str2
	 * @return <code>True</code> if the two strings are equal
	 */
	public static boolean equalsIgnoreCase(String str1, String str2) {

		if ((str2 == null) || (str1.length() != str2.length())) {
			return false;
		}
		return str1.toLowerCase().equals(str2.toLowerCase());
	}

	/**
	 * Format a string like "Where is {0}, in the {1}" withe the corresponding
	 * <code>args</code> values.
	 * 
	 * @param pattern
	 * @param args
	 * @return The formated string
	 */
	public static String format(String pattern, Object[] args) {
		if (pattern != null) {
			StringBuilder toAppendTo = new StringBuilder();
			int l = pattern.length();
			int n = 0, lIndex = -1, lastIndex = 0;
			for (int i = 0; i < l; i++) {
				if (pattern.charAt(i) == '{') {
					n++;
					if (n == 1) {
						lIndex = i;
						toAppendTo.append(pattern.substring(lastIndex, i));
						lastIndex = i;
					}
				}
				if (pattern.charAt(i) == '}') {
					if (n == 1) {
						toAppendTo.append(processPattern(
								pattern.substring(lIndex + 1, i), args));
						lIndex = -1;
						lastIndex = i + 1;
					}
					n--;
				}
			}
			if (n > 0) {
				toAppendTo.append(processPattern(pattern.substring(lIndex + 1),
						args));
			} else {
				toAppendTo.append(pattern.substring(lastIndex));
			}
			return toAppendTo.toString();
		}
		return null;
	}

	/**
	 * Convert to UTF8 formatting to string
	 * 
	 * @param ao
	 * @return Converted string
	 */
	public static String fromUTF8(byte[] ao) {
		int nCharCode, i;
		int nLength = ao.length;
		char[] ach = new char[nLength];

		int nCount = 0;

		loop: for (i = 0; i < nLength; i++) {
			nCharCode = (ao[i]) & 0x00ff;
			if (nCharCode >= 0x80) {
				if (nCharCode < 0xe0) {
					// need 2 bytes
					nCharCode = (nCharCode & 0x1f) << 6;
					nCharCode |= ((ao[++i]) & 0x3f);
				} else {
					// need 3 bytes
					nCharCode = (nCharCode & 0x0f) << 12;
					nCharCode |= ((ao[++i]) & 0x3f) << 6;
					nCharCode |= ((ao[++i]) & 0x3f);
					// ignore character added by Notepad
					if (nCharCode == 0xfeff) {
						continue loop;
					}
				}
			}
			ach[nCount++] = (char) nCharCode;
		}
		return new String(ach, 0, nCount);
	}

	/**
	 * Encodes the given string into a sequence of bytes using the ISO-8859-1
	 * charset, storing the result into a new byte array.
	 * 
	 * @param string
	 *            the String to encode, may be <code>null</code>
	 * @return encoded bytes, or <code>null</code> if the input string was
	 *         <code>null</code>
	 * @throws IllegalStateException
	 *             Thrown when the charset is missing, which should be never
	 *             according the the Java specification.
	 * @see <a
	 *      href="http://java.sun.com/j2se/1.4.2/docs/api/java/nio/charset/Charset.html">Standard
	 *      charsets</a>
	 * @see #getBytesUnchecked(String, String)
	 */
	public static byte[] getBytesIso8859_1(String string) {
		return StringUtils.getBytesUnchecked(string, "ISO-8859-1");
	}

	/**
	 * Encodes the given string into a sequence of bytes using the named
	 * charset, storing the result into a new byte array.
	 * <p>
	 * This method catches {@link UnsupportedEncodingException} and rethrows it
	 * as {@link IllegalStateException}, which should never happen for a
	 * required charset name. Use this method when the encoding is required to
	 * be in the JRE.
	 * </p>
	 * 
	 * @param string
	 *            the String to encode, may be <code>null</code>
	 * @param charsetName
	 *            The name of a required {@link java.nio.charset.Charset}
	 * @return encoded bytes, or <code>null</code> if the input string was
	 *         <code>null</code>
	 * @throws IllegalStateException
	 *             Thrown when a {@link UnsupportedEncodingException} is caught,
	 *             which should never happen for a required charset name.
	 * @see CharEncoding
	 * @see String#getBytes(String)
	 */
	public static byte[] getBytesUnchecked(String string, String charsetName) {
		if (string == null) {
			return null;
		}
		try {
			return string.getBytes(charsetName);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Encodes the given string into a sequence of bytes using the US-ASCII
	 * charset, storing the result into a new byte array.
	 * 
	 * @param string
	 *            the String to encode, may be <code>null</code>
	 * @return encoded bytes, or <code>null</code> if the input string was
	 *         <code>null</code>
	 * @throws IllegalStateException
	 *             Thrown when the charset is missing, which should be never
	 *             according the the Java specification.
	 * @see <a
	 *      href="http://java.sun.com/j2se/1.4.2/docs/api/java/nio/charset/Charset.html">Standard
	 *      charsets</a>
	 * @see #getBytesUnchecked(String, String)
	 */
	public static byte[] getBytesUsAscii(String string) {
		return StringUtils.getBytesUnchecked(string, "US-ASCII");
	}

	/**
	 * <p>
	 * Finds the first index within a String from a start position, handling
	 * <code>null</code>. This method uses {@link String#indexOf(int, int)}.
	 * </p>
	 * 
	 * <p>
	 * A <code>null</code> or empty ("") String will return
	 * <code>(INDEX_NOT_FOUND) -1</code>. A negative start position is treated
	 * as zero. A start position greater than the string length returns
	 * <code>-1</code>.
	 * </p>
	 * 
	 * <pre>
	 * StringUtils.indexOf(null, *, *)          = -1
	 * StringUtils.indexOf("", *, *)            = -1
	 * StringUtils.indexOf("aabaabaa", 'b', 0)  = 2
	 * StringUtils.indexOf("aabaabaa", 'b', 3)  = 5
	 * StringUtils.indexOf("aabaabaa", 'b', 9)  = -1
	 * StringUtils.indexOf("aabaabaa", 'b', -1) = 2
	 * </pre>
	 * 
	 * @param str
	 *            the String to check, may be null
	 * @param searchChar
	 *            the character to find
	 * @param startPos
	 *            the start position, negative treated as zero
	 * @return the first index of the search character, -1 if no match or
	 *         <code>null</code> string input
	 */
	public static int indexOf(String str, int searchChar, int startPos) {
		if (isBlank(str)) {
			return -1;
		}
		return str.indexOf(searchChar, startPos);
	}

	/**
	 * <p>
	 * Finds the first index within a String, handling <code>null</code>. This
	 * method uses {@link String#indexOf(String, int)}.
	 * </p>
	 * 
	 * <p>
	 * A <code>null</code> String will return <code>-1</code>. A negative start
	 * position is treated as zero. An empty ("") search String always matches.
	 * A start position greater than the string length only matches an empty
	 * search String.
	 * </p>
	 * 
	 * <pre>
	 * StringUtils.indexOf(null, *, *)          = -1
	 * StringUtils.indexOf(*, null, *)          = -1
	 * StringUtils.indexOf("", "", 0)           = 0
	 * StringUtils.indexOf("", *, 0)            = -1 (except when * = "")
	 * StringUtils.indexOf("aabaabaa", "a", 0)  = 0
	 * StringUtils.indexOf("aabaabaa", "b", 0)  = 2
	 * StringUtils.indexOf("aabaabaa", "ab", 0) = 1
	 * StringUtils.indexOf("aabaabaa", "b", 3)  = 5
	 * StringUtils.indexOf("aabaabaa", "b", 9)  = -1
	 * StringUtils.indexOf("aabaabaa", "b", -1) = 2
	 * StringUtils.indexOf("aabaabaa", "", 2)   = 2
	 * StringUtils.indexOf("abc", "", 9)        = 3
	 * </pre>
	 * 
	 * @param str
	 *            the String to check, may be null
	 * @param searchStr
	 *            the String to find, may be null
	 * @param startPos
	 *            the start position, negative treated as zero
	 * @return the first index of the search String, -1 if no match or
	 *         <code>null</code> string input
	 */
	public static int indexOf(String str, String searchStr, int startPos) {
		if (str == null || searchStr == null) {
			return -1;
		}
		return str.indexOf(searchStr, startPos);
	}

	/**
	 * <p>
	 * Checks if a CharSequence is whitespace, empty ("") or null.
	 * </p>
	 * 
	 * <pre>
	 * StringUtils.isBlank(null)      = true
	 * StringUtils.isBlank("")        = true
	 * StringUtils.isBlank(" ")       = true
	 * StringUtils.isBlank("bob")     = false
	 * StringUtils.isBlank("  bob  ") = false
	 * </pre>
	 * 
	 * @param cs
	 *            the CharSequence to check, may be null
	 * @return <code>true</code> if the CharSequence is null, empty or
	 *         whitespace
	 */
	public static boolean isBlank(CharSequence cs) {
		int strLen;
		if (cs == null || (strLen = cs.length()) == 0) {
			return true;
		}
		for (int i = 0; i < strLen; i++) {
			if ((Character.isWhitespace(cs.charAt(i)) == false)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 
	 * @param array
	 * @param chr
	 * @return
	 */
	public static boolean isConsecutive(char[] array, char chr) {
		boolean firstPosSet = false;
		int firstPos = 0;
		int secPos = 0;
		for (int i = 0; i < array.length; i++) {
			if (array[i] == chr) {
				if (!firstPosSet) {
					firstPos = i;
					firstPosSet = true;
				} else {
					if (secPos == 0) {
						secPos = i;
					}
				}

			}
		}
		return secPos == firstPos + 1;
	}

	/**
	 * Check if a character occurs consecutively in the given string
	 * 
	 * @param string
	 * @param chr
	 *            Charcter to check for consecutive occurence
	 * @return true if <code>chr</code> occurs consecutively
	 */
	public static boolean isConsecutive(String string, char chr) {
		return isConsecutive(string.toCharArray(), chr);
	}

	/**
	 * <p>
	 * Checks if a CharSequence is empty ("") or null.
	 * </p>
	 * 
	 * <pre>
	 * StringUtils.isEmpty(null)      = true
	 * StringUtils.isEmpty("")        = true
	 * StringUtils.isEmpty(" ")       = false
	 * StringUtils.isEmpty("bob")     = false
	 * StringUtils.isEmpty("  bob  ") = false
	 * </pre>
	 * 
	 * <p>
	 * NOTE: This method changed in Lang version 2.0. It no longer trims the
	 * CharSequence. That functionality is available in isBlank().
	 * </p>
	 * 
	 * @param cs
	 *            the CharSequence to check, may be null
	 * @return <code>true</code> if the CharSequence is empty or null
	 */
	public static boolean isEmpty(CharSequence cs) {
		return cs == null || cs.length() == 0;
	}

	/**
	 * Decode a text that is encoded as a Java string literal. The Java
	 * properties file format and Java source code format is supported.
	 * 
	 * @param s
	 *            the encoded string
	 * @return the string
	 * @throws Exception 
	 */
	public static String javaDecode(String s) throws Exception {
		int length = s.length();
		StringBuilder buff = new StringBuilder(length);
		for (int i = 0; i < length; i++) {
			char c = s.charAt(i);
			if (c == '\\') {
				if (i + 1 >= s.length()) {
					throw new Exception("String format error");
				}
				c = s.charAt(++i);
				switch (c) {
				case 't':
					buff.append('\t');
					break;
				case 'r':
					buff.append('\r');
					break;
				case 'n':
					buff.append('\n');
					break;
				case 'b':
					buff.append('\b');
					break;
				case 'f':
					buff.append('\f');
					break;
				case '#':
					// for properties files
					buff.append('#');
					break;
				case '=':
					// for properties files
					buff.append('=');
					break;
				case ':':
					// for properties files
					buff.append(':');
					break;
				case '"':
					buff.append('"');
					break;
				case '\\':
					buff.append('\\');
					break;
				case 'u': {
					try {
						c = (char) (Integer.parseInt(s.substring(i + 1, i + 5),
								16));
					} catch (NumberFormatException e) {
						 throw new Exception("String format error");
					}
					i += 4;
					buff.append(c);
					break;
				}
				default:
					if (c >= '0' && c <= '9') {
						try {
							c = (char) (Integer.parseInt(s.substring(i, i + 3),
									8));
						} catch (NumberFormatException e) {
							 throw new Exception("String format error");
						}
						i += 2;
						buff.append(c);
					} else {
						 throw new Exception("String format error");
					}
				}
			} else {
				buff.append(c);
			}
		}
		return buff.toString();
	}

	/**
	 * Convert a string to a Java literal using the correct escape sequences.
	 * The literal is not enclosed in double quotes. The result can be used in
	 * properties files or in Java source code.
	 * 
	 * @param s
	 *            the text to convert
	 * @return the Java representation
	 */
	public static String javaEncode(String s) {
		int length = s.length();
		StringBuilder buff = new StringBuilder(length);
		for (int i = 0; i < length; i++) {
			char c = s.charAt(i);
			switch (c) {
			// case '\b':
			// // BS backspace
			// // not supported in properties files
			// buff.append("\\b");
			// break;
			case '\t':
				// HT horizontal tab
				buff.append("\\t");
				break;
			case '\n':
				// LF linefeed
				buff.append("\\n");
				break;
			case '\f':
				// FF form feed
				buff.append("\\f");
				break;
			case '\r':
				// CR carriage return
				buff.append("\\r");
				break;
			case '"':
				// double quote
				buff.append("\\\"");
				break;
			case '\\':
				// backslash
				buff.append("\\\\");
				break;
			default:
				int ch = c & 0xffff;
				if (ch >= ' ' && (ch < 0x80)) {
					buff.append(c);
					// not supported in properties files
					// } else if(ch < 0xff) {
					// buff.append("\\");
					// // make sure it's three characters (0x200 is octal 1000)
					// buff.append(Integer.toOctalString(0x200 |
					// ch).substring(1));
				} else {
					buff.append("\\u");
					String hex = Integer.toHexString(ch);
					// make sure it's four characters
					for (int len = hex.length(); len < 4; len++) {
						buff.append('0');
					}
					buff.append(hex);
				}
			}
		}
		return buff.toString();
	}

	/**
	 * Join a string array into a string
	 * 
	 * @param string
	 * @return Concatenated string
	 */
	public static String join(String[] string) {
		StringBuilder buf = new StringBuilder();
		for (int i = 0; i < string.length; i++) {
			buf.append(string[i]);
		}
		return buf.toString();
	}

	/**
	 * Joins the String array with the mentioned character at the end of each
	 * string.
	 * <p>
	 * Common usage is adding a new line character at the end of each string
	 * </p>
	 */
	public static String joinWithChar(String[] string, char chr) {
		StringBuilder buf = new StringBuilder();
		for (int i = 0; i < string.length; i++) {
			buf.append(string[i]);
			buf.append(chr);
		}
		return buf.toString();
	}

	/**
	 * <p>
	 * Left pad a String with spaces (' ').
	 * </p>
	 * 
	 * <p>
	 * The String is padded to the size of <code>size</code>.
	 * </p>
	 * 
	 * <pre>
	 * StringUtils.leftPad(null, *)   = null
	 * StringUtils.leftPad("", 3)     = "   "
	 * StringUtils.leftPad("bat", 3)  = "bat"
	 * StringUtils.leftPad("bat", 5)  = "  bat"
	 * StringUtils.leftPad("bat", 1)  = "bat"
	 * StringUtils.leftPad("bat", -1) = "bat"
	 * </pre>
	 * 
	 * @param str
	 *            the String to pad out, may be null
	 * @param size
	 *            the size to pad to
	 * @return left padded String or original String if no padding is necessary,
	 *         <code>null</code> if null String input
	 */
	public static String leftPad(String str, int size) {
		return leftPad(str, size, ' ');
	}

	/**
	 * <p>
	 * Left pad a String with a specified character.
	 * </p>
	 * 
	 * <p>
	 * Pad to a size of <code>size</code>.
	 * </p>
	 * 
	 * <pre>
	 * StringUtils.leftPad(null, *, *)     = null
	 * StringUtils.leftPad("", 3, 'z')     = "zzz"
	 * StringUtils.leftPad("bat", 3, 'z')  = "bat"
	 * StringUtils.leftPad("bat", 5, 'z')  = "zzbat"
	 * StringUtils.leftPad("bat", 1, 'z')  = "bat"
	 * StringUtils.leftPad("bat", -1, 'z') = "bat"
	 * </pre>
	 * 
	 * @param str
	 *            the String to pad out, may be null
	 * @param size
	 *            the size to pad to
	 * @param padChar
	 *            the character to pad with
	 * @return left padded String or original String if no padding is necessary,
	 *         <code>null</code> if null String input
	 */
	public static String leftPad(String str, int size, char padChar) {
		if (str == null) {
			return null;
		}
		int pads = size - str.length();
		if (pads <= 0) {
			return str; // returns original String when possible
		}
		if (pads > PAD_LIMIT) {
			return leftPad(str, size, String.valueOf(padChar));
		}
		return padding(pads, padChar).concat(str);
	}

	/**
	 * <p>
	 * Left pad a String with a specified String.
	 * </p>
	 * 
	 * <p>
	 * Pad to a size of <code>size</code>.
	 * </p>
	 * 
	 * <pre>
	 * StringUtils.leftPad(null, *, *)      = null
	 * StringUtils.leftPad("", 3, "z")      = "zzz"
	 * StringUtils.leftPad("bat", 3, "yz")  = "bat"
	 * StringUtils.leftPad("bat", 5, "yz")  = "yzbat"
	 * StringUtils.leftPad("bat", 8, "yz")  = "yzyzybat"
	 * StringUtils.leftPad("bat", 1, "yz")  = "bat"
	 * StringUtils.leftPad("bat", -1, "yz") = "bat"
	 * StringUtils.leftPad("bat", 5, null)  = "  bat"
	 * StringUtils.leftPad("bat", 5, "")    = "  bat"
	 * </pre>
	 * 
	 * @param str
	 *            the String to pad out, may be null
	 * @param size
	 *            the size to pad to
	 * @param padStr
	 *            the String to pad with, null or empty treated as single space
	 * @return left padded String or original String if no padding is necessary,
	 *         <code>null</code> if null String input
	 */
	public static String leftPad(String str, int size, String padStr) {
		if (str == null) {
			return null;
		}
		if (isBlank(padStr)) {
			padStr = " ";
		}
		int padLen = padStr.length();
		int strLen = str.length();
		int pads = size - strLen;
		if (pads <= 0) {
			return str; // returns original String when possible
		}
		if (padLen == 1 && pads <= PAD_LIMIT) {
			return leftPad(str, size, padStr.charAt(0));
		}

		if (pads == padLen) {
			return padStr.concat(str);
		} else if (pads < padLen) {
			return padStr.substring(0, pads).concat(str);
		} else {
			char[] padding = new char[pads];
			char[] padChars = padStr.toCharArray();
			for (int i = 0; i < pads; i++) {
				padding[i] = padChars[i % padLen];
			}
			return new String(padding).concat(str);
		}
	}

	/**
	 * Gets a CharSequence length or <code>0</code> if the CharSequence is
	 * <code>null</code>.
	 * 
	 * @param cs
	 *            a CharSequence or <code>null</code>
	 * @return CharSequence length or <code>0</code> if the CharSequence is
	 *         <code>null</code>.
	 */
	public static int length(CharSequence cs) {
		return cs == null ? 0 : cs.length();
	}

	/**
	 * <p>
	 * Overlays part of a String with another String.
	 * </p>
	 * 
	 * <p>
	 * A {@code null} string input returns {@code null}. A negative index is
	 * treated as zero. An index greater than the string length is treated as
	 * the string length. The start index is always the smaller of the two
	 * indices.
	 * </p>
	 * 
	 * <pre>
	 * StringUtils.overlay(null, *, *, *)            = null
	 * StringUtils.overlay("", "abc", 0, 0)          = "abc"
	 * StringUtils.overlay("abcdef", null, 2, 4)     = "abef"
	 * StringUtils.overlay("abcdef", "", 2, 4)       = "abef"
	 * StringUtils.overlay("abcdef", "", 4, 2)       = "abef"
	 * StringUtils.overlay("abcdef", "zzzz", 2, 4)   = "abzzzzef"
	 * StringUtils.overlay("abcdef", "zzzz", 4, 2)   = "abzzzzef"
	 * StringUtils.overlay("abcdef", "zzzz", -1, 4)  = "zzzzef"
	 * StringUtils.overlay("abcdef", "zzzz", 2, 8)   = "abzzzz"
	 * StringUtils.overlay("abcdef", "zzzz", -2, -3) = "zzzzabcdef"
	 * StringUtils.overlay("abcdef", "zzzz", 8, 10)  = "abcdefzzzz"
	 * </pre>
	 * 
	 * @param str
	 *            the String to do overlaying in, may be null
	 * @param overlay
	 *            the String to overlay, may be null
	 * @param start
	 *            the position to start overlaying at
	 * @param end
	 *            the position to stop overlaying before
	 * @return overlayed String, {@code null} if null String input
	 * @since 2.0
	 */
	public static String overlay(String str, String overlay, int start, int end) {
		if (str == null) {
			return null;
		}
		if (overlay == null) {
			overlay = EMPTY;
		}
		int len = str.length();
		if (start < 0) {
			start = 0;
		}
		if (start > len) {
			start = len;
		}
		if (end < 0) {
			end = 0;
		}
		if (end > len) {
			end = len;
		}
		if (start > end) {
			int temp = start;
			start = end;
			end = temp;
		}
		return new StringBuilder(len + start - end + overlay.length() + 1)
				.append(str.substring(0, start)).append(overlay)
				.append(str.substring(end)).toString();
	}

	/**
	 * pad a string on both sides to center it
	 * 
	 * @param str
	 *            string
	 * @param width
	 * @return Padded string
	 */
	public static String padCenter(final String str, final int width) {
		if (str.length() > width) {
			return str.substring(0, width);
		} else {
			boolean rigth = true;
			final StringBuilder padded = new StringBuilder(str);
			// noinspection MethodCallInLoopCondition
			while (padded.length() < width) {
				if (rigth) {
					padded.append(' ');
				} else {
					padded.insert(0, ' ');
				}
				rigth = !rigth;
			}
			return padded.toString();
		}
	}

	/**
	 * <p>
	 * Returns padding using the specified delimiter repeated to a given length.
	 * </p>
	 * 
	 * <pre>
	 * StringUtils.padding(0, 'e')  = ""
	 * StringUtils.padding(3, 'e')  = "eee"
	 * StringUtils.padding(-2, 'e') = IndexOutOfBoundsException
	 * </pre>
	 * 
	 * <p>
	 * Note: this method doesn't not support padding with <a
	 * href="http://www.unicode.org/glossary/#supplementary_character">Unicode
	 * Supplementary Characters</a> as they require a pair of <code>char</code>s
	 * to be represented. If you are needing to support full I18N of your
	 * applications consider using {@link #repeat(String, int)} instead.
	 * </p>
	 * 
	 * @param repeat
	 *            number of times to repeat delim
	 * @param padChar
	 *            character to repeat
	 * @return String with repeated character
	 * @throws IndexOutOfBoundsException
	 *             if <code>repeat &lt; 0</code>
	 * @see #repeat(String, int)
	 */
	private static String padding(int repeat, char padChar)
			throws IndexOutOfBoundsException {
		if (repeat < 0) {
			throw new IndexOutOfBoundsException(
					"Cannot pad a negative amount: " + repeat);
		}
		final char[] buf = new char[repeat];
		for (int i = 0; i < buf.length; i++) {
			buf[i] = padChar;
		}
		return new String(buf);
	}

	/**
	 * Extracts N-th from an array of argumens.
	 * 
	 * @param indexString
	 *            a String number
	 * @param args
	 *            array of arguments
	 * @return The indexString-th parameter from the array
	 */
	private static String processPattern(String indexString, Object[] args) {
		try {
			int index = Integer.parseInt(indexString);
			if ((args != null) && (index >= 0) && (index < args.length)) {
				if (args[index] != null) {
					return args[index].toString();
				}
			}
		} catch (NumberFormatException nfe) {
			// NFE - nothing bad basically - the argument is not a number
			// swallow it for the time being and show default string
		}
		return "?";
	}

	/**
	 * Remove extra spaces if they occur in a sequence
	 * 
	 * @param string
	 * @param removeLeadTrailSpaces
	 *            Flag to remove leading an trailing spaces
	 * @return String with spaces removed
	 */
	public static String removeExtraSpaces(String string,
			boolean removeLeadTrailSpaces) {
		StringBuilder buffer = new StringBuilder();
		if (removeLeadTrailSpaces) {
			string = string.trim();
		}
		byte wasASpace = 0;
		for (int i = 0; i < string.length(); i++) {
			if ((string.charAt(i) == ' ') && (wasASpace == 1)) {
				continue;
			} else if (string.charAt(i) == ' ') {
				wasASpace = 1;
			} else {
				wasASpace = 0;
			}

			buffer.append(string.charAt(i));
		}

		return buffer.toString();
	}

	/**
	 * <p>
	 * Repeat a String <code>repeat</code> times to form a new String.
	 * </p>
	 * 
	 * <pre>
	 * StringUtils.repeat(null, 2) = null
	 * StringUtils.repeat("", 0)   = ""
	 * StringUtils.repeat("", 2)   = ""
	 * StringUtils.repeat("a", 3)  = "aaa"
	 * StringUtils.repeat("ab", 2) = "abab"
	 * StringUtils.repeat("a", -2) = ""
	 * </pre>
	 * 
	 * @param str
	 *            the String to repeat, may be null
	 * @param repeat
	 *            number of times to repeat str, negative treated as zero
	 * @return a new String consisting of the original String repeated,
	 *         <code>null</code> if null String input
	 */
	public static String repeat(String str, int repeat) {
		// Performance tuned for 2.0 (JDK1.4)

		if (str == null) {
			return null;
		}
		if (repeat <= 0) {
			return EMPTY;
		}
		int inputLength = str.length();
		if (repeat == 1 || inputLength == 0) {
			return str;
		}
		if (inputLength == 1 && repeat <= PAD_LIMIT) {
			return padding(repeat, str.charAt(0));
		}

		int outputLength = inputLength * repeat;
		switch (inputLength) {
		case 1:
			char ch = str.charAt(0);
			char[] output1 = new char[outputLength];
			for (int i = repeat - 1; i >= 0; i--) {
				output1[i] = ch;
			}
			return new String(output1);
		case 2:
			char ch0 = str.charAt(0);
			char ch1 = str.charAt(1);
			char[] output2 = new char[outputLength];
			for (int i = repeat * 2 - 2; i >= 0; i--, i--) {
				output2[i] = ch0;
				output2[i + 1] = ch1;
			}
			return new String(output2);
		default:
			StringBuilder buf = new StringBuilder(outputLength);
			for (int i = 0; i < repeat; i++) {
				buf.append(str);
			}
			return buf.toString();
		}
	}

	/**
	 * <p>
	 * Replaces all occurrences of a String within another String.
	 * </p>
	 * 
	 * <p>
	 * A <code>null</code> reference passed to this method is a no-op.
	 * </p>
	 * 
	 * <pre>
	 * StringUtils.replace(null, *, *)        = null
	 * StringUtils.replace("", *, *)          = ""
	 * StringUtils.replace("any", null, *)    = "any"
	 * StringUtils.replace("any", *, null)    = "any"
	 * StringUtils.replace("any", "", *)      = "any"
	 * StringUtils.replace("aba", "a", null)  = "aba"
	 * StringUtils.replace("aba", "a", "")    = "b"
	 * StringUtils.replace("aba", "a", "z")   = "zbz"
	 * </pre>
	 * 
	 * @see #replace(String text, String searchString, String replacement, int
	 *      max)
	 * @param text
	 *            text to search and replace in, may be null
	 * @param searchString
	 *            the String to search for, may be null
	 * @param replacement
	 *            the String to replace it with, may be null
	 * @return the text with any replacements processed, <code>null</code> if
	 *         null String input
	 */
	public static String replace(String text, String searchString,
			String replacement) {
		return replace(text, searchString, replacement, -1);
	}

	/**
	 * <p>
	 * Replaces a String with another String inside a larger String, for the
	 * first <code>max</code> values of the search String.
	 * </p>
	 * 
	 * <p>
	 * A <code>null</code> reference passed to this method is a no-op.
	 * </p>
	 * 
	 * <pre>
	 * StringUtils.replace(null, *, *, *)         = null
	 * StringUtils.replace("", *, *, *)           = ""
	 * StringUtils.replace("any", null, *, *)     = "any"
	 * StringUtils.replace("any", *, null, *)     = "any"
	 * StringUtils.replace("any", "", *, *)       = "any"
	 * StringUtils.replace("any", *, *, 0)        = "any"
	 * StringUtils.replace("abaa", "a", null, -1) = "abaa"
	 * StringUtils.replace("abaa", "a", "", -1)   = "b"
	 * StringUtils.replace("abaa", "a", "z", 0)   = "abaa"
	 * StringUtils.replace("abaa", "a", "z", 1)   = "zbaa"
	 * StringUtils.replace("abaa", "a", "z", 2)   = "zbza"
	 * StringUtils.replace("abaa", "a", "z", -1)  = "zbzz"
	 * </pre>
	 * 
	 * @param text
	 *            text to search and replace in, may be null
	 * @param searchString
	 *            the String to search for, may be null
	 * @param replacement
	 *            the String to replace it with, may be null
	 * @param max
	 *            maximum number of values to replace, or <code>-1</code> if no
	 *            maximum
	 * @return the text with any replacements processed, <code>null</code> if
	 *         null String input
	 */
	public static String replace(String text, String searchString,
			String replacement, int max) {
		if (isBlank(text) || isBlank(searchString) || replacement == null
				|| max == 0) {
			return text;
		}
		int start = 0;
		int end = text.indexOf(searchString, start);
		if (end == -1) {
			return text;
		}
		int replLength = searchString.length();
		int increase = replacement.length() - replLength;
		increase = (increase < 0 ? 0 : increase);
		increase *= (max < 0 ? 16 : (max > 64 ? 64 : max));
		StringBuilder buf = new StringBuilder(text.length() + increase);
		while (end != -1) {
			buf.append(text.substring(start, end)).append(replacement);
			start = end + replLength;
			if (--max == 0) {
				break;
			}
			end = text.indexOf(searchString, start);
		}
		buf.append(text.substring(start));
		return buf.toString();
	}

	/**
	 * Replace all occurance of the given string
	 * 
	 * @param input
	 *            string
	 * @param search
	 * @param replace
	 *            string to replace
	 * @return Replaced string
	 */
	public static String replaceAll(String input, String search, String replace) {
		StringBuilder buffer = new StringBuilder();
		byte found = 0;

		for (int i = 0; i < input.length(); i++) {
			if (input.charAt(i) == search.charAt(0)) {
				found = 1;
				for (int j = 0; j < search.length(); j++) {
					if (input.charAt(i + j) != search.charAt(j)) {
						found = 0;
						break;
					}
				}

				if (found == 1) {
					buffer.append(replace);
					i += search.length() - 1;
					continue;
				}
			} else {
				buffer.append(input.charAt(i));
			}
		}

		return buffer.toString();
	}

	/**
	 * Replace the first occurance of the the <code>search</code> string
	 * 
	 * @param input
	 *            string
	 * @param search
	 *            string to find
	 * @param replace
	 *            string to replace
	 * @return Replaced string
	 */
	public static String replaceFirst(String input, String search,
			String replace) {
		int pos = input.indexOf(search);
		if (pos != -1) {
			input = input.substring(0, pos) + replace
					+ input.substring(pos + search.length());
		}
		return input;
	}

	/**
	 * Replace the last occurance of the <code>search</code> string
	 * 
	 * @param input
	 *            string
	 * @param search
	 *            string to find
	 * @param replace
	 *            string to replace
	 * @return Replaced string
	 */
	public static String replaceLast(String input, String search, String replace) {
		int pos = input.indexOf(search);
		if (pos != -1) {
			int lastPos = pos;
			while (true) {
				pos = input.indexOf(search, lastPos + 1);
				if (pos == -1) {
					break;
				} else {
					lastPos = pos;
				}
			}
			input = input.substring(0, lastPos) + replace
					+ input.substring(lastPos + search.length());
		}
		return input;
	}

	/**
	 * <p>
	 * Replaces a String with another String inside a larger String, once.
	 * </p>
	 * 
	 * <p>
	 * A <code>null</code> reference passed to this method is a no-op.
	 * </p>
	 * 
	 * <pre>
	 * StringUtils.replaceOnce(null, *, *)        = null
	 * StringUtils.replaceOnce("", *, *)          = ""
	 * StringUtils.replaceOnce("any", null, *)    = "any"
	 * StringUtils.replaceOnce("any", *, null)    = "any"
	 * StringUtils.replaceOnce("any", "", *)      = "any"
	 * StringUtils.replaceOnce("aba", "a", null)  = "aba"
	 * StringUtils.replaceOnce("aba", "a", "")    = "ba"
	 * StringUtils.replaceOnce("aba", "a", "z")   = "zba"
	 * </pre>
	 * 
	 * @see #replace(String text, String searchString, String replacement, int
	 *      max)
	 * @param text
	 *            text to search and replace in, may be null
	 * @param searchString
	 *            the String to search for, may be null
	 * @param replacement
	 *            the String to replace with, may be null
	 * @return the text with any replacements processed, <code>null</code> if
	 *         null String input
	 */
	public static String replaceOnce(String text, String searchString,
			String replacement) {
		return replace(text, searchString, replacement, 1);
	}

	/**
	 * Reverse a given string
	 * 
	 * @param text
	 * @return reversed text
	 */
	public static String reverse(String text) {
		StringBuilder buffer = new StringBuilder();
		for (int i = 0; i < text.length(); i++) {
			buffer.append(text.charAt(text.length() - 1 - i));
		}
		return buffer.toString();
	}

	/**
	 * <p>
	 * Right pad a String with spaces (' ').
	 * </p>
	 * 
	 * <p>
	 * The String is padded to the size of <code>size</code>.
	 * </p>
	 * 
	 * <pre>
	 * StringUtils.rightPad(null, *)   = null
	 * StringUtils.rightPad("", 3)     = "   "
	 * StringUtils.rightPad("bat", 3)  = "bat"
	 * StringUtils.rightPad("bat", 5)  = "bat  "
	 * StringUtils.rightPad("bat", 1)  = "bat"
	 * StringUtils.rightPad("bat", -1) = "bat"
	 * </pre>
	 * 
	 * @param str
	 *            the String to pad out, may be null
	 * @param size
	 *            the size to pad to
	 * @return right padded String or original String if no padding is
	 *         necessary, <code>null</code> if null String input
	 */
	public static String rightPad(String str, int size) {
		return rightPad(str, size, ' ');
	}

	/**
	 * <p>
	 * Right pad a String with a specified character.
	 * </p>
	 * 
	 * <p>
	 * The String is padded to the size of <code>size</code>.
	 * </p>
	 * 
	 * <pre>
	 * StringUtils.rightPad(null, *, *)     = null
	 * StringUtils.rightPad("", 3, 'z')     = "zzz"
	 * StringUtils.rightPad("bat", 3, 'z')  = "bat"
	 * StringUtils.rightPad("bat", 5, 'z')  = "batzz"
	 * StringUtils.rightPad("bat", 1, 'z')  = "bat"
	 * StringUtils.rightPad("bat", -1, 'z') = "bat"
	 * </pre>
	 * 
	 * @param str
	 *            the String to pad out, may be null
	 * @param size
	 *            the size to pad to
	 * @param padChar
	 *            the character to pad with
	 * @return right padded String or original String if no padding is
	 *         necessary, <code>null</code> if null String input
	 */
	public static String rightPad(String str, int size, char padChar) {
		if (str == null) {
			return null;
		}
		int pads = size - str.length();
		if (pads <= 0) {
			return str; // returns original String when possible
		}
		if (pads > PAD_LIMIT) {
			return rightPad(str, size, String.valueOf(padChar));
		}
		return str.concat(padding(pads, padChar));
	}

	// /**
	// * Strips any defined character from the start and end of a String.
	// *
	// * @param str
	// * String to strip.
	// * @param character
	// * Character to strip from string.
	// * @return Stripped string.
	// */
	// private static String stripChar(final String str, final char character) {
	// StringBuilder stripBuffer = new StringBuilder(str);
	// boolean search = true;
	//
	// while(search && (stripBuffer.length() > 0)){
	// // strip the tail
	// char stripChar = stripBuffer.charAt(stripBuffer.length() - 1);
	// if(stripChar == character){
	// stripBuffer.deleteCharAt(stripBuffer.length() - 1);
	// } else{
	// search = false;
	// }
	// // strip the head
	// if(stripBuffer.length() > 0){
	// stripChar = stripBuffer.charAt(0);
	// if(stripChar == character){
	// stripBuffer.deleteCharAt(0);
	// search = true;
	// }
	// }
	// }
	// return stripBuffer.toString();
	// }

	/**
	 * <p>
	 * Right pad a String with a specified String.
	 * </p>
	 * 
	 * <p>
	 * The String is padded to the size of <code>size</code>.
	 * </p>
	 * 
	 * <pre>
	 * StringUtils.rightPad(null, *, *)      = null
	 * StringUtils.rightPad("", 3, "z")      = "zzz"
	 * StringUtils.rightPad("bat", 3, "yz")  = "bat"
	 * StringUtils.rightPad("bat", 5, "yz")  = "batyz"
	 * StringUtils.rightPad("bat", 8, "yz")  = "batyzyzy"
	 * StringUtils.rightPad("bat", 1, "yz")  = "bat"
	 * StringUtils.rightPad("bat", -1, "yz") = "bat"
	 * StringUtils.rightPad("bat", 5, null)  = "bat  "
	 * StringUtils.rightPad("bat", 5, "")    = "bat  "
	 * </pre>
	 * 
	 * @param str
	 *            the String to pad out, may be null
	 * @param size
	 *            the size to pad to
	 * @param padStr
	 *            the String to pad with, null or empty treated as single space
	 * @return right padded String or original String if no padding is
	 *         necessary, <code>null</code> if null String input
	 */
	public static String rightPad(String str, int size, String padStr) {
		if (str == null) {
			return null;
		}
		if (isBlank(padStr)) {
			padStr = " ";
		}
		int padLen = padStr.length();
		int strLen = str.length();
		int pads = size - strLen;
		if (pads <= 0) {
			return str; // returns original String when possible
		}
		if (padLen == 1 && pads <= PAD_LIMIT) {
			return rightPad(str, size, padStr.charAt(0));
		}

		if (pads == padLen) {
			return str.concat(padStr);
		} else if (pads < padLen) {
			return str.concat(padStr.substring(0, pads));
		} else {
			char[] padding = new char[pads];
			char[] padChars = padStr.toCharArray();
			for (int i = 0; i < pads; i++) {
				padding[i] = padChars[i % padLen];
			}
			return str.concat(new String(padding));
		}
	}

	/**
	 * Splits the provided text into an array, using whitespace as the
	 * separator.
	 * 
	 * @param str
	 *            the String to parse, may be null.
	 * @return an array of parsed Strings, null if null String input
	 */
	public static String[] split(final String str) {
		return split(str, null, -1);
	}

	/**
	 * Split a string into an array of string
	 * 
	 * @param toSplit
	 *            string to split
	 * @param delimiter
	 *            character
	 * @param ignoreEmpty
	 *            flag to ignore empty spaces
	 * @return array of string that is split at the given delimiter
	 */
	public static String[] split(String toSplit, char delimiter,
			boolean ignoreEmpty) {
		StringBuilder buffer = new StringBuilder();
		java.util.Stack<String> stringStack = new java.util.Stack<String>();
		try {
			for (int i = 0; i < toSplit.length(); i++) {
				if (toSplit.charAt(i) != delimiter) {

					buffer.append(toSplit.charAt(i));
				} else {
					if ((buffer.toString().trim().length() == 0) && ignoreEmpty) {

					} else {
						stringStack.addElement(buffer.toString());
					}
					buffer = new StringBuilder();
				}
			}
		} catch (StringIndexOutOfBoundsException e) {
			System.out.println("[StringUtil.split] " + e.toString());
		}
		if (buffer.length() != 0) {
			stringStack.addElement(buffer.toString());
		}

		String[] split = new String[stringStack.size()];
		for (int i = 0; i < split.length; i++) {
			split[split.length - 1 - i] = stringStack.pop();
		}
		stringStack = null;
		buffer = null;
		return split;
	}

	/**
	 * Splits the provided text into an array with a maximum length, separators
	 * specified.
	 * 
	 * @param str
	 *            the String to parse, may be null.
	 * @param separatorChars
	 *            the characters used as the delimiters, null splits on
	 *            whitespace
	 * @param max
	 *            the maximum number of elements to include in the array. A zero
	 *            or negative value implies no limit
	 * 
	 * @return an array of parsed Strings, null if null String input.
	 */
	public static String[] split(final String str, final String separatorChars,
			final int max) {
		return splitWorker(str, separatorChars, max, false);
	}

	private static String[] splitWorker(final String str,
			final String separatorChars, final int max,
			final boolean preserveAllTokens) {

		String[] result = null;

		if ((str != null) && (str.length() == 0)) {
			result = new String[0];
		} else if (str != null) {
			int len = str.length();
			ArrayList<String> list = new ArrayList<String>();
			int sizePlus1 = 1;
			int i = 0, start = 0;
			boolean match = false;
			boolean lastMatch = false;
			if (separatorChars == null) {
				// Null separator means use whitespace
				while (i < len) {
					if (SPACE == str.charAt(i)) {
						if (match || preserveAllTokens) {
							lastMatch = true;
							if (sizePlus1++ == max) {
								i = len;
								lastMatch = false;
							}
							list.add(str.substring(start, i));
							match = false;
						}
						start = ++i;
						continue;
					}
					lastMatch = false;
					match = true;
					i++;
				}
			} else if (separatorChars.length() == 1) {
				// Optimise 1 character case
				char sep = separatorChars.charAt(0);
				while (i < len) {
					if (str.charAt(i) == sep) {
						if (match || preserveAllTokens) {
							lastMatch = true;
							if (sizePlus1++ == max) {
								i = len;
								lastMatch = false;
							}
							list.add(str.substring(start, i));
							match = false;
						}
						start = ++i;
						continue;
					}
					lastMatch = false;
					match = true;
					i++;
				}
			} else {
				// standard case
				while (i < len) {
					if (separatorChars.indexOf(str.charAt(i)) >= 0) {
						if (match || preserveAllTokens) {
							lastMatch = true;
							if (sizePlus1++ == max) {
								i = len;
								lastMatch = false;
							}
							list.add(str.substring(start, i));
							match = false;
						}
						start = ++i;
						continue;
					}
					lastMatch = false;
					match = true;
					i++;
				}
			}
			if (match || (preserveAllTokens && lastMatch)) {
				list.add(str.substring(start, i));
			}

			result = new String[list.size()];
			for (int j = 0; j < list.size(); j++) {
				result[j] = list.get(j);
			}
		}
		return result;

	}

	/**
	 * 
	 * @param base
	 *            String to compare
	 * @param start
	 *            Starting string
	 * @return <code>start</code> string
	 */
	public static boolean startsWithIgnoreCase(String base, String start) {
		if (base.length() < start.length()) {
			return false;
		}
		return base.regionMatches(true, 0, start, 0, start.length());
	}

	/**
	 * Strips any of a set of characters from the start and end of a String.
	 * This method allows control of the characters to be stripped.
	 * 
	 * A null input String returns null. An empty string ("") input returns the
	 * empty string.
	 * 
	 * If the stripChars String is null, whitespace is stripped
	 * 
	 * @param str
	 *            String to strip.
	 * @param stripChars
	 *            Characters to strip.
	 * @return The striped String, or an empty String if null input.
	 */
	public static String strip(final String str, final String stripChars) {
		String result = str;
		if (!isBlank(str)) {
			result = stripEnd(stripStart(str, stripChars), stripChars);
		}
		return result;
	}

	/**
	 * Remove all the strings contained in the string array from the source
	 * string
	 * 
	 * @param str
	 *            String to strip
	 * @param toStrip
	 *            strings that need to be stripped
	 * @return
	 */
	public static String strip(String str, String[] toStrip) {
		for (int i = 0; i < toStrip.length; i++)
			str = str.replace(toStrip[i], "");
		return str;
	}

	/**
	 * Strips any of a set of characters from the end of a String. A null input
	 * String returns null. An empty string ("") input returns the empty string.
	 * 
	 * @param str
	 *            The String to remove characters from, may be null.
	 * @param stripChars
	 *            The characters to remove, null treated as whitespace.
	 * @return The stripped String, null if null String input.
	 */
	public static String stripEnd(final String str, final String stripChars) {
		String result = str;
		if (!isBlank(str)) {

			int end = str.length();
			if (stripChars == null) {
				while ((end != 0) && (SPACE == str.charAt(end - 1))) {
					end--;
				}
			} else if (stripChars.length() != 0) {
				while ((end != 0)
						&& (stripChars.indexOf(str.charAt(end - 1)) != -1)) {
					end--;
				}
			}
			result = str.substring(0, end);
		}
		return result;
	}

	/**
	 * Strips any of a set of characters from the start of a String. A null
	 * input String returns null. An empty string ("") input returns the empty
	 * string.
	 * 
	 * @param str
	 *            the String to remove characters from, may be null.
	 * @param stripChars
	 *            the characters to remove, null treated as whitespace.
	 * @return The stripped String or null(if str is null.
	 */
	public static String stripStart(final String str, final String stripChars) {
		String result = str;
		if (!isBlank(str)) {
			int start = 0;
			if (stripChars == null) {
				while ((start != str.length()) && (SPACE == str.charAt(start))) {
					start++;
				}
			} else if (stripChars.length() != 0) {
				while ((start != str.length())
						&& (stripChars.indexOf(str.charAt(start)) != -1)) {
					start++;
				}
			}
			result = str.substring(start);
		}
		return result;
	}

	/**
	 * Returns a new <code>CharSequence</code> that is a subsequence of this
	 * sequence starting with the <code>char</code> value at the specified
	 * index. The length (in <code>char</code>s) of the returned sequence is
	 * <code>length() - start</code>, so if <code>start == end</code> then an
	 * empty sequence is returned. </p>
	 * 
	 * @param cs
	 *            the specified subsequence, may be null
	 * @param start
	 *            the start index, inclusive
	 * @return a new subsequence or null
	 * 
	 * @throws IndexOutOfBoundsException
	 *             if <code>start</code> is negative or if <code>start</code> is
	 *             greater than <code>length()</code>
	 */
	public static CharSequence subSequence(CharSequence cs, int start) {
		return cs == null ? null : cs.subSequence(start, cs.length());
	}

	/**
	 * <p>
	 * Uncapitalizes a CharSequence changing the first letter to title case as
	 * per {@link Character#toLowerCase(char)}. No other letters are changed.
	 * </p>
	 * 
	 * <p>
	 * For a word based algorithm, see
	 * {@link org.apache.commons.lang3.text.WordUtils#uncapitalize(String)}. A
	 * <code>null</code> input String returns <code>null</code>.
	 * </p>
	 * 
	 * <pre>
	 * StringUtils.uncapitalize(null)  = null
	 * StringUtils.uncapitalize("")    = ""
	 * StringUtils.uncapitalize("Cat") = "cat"
	 * StringUtils.uncapitalize("CAT") = "cAT"
	 * </pre>
	 * 
	 * @param cs
	 *            the String to uncapitalize, may be null
	 * @return the uncapitalized String, <code>null</code> if null String input
	 * @see org.apache.commons.lang3.text.WordUtils#uncapitalize(String)
	 * @see #capitalize(CharSequence)
	 */
	public static String uncapitalize(CharSequence cs) {
		if (cs == null) {
			return null;
		}
		int strLen;
		if ((strLen = cs.length()) == 0) {
			return cs.toString();
		}
		return new StringBuilder(strLen)
				.append(Character.toLowerCase(cs.charAt(0)))
				.append(subSequence(cs, 1)).toString();
	}

	private StringUtils() {
	}
}
