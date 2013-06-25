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

package com.madrobot.di.xml.simpledeserializer;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.regex.Pattern;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import com.madrobot.di.Converter;
import com.madrobot.di.xml.simpledeserializer.annotations.ElementName;
import com.madrobot.di.xml.simpledeserializer.annotations.ItemType;

import android.util.Xml;

/**
 * Helper utility to deserialize any XML content into a specified model(java bean). <br/>
 * 
 * <p>
 * Steps to design the model:
 * <ol>
 * <li>Start by choosing a name for the model. This will represent the final class.</li>
 * <li>Add fields with names matching that of the elements in the XML to be parsed. For example, if the name of the
 * element is <code>message</code>, add a field with the name <code>message</code>.</li>
 * <li>If the name of the element is such that it does not qualify to be a valid Java identifier, for example,
 * <code>rss2.0</code>, use {@link ElementName} annotation. Give any name to the field and specify the name of the
 * element using the annotation.</li>
 * <li>The type of the fields that are possible are one of the following:
 * <ul>
 * <li>Any custom type with a public parameterless constructor</li>
 * <li>Any class that inherits from {@link List}, for example {@link ArrayList}. Such field will be referred to as a
 * collection field</li>
 * </ul>
 * </li>
 * <li>For a collection field, you must specify the type of items in the collection using the annotation
 * {@link ItemType}.</li>
 * <li>Create getters and setters for non-collection field.</li>
 * <li>
 * For a collection field, you must have the add-method with the name <code>addXXX</code> where <code>XXX</code> is the
 * capitalized name of the field. It must take exactly one parameter, of the type of the item in the collection.</li>
 * <li>
 * Use the method {@link #readToBean(InputStream, Class)} to deserialize the XML from the stream to appropriate entity
 * type.</li>
 * </ol>
 * </p>
 * 
 * <p>
 * Notes:
 * <ul>
 * <li>If no matching field is found for a specific element, the field will be ignored.</li>
 * <li>If there is an extra field in the model corresponding to which no element exists, it will continue to have its
 * default value</li>
 * <li>Similarly, if there is an error while converting the value, the field will retain its default value</li>
 * </ul>
 * </p>
 * 
 * <p>
 * An example is demonstrated below:
 * </p>
 * 
 * <p>
 * XML:
 * </p>
 * 
 * <pre>
 * &lt;xml&gt;
 *   &lt;message&gt;A-Value&lt;/message&gt;
 *   &lt;code2.0&gt;1234&lt;/code2.0&gt;
 *   &lt;item&gt;
 *     &lt;c&gt;C1-Value&lt;/c&gt;
 *   &lt;/item&gt;
 *   &lt;item&gt;
 *     &lt;c&gt;C2-Value&lt;/c&gt;
 *   &lt;/item&gt;
 * &lt;/xml&gt;
 * </pre>
 * 
 * <p>
 * Model:
 * </p>
 * 
 * <pre>
 * public class ModelA
 * {
 *    private String message;
 *    {@link ElementName ElementName("code2.0")}
 *    private int code;
 *    
 *    {@link ItemType ItemType(ModelB.class)}
 *    private List&lt;ModelB&gt; item = new List&lt;ModelB&gt;();
 *    
 *    public void setMessage(String message) { ... }
 *    public void setCode(int code) { ... }
 *    
 *    public void addItem(ModelB value) { ... }
 *    
 *    //Omitting the getters for brevity.
 * }
 * 
 * public class ModelB
 * {
 *    private String c;
 *    
 *    public void setC(String value) { ... }
 *    
 *    //Omitting the getters for brevity.
 * }
 * </pre>
 * 
 * <p>
 * To deserialize:
 * </p>
 * 
 * <pre>
 * InputStream input = getStreamFromSomewhere();
 * ModelA aValue = XMLDeserializer.getInstance().deserialize(input, ModelA.class);
 * // Work with aValue...
 * </pre>
 * 
 */
public final class XMLDeserializer {
	/**
	 * The single instance of the deserializer
	 */
	private static final XMLDeserializer instance = new XMLDeserializer();

	/**
	 * Regular expression patern for common baseline for Java identifier and XML element name
	 */
	private static final Pattern validFieldNamePattern = Pattern.compile("^[a-zA-Z][a-zA-Z0-9]*$");

	/**
	 * Returns the one and only instance of {@see BeanReader}
	 * 
	 * @return The BeanReader instance
	 */
	public static XMLDeserializer getInstance() {
		return instance;
	}

	/**
	 * Cached names of the add-methods
	 */
	private Map<String, String> addMethodNameMap = new HashMap<String, String>();

	/**
	 * Cached names of the getter methods
	 */
	private Map<String, String> getMethodNameMap = new HashMap<String, String>();

	/**
	 * Cached names of the setter methods
	 */
	private Map<String, String> setMethodNameMap = new HashMap<String, String>();

	/**
	 * Private constructor to disallow any public instantiation
	 */
	private XMLDeserializer() {
	}

	/**
	 * Adds a value to specified collection field
	 * 
	 * @param obj
	 *            Object whose field is to be deserialized
	 * @param info
	 *            The field details
	 * @param value
	 *            Value to add to collection
	 * @throws IllegalArgumentException
	 *             {@see Method#invoke(Object, Object...)}
	 * @throws IllegalAccessException
	 *             {@see Method#invoke(Object, Object...)}
	 * @throws InvocationTargetException
	 *             {@see Method#invoke(Object, Object...)}
	 */
	private void addFieldValue(Object obj, FieldInfo info, Object value) throws IllegalArgumentException,
			IllegalAccessException, InvocationTargetException {
		Method addMethod = info.getAddMethod();
		addMethod.invoke(obj, value);
	}

	/**
	 * Deserialize a specific element, recursively.
	 * 
	 * @param obj
	 *            Object whose fields need to be set
	 * @param parser
	 *            XML Parser to read data from
	 * @param stack
	 *            Stack of {@link ClassInfo} - entity type under consideration
	 * @throws XmlPullParserException
	 *             If an exception occurs during parsing
	 * @throws IOException
	 *             If an exception occurs while reading
	 */
	private void deserialize(Object obj, XmlPullParser parser, Stack<ClassInfo> stack) throws XmlPullParserException,
			IOException {
		int evtType = parser.next();
		String name;
		ClassInfo ci = stack.peek();
		Class<?> clz = ci.getType();

		// Read until the end of the document
		while (evtType != XmlPullParser.END_DOCUMENT) {

			// If we are done with the parsing of the current element, we done
			// with this level
			if (evtType == XmlPullParser.END_TAG) {
				if (ci.getElementName().equals(parser.getName())) {
					break;
				}
			}

			// Start of a tag signifies a field to be populated
			if (evtType == XmlPullParser.START_TAG) {
				name = parser.getName();
				FieldInfo info = getFieldInfo(clz, name);
				FieldType ft = info.getFieldType();

				if (ft.equals(FieldType.PSEUDO_PRIMITIVE)) {
					// For pseudo-primitive fields, directly convert the value
					// and set
					String value = parser.nextText();
					try {
						setFieldValue(obj, info, Converter.convertTo(value, info.getType()));
					} catch (Throwable e) {
						e.printStackTrace();
					}
				} else if (ft.equals(FieldType.COLLECTION)) {
					// For collection fields, deserialize the contents within
					// the element and add the value
					Field fld = info.getField();
					ItemType itemType = fld.getAnnotation(ItemType.class);
					if (itemType != null) {
						Class<?> itemValueType = itemType.value();
						Object value = null;
						if (Converter.isPseudoPrimitive(itemValueType)) {
							value = Converter.convertTo(parser.nextText(), itemValueType);
						} else {
							ClassInfo itemCI = new ClassInfo(itemValueType, name);
							stack.push(itemCI);
							try {
								Object subObj = itemValueType.newInstance();
								addFieldValue(obj, info, subObj);
								deserialize(subObj, parser, stack);
							} catch (Throwable e) {
								e.printStackTrace();
							}
						}
						if (value != null) {
							try {
								addFieldValue(obj, info, value);
							} catch (Throwable e) {
								e.printStackTrace();
							}
						}
					}
				} else if (ft.equals(FieldType.COMPOSITE)) {
					// For composite fields, instantiate appropriate data type
					// and set the value
					Class<?> subType = info.getType();
					ClassInfo itemCI = new ClassInfo(subType, name);
					stack.push(itemCI);
					try {
						Object subObj = subType.newInstance();
						setFieldValue(obj, info, subObj);
						deserialize(subObj, parser, stack);
					} catch (Throwable e) {
						e.printStackTrace();
					}
				} else if (ft.equals(FieldType.NOT_DEFINED)) {
					// process till element end
					skipElement(parser, name);
				}
			}
			evtType = parser.next();
		}
		stack.pop();
	}

	/**
	 * Returns the name of the add-method for a field.
	 * 
	 * @param fieldName
	 *            Field name under consideration
	 * @return Name of the add-method for the field
	 */
	public String getAddMethodName(String fieldName) {
		if (addMethodNameMap.containsKey(fieldName)) {
			return addMethodNameMap.get(fieldName);
		}
		String method = "add" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
		addMethodNameMap.put(fieldName, method);
		return method;
	}

	/**
	 * Retrieves the field information for the specified type and element name
	 * 
	 * @param clz
	 *            Entity type under consideration
	 * @param elementName
	 *            Name of the element being deserialized
	 * @return Field information with all details
	 */
	private FieldInfo getFieldInfo(Class<?> clz, String elementName) {
		FieldInfo info = new FieldInfo();
		info.setElementName(elementName);
		info.setFieldType(FieldType.NOT_DEFINED);

		String fieldName = getFieldName(clz, elementName);
		if (fieldName == null) {
			return info;
		}
		info.setFieldName(fieldName);

		try {
			Field field = clz.getDeclaredField(fieldName);
			if (field != null) {
				Method method = null;
				Class<?> type = field.getType();
				info.setType(type);
				info.setField(field);
				if (List.class.isAssignableFrom(field.getType())) {
					String methodName = getAddMethodName(fieldName);
					ItemType itemType = field.getAnnotation(ItemType.class);
					Class<?> itemValueType = (itemType != null) ? itemType.value() : Object.class;
					method = clz.getDeclaredMethod(methodName, itemValueType);
					info.setAddMethod(method);
				} else {
					String methodName = getSetMethodName(fieldName);
					method = clz.getDeclaredMethod(methodName, type);
					info.setSetMethod(method);
				}
				if (method != null) {
					if (Converter.isPseudoPrimitive(type)) {
						info.setFieldType(FieldType.PSEUDO_PRIMITIVE);
					} else if (List.class.isAssignableFrom(type)) {
						info.setFieldType(FieldType.COLLECTION);
					} else {
						info.setFieldType(FieldType.COMPOSITE);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return info;
	}

	/**
	 * Gets the field name for the corresponding element in the specified entity type.
	 * <p>
	 * If the field with exact name does not exist, it makes use of {@see ElementName} annotation on any of the fields
	 * declared in the class to identify the correct field in the entity
	 * </p>
	 * 
	 * @param clz
	 *            Entity type under consideration
	 * @param elementName
	 *            Name of the element being deserialized
	 * @return Name of the field, if found, null otherwise
	 */
	private String getFieldName(Class<?> clz, String elementName) {
		if (validFieldNamePattern.matcher(elementName).matches()) {
			return elementName;
		}
		Field[] fields = clz.getDeclaredFields();
		String fieldName = null;
		for (Field field : fields) {
			ElementName ename = field.getAnnotation(ElementName.class);
			if ((ename != null) && elementName.equals(ename.value())) {
				fieldName = field.getName();
				break;
			}
		}
		return fieldName;
	}

	/**
	 * Returns the name of the getter method for a field.
	 * 
	 * @param fieldName
	 *            Field name under consideration
	 * @return Name of the getter method for the field
	 */
	public String getGetMethodName(String fieldName) {
		if (getMethodNameMap.containsKey(fieldName)) {
			return getMethodNameMap.get(fieldName);
		}
		String method = "get" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
		getMethodNameMap.put(fieldName, method);
		return method;
	}

	/**
	 * Returns the name of the setter method for a field.
	 * 
	 * @param fieldName
	 *            Field name under consideration
	 * @return Name of the setter method for the field
	 */
	public String getSetMethodName(String fieldName) {
		if (setMethodNameMap.containsKey(fieldName)) {
			return setMethodNameMap.get(fieldName);
		}
		String method = "set" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
		setMethodNameMap.put(fieldName, method);
		return method;
	}

	/**
	 * Deserializes the XML data from the input to the corresponding entity type <br/>
	 * If there is an error while parsing, if possible it will try to ignore it, otherwise returns a null value.
	 * 
	 * @param input
	 *            Input stream to read data from
	 * @param bean
	 *            Type of the entity to deserialize data to
	 * @return Deserialized object, if successful, null otherwise
	 * @see #readToBean(XmlPullParser, Class)
	 */
	public <T> T readToBean(InputStream input, Class<T> bean) {
		XmlPullParser parser = Xml.newPullParser();
		try {
			parser.setInput(input, null);
			return readToBean(parser, bean);
		} catch (Throwable e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Deserializes the XML data from the input to the corresponding entity type <br/>
	 * If there is an error while parsing, if possible it will try to ignore it, otherwise returns a null value.
	 * 
	 * @param parser
	 *            Parser to read XML from
	 * @param bean
	 *            Type of the entity to deserialize data to
	 * @return Deserialized object, if successful, null otherwise
	 * @see #readToBean(InputStream, Class)
	 */
	public <T> T readToBean(XmlPullParser parser, Class<T> bean) {
		T rv = null;
		Stack<ClassInfo> stack = new Stack<ClassInfo>();

		try {
			int evtType = -1;
			evtType = parser.next();

			while (evtType != XmlPullParser.END_DOCUMENT) {
				if (evtType == XmlPullParser.START_TAG) {
					String name = parser.getName();
					if (bean != null) {
						ClassInfo rootCI = new ClassInfo(bean, name);
						stack.push(rootCI);
						rv = bean.newInstance();
						deserialize(rv, parser, stack);
						break;
					}
				}
				evtType = parser.next();
			}
		} catch (Throwable t) {
			t.printStackTrace();
		}
		return rv;
	}

	/**
	 * Set the value to a specified non-collection field
	 * 
	 * @param obj
	 *            Object whose field is to be deserialized
	 * @param info
	 *            The field details
	 * @param value
	 *            Value to add to collection
	 * @throws IllegalArgumentException
	 *             {@see Method#invoke(Object, Object...)}
	 * @throws IllegalAccessException
	 *             {@see Method#invoke(Object, Object...)}
	 * @throws InvocationTargetException
	 *             {@see Method#invoke(Object, Object...)}
	 */
	private void setFieldValue(Object obj, FieldInfo info, Object value) throws IllegalArgumentException,
			IllegalAccessException, InvocationTargetException {
		if (value != null) {
			Method setMethod = info.getSetMethod();
			setMethod.invoke(obj, value);
		}
	}

	/**
	 * Skips the current element.
	 * 
	 * @param parser
	 *            Parser for reading data
	 * @param elementName
	 *            Element to be skipped
	 * @throws XmlPullParserException
	 *             If an exception occurs during parsing
	 * @throws IOException
	 *             If an exception occurs while reading
	 */
	private void skipElement(XmlPullParser parser, String elementName) throws XmlPullParserException, IOException {
		int indent = 0;
		int evtType = parser.next();

		boolean finished = (indent == 0) && (evtType == XmlPullParser.END_TAG) && parser.getName().equals(elementName);

		while (!finished) {
			evtType = parser.next();
			if ((evtType == XmlPullParser.START_TAG) && parser.getName().equals(elementName)) {
				indent++;
			} else if ((evtType == XmlPullParser.START_TAG) && parser.getName().equals(elementName)) {
				indent--;
			}
			finished = (indent == 0) && (evtType == XmlPullParser.END_TAG) && parser.getName().equals(elementName);
		}
	}
}
