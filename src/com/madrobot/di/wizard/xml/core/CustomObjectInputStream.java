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

package com.madrobot.di.wizard.xml.core;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.NotActiveException;
import java.io.ObjectInputStream;
import java.io.ObjectInputValidation;
import java.io.ObjectStreamClass;
import java.io.StreamCorruptedException;
import java.util.Map;

import com.madrobot.di.wizard.xml.converters.ConversionException;
import com.madrobot.di.wizard.xml.converters.DataHolder;
import com.madrobot.util.FastStack;

public class CustomObjectInputStream extends ObjectInputStream {

	private class CustomGetField extends GetField {

		private Map fields;

		public CustomGetField(Map fields) {
			this.fields = fields;
		}

		@Override
		public boolean defaulted(String name) {
			return !fields.containsKey(name);
		}

		private Object get(String name) {
			return fields.get(name);
		}

		@Override
		public boolean get(String name, boolean val) {
			return defaulted(name) ? val : ((Boolean) get(name)).booleanValue();
		}

		@Override
		public byte get(String name, byte val) {
			return defaulted(name) ? val : ((Byte) get(name)).byteValue();
		}

		@Override
		public char get(String name, char val) {
			return defaulted(name) ? val : ((Character) get(name)).charValue();
		}

		@Override
		public double get(String name, double val) {
			return defaulted(name) ? val : ((Double) get(name)).doubleValue();
		}

		@Override
		public float get(String name, float val) {
			return defaulted(name) ? val : ((Float) get(name)).floatValue();
		}

		@Override
		public int get(String name, int val) {
			return defaulted(name) ? val : ((Integer) get(name)).intValue();
		}

		@Override
		public long get(String name, long val) {
			return defaulted(name) ? val : ((Long) get(name)).longValue();
		}

		@Override
		public Object get(String name, Object val) {
			return defaulted(name) ? val : get(name);
		}

		@Override
		public short get(String name, short val) {
			return defaulted(name) ? val : ((Short) get(name)).shortValue();
		}

		@Override
		public ObjectStreamClass getObjectStreamClass() {
			throw new UnsupportedOperationException();
		}

	}
	public static interface StreamCallback {
		void close() throws IOException;

		void defaultReadObject() throws IOException;

		Map readFieldsFromStream() throws IOException;

		Object readFromStream() throws IOException;

		void registerValidation(ObjectInputValidation validation, int priority) throws NotActiveException,
				InvalidObjectException;
	}

	private static final String DATA_HOLDER_KEY = CustomObjectInputStream.class.getName();

	/**
	 * @deprecated As of 1.4 use {@link #getInstance(DataHolder, StreamCallback, ClassLoader)}
	 */
	@Deprecated
	public static CustomObjectInputStream getInstance(DataHolder whereFrom, CustomObjectInputStream.StreamCallback callback) {
		return getInstance(whereFrom, callback, null);
	}

	public static synchronized CustomObjectInputStream getInstance(DataHolder whereFrom, CustomObjectInputStream.StreamCallback callback, ClassLoader classLoaderReference) {
		try {
			CustomObjectInputStream result = (CustomObjectInputStream) whereFrom.get(DATA_HOLDER_KEY);
			if (result == null) {
				result = new CustomObjectInputStream(callback, classLoaderReference);
				whereFrom.put(DATA_HOLDER_KEY, result);
			} else {
				result.pushCallback(callback);
			}
			return result;
		} catch (IOException e) {
			throw new ConversionException("Cannot create CustomObjectStream", e);
		}
	}

	private FastStack callbacks = new FastStack(1);

	private final ClassLoader classLoader;

	/**
	 * Warning, this object is expensive to create (due to functionality inherited from superclass). Use the static
	 * fetch() method instead, wherever possible.
	 * 
	 * @see #getInstance(DataHolder, StreamCallback, ClassLoader)
	 */
	public CustomObjectInputStream(StreamCallback callback, ClassLoader classLoader)
			throws IOException,
			SecurityException {
		super();
		this.callbacks.push(callback);
		this.classLoader = classLoader;
	}

	/****** Unsupported methods ******/

	@Override
	public int available() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void close() throws IOException {
		peekCallback().close();
	}

	@Override
	public void defaultReadObject() throws IOException {
		peekCallback().defaultReadObject();
	}

	@Override
	public void mark(int readlimit) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean markSupported() {
		return false;
	}

	public StreamCallback peekCallback() {
		return (StreamCallback) this.callbacks.peek();
	}

	public StreamCallback popCallback() {
		return (StreamCallback) this.callbacks.pop();
	}

	/**
	 * Allows the CustomObjectInputStream (which is expensive to create) to be reused.
	 */
	public void pushCallback(StreamCallback callback) {
		this.callbacks.push(callback);
	}

	@Override
	public int read() throws IOException {
		return readUnsignedByte();
	}

	@Override
	public int read(byte b[]) throws IOException {
		return read(b, 0, b.length);
	}

	@Override
	public int read(byte[] buf, int off, int len) throws IOException {
		byte[] b = (byte[]) peekCallback().readFromStream();
		if (b.length != len) {
			throw new StreamCorruptedException("Expected " + len + " bytes from stream, got " + b.length);
		}
		System.arraycopy(b, 0, buf, off, len);
		return len;
	}

	@Override
	public boolean readBoolean() throws IOException {
		return ((Boolean) peekCallback().readFromStream()).booleanValue();
	}

	@Override
	public byte readByte() throws IOException {
		return ((Byte) peekCallback().readFromStream()).byteValue();
	}

	@Override
	public char readChar() throws IOException {
		return ((Character) peekCallback().readFromStream()).charValue();
	}

	@Override
	public double readDouble() throws IOException {
		return ((Double) peekCallback().readFromStream()).doubleValue();
	}

	@Override
	public GetField readFields() throws IOException {
		return new CustomGetField(peekCallback().readFieldsFromStream());
	}

	@Override
	public float readFloat() throws IOException {
		return ((Float) peekCallback().readFromStream()).floatValue();
	}

	@Override
	public void readFully(byte[] buf) throws IOException {
		readFully(buf, 0, buf.length);
	}

	@Override
	public void readFully(byte[] buf, int off, int len) throws IOException {
		byte[] b = (byte[]) peekCallback().readFromStream();
		System.arraycopy(b, 0, buf, off, len);
	}

	@Override
	public int readInt() throws IOException {
		return ((Integer) peekCallback().readFromStream()).intValue();
	}

	@Override
	public String readLine() {
		throw new UnsupportedOperationException();
	}

	@Override
	public long readLong() throws IOException {
		return ((Long) peekCallback().readFromStream()).longValue();
	}

	@Override
	protected Object readObjectOverride() throws IOException {
		return peekCallback().readFromStream();
	}

	@Override
	public short readShort() throws IOException {
		return ((Short) peekCallback().readFromStream()).shortValue();
	}

	@Override
	public Object readUnshared() throws IOException, ClassNotFoundException {
		return readObject();
	}

	@Override
	public int readUnsignedByte() throws IOException {
		int b = ((Byte) peekCallback().readFromStream()).byteValue();
		if (b < 0) {
			b += Byte.MAX_VALUE;
		}
		return b;
	}

	@Override
	public int readUnsignedShort() throws IOException {
		int b = ((Short) peekCallback().readFromStream()).shortValue();
		if (b < 0) {
			b += Short.MAX_VALUE;
		}
		return b;
	}

	@Override
	public String readUTF() throws IOException {
		return (String) peekCallback().readFromStream();
	}

	@Override
	public void registerValidation(ObjectInputValidation validation, int priority) throws NotActiveException,
			InvalidObjectException {
		peekCallback().registerValidation(validation, priority);
	}

	@Override
	public void reset() {
		throw new UnsupportedOperationException();
	}

	@Override
	protected Class resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException {
		if (classLoader == null) {
			return super.resolveClass(desc);
		} else {
			return Class.forName(desc.getName(), false, classLoader);
		}
	}

	@Override
	public long skip(long n) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int skipBytes(int len) {
		throw new UnsupportedOperationException();
	}

}
