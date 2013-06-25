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
package com.madrobot.net.client.util;

import java.io.IOException;
import java.io.Writer;

/***
 * DotTerminatedMessageWriter is a class used to write messages to a
 * server that are terminated by a single dot followed by a
 * &lt;CR&gt;&lt;LF&gt;
 * sequence and with double dots appearing at the begining of lines which
 * do not signal end of message yet start with a dot. Various Internet
 * protocols such as NNTP and POP3 produce messages of this type.
 * <p>
 * This class handles the doubling of line-starting periods, converts single
 * linefeeds to NETASCII newlines, and on closing will send the final message
 * terminator dot and NETASCII newline sequence.
 * <p>
 * <p>
 * 
 * @author Daniel F. Savarese
 ***/

public final class DotTerminatedMessageWriter extends Writer {
	private static final int __LAST_WAS_CR_STATE = 1;
	private static final int __LAST_WAS_NL_STATE = 2;
	private static final int __NOTHING_SPECIAL_STATE = 0;

	private Writer __output;
	private int __state;

	/***
	 * Creates a DotTerminatedMessageWriter that wraps an existing Writer
	 * output destination.
	 * <p>
	 * 
	 * @param output
	 *            The Writer output destination to write the message.
	 ***/
	public DotTerminatedMessageWriter(Writer output) {
		super(output);
		__output = output;
		__state = __NOTHING_SPECIAL_STATE;
	}

	/***
	 * Flushes the underlying output, writing all buffered output, but doesn't
	 * actually close the underlying stream. The underlying stream may still
	 * be used for communicating with the server and therefore is not closed.
	 * <p>
	 * 
	 * @exception IOException
	 *                If an error occurs while writing to the underlying
	 *                output or closing the Writer.
	 ***/
	@Override
	public void close() throws IOException {
		synchronized(lock){
			if(__output == null){
				return;
			}

			if(__state == __LAST_WAS_CR_STATE){
				__output.write('\n');
			} else if(__state != __LAST_WAS_NL_STATE){
				__output.write("\r\n");
			}

			__output.write(".\r\n");

			__output.flush();
			__output = null;
		}
	}

	/***
	 * Flushes the underlying output, writing all buffered output.
	 * <p>
	 * 
	 * @exception IOException
	 *                If an error occurs while writing to the underlying
	 *                output.
	 ***/
	@Override
	public void flush() throws IOException {
		synchronized(lock){
			__output.flush();
		}
	}

	/***
	 * Writes a character array to the output.
	 * <p>
	 * 
	 * @param buffer
	 *            The character array to write.
	 * @exception IOException
	 *                If an error occurs while writing to the underlying
	 *                output.
	 ***/
	@Override
	public void write(char[] buffer) throws IOException {
		write(buffer, 0, buffer.length);
	}

	/***
	 * Writes a number of characters from a character array to the output
	 * starting from a given offset.
	 * <p>
	 * 
	 * @param buffer
	 *            The character array to write.
	 * @param offset
	 *            The offset into the array at which to start copying data.
	 * @param length
	 *            The number of characters to write.
	 * @exception IOException
	 *                If an error occurs while writing to the underlying
	 *                output.
	 ***/
	@Override
	public void write(char[] buffer, int offset, int length) throws IOException {
		synchronized(lock){
			while(length-- > 0){
				write(buffer[offset++]);
			}
		}
	}

	/***
	 * Writes a character to the output. Note that a call to this method
	 * may result in multiple writes to the underling Writer in order to
	 * convert naked linefeeds to NETASCII line separators and to double
	 * line-leading periods. This is transparent to the programmer and
	 * is only mentioned for completeness.
	 * <p>
	 * 
	 * @param ch
	 *            The character to write.
	 * @exception IOException
	 *                If an error occurs while writing to the
	 *                underlying output.
	 ***/
	@Override
	public void write(int ch) throws IOException {
		synchronized(lock){
			switch(ch) {
				case '\r':
					__state = __LAST_WAS_CR_STATE;
					__output.write('\r');
					return;
				case '\n':
					if(__state != __LAST_WAS_CR_STATE){
						__output.write('\r');
					}
					__output.write('\n');
					__state = __LAST_WAS_NL_STATE;
					return;
				case '.':
					// Double the dot at the beginning of a line
					if(__state == __LAST_WAS_NL_STATE){
						__output.write('.');
					}
					//$FALL-THROUGH$
				default:
					__state = __NOTHING_SPECIAL_STATE;
					__output.write(ch);
					return;
			}
		}
	}

	/***
	 * Writes a String to the output.
	 * <p>
	 * 
	 * @param string
	 *            The String to write.
	 * @exception IOException
	 *                If an error occurs while writing to the underlying
	 *                output.
	 ***/
	@Override
	public void write(String string) throws IOException {
		write(string.toCharArray());
	}

	/***
	 * Writes part of a String to the output starting from a given offset.
	 * <p>
	 * 
	 * @param string
	 *            The String to write.
	 * @param offset
	 *            The offset into the String at which to start copying data.
	 * @param length
	 *            The number of characters to write.
	 * @exception IOException
	 *                If an error occurs while writing to the underlying
	 *                output.
	 ***/
	@Override
	public void write(String string, int offset, int length) throws IOException {
		write(string.toCharArray(), offset, length);
	}

}
