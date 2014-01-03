package com.subsolr.entityprocessors.datasources;

public enum DataSourceType {

	SQL("SQLdatasource"), FILE("Filedatasource");

	private String name;

	DataSourceType(String name) {
		this.setName(name);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public static DataSourceType getDataSourceType(String type) {
		for(DataSourceType dataSourceType : values()) {
			if(dataSourceType.getName().equals(type)) {
				return dataSourceType;
			}
		}
		return null;
	}

}
