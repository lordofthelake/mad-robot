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

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

import com.madrobot.di.wizard.xml.DynamicProxyMapper;
import com.madrobot.di.wizard.xml.Mapper;
import com.madrobot.di.wizard.xml.io.HierarchicalStreamReader;
import com.madrobot.di.wizard.xml.io.HierarchicalStreamWriter;
import com.madrobot.reflect.FieldUtils;

/**
 * Converts a dynamic proxy to XML, storing the implemented interfaces and handler.
 * 
 * @author Joe Walnes
 */
public class DynamicProxyConverter implements Converter {

	private static final InvocationHandler DUMMY = new InvocationHandler() {
		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			return null;
		}
	};
	private static final Field HANDLER;
	static {
		Field field = null;
		try {
			field = Proxy.class.getDeclaredField("h");
			field.setAccessible(true);
		} catch (NoSuchFieldException e) {
			throw new ExceptionInInitializerError(e);
		}
		HANDLER = field;
	}
	private ClassLoader classLoader;

	private Mapper mapper;

	public DynamicProxyConverter(Mapper mapper) {
		this(mapper, DynamicProxyConverter.class.getClassLoader());
	}

	public DynamicProxyConverter(Mapper mapper, ClassLoader classLoader) {
		this.classLoader = classLoader;
		this.mapper = mapper;
	}

	private void addInterfacesToXml(Object source, HierarchicalStreamWriter writer) {
		Class[] interfaces = source.getClass().getInterfaces();
		for (int i = 0; i < interfaces.length; i++) {
			Class currentInterface = interfaces[i];
			writer.startNode("interface");
			writer.setValue(mapper.serializedClass(currentInterface));
			writer.endNode();
		}
	}

	@Override
	public boolean canConvert(Class type) {
		return type.equals(DynamicProxyMapper.DynamicProxy.class) || Proxy.isProxyClass(type);
	}

	@Override
	public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
		InvocationHandler invocationHandler = Proxy.getInvocationHandler(source);
		addInterfacesToXml(source, writer);
		writer.startNode("handler");
		String attributeName = mapper.aliasForSystemAttribute("class");
		if (attributeName != null) {
			writer.addAttribute(attributeName, mapper.serializedClass(invocationHandler.getClass()));
		}
		context.convertAnother(invocationHandler);
		writer.endNode();
	}

	@Override
	public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
		List interfaces = new ArrayList();
		InvocationHandler handler = null;
		Class handlerType = null;
		while (reader.hasMoreChildren()) {
			reader.moveDown();
			String elementName = reader.getNodeName();
			if (elementName.equals("interface")) {
				interfaces.add(mapper.realClass(reader.getValue()));
			} else if (elementName.equals("handler")) {
				String attributeName = mapper.aliasForSystemAttribute("class");
				if (attributeName != null) {
					handlerType = mapper.realClass(reader.getAttribute(attributeName));
					break;
				}
			}
			reader.moveUp();
		}
		if (handlerType == null) {
			throw new ConversionException("No InvocationHandler specified for dynamic proxy");
		}
		Class[] interfacesAsArray = new Class[interfaces.size()];
		interfaces.toArray(interfacesAsArray);
		Object proxy = Proxy.newProxyInstance(classLoader, interfacesAsArray, DUMMY);
		handler = (InvocationHandler) context.convertAnother(proxy, handlerType);
		reader.moveUp();
		try {
			FieldUtils.writeField(HANDLER, proxy, handler);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return proxy;
	}
}
