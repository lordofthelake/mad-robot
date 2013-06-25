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

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StreamCorruptedException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import android.util.Log;

public class IOUtils {
	public interface ProgressCallback {
		public void onComplete();

		public void onError(Throwable t);

		public void onProgress(int bytesWritten);
	}

	public static final int DEFAULT_BUFFER_SIZE = 1024 * 4;

	/**
	 * This convenience method allows to read a InputStream into a string. The platform's default character encoding is
	 * used for converting bytes into characters.
	 * 
	 * @param pStream
	 *            The input stream to read.
	 * @see #asString(InputStream, String)
	 * @return The streams contents, as a string.
	 * @throws IOException
	 *             An I/O error occurred.
	 */
	public static String asString(InputStream pStream) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		copy(pStream, baos, true);
		return baos.toString();
	}

	/**
	 * This convenience method allows to read a InputStream into a string, using the given character encoding.
	 * 
	 * @param pStream
	 *            The input stream to read.
	 * @param pEncoding
	 *            The character encoding, typically "UTF-8".
	 * @see #asString(InputStream)
	 * @return The streams contents, as a string.
	 * @throws IOException
	 *             An I/O error occurred.
	 */
	public static String asString(InputStream pStream, String pEncoding) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		copy(pStream, baos, true);
		return baos.toString(pEncoding);
	}

	/**
	 * Compare the contents of two Streams to determine if they are equal or not.
	 * <p>
	 * This method buffers the input internally using <code>BufferedInputStream</code> if they are not already buffered.
	 * 
	 * @param input1
	 *            the first stream
	 * @param input2
	 *            the second stream
	 * @return true if the content of the streams are equal or they both don't exist, false otherwise
	 * @throws NullPointerException
	 *             if either input is null
	 * @throws IOException
	 *             if an I/O error occurs
	 */
	public static boolean contentEquals(InputStream input1, InputStream input2) throws IOException {
		if (!(input1 instanceof BufferedInputStream)) {
			input1 = new BufferedInputStream(input1);
		}
		if (!(input2 instanceof BufferedInputStream)) {
			input2 = new BufferedInputStream(input2);
		}

		int ch = input1.read();
		while (-1 != ch) {
			int ch2 = input2.read();
			if (ch != ch2) {
				return false;
			}
			ch = input1.read();
		}

		int ch2 = input2.read();
		return (ch2 == -1);
	}

	public static byte[] copy(byte[] source, byte[] target) {
		int len = source.length;
		if (len > target.length) {
			target = new byte[len];
		}
		System.arraycopy(source, 0, target, 0, len);
		return target;
	}

	/**
	 * 
	 * Copy bytes from an InputStream to an OutputStream.
	 * 
	 * This method buffers the input internally, so there is no need to use a BufferedInputStream.
	 * 
	 * Large streams (over 2GB) will return a bytes copied value of -1 after the copy has completed since the correct
	 * number of bytes cannot be returned as an int. For large streams use the copyLarge(InputStream, OutputStream)
	 * method.
	 * 
	 * @param input
	 *            the InputStream to read from
	 * @param output
	 *            the OutputStream to write to
	 * @return the number of bytes copied
	 * @throws IOException
	 *             if an I/O error occurs
	 */
	public static int copy(InputStream input, OutputStream output) throws IOException {
		long count = copyLarge(input, output);
		if (count > Integer.MAX_VALUE) {
			return -1;
		}
		return (int) count;
	}

	/**
	 * Copies the contents of the given {@link InputStream} to the given {@link OutputStream}. Shortcut for
	 * 
	 * <pre>
	 * copy(pInputStream, pOutputStream, new byte[8192]);
	 * </pre>
	 * 
	 * @param pInputStream
	 *            The input stream, which is being read. It is guaranteed, that {@link InputStream#close()} is called on
	 *            the stream.
	 * @param pOutputStream
	 *            The output stream, to which data should be written. May be null, in which case the input streams
	 *            contents are simply discarded.
	 * @param pClose
	 *            True guarantees, that {@link OutputStream#close()} is called on the stream. False indicates, that only
	 *            {@link OutputStream#flush()} should be called finally.
	 * 
	 * @return Number of bytes, which have been copied.
	 * @throws IOException
	 *             An I/O error occurred.
	 */
	public static long copy(InputStream pInputStream, OutputStream pOutputStream, boolean pClose) throws IOException {
		return copy(pInputStream, pOutputStream, pClose, new byte[DEFAULT_BUFFER_SIZE]);
	}

	/**
	 * Copies the contents of the given {@link InputStream} to the given {@link OutputStream}.
	 * 
	 * @param pIn
	 *            The input stream, which is being read. It is guaranteed, that {@link InputStream#close()} is called on
	 *            the stream.
	 * @param pOut
	 *            The output stream, to which data should be written. May be null, in which case the input streams
	 *            contents are simply discarded.
	 * @param pClose
	 *            True guarantees, that {@link OutputStream#close()} is called on the stream. False indicates, that only
	 *            {@link OutputStream#flush()} should be called finally.
	 * @param pBuffer
	 *            Temporary buffer, which is to be used for copying data.
	 * @return Number of bytes, which have been copied.
	 * @throws IOException
	 *             An I/O error occurred.
	 */
	public static long copy(InputStream pIn, OutputStream pOut, boolean pClose, byte[] pBuffer) throws IOException {
		OutputStream out = pOut;
		InputStream in = pIn;
		try {
			long total = 0;
			for (;;) {
				int res = in.read(pBuffer);
				if (res == -1) {
					break;
				}
				if (res > 0) {
					total += res;
					if (out != null) {
						out.write(pBuffer, 0, res);
					}
				}
			}
			if (out != null) {
				if (pClose) {
					out.close();
				} else {
					out.flush();
				}
				out = null;
			}
			in.close();
			in = null;
			return total;
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (Throwable t) {
					/* Ignore me */
				}
			}
			if (pClose && out != null) {
				try {
					out.close();
				} catch (Throwable t) {
					/* Ignore me */
				}
			}
		}
	}

	public static void copy(InputStream in, OutputStream out, ProgressCallback callback) {
		try {
			byte[] buff = new byte[1024];
			int bytesRead = 0;
			while ((bytesRead = in.read(buff, 0, buff.length)) != -1) {
				out.write(buff, 0, bytesRead);
				if (callback != null) {
					callback.onProgress(bytesRead);
				}
			}
			out.close();
			in.close();

			callback.onComplete();
		} catch (IOException e) {
			callback.onError(e);
		}
	}

	/**
	 * Copy bytes from a large (over 2GB) InputStream to an OutputStream.
	 * 
	 * This method buffers the input internally, so there is no need to use a BufferedInputStream.
	 * 
	 * @param input
	 *            the InputStream to read from
	 * @param output
	 *            the OutputStream to write to
	 * @return the number of bytes copied
	 * @throws IOException
	 *             if an I/O error occurs
	 */
	private static long copyLarge(InputStream input, OutputStream output) throws IOException {
		byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
		long count = 0;
		int n = 0;
		while (-1 != (n = input.read(buffer))) {
			output.write(buffer, 0, n);
			count += n;
		}
		return count;
	}

	/**
	 * De-serialize the byte array to an object.
	 * 
	 * @param data
	 *            the byte array
	 * @return the object
	 * @throws IOException
	 * @throws StreamCorruptedException
	 * @throws ClassNotFoundException
	 * @throws DbException
	 *             if serialization fails
	 */
	public static Object deserialize(byte[] data) throws StreamCorruptedException, IOException, ClassNotFoundException {
		ByteArrayInputStream in = new ByteArrayInputStream(data);
		ObjectInputStream is = new ObjectInputStream(in);
		return is.readObject();
	}

	/**
	 * Read input from reader and write it to writer until there is no more input from reader.
	 * 
	 * @param reader
	 *            the reader to read from.
	 * @param writer
	 *            the writer to write to.
	 * @param buf
	 *            the char array to use as a buffer
	 */
	public static void flow(Reader reader, Writer writer, char[] buf) throws IOException {
		int numRead;
		while ((numRead = reader.read(buf)) >= 0) {
			writer.write(buf, 0, numRead);
		}
	}

	/**
	 * Compare two inputstreams
	 * 
	 * @param stream1
	 * @param stream2
	 * @return
	 * @throws IOException
	 */
	public static boolean isStreamsEqual(InputStream stream1, InputStream stream2) throws IOException {
		byte[] buf1 = new byte[4096];
		byte[] buf2 = new byte[4096];
		boolean done1 = false;
		boolean done2 = false;
		try {
			while (!done1) {
				int off1 = 0;
				int off2 = 0;

				while (off1 < buf1.length) {
					int count = stream1.read(buf1, off1, buf1.length - off1);
					if (count < 0) {
						done1 = true;
						break;
					}
					off1 += count;
				}
				while (off2 < buf2.length) {
					int count = stream2.read(buf2, off2, buf2.length - off2);
					if (count < 0) {
						done2 = true;
						break;
					}
					off2 += count;
				}
				if (off1 != off2 || done1 != done2)
					return false;
				for (int i = 0; i < off1; i++) {
					if (buf1[i] != buf2[i])
						return false;
				}
			}
			return true;
		} finally {
			stream1.close();
			stream2.close();
		}
	}

	/**
	 * Connects the given inputstream to the outputstream
	 * <p>
	 * The streams are closed after the operation is complete
	 * </p>
	 * 
	 * @param is
	 * @param os
	 * @throws IOException
	 */
	public static void performStreamPiping(InputStream is, OutputStream os) throws IOException {
		byte[] buf = new byte[2048];
		while (is.read(buf) != -1) {
			os.write(buf);
		}
		os.close();
		is.close();
	}

	/**
	 * Get the contents of an <code>InputStream</code> as a list of Strings, one entry per line, using the default
	 * character encoding of the platform.
	 * <p>
	 * This method buffers the input internally, so there is no need to use a <code>BufferedInputStream</code>.
	 * 
	 * @param input
	 *            the <code>InputStream</code> to read from, not null
	 * @return the list of Strings, never null
	 * @throws NullPointerException
	 *             if the input is null
	 * @throws IOException
	 *             if an I/O error occurs
	 * @since Commons IO 1.1
	 */
	public static List<String> readLines(InputStream input) throws IOException {
		InputStreamReader reader = new InputStreamReader(input);
		return readLines(reader);
	}

	/**
	 * Get the contents of an <code>InputStream</code> as a list of Strings, one entry per line, using the specified
	 * character encoding.
	 * <p>
	 * Character encoding names can be found at <a href="http://www.iana.org/assignments/character-sets">IANA</a>.
	 * <p>
	 * This method buffers the input internally, so there is no need to use a <code>BufferedInputStream</code>.
	 * 
	 * @param input
	 *            the <code>InputStream</code> to read from, not null
	 * @param encoding
	 *            the encoding to use, null means platform default
	 * @return the list of Strings, never null
	 * @throws NullPointerException
	 *             if the input is null
	 * @throws IOException
	 *             if an I/O error occurs
	 * @since Commons IO 1.1
	 */
	public static List<String> readLines(InputStream input, String encoding) throws IOException {
		if (encoding == null) {
			return readLines(input);
		} else {
			InputStreamReader reader = new InputStreamReader(input, encoding);
			return readLines(reader);
		}
	}

	/**
	 * Get the contents of a <code>Reader</code> as a list of Strings, one entry per line.
	 * <p>
	 * This method buffers the input internally, so there is no need to use a <code>BufferedReader</code>.
	 * 
	 * @param input
	 *            the <code>Reader</code> to read from, not null
	 * @return the list of Strings, never null
	 * @throws NullPointerException
	 *             if the input is null
	 * @throws IOException
	 *             if an I/O error occurs
	 * @since Commons IO 1.1
	 */
	public static List<String> readLines(Reader input) throws IOException {
		BufferedReader reader = new BufferedReader(input);
		List<String> list = new ArrayList<String>();
		String line = reader.readLine();
		while (line != null) {
			list.add(line);
			line = reader.readLine();
		}
		return list;
	}

	/**
	 * Serialize the object to a byte array.
	 * 
	 * @param obj
	 *            the object to serialize
	 * @return the byte array
	 * @throws IOException
	 */
	public static byte[] serialize(Object obj) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ObjectOutputStream os = new ObjectOutputStream(out);
		os.writeObject(obj);
		return out.toByteArray();
	}

	/**
	 * Get the contents of an <code>InputStream</code> as a <code>byte[]</code>.
	 * <p>
	 * This method buffers the input internally, so there is no need to use a <code>BufferedInputStream</code>.
	 * 
	 * @param input
	 *            the <code>InputStream</code> to read from
	 * @return the requested byte array
	 * @throws NullPointerException
	 *             if the input is null
	 * @throws IOException
	 *             if an I/O error occurs
	 */
	public static byte[] toByteArray(InputStream input) throws IOException {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		copy(input, output);
		return output.toByteArray();
	}

	public static void writeToStream(InputStream is) {
		byte[] buf = new byte[2048];
		StringBuilder builder = new StringBuilder();
		try {
			while (is.read(buf) != -1) {
				builder.append(new String(buf));
			}
			Log.e("Blog/DM", "::: Stream Response " + builder);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
