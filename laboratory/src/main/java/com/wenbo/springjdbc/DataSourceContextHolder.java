package com.wenbo.springjdbc;

public class DataSourceContextHolder {

	private static final ThreadLocal<String> contextHolder = new ThreadLocal<String>();
	
	public static final String DEFAULT_DATASOURCE = "dataSource";
	
	public static void setDataSource(String dataSource) {
        contextHolder.set(dataSource);
    }
 
    public static String getDataSource() {
        return contextHolder.get();
    }
    
    public static void clearDbType() {
        contextHolder.remove();
    }
}
