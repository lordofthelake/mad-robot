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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.AttributesImpl;

import com.madrobot.di.wizard.xml.XMLWizard;
import com.madrobot.di.wizard.xml.io.AbstractWriter;
import com.madrobot.di.wizard.xml.io.NameCoder;
import com.madrobot.di.wizard.xml.io.StreamException;

/**
 * A SAX {@link org.xml.sax.XMLReader parser} that acts as an XmlWizard
 * {@link com.madrobot.di.wizard.xml.io.HierarchicalStreamWriter} to enable direct generation of a SAX event flow from
 * the XmlWizard serialization of a list of list of Java objects.
 * <p/>
 * As a custom SAX parser, this class ignores the arguments of the two standard parse methods (
 * {@link #parse(java.lang.String)} and {@link #parse(org.xml.sax.InputSource)}) but relies on a proprietary SAX
 * property {@link #SOURCE_OBJECT_LIST_PROPERTY} to define the list of objects to serialize.
 * </p>
 * <p/>
 * Configuration of this SAX parser is achieved through the standard {@link #setProperty SAX property mechanism}. While
 * specific setter methods require direct access to the parser instance, SAX properties support configuration settings
 * to be propagated through a chain of {@link org.xml.sax.XMLFilter filters} down to the underlying parser object.
 * </p>
 * <p/>
 * This mechanism shall be used to configure the {@link #SOURCE_OBJECT_LIST_PROPERTY objects to be serialized} as well
 * as the {@link #CONFIGURED_XSTREAM_PROPERTY XStream facade}.
 * </p>
 * 
 */
public final class SaxWriter extends AbstractWriter implements XMLReader {
	/**
	 * The {@link #setProperty SAX property} to configure the XStream facade to be used for object serialization. If the
	 * property is not set, a new XStream facade will be allocated for each parse.
	 */
	public final static String CONFIGURED_XSTREAM_PROPERTY = "http://com.thoughtworks.xstream/sax/property/configured-xstream";

	/**
	 * The {@link #setProperty SAX property} to configure the list of Java objects to serialize. Setting this property
	 * prior invoking one of the parse() methods is mandatory.
	 * 
	 * @see #parse(java.lang.String)
	 * @see #parse(org.xml.sax.InputSource)
	 */
	public final static String SOURCE_OBJECT_LIST_PROPERTY = "http://com.thoughtworks.xstream/sax/property/source-object-list";

	// =========================================================================
	// SAX XMLReader interface support
	// =========================================================================

	private final AttributesImpl attributeList = new AttributesImpl();

	private char[] buffer = new char[128];

	/**
	 * The SAX ContentHandler associated to this XMLReader.
	 */
	private ContentHandler contentHandler = null;

	private int depth = 0;

	/**
	 * The SAX DTDHandler associated to this XMLReader.
	 */
	private DTDHandler dtdHandler = null;

	private List elementStack = new LinkedList();

	/**
	 * The SAX EntityResolver associated to this XMLReader.
	 */
	private EntityResolver entityResolver = null;

	/**
	 * The SAX ErrorHandler associated to this XMLReader.
	 */
	private ErrorHandler errorHandler = null;

	/**
	 * The SAX features defined for this XMLReader.
	 * <p/>
	 * This class does not define any feature (yet) and ignores the SAX mandatory feature. Thus, this member is present
	 * only to support the mandatory feature setting and retrieval logic defined by SAX.
	 * </p>
	 */
	private Map features = new HashMap();

	private final boolean includeEnclosingDocument;

	/**
	 * The SAX properties defined for this XMLReader.
	 */
	private final Map properties = new HashMap();

	private boolean startTagInProgress = false;

	public SaxWriter() {
		this(true);
	}

	// -------------------------------------------------------------------------
	// Configuration
	// -------------------------------------------------------------------------

	public SaxWriter(boolean includeEnclosingDocument) {
		this(includeEnclosingDocument, new XmlFriendlyNameCoder());
	}

	/**
	 * @since 1.4
	 */
	public SaxWriter(boolean includeEnclosingDocument, NameCoder nameCoder) {
		super(nameCoder);
		this.includeEnclosingDocument = includeEnclosingDocument;
	}

	/**
	 * @deprecated As of 1.4 use {@link SaxWriter#SaxWriter(boolean, NameCoder)} instead.
	 */
	@Deprecated
	public SaxWriter(boolean includeEnclosingDocument, XmlFriendlyNameCoder replacer) {
		this(includeEnclosingDocument, (NameCoder) replacer);
	}

	/**
	 * @since 1.4
	 */
	public SaxWriter(NameCoder nameCoder) {
		this(true, nameCoder);
	}

	// ---------------------------------------------------------------------
	// Event handlers
	// ---------------------------------------------------------------------

	/**
	 * @deprecated As of 1.4 use {@link SaxWriter#SaxWriter(NameCoder)} instead.
	 */
	@Deprecated
	public SaxWriter(XmlFriendlyNameCoder replacer) {
		this(true, replacer);
	}

	@Override
	public void addAttribute(String name, String value) {
		if (this.startTagInProgress) {
			String escapedName = encodeNode(name);
			this.attributeList.addAttribute("", escapedName, escapedName, "CDATA", value);
		} else {
			throw new StreamException(new IllegalStateException("No startElement being processed"));
		}
	}

	@Override
	public void close() {
		// don't need to do anything
	}

	/**
	 * Fires the SAX endDocument event towards the configured ContentHandler.
	 * 
	 * @param multiObjectMode
	 *            whether serialization of several object will be merge into a single SAX document.
	 * @throws SAXException
	 *             if thrown by the ContentHandler.
	 */
	private void endDocument(boolean multiObjectMode) throws SAXException {
		if ((this.depth == 0) || ((this.depth == 1) && (multiObjectMode))) {
			this.contentHandler.endDocument();
			this.depth = 0;
		}
	}

	@Override
	public void endNode() {
		try {
			this.flushStartTag();

			String tagName = (String) (this.elementStack.remove(0));

			this.contentHandler.endElement("", tagName, tagName);

			this.depth--;
			if (this.depth == 0 && includeEnclosingDocument) {
				this.endDocument(false);
			}
		} catch (SAXException e) {
			throw new StreamException(e);
		}
	}

	@Override
	public void flush() {
		// don't need to do anything
	}

	/**
	 * Fires any pending SAX startElement event towards the configured ContentHandler.
	 * 
	 * @throws SAXException
	 *             if thrown by the ContentHandler.
	 */
	private void flushStartTag() throws SAXException {
		if (this.startTagInProgress) {
			String tagName = (String) (this.elementStack.get(0));

			this.contentHandler.startElement("", tagName, tagName, this.attributeList);
			this.attributeList.clear();
			this.startTagInProgress = false;
		}
	}

	/**
	 * Returns the current content handler.
	 * 
	 * @return the current content handler, or <code>null</code> if none has been registered.
	 * @see #setContentHandler
	 */
	@Override
	public ContentHandler getContentHandler() {
		return this.contentHandler;
	}

	// ---------------------------------------------------------------------
	// Parsing
	// ---------------------------------------------------------------------

	/**
	 * Returns the current DTD handler.
	 * 
	 * @return the current DTD handler, or <code>null</code> if none has been registered.
	 * @see #setDTDHandler
	 */
	@Override
	public DTDHandler getDTDHandler() {
		return this.dtdHandler;
	}

	/**
	 * Returns the current entity resolver.
	 * 
	 * @return the current entity resolver, or <code>null</code> if none has been registered.
	 * @see #setEntityResolver
	 */
	@Override
	public EntityResolver getEntityResolver() {
		return this.entityResolver;
	}

	/**
	 * Returns the current error handler.
	 * 
	 * @return the current error handler, or <code>null</code> if none has been registered.
	 * @see #setErrorHandler
	 */
	@Override
	public ErrorHandler getErrorHandler() {
		return this.errorHandler;
	}

	// =========================================================================
	// XStream HierarchicalStreamWriter interface support
	// =========================================================================

	/**
	 * Looks up the value of a feature.
	 * <p/>
	 * The feature name is any fully-qualified URI. It is possible for an XMLReader to recognize a feature name but to
	 * be unable to return its value; this is especially true in the case of an adapter for a SAX1 Parser, which has no
	 * way of knowing whether the underlying parser is performing validation or expanding external entities.
	 * </p>
	 * <p/>
	 * All XMLReaders are required to recognize the <code>http://xml.org/sax/features/namespaces</code> and the
	 * <code>http://xml.org/sax/features/namespace-prefixes</code> feature names.
	 * </p>
	 * <p/>
	 * Some feature values may be available only in specific contexts, such as before, during, or after a parse.
	 * </p>
	 * <p/>
	 * Implementors are free (and encouraged) to invent their own features, using names built on their own URIs.
	 * </p>
	 * 
	 * @param name
	 *            the feature name, which is a fully-qualified URI.
	 * @return the current state of the feature (true or false).
	 * @throws SAXNotRecognizedException
	 *             when the XMLReader does not recognize the feature name.
	 * @see #setFeature
	 */
	@Override
	public boolean getFeature(String name) throws SAXNotRecognizedException {
		if ((name.equals("http://xml.org/sax/features/namespaces"))
				|| (name.equals("http://xml.org/sax/features/namespace-prefixes"))) {
			Boolean value = (Boolean) (this.features.get(name));

			if (value == null) {
				value = Boolean.FALSE;
			}
			return value.booleanValue();
		} else {
			throw new SAXNotRecognizedException(name);
		}
	}
	/**
	 * Looks up the value of a property.
	 * <p/>
	 * The property name is any fully-qualified URI. It is possible for an XMLReader to recognize a property name but to
	 * be unable to return its state.
	 * </p>
	 * <p/>
	 * XMLReaders are not required to recognize any specific property names, though an initial core set is documented
	 * for SAX2.
	 * </p>
	 * <p/>
	 * Some property values may be available only in specific contexts, such as before, during, or after a parse.
	 * </p>
	 * <p/>
	 * Implementors are free (and encouraged) to invent their own properties, using names built on their own URIs.
	 * </p>
	 * 
	 * @param name
	 *            the property name, which is a fully-qualified URI.
	 * @return the current value of the property.
	 * @throws SAXNotRecognizedException
	 *             when the XMLReader does not recognize the property name.
	 * @see #getProperty
	 */
	@Override
	public Object getProperty(String name) throws SAXNotRecognizedException {
		if ((name.equals(CONFIGURED_XSTREAM_PROPERTY)) || (name.equals(SOURCE_OBJECT_LIST_PROPERTY))) {
			return this.properties.get(name);
		} else {
			throw new SAXNotRecognizedException(name);
		}
	}
	/**
	 * Serializes the Java objects of the configured list into a flow of SAX events.
	 * 
	 * @throws SAXException
	 *             if the configured object list is invalid or object serialization failed.
	 */
	private void parse() throws SAXException {
		XMLWizard xstream = (XMLWizard) (this.properties.get(CONFIGURED_XSTREAM_PROPERTY));
		if (xstream == null) {
			xstream = new XMLWizard();
		}

		List source = (List) (this.properties.get(SOURCE_OBJECT_LIST_PROPERTY));
		if ((source == null) || (source.isEmpty())) {
			throw new SAXException("Missing or empty source object list. Setting property \""
					+ SOURCE_OBJECT_LIST_PROPERTY + "\" is mandatory");
		}

		try {
			this.startDocument(true);
			for (Iterator i = source.iterator(); i.hasNext();) {
				xstream.marshal(i.next(), this);
			}
			this.endDocument(true);
		} catch (StreamException e) {
			if (e.getCause() instanceof SAXException) {
				throw (SAXException) (e.getCause());
			} else {
				throw new SAXException(e);
			}
		}
	}
	/**
	 * Parse an XML document.
	 * <p/>
	 * The application can use this method to instruct the XML reader to begin parsing an XML document from any valid
	 * input source (a character stream, a byte stream, or a URI).
	 * </p>
	 * <p/>
	 * Applications may not invoke this method while a parse is in progress (they should create a new XMLReader instead
	 * for each nested XML document). Once a parse is complete, an application may reuse the same XMLReader object,
	 * possibly with a different input source.
	 * </p>
	 * <p/>
	 * During the parse, the XMLReader will provide information about the XML document through the registered event
	 * handlers.
	 * </p>
	 * <p/>
	 * This method is synchronous: it will not return until parsing has ended. If a client application wants to
	 * terminate parsing early, it should throw an exception.
	 * </p>
	 * <p/>
	 * <strong>Note</strong>: As a custom SAX parser, this class ignores the <code>source</code> argument of this method
	 * and relies on the proprietary SAX property {@link #SOURCE_OBJECT_LIST_PROPERTY}) to define the list of objects to
	 * serialize.
	 * </p>
	 * 
	 * @param input
	 *            The input source for the top-level of the XML document.
	 * @throws SAXException
	 *             Any SAX exception, possibly wrapping another exception.
	 * @see org.xml.sax.InputSource
	 * @see #parse(java.lang.String)
	 * @see #setEntityResolver
	 * @see #setDTDHandler
	 * @see #setContentHandler
	 * @see #setErrorHandler
	 */
	@Override
	public void parse(InputSource input) throws SAXException {
		this.parse();
	}
	/**
	 * Parses an XML document from a system identifier (URI).
	 * <p/>
	 * This method is a shortcut for the common case of reading a document from a system identifier. It is the exact
	 * equivalent of the following:
	 * </p>
	 * <blockquote>
	 * 
	 * <pre>
	 * parse(new InputSource(systemId));
	 * </pre>
	 * 
	 * </blockquote>
	 * <p/>
	 * If the system identifier is a URL, it must be fully resolved by the application before it is passed to the
	 * parser.
	 * </p>
	 * <p/>
	 * <strong>Note</strong>: As a custom SAX parser, this class ignores the <code>systemId</code> argument of this
	 * method and relies on the proprietary SAX property {@link #SOURCE_OBJECT_LIST_PROPERTY}) to define the list of
	 * objects to serialize.
	 * </p>
	 * 
	 * @param systemId
	 *            the system identifier (URI).
	 * @throws SAXException
	 *             Any SAX exception, possibly wrapping another exception.
	 * @see #parse(org.xml.sax.InputSource)
	 */
	@Override
	public void parse(String systemId) throws SAXException {
		this.parse();
	}

	/**
	 * Allows an application to register a content event handler.
	 * <p/>
	 * If the application does not register a content handler, all content events reported by the SAX parser will be
	 * silently ignored.
	 * </p>
	 * <p/>
	 * Applications may register a new or different handler in the middle of a parse, and the SAX parser must begin
	 * using the new handler immediately.
	 * </p>
	 * 
	 * @param handler
	 *            the content handler.
	 * @throws NullPointerException
	 *             if the handler argument is <code>null</code>.
	 * @see #getContentHandler
	 */
	@Override
	public void setContentHandler(ContentHandler handler) {
		if (handler == null) {
			throw new NullPointerException("handler");
		}
		this.contentHandler = handler;
		return;
	}

	/**
	 * Allows an application to register a DTD event handler.
	 * <p/>
	 * If the application does not register a DTD handler, all DTD events reported by the SAX parser will be silently
	 * ignored.
	 * </p>
	 * <p/>
	 * Applications may register a new or different handler in the middle of a parse, and the SAX parser must begin
	 * using the new handler immediately.
	 * </p>
	 * 
	 * @param handler
	 *            the DTD handler.
	 * @throws NullPointerException
	 *             if the handler argument is <code>null</code>.
	 * @see #getDTDHandler
	 */
	@Override
	public void setDTDHandler(DTDHandler handler) {
		if (handler == null) {
			throw new NullPointerException("handler");
		}
		this.dtdHandler = handler;
		return;
	}

	/**
	 * Allows an application to register an entity resolver.
	 * <p/>
	 * If the application does not register an entity resolver, the XMLReader will perform its own default resolution.
	 * </p>
	 * <p/>
	 * Applications may register a new or different resolver in the middle of a parse, and the SAX parser must begin
	 * using the new resolver immediately.
	 * </p>
	 * 
	 * @param resolver
	 *            the entity resolver.
	 * @throws NullPointerException
	 *             if the resolver argument is <code>null</code>.
	 * @see #getEntityResolver
	 */
	@Override
	public void setEntityResolver(EntityResolver resolver) {
		if (resolver == null) {
			throw new NullPointerException("resolver");
		}
		this.entityResolver = resolver;
		return;
	}

	/**
	 * Allows an application to register an error event handler.
	 * <p/>
	 * If the application does not register an error handler, all error events reported by the SAX parser will be
	 * silently ignored; however, normal processing may not continue. It is highly recommended that all SAX applications
	 * implement an error handler to avoid unexpected bugs.
	 * </p>
	 * <p/>
	 * Applications may register a new or different handler in the middle of a parse, and the SAX parser must begin
	 * using the new handler immediately.
	 * </p>
	 * 
	 * @param handler
	 *            the error handler.
	 * @throws NullPointerException
	 *             if the handler argument is <code>null</code>.
	 * @see #getErrorHandler
	 */
	@Override
	public void setErrorHandler(ErrorHandler handler) {
		if (handler == null) {
			throw new NullPointerException("handler");
		}
		this.errorHandler = handler;
		return;
	}

	/**
	 * Sets the state of a feature.
	 * <p/>
	 * The feature name is any fully-qualified URI.
	 * </p>
	 * <p/>
	 * All XMLReaders are required to support setting <code>http://xml.org/sax/features/namespaces</code> to
	 * <code>true</code> and <code>http://xml.org/sax/features/namespace-prefixes</code> to <code>false</code>.
	 * </p>
	 * <p/>
	 * Some feature values may be immutable or mutable only in specific contexts, such as before, during, or after a
	 * parse.
	 * </p>
	 * <p/>
	 * <strong>Note</strong>: This implementation only supports the two mandatory SAX features.
	 * </p>
	 * 
	 * @param name
	 *            the feature name, which is a fully-qualified URI.
	 * @param value
	 *            the requested state of the feature (true or false).
	 * @throws SAXNotRecognizedException
	 *             when the XMLReader does not recognize the feature name.
	 * @see #getFeature
	 */
	@Override
	public void setFeature(String name, boolean value) throws SAXNotRecognizedException {
		if ((name.equals("http://xml.org/sax/features/namespaces"))
				|| (name.equals("http://xml.org/sax/features/namespace-prefixes"))) {
			this.features.put(name, value ? Boolean.TRUE : Boolean.FALSE); // JDK 1.3 friendly
		} else {
			throw new SAXNotRecognizedException(name);
		}
	}

	/**
	 * Sets the value of a property.
	 * <p/>
	 * The property name is any fully-qualified URI. It is possible for an XMLReader to recognize a property name but to
	 * be unable to set its value.
	 * </p>
	 * <p/>
	 * XMLReaders are not required to recognize setting any specific property names, though a core set is provided with
	 * SAX2.
	 * </p>
	 * <p/>
	 * Some property values may be immutable or mutable only in specific contexts, such as before, during, or after a
	 * parse.
	 * </p>
	 * <p/>
	 * This method is also the standard mechanism for setting extended handlers.
	 * </p>
	 * <p/>
	 * <strong>Note</strong>: This implementation only supports two (proprietary) properties:
	 * {@link #CONFIGURED_XSTREAM_PROPERTY} and {@link #SOURCE_OBJECT_LIST_PROPERTY}.
	 * </p>
	 * 
	 * @param name
	 *            the property name, which is a fully-qualified URI.
	 * @param value
	 *            the requested value for the property.
	 * @throws SAXNotRecognizedException
	 *             when the XMLReader does not recognize the property name.
	 * @throws SAXNotSupportedException
	 *             when the XMLReader recognizes the property name but cannot set the requested value.
	 * @see #getProperty
	 */
	@Override
	public void setProperty(String name, Object value) throws SAXNotRecognizedException, SAXNotSupportedException {
		if (name.equals(CONFIGURED_XSTREAM_PROPERTY)) {
			if (!(value instanceof XMLWizard)) {
				throw new SAXNotSupportedException("Value for property \"" + CONFIGURED_XSTREAM_PROPERTY
						+ "\" must be a non-null XStream object");
			}
		} else if (name.equals(SOURCE_OBJECT_LIST_PROPERTY)) {
			if (value instanceof List) {
				List list = (List) value;

				if (list.isEmpty()) {
					throw new SAXNotSupportedException("Value for property \"" + SOURCE_OBJECT_LIST_PROPERTY
							+ "\" shall not be an empty list");
				} else {
					// Perform a copy of the list to prevent the application to
					// modify its content while the parse is being performed.
					value = Collections.unmodifiableList(new ArrayList(list));
				}
			} else {
				throw new SAXNotSupportedException("Value for property \"" + SOURCE_OBJECT_LIST_PROPERTY
						+ "\" must be a non-null List object");
			}
		} else {
			throw new SAXNotRecognizedException(name);
		}
		this.properties.put(name, value);
	}

	@Override
	public void setValue(String text) {
		try {
			this.flushStartTag();

			int lg = text.length();
			if (lg > buffer.length) {
				buffer = new char[lg];
			}
			text.getChars(0, lg, buffer, 0);

			this.contentHandler.characters(buffer, 0, lg);
		} catch (SAXException e) {
			throw new StreamException(e);
		}
	}

	/**
	 * Fires the SAX startDocument event towards the configured ContentHandler.
	 * 
	 * @param multiObjectMode
	 *            whether serialization of several object will be merge into a single SAX document.
	 * @throws SAXException
	 *             if thrown by the ContentHandler.
	 */
	private void startDocument(boolean multiObjectMode) throws SAXException {
		if (this.depth == 0) {
			// Notify contentHandler of document start.
			this.contentHandler.startDocument();

			if (multiObjectMode) {
				// Prevent marshalling of each object to fire its own
				// start/endDocument events.
				this.depth++;
			}
		}
	}

	@Override
	public void startNode(String name) {
		try {
			if (this.depth != 0) {
				this.flushStartTag();
			} else if (includeEnclosingDocument) {
				this.startDocument(false);
			}
			this.elementStack.add(0, encodeNode(name));

			this.startTagInProgress = true;
			this.depth++;
		} catch (SAXException e) {
			throw new StreamException(e);
		}
	}
}
