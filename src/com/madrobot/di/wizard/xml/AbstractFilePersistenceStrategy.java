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

package com.madrobot.di.wizard.xml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.madrobot.di.wizard.xml.converters.ConverterLookup;
import com.madrobot.di.wizard.xml.core.PersistenceStrategy;
import com.madrobot.di.wizard.xml.io.StreamException;

/**
 * Abstract base class for file based persistence strategies.
 * 
 * @since 1.3.1
 */
abstract class AbstractFilePersistenceStrategy implements PersistenceStrategy {

	protected class ValidFilenameFilter implements FilenameFilter {
		@Override
		public boolean accept(final File dir, final String name) {
			return new File(dir, name).isFile() && isValid(dir, name);
		}
	}
	protected class XmlMapEntriesIterator implements Iterator {

		private File current = null;

		private final File[] files = baseDirectory.listFiles(filter);

		private int position = -1;

		@Override
		public boolean hasNext() {
			return position + 1 < files.length;
		}

		@Override
		public Object next() {
			return new Map.Entry() {
				private final File file = current = files[++position];
				private final Object key = extractKey(file.getName());

				@Override
				public boolean equals(final Object obj) {
					if (!(obj instanceof Entry)) {
						return false;
					}
					Object value = getValue();
					final Entry e2 = (Entry) obj;
					Object key2 = e2.getKey();
					Object value2 = e2.getValue();
					return (key == null ? key2 == null : key.equals(key2))
							&& (value == null ? value2 == null : getValue().equals(e2.getValue()));
				}

				@Override
				public Object getKey() {
					return key;
				}

				@Override
				public Object getValue() {
					return readFile(file);
				}

				@Override
				public Object setValue(final Object value) {
					return put(key, value);
				}
			};
		}

		@Override
		public void remove() {
			if (current == null) {
				throw new IllegalStateException();
			}
			// removes without loading
			current.delete();
		}
	}
	private final File baseDirectory;
	private final String encoding;

	private final FilenameFilter filter;

	private final transient XMLWizard xstream;

	AbstractFilePersistenceStrategy(final File baseDirectory, final XMLWizard xstream, final String encoding) {
		this.baseDirectory = baseDirectory;
		this.xstream = xstream;
		this.encoding = encoding;
		filter = new ValidFilenameFilter();
	}

	public boolean containsKey(final Object key) {
		// faster lookup
		final File file = getFile(getName(key));
		return file.isFile();
	}

	/**
	 * Given a filename, the unescape method returns the key which originated it.
	 * 
	 * @param name
	 *            the filename
	 * @return the original key
	 */
	protected abstract Object extractKey(String name);

	@Override
	public Object get(final Object key) {
		return readFile(getFile(getName(key)));
	}

	protected ConverterLookup getConverterLookup() {
		return xstream.getConverterLookup();
	}

	private File getFile(final String filename) {
		return new File(baseDirectory, filename);
	}

	protected Mapper getMapper() {
		return xstream.getMapper();
	}

	/**
	 * Given a key, the escape method returns the filename which shall be used.
	 * 
	 * @param key
	 *            the key
	 * @return the desired and escaped filename
	 */
	protected abstract String getName(Object key);

	protected boolean isValid(final File dir, final String name) {
		return name.endsWith(".xml");
	}

	@Override
	public Iterator iterator() {
		return new XmlMapEntriesIterator();
	}

	@Override
	public Object put(final Object key, final Object value) {
		final Object oldValue = get(key);
		final String filename = getName(key);
		writeFile(new File(baseDirectory, filename), value);
		return oldValue;
	}

	private Object readFile(final File file) {
		try {
			final FileInputStream in = new FileInputStream(file);
			final Reader reader = encoding != null ? new InputStreamReader(in, encoding) : new InputStreamReader(in);
			try {
				return xstream.fromXML(reader);
			} finally {
				reader.close();
			}
		} catch (final FileNotFoundException e) {
			// not found... file.exists might generate a sync problem
			return null;
		} catch (final IOException e) {
			throw new StreamException(e);
		}
	}

	@Override
	public Object remove(final Object key) {
		// faster lookup
		final File file = getFile(getName(key));
		Object value = null;
		if (file.isFile()) {
			value = readFile(file);
			file.delete();
		}
		return value;
	}

	@Override
	public int size() {
		return baseDirectory.list(filter).length;
	}

	private void writeFile(final File file, final Object value) {
		try {
			final FileOutputStream out = new FileOutputStream(file);
			final Writer writer = encoding != null ? new OutputStreamWriter(out, encoding)
					: new OutputStreamWriter(out);
			try {
				xstream.toXML(value, writer);
			} finally {
				writer.close();
			}
		} catch (final IOException e) {
			throw new StreamException(e);
		}
	}

}
