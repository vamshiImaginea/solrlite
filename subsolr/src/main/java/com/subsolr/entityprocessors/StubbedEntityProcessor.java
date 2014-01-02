package com.subsolr.entityprocessors;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.subsolr.contextprocessor.FieldContextProcessor;
import com.subsolr.contextprocessor.model.FieldDefinition;
import com.subsolr.entityprocessors.model.Record;

public class StubbedEntityProcessor implements EntityProcessor {
	
	private FieldContextProcessor fieldContextProcessor;

	
	public StubbedEntityProcessor(FieldContextProcessor fieldContextProcessor) {
		this.fieldContextProcessor = fieldContextProcessor;
	}
	
	
	public List<Record> getRecords() {
		List<Record> stubbedRecords = Lists.newArrayList();
		Map<FieldDefinition, String> valueByIndexName = Maps.newHashMap();
		valueByIndexName.put(fieldContextProcessor.getFieldDefinitionsByName("test"), "value");		
		Record record1 = new Record(valueByIndexName);
		stubbedRecords.add(record1);
		return stubbedRecords;
	}


}
