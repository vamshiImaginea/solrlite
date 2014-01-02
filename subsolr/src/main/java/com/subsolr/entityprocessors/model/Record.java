package com.subsolr.entityprocessors.model;

import java.util.Map;

import com.subsolr.contextprocessor.model.FieldDefinition;

public class Record {
	
	private Map<FieldDefinition,String> valueByIndexName;

	public Record(Map<FieldDefinition, String> valueByIndexName) {
		this.valueByIndexName = valueByIndexName;
	}

	public Map<FieldDefinition, String> getValueByIndexName() {
		return valueByIndexName;
	}

	public void setValueByIndexName(Map<FieldDefinition,String> valueByIndexName) {
		this.valueByIndexName = valueByIndexName;
	}

}
