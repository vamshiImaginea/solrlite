package com.subsolr.contextprocessors.model;

import java.util.List;

import org.apache.lucene.document.Field;

import com.subsolr.entityprocessors.EntityProcessor;
import com.subsolr.entityprocessors.datasources.DataSource;

public class DocumentDefinition {
	private List<Field> Fields;
	private DataSource dataSource;
	private EntityProcessor entityProcessor;

	public List<Field> getFields() {
		return Fields;
	}

	public void setFields(List<Field> fields) {
		Fields = fields;
	}

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

}
