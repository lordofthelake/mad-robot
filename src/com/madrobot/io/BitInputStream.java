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
package com.madrobot.io;

import java.io.IOException;
import java.io.InputStream;

import com.madrobot.math.BinaryConstants;

public class BitInputStream extends InputStream implements BinaryConstants
{
	private int bitCache = 0;
	private int bitsInCache = 0;
	private final int byteOrder;

	private long bytesRead = 0;

	private final InputStream is;

	private boolean tiffLZWMode = false;
	public BitInputStream(InputStream is, int byteOrder)
	{
		this.byteOrder = byteOrder;
		this.is = is;
	}
	public void flushCache()
	{
		bitsInCache = 0;
		bitCache = 0;
	}

	public long getBytesRead()
	{
		return bytesRead;
	}

	@Override
	public int read() throws IOException
	{
		return readBits(8);
	}

	public int readBits(int SampleBits) throws IOException
	{
		while (bitsInCache < SampleBits)
		{
			int next = is.read();

			if (next < 0)
			{
				if (tiffLZWMode)
				{
					// pernicious special case!
					return 257;
				}
				return -1;
			}

			int newByte = (0xff & next);

			if (byteOrder == BYTE_ORDER_NETWORK){
				bitCache = (bitCache << 8) | newByte;
			} else if (byteOrder == BYTE_ORDER_INTEL){
				bitCache = (newByte << bitsInCache) | bitCache;
			} else{
				throw new IOException("Unknown byte order: " + byteOrder);
			}

			bytesRead++;
			bitsInCache += 8;
		}
		int sampleMask = (1 << SampleBits) - 1;

		int sample;

		if (byteOrder == BYTE_ORDER_NETWORK) // MSB, so read from left
		{
			sample = sampleMask & (bitCache >> (bitsInCache - SampleBits));
		}
		else if (byteOrder == BYTE_ORDER_INTEL) // LSB, so read from right
		{
			sample = sampleMask & bitCache;
			bitCache >>= SampleBits;
		} else{
			throw new IOException("Unknown byte order: " + byteOrder);
		}

		int result = sample;

		bitsInCache -= SampleBits;
		int remainderMask = (1 << bitsInCache) - 1;
		bitCache &= remainderMask;

		return result;
	}

	public void setTiffLZWMode()
	{
		tiffLZWMode = true;
	}

}
