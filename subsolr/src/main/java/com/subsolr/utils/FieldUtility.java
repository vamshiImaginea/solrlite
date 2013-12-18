package com.subsolr.utils;

import java.io.IOException;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.lucene.analysis.Analyzer;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.subsolr.contextprocessor.model.FieldTypeDefinition;

public class FieldUtility {
	private static final String fieldType = "fieldType";

	public static Map<String, FieldTypeDefinition> getFieldTypeDefinitions(
			String fileName) throws ParserConfigurationException, SAXException,
			IOException {
		return fieldTypeDomNode2FieldType(fileName);
	}

	private static NodeList getFieldTypeDomNodes(String fileName)
			throws ParserConfigurationException, SAXException, IOException {
		return DomUtility.getNodeList(fileName, fieldType);
	}

	private static Map<String, FieldTypeDefinition> fieldTypeDomNode2FieldType(
			String fileName) throws ParserConfigurationException, SAXException,
			IOException {
		NodeList fieldTypeNodes = getFieldTypeDomNodes(fileName);
		for (int i = 0; i < fieldTypeNodes.getLength(); i++) {
			// System.out.println(fieldTypeNodes.item(i).getNodeName());
			// to refactor this later currently handling only Analyzers
			NodeList childNodes = fieldTypeNodes.item(i).getChildNodes();
			for (int p = 0; p < childNodes.getLength(); p++) {
				if (childNodes.item(p).getNodeName() == "analyzer")
					getAnalyzer(childNodes.item(p));
			}
		}
		return null;
	}

	/**
	 * pass analyzer type node and build custom analyzer or use an existing one.
	 * 
	 * @param fieldType
	 * @return
	 */
	private static Analyzer getAnalyzer(Node analyzer) {
		System.out.println(analyzer.getNodeName());
		NamedNodeMap analyzerAttribs = analyzer.getAttributes();
		String analyzerClassName = analyzerAttribs.getNamedItem("class")
				.getNodeValue();
		if (analyzerClassName != null) {

		}
		return null;
	}
}
