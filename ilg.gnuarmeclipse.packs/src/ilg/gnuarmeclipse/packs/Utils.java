/*******************************************************************************
 * Copyright (c) 2014 Liviu Ionescu.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Liviu Ionescu - initial implementation.
 *******************************************************************************/

package ilg.gnuarmeclipse.packs;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class Utils {

	public static String xmlEscape(String value) {
		value = value.replaceAll("\\&", "&amp;"); //$NON-NLS-1$ //$NON-NLS-2$ 
		value = value.replaceAll("\\\"", "&quot;"); //$NON-NLS-1$ //$NON-NLS-2$ 
		value = value.replaceAll("\\\'", "&apos;"); //$NON-NLS-1$ //$NON-NLS-2$ 
		value = value.replaceAll("\\<", "&lt;"); //$NON-NLS-1$ //$NON-NLS-2$ 
		value = value.replaceAll("\\>", "&gt;"); //$NON-NLS-1$ //$NON-NLS-2$ 
		return value;
	}

	public static Element getChildElement(Element el, String name) {

		NodeList nodeList = el.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); ++i) {
			Node node = nodeList.item(i);

			if (node.getNodeType() == Node.ELEMENT_NODE) {
				if (node.getNodeName().equals(name)) {

					// Return the first element with the given name
					return (Element) node;
				}
			}
		}
		return null;
	}

	public static List<Element> getChildElementsList(Element el) {

		return getChildElementsList(el, null);
	}

	public static List<Element> getChildElementsList(Element el, String name) {

		NodeList nodeList = el.getChildNodes();

		// Allocate exactly the number of children
		List<Element> list = new ArrayList<Element>(nodeList.getLength());

		for (int i = 0; i < nodeList.getLength(); ++i) {
			Node node = nodeList.item(i);

			if (node.getNodeType() == Node.ELEMENT_NODE) {
				if ((name == null) || node.getNodeName().equals(name)) {
					list.add((Element) node);
				}
			}
		}
		return list;
	}

	public static String getElementContent(Element el) {

		String content = "";

		if (el != null && el.getNodeType() == Node.ELEMENT_NODE) {
			content = el.getTextContent();
			if (content != null) {
				content = content.trim();
			}
		}
		return content;
	}

	public static String getElementMultiLineContent(Element el) {

		String s = getElementContent(el);

		String sa[] = s.split("\\r?\\n");
		if (sa.length > 1) {
			s = "";
			for (int i = 0; i < sa.length; ++i) {
				if (i > 0) {
					s += '\n';
				}
				s += sa[i].trim();
			}
		}

		return s;
	}

	public static Element getParentElement(Element el) {

		do {
			el = (Element) el.getParentNode();
		} while (el != null && el.getNodeType() != Node.ELEMENT_NODE);

		return el;
	}

	public static List<String> getAttributesNames(Element el) {

		List<String> list = new LinkedList<String>();

		NamedNodeMap attribs = el.getAttributes();
		for (int i = 0; i < attribs.getLength(); ++i) {
			String name = attribs.item(i).getNodeName();
			list.add(name);
		}

		return list;
	}

	public static List<String> getAttributesNames(Element el, String sa[]) {

		List<String> list = new LinkedList<String>();

		NamedNodeMap attribs = el.getAttributes();
		for (int i = 0; i < attribs.getLength(); ++i) {
			String name = attribs.item(i).getNodeName();
			list.add(name);
		}

		List<String> listOut = new LinkedList<String>();
		for (String s : sa) {
			if (list.contains(s)) {
				list.remove(s);
				listOut.add(s);
			}
		}
		listOut.addAll(list);
		return listOut;
	}

	public static int convertHexInt(String hex) {

		boolean isNegative = false;
		if (hex.startsWith("+")) {
			hex = hex.substring(1);
		} else if (hex.startsWith("-")) {
			hex = hex.substring(1);
			isNegative = true;
		}

		if (hex.startsWith("0x") || hex.startsWith("0X")) {
			hex = hex.substring(2);
		}

		int value = Integer.valueOf(hex, 16);
		if (isNegative)
			value = -value;

		return value;
	}

	public static String cosmetiseUrl(String url) {
		if (url.endsWith("/")) {
			return url;
		} else {
			return url + "/";
		}
	}

	public static int getRemoteFileSize(URL url) throws IOException {

		URLConnection connection = url.openConnection();
		connection.getInputStream();

		return connection.getContentLength();
	}

	public static String convertSizeToString(int size) {

		String sizeString;
		if (size < 1024) {
			sizeString = String.valueOf(size) + "B";
		} else if (size < 1024 * 1024) {
			sizeString = String.valueOf((size + (1024 / 2)) / 1024) + "kB";
		} else {
			sizeString = String.valueOf((size + ((1024 * 1024) / 2))
					/ (1024 * 1024))
					+ "MB";
		}
		return sizeString;
	}

	public static String capitalize(String s) {
		if (s.length() == 0)
			return s;
		return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
	}

	public static Document parseXml(File file)
			throws ParserConfigurationException, SAXException, IOException {

		InputSource inputSource = new InputSource(new FileInputStream(file));

		DocumentBuilder xml = DocumentBuilderFactory.newInstance()
				.newDocumentBuilder();
		return xml.parse(inputSource);
	}

	public static String getCurrentDateTime() {

		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar cal = Calendar.getInstance();
		return dateFormat.format(cal.getTime());
	}

	public static InputStream checkForUtf8BOM(InputStream inputStream)
			throws IOException {

		PushbackInputStream pushbackInputStream = new PushbackInputStream(
				new BufferedInputStream(inputStream), 3);
		byte[] bom = new byte[3];
		if (pushbackInputStream.read(bom) != -1) {
			if (!(bom[0] == (byte) 0xEF && bom[1] == (byte) 0xBB && bom[2] == (byte) 0xBF)) {
				pushbackInputStream.unread(bom);
			}
		}
		return pushbackInputStream;
	}
}
