package com.subsolr.entityprocessors.datasources;

import org.apache.commons.lang.StringUtils;

public class DataSourceFactory {
	
	public static DataSource getInstance(String type){
        if(StringUtils.isEmpty(type)) {
            throw new IllegalStateException("Type can not be null.");
        }
        DataSourceType dataSourceType = DataSourceType.getDataSourceType(type);
        DataSource dataSource = null;
        if(dataSourceType.equals(DataSourceType.SQL)) {
            dataSource = new SQLDataSource();
        }
        return dataSource;
    }

}
