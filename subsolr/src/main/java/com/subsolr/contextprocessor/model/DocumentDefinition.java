package com.subsolr.contextprocessor.model;

import java.util.List;

/**
 * Contains the possible lucene document definitions using field name -- source mapping
 * 
 * @author aditya
 * 
 */
public class DocumentDefinition {
	private String documentName;
	private List<FieldSetDefinition> fieldSets;

	public String getDocumentName() {
		return documentName;
	}

	public void setDocumentName(String documentName) {
		this.documentName = documentName;
	}

	public List<FieldSetDefinition> getFieldSets() {
		return fieldSets;
	}

	public void setFieldSets(List<FieldSetDefinition> fieldSets) {
		this.fieldSets = fieldSets;
	}

}
