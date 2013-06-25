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

/**
 * Fast implementation of RSA's MD5 hash generator
 **/

class MD5State {
	/**
	 * 64-byte buffer (512 bits) for storing to-be-hashed characters
	 */
	byte buffer[];

	/**
	 * 64-bit character count
	 */
	private long count;

	/**
	 * 128-bit state
	 */
	private int[] state;

	MD5State() {
		buffer = new byte[64];
		count = 0;
		state = new int[4];

		state[0] = 0x67452301;
		state[1] = 0xefcdab89;
		state[2] = 0x98badcfe;
		state[3] = 0x10325476;

	}

	/** Create this State as a copy of another state */
	MD5State(MD5State from) {
		this();

		int i;

		for (i = 0; i < buffer.length; i++)
			this.buffer[i] = from.buffer[i];

		for (i = 0; i < state.length; i++)
			this.state[i] = from.state[i];

		this.count = from.count;
	}

	long getCount() {
		return count;
	}

	int[] getState() {
		return state;
	}

	void setCount(long count) {
		this.count = count;
	}

	void setState(int[] state) {
		this.state = state;
	}
};
