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

/**
 * ByteArrayOutputStream thats NOT thread safe.
 */
public class FastByteArrayOutputStream extends OutputStream
{
	private final byte bytes[];

	private int count = 0;

	public FastByteArrayOutputStream(int length) {
		bytes = new byte[length];
	}

	public int getBytesWritten() {
		return count;
	}

	public byte[] toByteArray() {
		if(count < bytes.length){
			byte result[] = new byte[count];
			System.arraycopy(bytes, 0, result, 0, count);
			return result;
		}
		return bytes;
	}

	@Override
	public void write(int value) throws IOException {
		if(count >= bytes.length){
			throw new IOException("Write exceeded expected length (" + count + ", " + bytes.length + ")");
		}

		bytes[count] = (byte) value;
		count++;
	}
}
