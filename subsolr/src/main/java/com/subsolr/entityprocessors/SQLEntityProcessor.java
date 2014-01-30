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
import com.subsolr.contextprocessor.model.FieldSetDefinition;
import com.subsolr.entityprocessors.datasources.SQLDataSource;
import com.subsolr.entityprocessors.model.Record;

/**
 * Processor  for sql queries and mapping to fieldset def
 * @author vamsiy-mac aditya
 */

public class SQLEntityProcessor implements EntityProcessor {

	public static final Logger logger = LoggerFactory.getLogger(SQLEntityProcessor.class);

	public List<Record> getRecords(FieldSetDefinition fieldSetDefinition) {
		SQLDataSource sqlDataSource = (SQLDataSource) fieldSetDefinition.getDataSource();
		final List<Record> records = Lists.newArrayList();
		final Map<String, String> fieldNameToEntityNameMap = fieldSetDefinition.getFieldNameToEntityNameMap();
		JdbcTemplate jdbcTemplate = getJdbcTempate(sqlDataSource);
		logger.info("processing data in FieldSetDef :"+fieldSetDefinition.getName());
		jdbcTemplate.query(fieldSetDefinition.getPropertiesForEntityProcessor().get("SQLQuery"), new RowCallbackHandler() {
			public void processRow(ResultSet rs) throws SQLException {
				Map<String, String> valueByIndexName = Maps.newHashMap();
				for (String fieldName : fieldNameToEntityNameMap.keySet()) {
					String fieldValue = rs.getString(fieldNameToEntityNameMap.get(fieldName));
					if(fieldValue == null)
						fieldValue = " ";
					valueByIndexName.put(fieldName, fieldValue);
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
		        throw new RuntimeException(e.getMessage(), e.getCause());
		}
		return jdbcTemplate;
	}


	
	

}
