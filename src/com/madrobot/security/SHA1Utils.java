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
package com.madrobot.security;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.MessageDigest;
import java.util.Arrays;

import com.madrobot.io.IOUtils;

/**
 * 
 *
 */
public class SHA1Utils {

	final static String CLASS_NAME = "SHA1Util";

	/**
	 * This function encodes byte[] into a hex
	 * 
	 * @param b
	 * @return
	 */
	public static String byteArrayToHexString(byte[] b) {
		if(b == null)
			return null;

		StringBuffer sb = new StringBuffer(b.length * 2);
		for(int i = 0; i < b.length; i++){
			int v = b[i] & 0xff;
			if(v < 16){
				sb.append('0');
			}
			sb.append(Integer.toHexString(v));
		}
		return sb.toString().toUpperCase();
	}

	/**
	 * This function compares two bytes[]
	 * 
	 * @param b1
	 * @param b2
	 * @return
	 */
	public static boolean compareByteArrays(byte[] b1, byte[] b2) {
		return b1 != null && b2 != null && Arrays.equals(b1, b2);
	}

	/**
	 * This function compares two Strings.
	 * 
	 * @param s1
	 * @param s2
	 * @return
	 */
	public static boolean compareHexString(String s1, String s2) {
		return s1 != null && s2 != null && s1.equalsIgnoreCase(s2);
	}

	/**
	 * This function generates a SHA1 byte[] from another byte[].
	 * 
	 * @param bytes
	 * @return
	 */
	public static byte[] generateSHA1(byte[] bytes) {
		byte[] encryted = null;
		try{
			MessageDigest digest = MessageDigest.getInstance("SHA-1");
			digest.reset();
			digest.update(bytes);
			encryted = digest.digest();

		} catch(Exception e){
			e.printStackTrace();
		}
		return encryted;
	}

	// Genera SHA-1 de un char[]
	public static byte[] generateSHA1(char chars[]) {
		return generateSHA1(new String(chars));
	}

	/**
	 * This function generates a SHA1 byte[] from a file
	 * 
	 * @param file
	 * @return
	 */
	public static byte[] generateSHA1(File file) {
		try{
			return generateSHA1(new FileInputStream(file));
		} catch(Exception e){
			return null;
		}
	}

	// Genera SHA-1 de un InputStream
	public static byte[] generateSHA1(InputStream is) {
		try{
			return generateSHA1(IOUtils.toByteArray(is));
		} catch(Exception e){
			return null;
		}
	}

	// Genera SHA-1 de un String
	public static byte[] generateSHA1(String str) {
		return generateSHA1(str.getBytes());
	}

	/**
	 * This function converts an InputStream into a SHA1 String
	 * 
	 * @param is
	 * @return
	 */
	public static String generateSHA1toString(InputStream is) {
		try{
			return new String((generateSHA1(IOUtils.toByteArray(is))),"ISO-8859-1");
		} catch(Exception e){
			return null;
		}
	}

	/**
	 * This function converts a string without conding into a String encoded
	 * into a SHA1
	 * 
	 * @param str
	 * @return
	 */
	public static String generateSHA1toString(String str) {
		try{
			byte[] datos = generateSHA1(str.getBytes());
			return byteArrayToHexString(datos);

		} catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * This function encodes a Hex String into a byte[]
	 * 
	 * @param s
	 * @return
	 */
	public static byte[] hexStringToByteArray(String s) {
		if(s == null)
			return null;

		byte[] b = new byte[s.length() / 2];
		for(int i = 0; i < b.length; i++){
			int index = i * 2;
			int v = Integer.parseInt(s.substring(index, index + 2), 16);
			b[i] = (byte) v;
		}
		return b;
	}

}
