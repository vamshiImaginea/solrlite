package com.subsolr.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.subsolr.contextprocessors.DomUtilityTest;

public class DomUtility {
	public static Map<String, NodeList> getNodeMap(String xmlFilePath,
			Set<String> nodeNames) throws ParserConfigurationException,
			SAXException, IOException {
		InputStream fieldContext = DomUtility.class
				.getResourceAsStream("FieldContext.xml");

		return getNodeMap(fieldContext, nodeNames);
	}

	public static Map<String, NodeList> getNodeMap(String xmlFilePath,
			String nodeName) throws ParserConfigurationException, SAXException,
			IOException {
		InputStream fieldContext = DomUtility.class
				.getResourceAsStream(xmlFilePath);

		return getNodeMap(fieldContext,
				new HashSet<String>(Arrays.asList(nodeName)));
	}

	// useful when we want to retrive a number of items at one shot
	public static Map<String, NodeList> getNodeMap(InputStream is,
			Set<String> nodeNames) throws ParserConfigurationException,
			SAXException, IOException {
		Map<String, NodeList> nodeMap = new HashMap<String, NodeList>();

		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(is);
		doc.getDocumentElement().normalize();

		for (String nodeName : nodeNames) {
			nodeMap.put(nodeName, doc.getElementsByTagName(nodeName));
		}

		return nodeMap;
	}

	// useful when we want to retrive a nodelist of a node name
	public static NodeList getNodeList(InputStream is, String nodeName)
			throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(is);
		doc.getDocumentElement().normalize();
		return doc.getElementsByTagName(nodeName);
	}

	public static NodeList getNodeList(String xmlFilePath, String nodeName)
			throws ParserConfigurationException, SAXException, IOException {
		InputStream fieldContext = DomUtilityTest.class
				.getResourceAsStream(xmlFilePath);

		return getNodeList(fieldContext, nodeName);
	}
}
