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

package com.madrobot.di.wizard.xml.io.xml;

import java.io.Writer;

import com.madrobot.di.wizard.xml.io.AbstractWriter;
import com.madrobot.di.wizard.xml.io.NameCoder;
import com.madrobot.di.wizard.xml.io.StreamException;
import com.madrobot.util.FastStack;
import com.madrobot.util.QuickWriter;

/**
 * A simple writer that outputs XML in a pretty-printed indented stream.
 * <p>
 * By default, the chars <code><pre>
 * &amp; &lt; &gt; &quot; ' \r
 * </pre></code> are escaped and replaced with a suitable XML entity. To alter this behavior, override the the
 * {@link #writeText(com.madrobot.util.QuickWriter, String)} and
 * {@link #writeAttributeValue(com.madrobot.util.QuickWriter, String)} methods.
 * </p>
 * <p>
 * Note: Depending on the XML version some characters cannot be written. Especially a 0 character is never valid in XML,
 * neither directly nor as entity nor within CDATA. However, this writer works by default in a quirks mode, where it
 * will write any character at least as character entity (even a null character). You may switch into XML_1_1 mode
 * (which supports most characters) or XML_1_0 that does only support a very limited number of control characters. See
 * XML specification for version <a href="http://www.w3.org/TR/2006/REC-xml-20060816/#charsets">1.0</a> or <a
 * href="http://www.w3.org/TR/2006/REC-xml11-20060816/#charsets">1.1</a>. If a character is not supported, a
 * {@link StreamException} is thrown. Select a proper parser implementation that respects the version in the XML header
 * (the Xpp3 parser will also read character entities of normally invalid characters).
 * </p>
 * 
 */
public class PrettyPrintWriter extends AbstractWriter {

	private static final char[] AMP = "&amp;".toCharArray();
	private static final char[] APOS = "&apos;".toCharArray();
	private static final char[] CLOSE = "</".toCharArray();

	private static final char[] CR = "&#xd;".toCharArray();
	private static final char[] GT = "&gt;".toCharArray();
	private static final char[] LT = "&lt;".toCharArray();
	private static final char[] NULL = "&#x0;".toCharArray();

	private static final char[] QUOT = "&quot;".toCharArray();
	public static int XML_1_0 = 0;
	public static int XML_1_1 = 1;
	public static int XML_QUIRKS = -1;
	protected int depth;

	private final FastStack elementStack = new FastStack(16);
	private final char[] lineIndenter;
	private final int mode;
	private String newLine;
	private boolean readyForNewLine;
	private boolean tagInProgress;
	private boolean tagIsEmpty;
	private final QuickWriter writer;

	public PrettyPrintWriter(Writer writer) {
		this(writer, new char[] { ' ', ' ' });
	}

	public PrettyPrintWriter(Writer writer, char[] lineIndenter) {
		this(writer, XML_QUIRKS, lineIndenter);
	}

	/**
	 * @deprecated As of 1.3
	 */
	@Deprecated
	public PrettyPrintWriter(Writer writer, char[] lineIndenter, String newLine) {
		this(writer, lineIndenter, newLine, new XmlFriendlyNameCoder());
	}

	/**
	 * @since 1.2
	 * @deprecated As of 1.3
	 */
	@Deprecated
	public PrettyPrintWriter(Writer writer, char[] lineIndenter, String newLine, XmlFriendlyNameCoder replacer) {
		this(writer, XML_QUIRKS, lineIndenter, replacer, newLine);
	}

	/**
	 * @since 1.3
	 */
	public PrettyPrintWriter(Writer writer, int mode) {
		this(writer, mode, new char[] { ' ', ' ' });
	}

	/**
	 * @since 1.3
	 */
	public PrettyPrintWriter(Writer writer, int mode, char[] lineIndenter) {
		this(writer, mode, lineIndenter, new XmlFriendlyNameCoder());
	}

	/**
	 * @since 1.4
	 */
	public PrettyPrintWriter(Writer writer, int mode, char[] lineIndenter, NameCoder nameCoder) {
		this(writer, mode, lineIndenter, nameCoder, "\n");
	}

	private PrettyPrintWriter(Writer writer, int mode, char[] lineIndenter, NameCoder nameCoder, String newLine) {
		super(nameCoder);
		this.writer = new QuickWriter(writer);
		this.lineIndenter = lineIndenter;
		this.newLine = newLine;
		this.mode = mode;
		if (mode < XML_QUIRKS || mode > XML_1_1) {
			throw new IllegalArgumentException("Not a valid XML mode");
		}
	}

	/**
	 * @since 1.3
	 * @deprecated As of 1.4 use {@link PrettyPrintWriter#PrettyPrintWriter(Writer, int, char[], NameCoder)} instead
	 */
	@Deprecated
	public PrettyPrintWriter(Writer writer, int mode, char[] lineIndenter, XmlFriendlyNameCoder replacer) {
		this(writer, mode, lineIndenter, replacer, "\n");
	}

	/**
	 * @since 1.4
	 */
	public PrettyPrintWriter(Writer writer, int mode, NameCoder nameCoder) {
		this(writer, mode, new char[] { ' ', ' ' }, nameCoder);
	}

	/**
	 * @since 1.3
	 */
	public PrettyPrintWriter(Writer writer, int mode, String lineIndenter) {
		this(writer, mode, lineIndenter.toCharArray());
	}

	/**
	 * @since 1.3
	 * @deprecated As of 1.4 use {@link PrettyPrintWriter#PrettyPrintWriter(Writer, int, NameCoder)} instead
	 */
	@Deprecated
	public PrettyPrintWriter(Writer writer, int mode, XmlFriendlyNameCoder replacer) {
		this(writer, mode, new char[] { ' ', ' ' }, replacer);
	}

	/**
	 * @since 1.4
	 */
	public PrettyPrintWriter(Writer writer, NameCoder nameCoder) {
		this(writer, XML_QUIRKS, new char[] { ' ', ' ' }, nameCoder, "\n");
	}

	public PrettyPrintWriter(Writer writer, String lineIndenter) {
		this(writer, lineIndenter.toCharArray());
	}

	/**
	 * @deprecated As of 1.3
	 */
	@Deprecated
	public PrettyPrintWriter(Writer writer, String lineIndenter, String newLine) {
		this(writer, lineIndenter.toCharArray(), newLine);
	}

	/**
	 * @deprecated As of 1.4 use {@link PrettyPrintWriter#PrettyPrintWriter(Writer, NameCoder)} instead.
	 */
	@Deprecated
	public PrettyPrintWriter(Writer writer, XmlFriendlyNameCoder replacer) {
		this(writer, new char[] { ' ', ' ' }, "\n", replacer);
	}

	@Override
	public void addAttribute(String key, String value) {
		writer.write(' ');
		writer.write(encodeAttribute(key));
		writer.write('=');
		writer.write('\"');
		writeAttributeValue(writer, value);
		writer.write('\"');
	}

	@Override
	public void close() {
		writer.close();
	}

	@Override
	public void endNode() {
		depth--;
		if (tagIsEmpty) {
			writer.write('/');
			readyForNewLine = false;
			finishTag();
			elementStack.popSilently();
		} else {
			finishTag();
			writer.write(CLOSE);
			writer.write((String) elementStack.pop());
			writer.write('>');
		}
		readyForNewLine = true;
		if (depth == 0) {
			writer.flush();
		}
	}

	protected void endOfLine() {
		writer.write(getNewLine());
		for (int i = 0; i < depth; i++) {
			writer.write(lineIndenter);
		}
	}

	private void finishTag() {
		if (tagInProgress) {
			writer.write('>');
		}
		tagInProgress = false;
		if (readyForNewLine) {
			endOfLine();
		}
		readyForNewLine = false;
		tagIsEmpty = false;
	}

	@Override
	public void flush() {
		writer.flush();
	}

	protected String getNewLine() {
		return newLine;
	}

	@Override
	public void setValue(String text) {
		readyForNewLine = false;
		tagIsEmpty = false;
		finishTag();

		writeText(writer, text);
	}

	@Override
	public void startNode(String name) {
		String escapedName = encodeNode(name);
		tagIsEmpty = false;
		finishTag();
		writer.write('<');
		writer.write(escapedName);
		elementStack.push(escapedName);
		tagInProgress = true;
		depth++;
		readyForNewLine = true;
		tagIsEmpty = true;
	}

	@Override
	public void startNode(String name, Class clazz) {
		startNode(name);
	}

	protected void writeAttributeValue(QuickWriter writer, String text) {
		writeText(text, true);
	}

	protected void writeText(QuickWriter writer, String text) {
		writeText(text, false);
	}

	private void writeText(String text, boolean isAttribute) {
		int length = text.length();
		for (int i = 0; i < length; i++) {
			char c = text.charAt(i);
			switch (c) {
			case '\0':
				if (mode == XML_QUIRKS) {
					this.writer.write(NULL);
				} else {
					throw new StreamException("Invalid character 0x0 in XML stream");
				}
				break;
			case '&':
				this.writer.write(AMP);
				break;
			case '<':
				this.writer.write(LT);
				break;
			case '>':
				this.writer.write(GT);
				break;
			case '"':
				this.writer.write(QUOT);
				break;
			case '\'':
				this.writer.write(APOS);
				break;
			case '\r':
				this.writer.write(CR);
				break;
			case '\t':
			case '\n':
				if (!isAttribute) {
					this.writer.write(c);
					break;
				}
			default:
				if (Character.isDefined(c) && !Character.isISOControl(c)) {
					if (mode != XML_QUIRKS) {
						if (c > '\ud7ff' && c < '\ue000') {
							throw new StreamException("Invalid character 0x" + Integer.toHexString(c)
									+ " in XML stream");
						}
					}
					this.writer.write(c);
				} else {
					if (mode == XML_1_0) {
						if (c < 9 || c == '\u000b' || c == '\u000c' || c == '\u000e' || c == '\u000f') {
							throw new StreamException("Invalid character 0x" + Integer.toHexString(c)
									+ " in XML 1.0 stream");
						}
					}
					if (mode != XML_QUIRKS) {
						if (c == '\ufffe' || c == '\uffff') {
							throw new StreamException("Invalid character 0x" + Integer.toHexString(c)
									+ " in XML stream");
						}
					}
					this.writer.write("&#x");
					this.writer.write(Integer.toHexString(c));
					this.writer.write(';');
				}
			}
		}
	}
}
