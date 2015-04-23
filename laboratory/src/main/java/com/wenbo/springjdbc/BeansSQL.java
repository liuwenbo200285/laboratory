package com.wenbo.springjdbc;

import java.util.ArrayList;
import java.util.List;

public class BeansSQL {
	private String sql;
	private List<SqlParameter> parameters = new ArrayList<SqlParameter>();

	public String getSql() {
		return this.sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public List<SqlParameter> getParams() {
		return this.parameters;
	}

	public void AddParam(SqlParameter parameter) {
		this.parameters.add(parameter);
	}
}
