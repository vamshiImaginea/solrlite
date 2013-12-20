package com.subsolr.contextprocessor;

import java.io.File;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.xpath.XPath;

import org.springframework.beans.factory.InitializingBean;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import com.google.common.collect.Maps;
import com.subsolr.contextprocessor.model.DocumentDefinition;

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
	

	
	public DocumentContextProcessor(String documentConfigFiles, FieldContextProcessor fieldContextProcessor, XPath xPath, DocumentBuilder documentBuilder) {
		this.fieldContextProcessor = fieldContextProcessor;
		this.xPath = xPath;
		this.documentBuilder = documentBuilder;
		this.documentConfigFiles = documentConfigFiles;
	}
	private Map<String, DocumentDefinition> documentDefinitionsByName;


	Map<String,DocumentDefinition> getDocumentDefinitions() {
		return documentDefinitionsByName;
	}

	DocumentDefinition getDocumentDefinitionByName(String documentName) {
		return documentDefinitionsByName.get(documentName);
	}

	public void afterPropertiesSet() throws Exception {
		Document DocumentTypeConfigDocument = documentBuilder.parse(new File(documentConfigFiles));
		setDocumentDefinitions(DocumentTypeConfigDocument.getElementsByTagName("document"));
	}

	private void setDocumentDefinitions(NodeList DocumentDefinitionNodeList) {
		documentDefinitionsByName = Maps.<String,DocumentDefinition>newHashMap();
	}
}
