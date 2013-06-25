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
import java.io.InputStream;
import java.io.OutputStream;

import com.madrobot.io.BitInputStream;

public final class LZWDecompressor
{
	public static interface Listener
	{
		public void code(int code);
		
		public void init(int clearCode, int eoiCode);
	}

	private static final int MAX_TABLE_SIZE = 1 << 12;
	private final int byteOrder;
	private final int clearCode;
	private int codes = -1;

	private int codeSize;

	private final int eoiCode;

	private final int initialCodeSize;

	private final Listener listener;

	private final byte[][] table;

	private boolean tiffLZWMode = false;

	private int written = 0;

	public LZWDecompressor(int initialCodeSize, int byteOrder)
	{
		this(initialCodeSize, byteOrder, null);
	}
	public LZWDecompressor(int initialCodeSize, int byteOrder,
			Listener listener)
	{
		this.listener = listener;
		this.byteOrder = byteOrder;

		this.initialCodeSize = initialCodeSize;

		table = new byte[MAX_TABLE_SIZE][];
		clearCode = 1 << initialCodeSize;
		eoiCode = clearCode + 1;

		if (null != listener){
			listener.init(clearCode, eoiCode);
		}

		InitializeTable();
	}

	private final void addStringToTable(byte bytes[]) throws IOException
	{
		if (codes < (1 << codeSize))
		{
			table[codes] = bytes;
			codes++;
		} else{
			throw new IOException("AddStringToTable: codes: " + codes
					+ " code_size: " + codeSize);
		}

		checkCodeSize();
	}

	private final byte[] appendBytes(byte bytes[], byte b)
	{
		byte result[] = new byte[bytes.length + 1];

		System.arraycopy(bytes, 0, result, 0, bytes.length);
		result[result.length - 1] = b;
		return result;
	}

	private final void checkCodeSize() // throws IOException
	{
		int limit = (1 << codeSize);
		if (tiffLZWMode){
			limit--;
		}

		if (codes == limit){
			incrementCodeSize();
		}
	}

	private final void clearTable()
	{
		codes = (1 << initialCodeSize) + 2;
		codeSize = initialCodeSize;
		incrementCodeSize();
	}

	public byte[] decompress(InputStream is, int expectedLength)
			throws IOException
	{
		int code, oldCode = -1;
		BitInputStream mbis = new BitInputStream(is, byteOrder);
		if (tiffLZWMode){
			mbis.setTiffLZWMode();
		}

		ByteArrayOutputStream baos = new ByteArrayOutputStream(expectedLength);

		clearTable();

		while ((code = getNextCode(mbis)) != eoiCode)
		{
			if (code == clearCode)
			{
				clearTable();

				if (written >= expectedLength){
					break;
				}
				code = getNextCode(mbis);

				if (code == eoiCode)
				{
					break;
				}
				writeToResult(baos, stringFromCode(code));

				oldCode = code;
			} // end of ClearCode case
			else
			{
				if (isInTable(code))
				{
					writeToResult(baos, stringFromCode(code));

					addStringToTable(appendBytes(stringFromCode(oldCode),
							firstChar(stringFromCode(code))));
					oldCode = code;
				} else
				{
					byte OutString[] = appendBytes(stringFromCode(oldCode),
							firstChar(stringFromCode(oldCode)));
					writeToResult(baos, OutString);
					addStringToTable(OutString);
					oldCode = code;
				}
			} // end of not-ClearCode case

			if (written >= expectedLength){
				break;
			}
		} // end of while loop

		byte result[] = baos.toByteArray();

		return result;
	}

	private final byte firstChar(byte bytes[])
	{
		return bytes[0];
	}

	private final int getNextCode(BitInputStream is) throws IOException
	{
		int code = is.readBits(codeSize);

		if (null != listener){
			listener.code(code);
		}
		return code;
	}

	private final void incrementCodeSize() // throws IOException
	{
		if (codeSize != 12){
			codeSize++;
		}
	}

	private final void InitializeTable()
	{
		codeSize = initialCodeSize;

		int intial_entries_count = 1 << codeSize + 2;

		for (int i = 0; i < intial_entries_count; i++){
			table[i] = new byte[] { (byte) i, };
		}
	}

	private final boolean isInTable(int Code)
	{
		return Code < codes;
	}

	public void setTiffLZWMode()
	{
		tiffLZWMode = true;
	}

	private final byte[] stringFromCode(int code) throws IOException
	{
		if ((code >= codes) || (code < 0)){
			throw new IOException("Bad Code: " + code + " codes: " + codes
					+ " code_size: " + codeSize + ", table: " + table.length);
		}

		return table[code];
	}

	private final void writeToResult(OutputStream os, byte bytes[])
			throws IOException
	{
		os.write(bytes);
		written += bytes.length;
	}
}
