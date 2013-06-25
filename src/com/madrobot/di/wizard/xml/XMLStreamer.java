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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

import com.madrobot.di.wizard.xml.converters.ConversionException;
import com.madrobot.di.wizard.xml.io.HierarchicalStreamDriver;
import com.madrobot.di.wizard.xml.io.HierarchicalStreamReader;
import com.madrobot.di.wizard.xml.io.xml.XppDriver;

/**
 * Self-contained XStream generator. The class is a utility to write XML streams that contain additionally the
 * XMLWizarda that was used to serialize the object graph. Such a stream can be unmarshalled using this embedded XStream
 * instance, that kept any settings.
 * 
 * @since 1.2
 */
class XMLStreamer {

	/**
	 * Deserialize a self-contained XStream with object from an XML Reader.
	 * 
	 * @throws IOException
	 *             if an error occurs reading from the Reader.
	 * @throws ClassNotFoundException
	 *             if a class in the XML stream cannot be found
	 * @throws com.madrobot.di.wizard.xml.XMLWizardException
	 *             if the object cannot be deserialized
	 * @since 1.2
	 */
	Object fromXML(HierarchicalStreamDriver driver, Reader xml) throws IOException, ClassNotFoundException {
		XMLWizard outer = new XMLWizard(driver);
		HierarchicalStreamReader reader = driver.createReader(xml);
		ObjectInputStream configIn = outer.createObjectInputStream(reader);
		try {
			XMLWizard configured = (XMLWizard) configIn.readObject();
			ObjectInputStream in = configured.createObjectInputStream(reader);
			try {
				return in.readObject();
			} finally {
				in.close();
			}
		} finally {
			configIn.close();
		}
	}

	/**
	 * Deserialize a self-contained XStream with object from a String.
	 * 
	 * @throws ClassNotFoundException
	 *             if a class in the XML stream cannot be found
	 * @throws ObjectStreamException
	 *             if the XML contains non-deserializable elements
	 * @throws com.madrobot.di.wizard.xml.XMLWizardException
	 *             if the object cannot be deserialized
	 * @since 1.2
	 * @see #toXML(XMLWizard, Object, Writer)
	 */
	Object fromXML(HierarchicalStreamDriver driver, String xml) throws ClassNotFoundException, ObjectStreamException {
		try {
			return fromXML(driver, new StringReader(xml));
		} catch (ObjectStreamException e) {
			throw e;
		} catch (IOException e) {
			throw new ConversionException("Unexpeced IO error from a StringReader", e);
		}
	}

	/**
	 * Deserialize a self-contained XStream with object from an XML Reader. The method will use internally an XppDriver
	 * to load the contained XStream instance.
	 * 
	 * @throws IOException
	 *             if an error occurs reading from the Reader.
	 * @throws ClassNotFoundException
	 *             if a class in the XML stream cannot be found
	 * @throws com.madrobot.di.wizard.xml.XMLWizardException
	 *             if the object cannot be deserialized
	 * @since 1.2
	 * @see #toXML(XMLWizard, Object, Writer)
	 */
	Object fromXML(Reader xml) throws IOException, ClassNotFoundException {
		return fromXML(new XppDriver(), xml);
	}

	/**
	 * Deserialize a self-contained XStream with object from a String. The method will use internally an XppDriver to
	 * load the contained XStream instance.
	 * 
	 * @throws ClassNotFoundException
	 *             if a class in the XML stream cannot be found
	 * @throws ObjectStreamException
	 *             if the XML contains non-deserializable elements
	 * @throws com.madrobot.di.wizard.xml.XMLWizardException
	 *             if the object cannot be deserialized
	 * @since 1.2
	 * @see #toXML(XMLWizard, Object, Writer)
	 */
	Object fromXML(String xml) throws ClassNotFoundException, ObjectStreamException {
		try {
			return fromXML(new StringReader(xml));
		} catch (ObjectStreamException e) {
			throw e;
		} catch (IOException e) {
			throw new ConversionException("Unexpeced IO error from a StringReader", e);
		}
	}

	/**
	 * Serialize an object including the XStream to a pretty-printed XML String.
	 * 
	 * @throws ObjectStreamException
	 *             if the XML contains non-serializable elements
	 * @throws com.madrobot.di.wizard.xml.XMLWizardException
	 *             if the object cannot be serialized
	 * @since 1.2
	 * @see #toXML(XMLWizard, Object, Writer)
	 */
	String toXML(XMLWizard xstream, Object obj) throws ObjectStreamException {
		Writer writer = new StringWriter();
		try {
			toXML(xstream, obj, writer);
		} catch (ObjectStreamException e) {
			throw e;
		} catch (IOException e) {
			throw new ConversionException("Unexpeced IO error from a StringWriter", e);
		}
		return writer.toString();
	}

	/**
	 * Serialize an object including the XStream to the given Writer as pretty-printed XML.
	 * <p>
	 * Warning: XStream will serialize itself into this XML stream. To read such an XML code, you should use
	 * {@link XMLStreamer#fromXML(Reader)} or one of the other overloaded methods. Since a lot of internals are written
	 * into the stream, you cannot expect to use such an XML to work with another XStream version or with XStream
	 * running on different JDKs and/or versions. We have currently no JDK 1.3 support, nor will the
	 * PureReflectionConverter work with a JDK less than 1.5.
	 * </p>
	 * 
	 * @throws IOException
	 *             if an error occurs reading from the Writer.
	 * @throws com.madrobot.di.wizard.xml.XMLWizardException
	 *             if the object cannot be serialized
	 * @since 1.2
	 */
	void toXML(XMLWizard xstream, Object obj, Writer out) throws IOException {
		XMLWizard outer = new XMLWizard();
		ObjectOutputStream oos = outer.createObjectOutputStream(out);
		try {
			oos.writeObject(xstream);
			oos.flush();
			xstream.toXML(obj, out);
		} finally {
			oos.close();
		}
	}

}
