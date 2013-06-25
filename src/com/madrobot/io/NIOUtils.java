package com.madrobot.io;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class NIOUtils {
	public static final int KB = 1024;
	public static final int MB = KB * KB;
	public static final int GB = KB * MB;
	public static final long TB = KB * GB;

	/**
	 * Slice the given byte buffer at the given postion to the given size
	 * 
	 * @param buf
	 * @param pos
	 * @param size
	 * @return
	 */
	public static ByteBuffer slice(ByteBuffer buf, int pos, int size) {
		int origPos = buf.position();
		int origLim = buf.limit();
		buf.clear();
		buf.position(pos);
		buf.limit(pos + size);
		ByteBuffer res = buf.slice();
		res.order(ByteOrder.nativeOrder());
		buf.clear();
		buf.position(origPos);
		buf.limit(origLim);
		return res;
	}
}
