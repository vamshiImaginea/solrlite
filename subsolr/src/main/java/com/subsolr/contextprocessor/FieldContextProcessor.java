package com.subsolr.contextprocessor;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import org.apache.lucene.analysis.util.CharFilterFactory;
import org.apache.lucene.analysis.util.TokenFilterFactory;
import org.apache.lucene.analysis.util.TokenizerFactory;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.solr.analysis.SolrAnalyzer;
import org.apache.solr.analysis.TokenizerChain;
import org.apache.solr.schema.FieldType;
import org.springframework.beans.factory.InitializingBean;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.google.common.collect.Maps;
import com.subsolr.contextprocessor.model.FieldDefinition;
import com.subsolr.contextprocessor.model.FieldTypeDefinition;

/**
 * Reads the FieldContext and generates the FieldDefinition list and the Domain
 * Fields list
 * 
 * @author aditya
 * 
 */
public class FieldContextProcessor implements InitializingBean {
	private String fieldConfigFileName;
	private DocumentBuilder documentBuilder;
	private XPath xPath;

	public FieldContextProcessor(String fieldConfigFileName, DocumentBuilder documentBuilder, XPath xPath) {
		this.fieldConfigFileName = fieldConfigFileName;
		this.documentBuilder = documentBuilder;
		this.xPath = xPath;
	}

	public Map<String, FieldTypeDefinition> getFieldTypeDefinitionsByName() {
		return fieldTypeDefinitionsByName;
	}

	public void setFieldTypeDefinitionsByName(
			Map<String, FieldTypeDefinition> fieldTypeDefinitionsByName) {
		this.fieldTypeDefinitionsByName = fieldTypeDefinitionsByName;
	}

	public FieldDefinition getFieldDefinitionsByName(String name) {
		return fieldDefinitionsByName.get(name);
	}

	public void setFieldDefinitionsByName(
			Map<String, FieldDefinition> fieldDefinitionsByName) {
		this.fieldDefinitionsByName = fieldDefinitionsByName;
	}

	private Map<String, FieldTypeDefinition> fieldTypeDefinitionsByName;
	private Map<String, FieldDefinition> fieldDefinitionsByName;

	private void setFieldDefinitions(NodeList fieldTypeDefinitionNodes) throws XPathExpressionException, InstantiationException, IllegalAccessException, ClassNotFoundException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		fieldTypeDefinitionsByName = Maps.<String, FieldTypeDefinition> newHashMap();
		int noOfFieldTypeDefinitons = fieldTypeDefinitionNodes.getLength();
		for (int i = 0; i < noOfFieldTypeDefinitons; i++) {
			Node fieldTypeDefinitionNode = fieldTypeDefinitionNodes.item(i);
			Node similarityNode = (Node) xPath.evaluate("./similarity", fieldTypeDefinitionNode, XPathConstants.NODE);
			Node analyzerNode = (Node) xPath.evaluate("./analyzer", fieldTypeDefinitionNode, XPathConstants.NODE);

			String fieldTypeName = getAttributeValueInNode(fieldTypeDefinitionNode, "name");
			FieldTypeDefinition fieldTypeDefinition = new FieldTypeDefinition(fieldTypeName, FieldType.class.cast(Class.forName(getAttributeValueInNode(fieldTypeDefinitionNode, "class")).newInstance()), Integer.valueOf(getAttributeValueInNode(fieldTypeDefinitionNode, "positionIncrementGap")), Similarity.class.cast(Class.forName(getAttributeValueInNode(similarityNode, "class")).newInstance()), getAnalyzer(analyzerNode));
			fieldTypeDefinitionsByName.put(fieldTypeName, fieldTypeDefinition);

		}

	}

	private SolrAnalyzer getAnalyzer(Node analyzerNode) throws XPathExpressionException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, ClassNotFoundException {
		Node tokenizerNode = (Node) xPath.evaluate("./tokenizer", analyzerNode, XPathConstants.NODE);
		NodeList filterNodes = (NodeList) xPath.evaluate("./filter", analyzerNode, XPathConstants.NODESET);

		int totalFilters = filterNodes.getLength();
		NodeList charFilterNodes = (NodeList) xPath.evaluate("./charFilter", analyzerNode, XPathConstants.NODESET);
		TokenizerFactory tokenizer = (TokenizerFactory) Class.forName(getAttributeValueInNode(tokenizerNode, "class")).getConstructor(Map.class).newInstance(toMap(tokenizerNode.getAttributes()));
		TokenFilterFactory[] tokenFilters = new TokenFilterFactory[totalFilters];
		for (int i = 0; i < totalFilters; i++) {
			Node filterNode = filterNodes.item(i);
			tokenFilters[i] = TokenFilterFactory.forName(getAttributeValueInNode(tokenizerNode, "class"), toMap(filterNode.getAttributes()));
		}
		totalFilters = charFilterNodes.getLength();
		CharFilterFactory[] charFilters = new CharFilterFactory[totalFilters];

		for (int i = 0; i < totalFilters; i++) {
			Node filterNode = charFilterNodes.item(i);
			charFilters[i] = CharFilterFactory.forName(getAttributeValueInNode(tokenizerNode, "class"), toMap(filterNode.getAttributes()));
		}

		return new TokenizerChain(charFilters, tokenizer, tokenFilters);

	}

	private Map<String, String> toMap(NamedNodeMap attributes) {
		int noOfAttributes = attributes.getLength();
		Map<String, String> attributesMap = Maps.<String, String> newHashMap();
		for (int i = 0; i < noOfAttributes; i++) {
			attributesMap.put(attributes.item(i).getNodeName(), attributes.item(i).getNodeValue());
		}
		return attributesMap;
	}

	private void setFieldTypeDefinitions(NodeList fieldDefinitionNodes) {
		fieldDefinitionsByName = Maps.<String, FieldDefinition> newHashMap();
		int noOfFieldDefinitons = fieldDefinitionNodes.getLength();
		for (int i = 0; i < noOfFieldDefinitons; i++) {
			Node fieldDefinitionNode = fieldDefinitionNodes.item(i);
			String fieldName = getAttributeValueInNode(fieldDefinitionNode, "name");
			FieldDefinition fieldDefinition = new FieldDefinition.FieldDefinitionBuilder().fieldName(fieldName).fieldTypeDefinition(fieldTypeDefinitionsByName.get(getAttributeValueInNode(fieldDefinitionNode, "type"))).analyzed(Boolean.valueOf(getAttributeValueInNode(fieldDefinitionNode, "analyzed"))).stored(Boolean.valueOf(getAttributeValueInNode(fieldDefinitionNode, "stored"))).indexed(Boolean.valueOf(getAttributeValueInNode(fieldDefinitionNode, "indexed"))).mandatory(Boolean.valueOf(getAttributeValueInNode(fieldDefinitionNode, "required"))).build();
			fieldDefinitionsByName.put(fieldName, fieldDefinition);
		}

	}

	private String getAttributeValueInNode(Node fieldDefinitionNode, String attributeName) {
		return fieldDefinitionNode.getAttributes().getNamedItem(attributeName).getNodeValue();
	}

	public void afterPropertiesSet() throws Exception {
		Document fieldConfigDocument = documentBuilder.parse(new File(fieldConfigFileName));
		setFieldTypeDefinitions(fieldConfigDocument.getElementsByTagName("field_type"));
		setFieldDefinitions(fieldConfigDocument.getElementsByTagName("field"));
	}

}
