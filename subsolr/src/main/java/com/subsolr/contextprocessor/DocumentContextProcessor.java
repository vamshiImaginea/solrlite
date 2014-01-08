package com.subsolr.contextprocessor;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.google.common.collect.Maps;
import com.subsolr.contextprocessor.model.DocumentDefinition;
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
	private final Resource resource;
	private Map<String, DocumentDefinition> documentDefinitionsByName;
	Map<String, SQLDataSource> sqlDataSourceByName;

	public DocumentContextProcessor(Resource resource, FieldContextProcessor fieldContextProcessor, XPath xPath, DocumentBuilder documentBuilder) {
		this.fieldContextProcessor = fieldContextProcessor;
		this.xPath = xPath;
		this.documentBuilder = documentBuilder;
		this.resource = resource;
	}

	public Map<String, DocumentDefinition> getDocumentDefinitions() {
		return documentDefinitionsByName;
	}

	DocumentDefinition getDocumentDefinitionByName(String documentName) {
		return documentDefinitionsByName.get(documentName);
	}

	public void afterPropertiesSet() throws Exception {
		Document documentTypeConfigDocument = documentBuilder.parse(resource.getFile());
		setDataBaseSources(documentTypeConfigDocument.getElementsByTagName("SQLdatasource"));
		//setFileDataSources(DocumentTypeConfigDocument.getElementsByTagName("Filedatasource"));// TODO
		setDocumentDefinitions(documentTypeConfigDocument.getElementsByTagName("document"));
	}

	private String getAttributeValueInNode(Node fieldDefinitionNode, String attributeName) {
		return fieldDefinitionNode.getAttributes().getNamedItem(attributeName).getNodeValue();
	}

	private void setDataBaseSources(NodeList sqlDataSources) throws XPathExpressionException {
		int noOfSQLDataSources = sqlDataSources.getLength();
		sqlDataSourceByName = Maps.newHashMap();
		for (int i = 0; i < noOfSQLDataSources; i++) {
			Node sqlDataSourceNode = sqlDataSources.item(i);
			String dataSouceName = getAttributeValueInNode(sqlDataSourceNode, "id");
			Node hostNode = (Node) xPath.evaluate("./host", sqlDataSourceNode, XPathConstants.NODE);
			Node userNameNode = (Node) xPath.evaluate("./userid", sqlDataSourceNode, XPathConstants.NODE);
			Node passwordNode = (Node) xPath.evaluate("./password", sqlDataSourceNode, XPathConstants.NODE);
			Node driverNode = (Node) xPath.evaluate("./driver", sqlDataSourceNode, XPathConstants.NODE);
			SQLDataSource sqlDataSource = new SQLDataSource(driverNode.getTextContent(), hostNode.getTextContent(), userNameNode.getTextContent(), passwordNode.getTextContent());
			sqlDataSourceByName.put(dataSouceName, sqlDataSource);
		}

	}

	private void setFileDataSources(NodeList elementsByTagName) {
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
			NodeList fieldsetDefinitions = (NodeList) xPath.evaluate("./fieldset", documentDefinitionNode, XPathConstants.NODESET);
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
			fieldSetDefinition.setEntityProcessor(entityProcessor.newInstance());
			Node queryNode = (Node) xPath.evaluate("./query/statement", fieldSetNode, XPathConstants.NODE);
			fieldSetDefinition.setQuery(queryNode.getTextContent());
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
