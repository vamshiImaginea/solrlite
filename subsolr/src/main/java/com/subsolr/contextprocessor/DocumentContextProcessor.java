package com.subsolr.contextprocessor;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import org.apache.lucene.util.Version;
import org.springframework.beans.factory.InitializingBean;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.subsolr.contextprocessor.model.DocumentDefinition;
import com.subsolr.contextprocessor.model.FieldDefinition;
import com.subsolr.contextprocessor.model.FieldSetDefinition;
import com.subsolr.entityprocessors.EntityProcessor;
import com.subsolr.entityprocessors.datasources.SQLDataSource;

/**
 * Reads the Document context and creates the document defnition list.
 * 
 * @author aditya
 * 
 */
public class DocumentContextProcessor implements InitializingBean {

	private final FieldContextProcessor fieldContextProcessor;
	private final XPath xPath;
	private final DocumentBuilder documentBuilder;
	private final String documentConfigFiles;
	private Map<String, DocumentDefinition> documentDefinitionsByName;
	Map<String, SQLDataSource> sqlDataSourceByName;

	public DocumentContextProcessor(String documentConfigFiles, FieldContextProcessor fieldContextProcessor, XPath xPath, DocumentBuilder documentBuilder) {
		this.fieldContextProcessor = fieldContextProcessor;
		this.xPath = xPath;
		this.documentBuilder = documentBuilder;
		this.documentConfigFiles = documentConfigFiles;
	}

	public Map<String, DocumentDefinition> getDocumentDefinitions() {
		return documentDefinitionsByName;
	}

	DocumentDefinition getDocumentDefinitionByName(String documentName) {
		return documentDefinitionsByName.get(documentName);
	}

	public void afterPropertiesSet() throws Exception {
		Document DocumentTypeConfigDocument = documentBuilder.parse(new File(documentConfigFiles));
		setDataBaseSources(DocumentTypeConfigDocument.getElementsByTagName("SQLdatasource"));
		setFileDataSources(DocumentTypeConfigDocument.getElementsByTagName("Filedatasource"));// TODO
		setDocumentDefinitions(DocumentTypeConfigDocument.getElementsByTagName("document"));
	}

	private String getAttributeValueInNode(Node fieldDefinitionNode, String attributeName) {
		return fieldDefinitionNode.getAttributes().getNamedItem(attributeName).getNodeValue();
	}

	private void setFileDataSources(NodeList sqlDataSources) throws XPathExpressionException {
		int noOfSQLDataSources = sqlDataSources.getLength();
		sqlDataSourceByName = Maps.newHashMap();
		for (int i = 0; i < noOfSQLDataSources; i++) {
			Node sqlDataSourceNode = sqlDataSources.item(i);
			String dataSouceName = getAttributeValueInNode(sqlDataSourceNode, "id");
			Node hostNode = (Node) xPath.evaluate("./host", sqlDataSourceNode, XPathConstants.NODE);
			Node userNameNode = (Node) xPath.evaluate("./userid", sqlDataSourceNode, XPathConstants.NODE);
			Node passwordNode = (Node) xPath.evaluate("./password", sqlDataSourceNode, XPathConstants.NODE);
			Node driverNode = (Node) xPath.evaluate("./driver", sqlDataSourceNode, XPathConstants.NODE);
			SQLDataSource sqlDataSource = new SQLDataSource(driverNode.getNodeValue(), hostNode.getNodeValue(), userNameNode.getNodeValue(), passwordNode.getNodeValue());
			sqlDataSourceByName.put(dataSouceName, sqlDataSource);
		}

	}

	private void setDataBaseSources(NodeList elementsByTagName) {
		// TODO Auto-generated method stub

	}

	private void setDocumentDefinitions(NodeList documentDefinitionNodeList) throws XPathExpressionException, InstantiationException, IllegalAccessException, ClassNotFoundException,
			IllegalArgumentException, SecurityException, InvocationTargetException, NoSuchMethodException {
		int noOfDocumentDefinitions = documentDefinitionNodeList.getLength();
		documentDefinitionsByName = Maps.<String, DocumentDefinition> newHashMap();
		DocumentDefinition documentDefinition = null;
		for (int i = 0; i < noOfDocumentDefinitions; i++) {
			Node documentDefinitionNode = documentDefinitionNodeList.item(i);
			String documentName = getAttributeValueInNode(documentDefinitionNode, "name");
			NodeList fieldsetDefinitions = (NodeList) xPath.evaluate("./feildset", documentDefinitionNode, XPathConstants.NODESET);
			extractFieldSetDefintions(fieldsetDefinitions);
			documentDefinition = new DocumentDefinition();
			documentDefinition.setDocumentName(documentName);
			documentDefinition.setFieldSets(extractFieldSetDefintions(fieldsetDefinitions));
			documentDefinitionsByName.put(documentName, documentDefinition);
		}

	}

	private Map<String, FieldSetDefinition> extractFieldSetDefintions(NodeList fieldsetDefinitionNodeList) throws XPathExpressionException, InstantiationException, IllegalAccessException,
			ClassNotFoundException, IllegalArgumentException, SecurityException, InvocationTargetException, NoSuchMethodException {
		int noOfFieldSetsInDoc = fieldsetDefinitionNodeList.getLength();
		Map<String,FieldSetDefinition> fieldSetsByName = Maps.newHashMap();
		for (int i = 0; i < noOfFieldSetsInDoc; i++) {
			FieldSetDefinition fieldSetDefinition = new FieldSetDefinition();
			Node fieldSetNode = fieldsetDefinitionNodeList.item(i);
			String fieldSetName = getAttributeValueInNode(fieldSetNode, "name");
			String sourceId = getAttributeValueInNode(fieldSetNode, "sourceId");
			String entityProcessorClass = getAttributeValueInNode(fieldSetNode, "EntityProcessor");
			fieldSetDefinition.setDataSource(sqlDataSourceByName.get(sourceId));
			Class<? extends EntityProcessor> entityProcessor = (Class<? extends EntityProcessor>) Class.forName(entityProcessorClass);
			fieldSetDefinition.setEntityProcessor(entityProcessor.getConstructor(FieldContextProcessor.class).newInstance(fieldContextProcessor));
			Node queryNode = (Node) xPath.evaluate("./query/statement", fieldSetNode, XPathConstants.NODE);
			fieldSetDefinition.setQuery(queryNode.getNodeValue());
			NodeList fieldMappingNodes = (NodeList) xPath.evaluate("./field", fieldSetNode, XPathConstants.NODESET);
			Map<String, String> fieldToColumnMapping = extractFieldMappings(fieldMappingNodes);
		    fieldSetDefinition.setFieldNameToEntityNameMap(fieldToColumnMapping);
		    fieldSetsByName.put(fieldSetName, fieldSetDefinition);
		}

		return fieldSetsByName;

	}

	private Map<String, String> extractFieldMappings(NodeList fieldMappings) {
		int noOfFields = fieldMappings.getLength();
		Map<String, String> fieldToColumnMapping = Maps.newHashMap();
		for (int i = 0; i < noOfFields; i++) {
			Node fieldNode = fieldMappings.item(i);
			String colName = getAttributeValueInNode(fieldNode, "column_name");
			String fieldName = getAttributeValueInNode(fieldNode, "field_map_name");
			fieldToColumnMapping.put(fieldName, colName);

		}
		return fieldToColumnMapping;

	}
}
