package com.subsolr.contextprocessor.model;

import java.util.List;
import java.util.Map;

import com.subsolr.entityprocessors.model.Record;

/**
 * Contains the possible lucene document definitions using field name -- source mapping
 * 
 * @author aditya
 * 
 */
public class DocumentDefinition {
	private String documentName;
	private Map<String,FieldSetDefinition> fieldSets;
	//TODO should have logic to combine all fieldsets to generate a single collection of record

	public String getDocumentName() {
		return documentName;
	}

	public void setDocumentName(String documentName) {
		this.documentName = documentName;
	}

	public Map<String,FieldSetDefinition> getFieldSets() {
		return fieldSets;
	}

	public void setFieldSets(Map<String, FieldSetDefinition> map) {
		this.fieldSets = map;
	}
	
	//TODO merge field sets
	public List<Record> getRecordsToBeIndexedd(){
		return fieldSets.get(0).getEntityProcessor().getRecords(fieldSets.get(0));
	}
	
	
	

}
