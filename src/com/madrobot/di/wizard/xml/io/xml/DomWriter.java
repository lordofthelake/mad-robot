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

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.madrobot.di.wizard.xml.io.NameCoder;

/**
 * @author Michael Kopp
 */
public class DomWriter extends AbstractDocumentWriter {

	private final Document document;
	private boolean hasRootElement;

	public DomWriter(final Document document) {
		this(document, new XmlFriendlyNameCoder());
	}

	/**
	 * @since 1.4
	 */
	public DomWriter(final Document document, final NameCoder nameCoder) {
		this(document.getDocumentElement(), document, nameCoder);
	}

	/**
	 * @since 1.2
	 * @deprecated As of 1.4 use {@link DomWriter#DomWriter(Document, NameCoder)} instead.
	 */
	@Deprecated
	public DomWriter(final Document document, final XmlFriendlyNameCoder replacer) {
		this(document.getDocumentElement(), document, (NameCoder) replacer);
	}

	public DomWriter(final Element rootElement) {
		this(rootElement, new XmlFriendlyNameCoder());
	}

	/**
	 * @since 1.4
	 */
	public DomWriter(final Element element, final Document document, final NameCoder nameCoder) {
		super(element, nameCoder);
		this.document = document;
		hasRootElement = document.getDocumentElement() != null;
	}

	/**
	 * @since 1.2.1
	 * @deprecated As of 1.4 use {@link DomWriter#DomWriter(Element, Document, NameCoder)} instead.
	 */
	@Deprecated
	public DomWriter(final Element element, final Document document, final XmlFriendlyNameCoder replacer) {
		this(element, document, (NameCoder) replacer);
	}

	/**
	 * @since 1.4
	 */
	public DomWriter(final Element rootElement, final NameCoder nameCoder) {
		this(rootElement, rootElement.getOwnerDocument(), nameCoder);
	}

	/**
	 * @since 1.2
	 * @deprecated As of 1.4 use {@link DomWriter#DomWriter(Element, NameCoder)} instead.
	 */
	@Deprecated
	public DomWriter(final Element rootElement, final XmlFriendlyNameCoder replacer) {
		this(rootElement, rootElement.getOwnerDocument(), (NameCoder) replacer);
	}

	@Override
	public void addAttribute(final String name, final String value) {
		top().setAttribute(encodeAttribute(name), value);
	}

	@Override
	protected Object createNode(final String name) {
		final Element child = document.createElement(encodeNode(name));
		final Element top = top();
		if (top != null) {
			top().appendChild(child);
		} else if (!hasRootElement) {
			document.appendChild(child);
			hasRootElement = true;
		}
		return child;
	}

	@Override
	public void setValue(final String text) {
		top().appendChild(document.createTextNode(text));
	}

	private Element top() {
		return (Element) getCurrent();
	}
}
