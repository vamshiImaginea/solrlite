package com.subsolr.contextprocessor.model;

import java.util.Map;

import com.subsolr.entityprocessors.EntityProcessor;
import com.subsolr.entityprocessors.datasources.DataSource;

public class FieldSetDefinition {

	private Map<String, String> fieldNameToEntityNameMap;
	private DataSource dataSource;
	private EntityProcessor entityProcessor;
	private String query; // TODO generalize for all field set definitions ...
							// properties ??

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

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public Map<String, String> getFieldNameToEntityNameMap() {
		return fieldNameToEntityNameMap;
	}

	public void setFieldNameToEntityNameMap(Map<String, String> fieldNameToEntityNameMap) {
		this.fieldNameToEntityNameMap = fieldNameToEntityNameMap;
	}

}
