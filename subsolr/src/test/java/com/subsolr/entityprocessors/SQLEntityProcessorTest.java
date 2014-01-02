package com.subsolr.entityprocessors;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;

import com.subsolr.contextprocessor.FieldContextProcessor;
import com.subsolr.contextprocessor.model.FieldSetDefinition;
import com.subsolr.entityprocessors.datasources.SQLDataSource;
import com.subsolr.entityprocessors.model.Record;

public class SQLEntityProcessorTest {

    private SQLEntityProcessor sqlEntityProcessor;

    private FieldSetDefinition fieldSetDefinition;
    
    @Before
    public void setup() {
        String sql = "SELECT emp_no as EMP_ID,  first_name as EMP_NAME, gender as EMP_GENDER, "
                + "hire_date as EMP_HIRE_DATE FROM  employees";
        sqlEntityProcessor = new SQLEntityProcessor();
        sqlEntityProcessor.setQuery(sql);

        SQLDataSource mockedDataSource = new SQLDataSource();
        mockedDataSource.setDriver("com.mysql.jdbc.Driver");
        mockedDataSource.setUrl("jdbc:mysql://localhost:3306/subsolr");
        mockedDataSource.setUserId("root");
        mockedDataSource.setPassword("root");
        
        Map<String, String> map = new HashMap<String, String>();
        map.put("EMP_ID","EMP_ID");
        map.put("EMP_NAME","EMP_NAME");
        map.put("EMP_GENDER","EMP_GENDER");
        map.put("EMP_HIRE_DATE","EMP_HIRE_DATE");

        fieldSetDefinition = new FieldSetDefinition();
        fieldSetDefinition.setDataSource(mockedDataSource);
        fieldSetDefinition.setFieldName2DataSourceMap(map);
        fieldSetDefinition.setEntityProcessor(sqlEntityProcessor);
		
        sqlEntityProcessor.setFieldSetDefinition(fieldSetDefinition);
        FieldContextProcessor mockedFieldContextProcessor = null; // new FieldContextProcessor(fieldConfigFileName, documentBuilder, xPath);
		sqlEntityProcessor.setFieldContextProcessor(mockedFieldContextProcessor);
    }

    //@Test() // TODO mockedFieldContextProcessor pending. 
    public void testGetRecords() {
        List<Record> records = sqlEntityProcessor.getRecords();
        Assert.assertTrue(records.size() == 10);
    }

}
