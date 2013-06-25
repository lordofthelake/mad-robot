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
import java.util.List;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import com.madrobot.di.wizard.xml.io.NameCoder;

public class DomReader extends AbstractDocumentReader {

	private List childElements;
	private Element currentElement;
	private StringBuffer textBuffer;

	public DomReader(Document document) {
		this(document.getDocumentElement());
	}

	/**
	 * @since 1.4
	 */
	public DomReader(Document document, NameCoder nameCoder) {
		this(document.getDocumentElement(), nameCoder);
	}

	/**
	 * @since 1.2
	 * @deprecated As of 1.4, use {@link DomReader#DomReader(Document, NameCoder)} instead.
	 */
	@Deprecated
	public DomReader(Document document, XmlFriendlyNameCoder replacer) {
		this(document.getDocumentElement(), (NameCoder) replacer);
	}

	public DomReader(Element rootElement) {
		this(rootElement, new XmlFriendlyNameCoder());
	}

	/**
	 * @since 1.4
	 */
	public DomReader(Element rootElement, NameCoder nameCoder) {
		super(rootElement, nameCoder);
		textBuffer = new StringBuffer();
	}

	/**
	 * @since 1.2
	 * @deprecated As of 1.4, use {@link DomReader#DomReader(Element, NameCoder)} instead.
	 */
	@Deprecated
	public DomReader(Element rootElement, XmlFriendlyNameCoder replacer) {
		this(rootElement, (NameCoder) replacer);
	}

	@Override
	public String getAttribute(int index) {
		return ((Attr) currentElement.getAttributes().item(index)).getValue();
	}

	@Override
	public String getAttribute(String name) {
		Attr attribute = currentElement.getAttributeNode(encodeAttribute(name));
		return attribute == null ? null : attribute.getValue();
	}

	@Override
	public int getAttributeCount() {
		return currentElement.getAttributes().getLength();
	}

	@Override
	public String getAttributeName(int index) {
		return decodeAttribute(((Attr) currentElement.getAttributes().item(index)).getName());
	}

	@Override
	protected Object getChild(int index) {
		return childElements.get(index);
	}

	@Override
	protected int getChildCount() {
		return childElements.size();
	}

	@Override
	public String getNodeName() {
		return decodeNode(currentElement.getTagName());
	}

	@Override
	protected Object getParent() {
		return currentElement.getParentNode();
	}

	@Override
	public String getValue() {
		NodeList childNodes = currentElement.getChildNodes();
		textBuffer.setLength(0);
		int length = childNodes.getLength();
		for (int i = 0; i < length; i++) {
			Node childNode = childNodes.item(i);
			if (childNode instanceof Text) {
				Text text = (Text) childNode;
				textBuffer.append(text.getData());
			}
		}
		return textBuffer.toString();
	}

	@Override
	public String peekNextChild() {
		NodeList childNodes = currentElement.getChildNodes();
		for (int i = 0; i < childNodes.getLength(); i++) {
			Node node = childNodes.item(i);
			if (node instanceof Element) {
				return decodeNode(((Element) node).getTagName());
			}
		}
		return null;
	}

	@Override
	protected void reassignCurrentElement(Object current) {
		currentElement = (Element) current;
		NodeList childNodes = currentElement.getChildNodes();
		childElements = new ArrayList();
		for (int i = 0; i < childNodes.getLength(); i++) {
			Node node = childNodes.item(i);
			if (node instanceof Element) {
				childElements.add(node);
			}
		}
	}
}
