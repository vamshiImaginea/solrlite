package com.subsolr.contextprocessor.model;

import java.util.Map;

import com.subsolr.entityprocessors.EntityProcessor;
import com.subsolr.entityprocessors.datasources.DataSource;

/**
 * Contains the possible lucene document definitions using field name -- source
 * mapping
 * 
 * @author aditya
 * 
 */
public class DocumentDefinition {
	private String documentName;
	private Map<String, String> fieldName2DataSourceMap;
	private DataSource dataSource;
	private EntityProcessor entityProcessor;

	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public EntityProcessor getEntityProcessor() {
		return entityProcessor;
	}

	public void setEntityProcessor(EntityProcessor entityProcessor) {
		this.entityProcessor = entityProcessor;
	}

	public Map<String, String> getFieldName2DataSourceMap() {
		return fieldName2DataSourceMap;
	}

	public void setFieldName2DataSourceMap(
			Map<String, String> fieldName2DataSourceMap) {
		this.fieldName2DataSourceMap = fieldName2DataSourceMap;
	}

	public String getDocumentName() {
		return documentName;
	}

	public void setDocumentName(String documentName) {
		this.documentName = documentName;
	}

}