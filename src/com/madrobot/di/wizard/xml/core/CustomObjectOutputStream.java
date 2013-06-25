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
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.Map;

import com.madrobot.di.wizard.xml.converters.ConversionException;
import com.madrobot.di.wizard.xml.converters.DataHolder;
import com.madrobot.util.FastStack;
import com.madrobot.util.OrderRetainingMap;

public class CustomObjectOutputStream extends ObjectOutputStream {

	private class CustomPutField extends PutField {

		private final Map fields = new OrderRetainingMap();

		public Map asMap() {
			return fields;
		}

		@Override
		public void put(String name, boolean val) {
			put(name, val ? Boolean.TRUE : Boolean.FALSE); // JDK 1.3 friendly
		}

		@Override
		public void put(String name, byte val) {
			put(name, new Byte(val));
		}

		@Override
		public void put(String name, char val) {
			put(name, new Character(val));
		}

		@Override
		public void put(String name, double val) {
			put(name, new Double(val));
		}

		@Override
		public void put(String name, float val) {
			put(name, new Float(val));
		}

		@Override
		public void put(String name, int val) {
			put(name, new Integer(val));
		}

		@Override
		public void put(String name, long val) {
			put(name, new Long(val));
		}

		@Override
		public void put(String name, Object val) {
			fields.put(name, val);
		}

		@Override
		public void put(String name, short val) {
			put(name, new Short(val));
		}

		@Override
		public void write(ObjectOutput out) throws IOException {
			peekCallback().writeToStream(asMap());
		}

	}
	public static interface StreamCallback {
		void close() throws IOException;

		void defaultWriteObject() throws IOException;

		void flush() throws IOException;

		void writeFieldsToStream(Map fields) throws IOException;

		void writeToStream(Object object) throws IOException;
	}

	private static final String DATA_HOLDER_KEY = CustomObjectOutputStream.class.getName();

	public static synchronized CustomObjectOutputStream getInstance(DataHolder whereFrom, StreamCallback callback) {
		try {
			CustomObjectOutputStream result = (CustomObjectOutputStream) whereFrom.get(DATA_HOLDER_KEY);
			if (result == null) {
				result = new CustomObjectOutputStream(callback);
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

	private FastStack customFields = new FastStack(1);

	/**
	 * Warning, this object is expensive to create (due to functionality inherited from superclass). Use the static
	 * fetch() method instead, wherever possible.
	 * 
	 * @see #getInstance(com.madrobot.di.wizard.xml.converters.DataHolder,
	 *      com.madrobot.di.xml.core.thoughtworks.xstream.core.util.CustomObjectOutputStream.StreamCallback)
	 */
	public CustomObjectOutputStream(StreamCallback callback) throws IOException, SecurityException {
		this.callbacks.push(callback);
	}

	@Override
	public void close() throws IOException {
		peekCallback().close();
	}

	/*** Methods to delegate to callback ***/

	@Override
	public void defaultWriteObject() throws IOException {
		peekCallback().defaultWriteObject();
	}

	@Override
	public void flush() throws IOException {
		peekCallback().flush();
	}

	public StreamCallback peekCallback() {
		return (StreamCallback) this.callbacks.peek();
	}

	public StreamCallback popCallback() {
		return (StreamCallback) this.callbacks.pop();
	}

	/**
	 * Allows the CustomObjectOutputStream (which is expensive to create) to be reused.
	 */
	public void pushCallback(StreamCallback callback) {
		this.callbacks.push(callback);
	}

	@Override
	public PutField putFields() {
		CustomPutField result = new CustomPutField();
		customFields.push(result);
		return result;
	}

	/****** Unsupported methods ******/

	@Override
	public void reset() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void useProtocolVersion(int version) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void write(byte[] buf) throws IOException {
		peekCallback().writeToStream(buf);
	}

	@Override
	public void write(byte[] buf, int off, int len) throws IOException {
		byte[] b = new byte[len];
		System.arraycopy(buf, off, b, 0, len);
		peekCallback().writeToStream(b);
	}

	@Override
	public void write(int val) throws IOException {
		peekCallback().writeToStream(new Byte((byte) val));
	}

	@Override
	public void writeBoolean(boolean val) throws IOException {
		peekCallback().writeToStream(val ? Boolean.TRUE : Boolean.FALSE); // JDK 1.3 friendly
	}

	@Override
	public void writeByte(int val) throws IOException {
		peekCallback().writeToStream(new Byte((byte) val));
	}

	@Override
	public void writeBytes(String str) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void writeChar(int val) throws IOException {
		peekCallback().writeToStream(new Character((char) val));
	}

	@Override
	public void writeChars(String str) throws IOException {
		peekCallback().writeToStream(str.toCharArray());
	}

	@Override
	public void writeDouble(double val) throws IOException {
		peekCallback().writeToStream(new Double(val));
	}

	@Override
	public void writeFields() throws IOException {
		CustomPutField customPutField = (CustomPutField) customFields.pop();
		peekCallback().writeFieldsToStream(customPutField.asMap());
	}

	@Override
	public void writeFloat(float val) throws IOException {
		peekCallback().writeToStream(new Float(val));
	}

	@Override
	public void writeInt(int val) throws IOException {
		peekCallback().writeToStream(new Integer(val));
	}

	@Override
	public void writeLong(long val) throws IOException {
		peekCallback().writeToStream(new Long(val));
	}

	@Override
	protected void writeObjectOverride(Object obj) throws IOException {
		peekCallback().writeToStream(obj);
	}

	@Override
	public void writeShort(int val) throws IOException {
		peekCallback().writeToStream(new Short((short) val));
	}

	@Override
	public void writeUnshared(Object obj) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void writeUTF(String str) throws IOException {
		peekCallback().writeToStream(str);
	}

}
