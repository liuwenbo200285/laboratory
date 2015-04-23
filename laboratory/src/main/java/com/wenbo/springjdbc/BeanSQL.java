package com.wenbo.springjdbc;

public class BeanSQL {
	private String sql;
	private SqlParameter params = new SqlParameter();

	public String getSql() {
		return this.sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public SqlParameter getParams() {
		return this.params;
	}

	public void AddParam(Object param) {
		if ((param instanceof String))
			this.params.setString(param.toString());
		else if ((param instanceof Integer))
			this.params.setInt(Integer.valueOf(Integer.parseInt(param
					.toString())));
		else if ((param instanceof Long))
			this.params.setLong(Long.valueOf(Long.parseLong(param.toString())));
		else if ((param instanceof Double))
			this.params.setDouble(Double.valueOf(Double.parseDouble(param
					.toString())));
		else
			this.params.setObject(param);
	}
}
