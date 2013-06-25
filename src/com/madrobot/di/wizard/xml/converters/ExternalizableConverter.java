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

package com.madrobot.di.wizard.xml.converters;

import java.io.Externalizable;
import java.io.IOException;
import java.io.NotActiveException;
import java.io.ObjectInputValidation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import com.madrobot.di.wizard.xml.Mapper;
import com.madrobot.di.wizard.xml.core.CustomObjectInputStream;
import com.madrobot.di.wizard.xml.core.CustomObjectOutputStream;
import com.madrobot.di.wizard.xml.core.HierarchicalStreams;
import com.madrobot.di.wizard.xml.io.ExtendedHierarchicalStreamWriterHelper;
import com.madrobot.di.wizard.xml.io.HierarchicalStreamReader;
import com.madrobot.di.wizard.xml.io.HierarchicalStreamWriter;

/**
 * Converts any object that implements the java.io.Externalizable interface, allowing compatibility with native Java
 * serialization.
 * 
 * @author Joe Walnes
 */
public class ExternalizableConverter implements Converter {

	private final ClassLoader classLoader;
	private Mapper mapper;

	/**
	 * @deprecated As of 1.4 use {@link #ExternalizableConverter(Mapper, ClassLoader)}
	 */
	@Deprecated
	public ExternalizableConverter(Mapper mapper) {
		this(mapper, null);
	}

	public ExternalizableConverter(Mapper mapper, ClassLoader classLoader) {
		this.mapper = mapper;
		this.classLoader = classLoader;
	}

	@Override
	public boolean canConvert(Class type) {
		return Externalizable.class.isAssignableFrom(type);
	}

	@Override
	public void marshal(Object source, final HierarchicalStreamWriter writer, final MarshallingContext context) {
		try {
			Externalizable externalizable = (Externalizable) source;
			CustomObjectOutputStream.StreamCallback callback = new CustomObjectOutputStream.StreamCallback() {
				@Override
				public void close() {
					throw new UnsupportedOperationException(
							"Objects are not allowed to call ObjectOutput.close() from writeExternal()");
				}

				@Override
				public void defaultWriteObject() {
					throw new UnsupportedOperationException();
				}

				@Override
				public void flush() {
					writer.flush();
				}

				@Override
				public void writeFieldsToStream(Map fields) {
					throw new UnsupportedOperationException();
				}

				@Override
				public void writeToStream(Object object) {
					if (object == null) {
						writer.startNode("null");
						writer.endNode();
					} else {
						ExtendedHierarchicalStreamWriterHelper.startNode(writer,
								mapper.serializedClass(object.getClass()), object.getClass());
						context.convertAnother(object);
						writer.endNode();
					}
				}
			};
			CustomObjectOutputStream objectOutput = CustomObjectOutputStream.getInstance(context, callback);
			externalizable.writeExternal(objectOutput);
			objectOutput.popCallback();
		} catch (IOException e) {
			throw new ConversionException("Cannot serialize " + source.getClass().getName() + " using Externalization",
					e);
		}
	}

	@Override
	public Object unmarshal(final HierarchicalStreamReader reader, final UnmarshallingContext context) {
		final Class type = context.getRequiredType();
		final Constructor defaultConstructor;
		try {
			defaultConstructor = type.getDeclaredConstructor((Class[]) null);
			if (!defaultConstructor.isAccessible()) {
				defaultConstructor.setAccessible(true);
			}
			final Externalizable externalizable = (Externalizable) defaultConstructor.newInstance((Object[]) null);
			CustomObjectInputStream.StreamCallback callback = new CustomObjectInputStream.StreamCallback() {
				@Override
				public void close() {
					throw new UnsupportedOperationException(
							"Objects are not allowed to call ObjectInput.close() from readExternal()");
				}

				@Override
				public void defaultReadObject() {
					throw new UnsupportedOperationException();
				}

				@Override
				public Map readFieldsFromStream() {
					throw new UnsupportedOperationException();
				}

				@Override
				public Object readFromStream() {
					reader.moveDown();
					Class type = HierarchicalStreams.readClassType(reader, mapper);
					Object streamItem = context.convertAnother(externalizable, type);
					reader.moveUp();
					return streamItem;
				}

				@Override
				public void registerValidation(ObjectInputValidation validation, int priority)
						throws NotActiveException {
					throw new NotActiveException("stream inactive");
				}
			};
			CustomObjectInputStream objectInput = CustomObjectInputStream.getInstance(context, callback, classLoader);
			externalizable.readExternal(objectInput);
			objectInput.popCallback();
			return externalizable;
		} catch (NoSuchMethodException e) {
			throw new ConversionException("Cannot construct " + type.getClass() + ", missing default constructor", e);
		} catch (InvocationTargetException e) {
			throw new ConversionException("Cannot construct " + type.getClass(), e);
		} catch (InstantiationException e) {
			throw new ConversionException("Cannot construct " + type.getClass(), e);
		} catch (IllegalAccessException e) {
			throw new ConversionException("Cannot construct " + type.getClass(), e);
		} catch (IOException e) {
			throw new ConversionException("Cannot externalize " + type.getClass(), e);
		} catch (ClassNotFoundException e) {
			throw new ConversionException("Cannot externalize " + type.getClass(), e);
		}
	}
}
