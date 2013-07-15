package com.subsolr.contextprocessors.model;

import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.document.FieldType;

public class FieldTypeDefinition {
	
	private String fieldTypeName;

	private FieldType fieldType;
	
	private List<Analyzer> analyzers;

	private List<TokenFilter> tokenFilters;
	
	private boolean isMandatory; // can skip indexing record if field is missing


	public List<Analyzer> getAnalyzers() {
		return analyzers;
	}
	
	public String getFieldTypeName() {
		return fieldTypeName;
	}
	

	public List<TokenFilter> getTokenFilters() {
		return tokenFilters;
	}

	public void setAnalyzers(List<Analyzer> analyzers) {
		this.analyzers = analyzers;
	}

	public void setFieldTypeName(String fieldTypeName) {
		this.fieldTypeName = fieldTypeName;
	}

	public void setTokenFilters(List<TokenFilter> tokenFilters) {
		this.tokenFilters = tokenFilters;
	}

	public FieldType getFieldType() {
		return fieldType;
	}

	public void setFieldType(FieldType fieldType) {
		this.fieldType = fieldType;
	}

	public boolean isMandatory() {
		return isMandatory;
	}

	public void setMandatory(boolean isMandatory) {
		this.isMandatory = isMandatory;
	}
}
