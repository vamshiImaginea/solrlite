package com.subsolr.contextprocessors.model;

import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.FieldInfo;

public class FieldTypeDefinition {
	private String fieldTypeName;

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

	public String getFieldTypeName() {
		return fieldTypeName;
	}

	public void setFieldTypeName(String fieldTypeName) {
		this.fieldTypeName = fieldTypeName;
	}
}
