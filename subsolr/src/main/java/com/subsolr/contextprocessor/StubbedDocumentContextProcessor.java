package com.subsolr.contextprocessor;

import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.xpath.XPath;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.subsolr.contextprocessor.model.DocumentDefinition;
import com.subsolr.contextprocessor.model.FieldSetDefinition;
import com.subsolr.entityprocessors.StubbedEntityProcessor;

public class StubbedDocumentContextProcessor extends DocumentContextProcessor {

	private FieldContextProcessor fieldContextProcessor;

	public StubbedDocumentContextProcessor(String documentConfigFiles, FieldContextProcessor fieldContextProcessor, XPath xPath, DocumentBuilder documentBuilder) {
		super(documentConfigFiles, fieldContextProcessor, xPath, documentBuilder);
		this.fieldContextProcessor = fieldContextProcessor;
	}

	Map<String, DocumentDefinition> getDocumentDefinitions() {
		Map<String, DocumentDefinition> mockedDocumentDefinitions = Maps.newHashMap();
		DocumentDefinition mockedDocumentDefintion = new DocumentDefinition();
		mockedDocumentDefintion.setDocumentName("TestDocumnet");

		FieldSetDefinition mockedFieldSetDef = new FieldSetDefinition();
		mockedFieldSetDef.setEntityProcessor(new StubbedEntityProcessor(fieldContextProcessor));
		List<FieldSetDefinition> fieldSets = Lists.newArrayList();
		fieldSets.add(mockedFieldSetDef);
		mockedDocumentDefintion.setFieldSets(fieldSets);
		//mockedDocumentDefintion.setEntityProcessor(new StubbedEntityProcessor(fieldContextProcessor));
		return mockedDocumentDefinitions;
	}

}
