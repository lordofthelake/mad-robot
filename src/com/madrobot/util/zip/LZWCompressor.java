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
package com.madrobot.util.zip;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.madrobot.io.BitOutputStream;

public class LZWCompressor
{

	// private static final int MAX_TABLE_SIZE = 1 << 12;

	private final static class ByteArray
	{
		private final byte bytes[];
		private final int hash;
		private final int length;
		private final int start;

		public ByteArray(byte bytes[])
		{
			this(bytes, 0, bytes.length);
		}

		public ByteArray(byte bytes[], int start, int length)
		{
			this.bytes = bytes;
			this.start = start;
			this.length = length;

			int tempHash = length;

			for (int i = 0; i < length; i++)
			{
				int b = 0xff & bytes[i + start];
				tempHash = tempHash + (tempHash << 8) ^ b ^ i;
			}

			hash = tempHash;
		}

		@Override
		public final boolean equals(Object o)
		{
			if(o==null){
				return false;
			}
			ByteArray other = (ByteArray) o;
			if (other.hash != hash){
				return false;
			}
			if (other.length != length){
				return false;
			}

			for (int i = 0; i < length; i++)
			{
				if (other.bytes[i + other.start] != bytes[i + start]){
					return false;
				}
			}

			return true;
		}

		@Override
		public final int hashCode()
		{
			return hash;
		}
	}
	public static interface Listener
	{
		public void clearCode(int code);

		public void dataCode(int code);

		public void eoiCode(int code);
		
		public void init(int clearCode, int eoiCode);
	}
	private final int byteOrder;

	private final int clearCode;
	private int codes = -1;
	private int codeSize;
	private final boolean earlyLimit;
	private final int eoiCode;

	private final int initialCodeSize;

	private final Listener listener;

	private final Map map = new HashMap();

	public LZWCompressor(int initialCodeSize, int byteOrder,
			boolean earlyLimit)
	{
		this(initialCodeSize, byteOrder, earlyLimit, null);
	}

	public LZWCompressor(int initialCodeSize, int byteOrder,
			boolean earlyLimit, Listener listener)
	{
		this.listener = listener;
		this.byteOrder = byteOrder;
		this.earlyLimit = earlyLimit;

		this.initialCodeSize = initialCodeSize;

		clearCode = 1 << initialCodeSize;
		eoiCode = clearCode + 1;

		if (null != listener){
			listener.init(clearCode, eoiCode);
		}

		InitializeStringTable();
	}

	private final boolean addTableEntry(BitOutputStream bos, byte bytes[],
			int start, int length) throws IOException
	{
		Object key = arrayToKey(bytes, start, length);
		return addTableEntry(bos, key);
	}

	private final boolean addTableEntry(BitOutputStream bos, Object key)
			throws IOException
	{
		boolean cleared = false;

		{
			int limit = (1 << codeSize);
			if (earlyLimit){
				limit--;
			}

			if (codes == limit)
			{
				if (codeSize < 12){
					incrementCodeSize();
				} else
				{
					writeClearCode(bos);
					clearTable();
					cleared = true;
				}
			}
		}

		if (!cleared)
		{
			map.put(key, new Integer(codes));
			codes++;
		}

		return cleared;
	}

	private final Object arrayToKey(byte b)
	{
		return arrayToKey(new byte[] { b, }, 0, 1);
	}

	private final Object arrayToKey(byte bytes[], int start, int length)
	{
		return new ByteArray(bytes, start, length);
	}

	private final void clearTable()
	{
		InitializeStringTable();
		incrementCodeSize();
	}


	private final int codeFromString(byte bytes[], int start, int length)
			throws IOException
	{
		Object key = arrayToKey(bytes, start, length);
		Object o = map.get(key);
		if (o == null){
			throw new IOException("CodeFromString");
		}
		return ((Integer) o).intValue();
	}

	public byte[] compress(byte bytes[]) throws IOException
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream(bytes.length);
		BitOutputStream bos = new BitOutputStream(baos, byteOrder);

		InitializeStringTable();
		clearTable();
		writeClearCode(bos);
		boolean cleared = false;

		int w_start = 0;
		int w_length = 0;

		for (int i = 0; i < bytes.length; i++)
		{
			if (isInTable(bytes, w_start, w_length + 1))
			{
				w_length++;

				cleared = false;
			} else
			{
				int code = codeFromString(bytes, w_start, w_length);
				writeDataCode(bos, code);
				cleared = addTableEntry(bos, bytes, w_start, w_length + 1);

				w_start = i;
				w_length = 1;
			}
		} /* end of for loop */

		int code = codeFromString(bytes, w_start, w_length);
		writeDataCode(bos, code);

		writeEoiCode(bos);

		bos.flushCache();

		return baos.toByteArray();
	}

	private final void incrementCodeSize()
	{
		if (codeSize != 12){
			codeSize++;
		}
	}

	private final void InitializeStringTable()
	{
		codeSize = initialCodeSize;

		int intial_entries_count = (1 << codeSize) + 2;

		map.clear();
		for (codes = 0; codes < intial_entries_count; codes++)
		{
			if ((codes != clearCode) && (codes != eoiCode))
			{
				Object key = arrayToKey((byte) codes);

				map.put(key, new Integer(codes));
			}
		}
	}

	private final boolean isInTable(byte bytes[], int start, int length)
	{
		Object key = arrayToKey(bytes, start, length);

		return map.containsKey(key);
	}

	private final void writeClearCode(BitOutputStream bos) throws IOException
	{
		if (null != listener){
			listener.dataCode(clearCode);
		}
		writeCode(bos, clearCode);
	}

	private final void writeCode(BitOutputStream bos, int code)
			throws IOException
	{
		bos.writeBits(code, codeSize);
	}

	private final void writeDataCode(BitOutputStream bos, int code)
			throws IOException
	{
		if (null != listener){
			listener.dataCode(code);
		}
		writeCode(bos, code);
	};

	private final void writeEoiCode(BitOutputStream bos) throws IOException
	{
		if (null != listener){
			listener.eoiCode(eoiCode);
		}
		writeCode(bos, eoiCode);
	}
}
