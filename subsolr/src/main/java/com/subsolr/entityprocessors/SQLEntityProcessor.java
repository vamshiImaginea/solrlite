package com.subsolr.entityprocessors;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.subsolr.contextprocessor.FieldContextProcessor;
import com.subsolr.contextprocessor.model.FieldDefinition;
import com.subsolr.contextprocessor.model.FieldSetDefinition;
import com.subsolr.entityprocessors.datasources.SQLDataSource;
import com.subsolr.entityprocessors.model.Record;

public class SQLEntityProcessor implements EntityProcessor {

	private FieldContextProcessor fieldContextProcessor;

	public SQLEntityProcessor(FieldContextProcessor fieldContextProcessor) {
		this.fieldContextProcessor = fieldContextProcessor;
	}

	public static final Logger logger = LoggerFactory.getLogger(SQLEntityProcessor.class);

	public List<Record> getRecords(FieldSetDefinition fieldSetDefinition) {
		SQLDataSource sqlDataSource = (SQLDataSource) fieldSetDefinition.getDataSource();
		final List<Record> records = Lists.newArrayList();
		final Map<String, String> fieldName2DataSourceMap = fieldSetDefinition.getFieldNameToEntityNameMap();
		JdbcTemplate jdbcTemplate = getJdbcTempate(sqlDataSource);
		jdbcTemplate.query(fieldSetDefinition.getQuery(), new RowCallbackHandler() {
			public void processRow(ResultSet rs) throws SQLException {
				Map<FieldDefinition, String> valueByIndexName = Maps.newHashMap();
				for (String fieldName : fieldName2DataSourceMap.keySet()) {
					String fieldValue = rs.getString(fieldName2DataSourceMap.get(fieldName));
					valueByIndexName.put(fieldContextProcessor.getFieldDefinitionsByName(fieldName), fieldValue);
				}
				records.add(new Record(valueByIndexName));
			}
		});
		return records;
	}

	private JdbcTemplate getJdbcTempate(SQLDataSource sqlDataSource) {
		JdbcTemplate jdbcTemplate = null;
		try {
			Class.forName(sqlDataSource.getDriver());

			DriverManagerDataSource dataSource = new DriverManagerDataSource();
			dataSource.setDriverClassName(sqlDataSource.getDriver());
			dataSource.setUrl(sqlDataSource.getUrl());
			dataSource.setUsername(sqlDataSource.getUserId());
			dataSource.setPassword(sqlDataSource.getPassword());
			jdbcTemplate = new JdbcTemplate(dataSource);

		} catch (ClassNotFoundException e) {
			logger.error("Exception occurred while getting connection" + e);
		}
		return jdbcTemplate;
	}

	public FieldContextProcessor getFieldContextProcessor() {
		return fieldContextProcessor;
	}

	public void setFieldContextProcessor(FieldContextProcessor fieldContextProcessor) {
		this.fieldContextProcessor = fieldContextProcessor;
	}
	

}
