package com.subsolr.entityprocessors.model;

import java.util.Map;

import com.subsolr.contextprocessor.model.FieldDefinition;

public class Record {
	
	private Map<String,String> valueByIndexName;

	public Record(Map<String, String> valueByIndexName) {
		this.valueByIndexName = valueByIndexName;
	}

	public Map<String, String> getValueByIndexName() {
		return valueByIndexName;
	}

	public void setValueByIndexName(Map<String,String> valueByIndexName) {
		this.valueByIndexName = valueByIndexName;
	}

}
