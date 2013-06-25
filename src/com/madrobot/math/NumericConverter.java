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
package com.madrobot.math;

/**
 * Contains all types of numeric & date conversion functions
 * 
 * 
 */
public final class NumericConverter {

	/**
	 * Convert two characters representing hex 00 to ff (or FF) to a byte.
	 */
	public static int hexToByte(char c1, char c2) {
		int b;
		if((c1 >= 'a')){
			b = c1 - 'a' + 10;
		} else if(c1 >= 'A'){
			b = c1 - 'A' + 10;
		} else{
			b = c1 - '0';
		}
		b <<= 4;
		if((c2 >= 'a')){
			b |= c2 - 'a' + 10;
		} else if(c2 >= 'A'){
			b |= c2 - 'A' + 10;
		} else{
			b |= c2 - '0';
		}
		return b;
	}

	private static void putBytes(byte[] buf, int pos, int val) {
		buf[pos++] = (byte) (val >>> 24);
		buf[pos++] = (byte) (val >>> 16);
		buf[pos++] = (byte) (val >>> 8);
		buf[pos++] = (byte) (val >>> 0);
	}

	private static void putBytes(byte[] buf, int pos, long val) {
		buf[pos++] = (byte) (val >>> 56);
		buf[pos++] = (byte) (val >>> 48);
		buf[pos++] = (byte) (val >>> 40);
		buf[pos++] = (byte) (val >>> 32);
		buf[pos++] = (byte) (val >>> 24);
		buf[pos++] = (byte) (val >>> 16);
		buf[pos++] = (byte) (val >>> 8);
		buf[pos++] = (byte) (val >>> 0);
	}

	/**
	 * Convert a String to byte
	 * 
	 * @param str
	 * @return Byte warpper for the primitive
	 */
	public static Byte toByte(String str) {
		try{
			return (str != null) ? new Byte(Byte.parseByte(str.trim())) : null;
		} catch(NumberFormatException ex){
			return null;
		}
	}

	/**
	 * Convert a int to a byte[]
	 * 
	 * @param val
	 *            integer
	 * @return byte arrya
	 */
	public static byte[] toBytes(int val) {
		byte[] result = new byte[4];
		putBytes(result, 0, val);
		return result;
	}

	/**
	 * convert a long to byte[]
	 * 
	 * @param value
	 * @return Array of byte representing <code> value</code>
	 */
	public static byte[] toBytes(long value) {
		byte[] result = new byte[8];
		putBytes(result, 0, value);
		return result;
	}

	/**
	 * Convert a short to a byte array and set it into <code>buffer</code> at
	 * specified <code>offset</code>
	 * 
	 * @param value
	 * @param buffer
	 * @param offset
	 * @return Array of byte representing <code> value</code>
	 */
	public static void toBytes(short value, byte[] buffer, int offset) {
		if(buffer.length - offset < 2){
			throw new ArrayIndexOutOfBoundsException();
		}

		buffer[offset] = (byte) (value >> 8);
		buffer[offset + 1] = (byte) value;
	}

	/**
	 * Convert a byte array to an int value
	 * 
	 * @param bytes
	 * @param offset
	 * @return the int value corresponding to the 4 first bytes of the byte
	 *         array
	 */
	public static int toInt(byte[] bytes, int offset) {
		int value = 0;
		if((bytes != null) && (bytes.length >= 4)){
			for(int i = 0; i < 4; ++i){
				value += (0x000000FF & bytes[offset + i]) << ((3 - i) * 8);
			}
		}
		return value;
	}

	/**
	 * Convert a byte array to a long value
	 * 
	 * @param bytes
	 * @param offset
	 * @return the long value corresponding to the 8 first bytes of the byte
	 *         array
	 */
	public static long toLong(byte[] bytes, int offset) {
		int value = 0;
		if((bytes != null) && (bytes.length >= 8)){
			for(int i = 0; i < 8; ++i){
				value += (0x000000FF & bytes[offset + i]) << ((7 - i) * 8);
			}
		}
		return value;
	}

	/**
	 * Convert a byte array to a short value
	 * 
	 * @param bytes
	 * @param offset
	 * @return the short value corresponding to the 2 first bytes of the byte
	 *         array
	 */
	public static short toShort(byte[] bytes, int offset) {
		short value = 0;
		if((bytes != null) && (bytes.length >= 2)){
			value += (0x000000FF & bytes[offset]) << 8;
			value += (0x000000FF & bytes[offset + 1]);
		}
		return value;
	}

	public static byte toSignedByte(byte b) {
		return (byte) (b + Byte.MIN_VALUE);
	}

	public static int toUnsignedInt(byte b) {
		return (b - Byte.MIN_VALUE);
	}

}
