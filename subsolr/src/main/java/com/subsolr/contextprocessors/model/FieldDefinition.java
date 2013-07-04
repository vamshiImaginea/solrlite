package com.subsolr.contextprocessors.model;

import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.FieldInfo;

public class FieldDefinition {
	private String fieldName;

	private FieldInfo fieldInfo;
	
	private List<Analyzer> analyzers;

	public FieldInfo getFieldInfo() {
		return fieldInfo;
	}

	public void setFieldInfo(FieldInfo fieldInfo) {
		this.fieldInfo = fieldInfo;
	}

	public List<Analyzer> getAnalyzers() {
		return analyzers;
	}

	public void setAnalyzers(List<Analyzer> analyzers) {
		this.analyzers = analyzers;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
}
