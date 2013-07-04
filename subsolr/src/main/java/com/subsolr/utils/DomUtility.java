package com.subsolr.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class DomUtility {
	public static Map<String, NodeList> getNodeMap(String xmlFilePath,
			Set<String> nodeNames) throws ParserConfigurationException,
			SAXException, IOException {
		Map<String, NodeList> nodeMap = new HashMap<String, NodeList>();
		File fXmlFile = new File(xmlFilePath);
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(fXmlFile);
		doc.getDocumentElement().normalize();

		for (String nodeName : nodeNames) {
			nodeMap.put(nodeName, doc.getElementsByTagName(nodeName));
		}

		return nodeMap;
	}

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
}
