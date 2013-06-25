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
import java.io.OutputStream;

import com.madrobot.math.BinaryConstants;

public class BitOutputStream extends OutputStream implements BinaryConstants {
	private int bitCache = 0;
	private int bitsInCache = 0;

	private final int byteOrder;

	private int bytesWritten = 0;

	private final OutputStream os;
	public BitOutputStream(OutputStream os, int byteOrder) {
		this.byteOrder = byteOrder;
		this.os = os;
	}

	private void actualWrite(int value) throws IOException {
		os.write(value);
		bytesWritten++;
	}

	public void flushCache() throws IOException {
		if(bitsInCache > 0){
			int bitMask = (1 << bitsInCache) - 1;
			int b = bitMask & bitCache;

			if(byteOrder == BYTE_ORDER_NETWORK) // MSB, so write from left
			{
				b <<= 8 - bitsInCache; // left align fragment.
				os.write(b);
			} else if(byteOrder == BYTE_ORDER_INTEL) // LSB, so write from right
			{
				os.write(b);
			}
		}

		bitsInCache = 0;
		bitCache = 0;
	}

	public int getBytesWritten() {
		return bytesWritten + ((bitsInCache > 0) ? 1 : 0);
	}

	@Override
	public void write(int value) throws IOException {
		writeBits(value, 8);
	}

	// TODO: in and out streams CANNOT accurately read/write 32bits at a time,
	// as int will overflow. should have used a long
	public void writeBits(int value, int SampleBits) throws IOException {
		int sampleMask = (1 << SampleBits) - 1;
		value &= sampleMask;

		if(byteOrder == BYTE_ORDER_NETWORK) // MSB, so add to right
		{
			bitCache = (bitCache << SampleBits) | value;
		} else if(byteOrder == BYTE_ORDER_INTEL) // LSB, so add to left
		{
			bitCache = bitCache | (value << bitsInCache);
		} else{
			throw new IOException("Unknown byte order: " + byteOrder);
		}
		bitsInCache += SampleBits;

		while(bitsInCache >= 8){
			if(byteOrder == BYTE_ORDER_NETWORK) // MSB, so write from left
			{
				int b = 0xff & (bitCache >> (bitsInCache - 8));
				actualWrite(b);

				bitsInCache -= 8;
			} else if(byteOrder == BYTE_ORDER_INTEL) // LSB, so write from right
			{
				int b = 0xff & bitCache;
				actualWrite(b);

				bitCache >>= 8;
				bitsInCache -= 8;
			}
			int remainderMask = (1 << bitsInCache) - 1; // unneccesary
			bitCache &= remainderMask; // unneccesary
		}

	}

}
