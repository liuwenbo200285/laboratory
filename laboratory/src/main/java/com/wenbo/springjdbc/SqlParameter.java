package com.wenbo.springjdbc;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SqlParameter {
	int parameterInd = 1;

	private Map<Integer, Object> params = new HashMap<Integer, Object>();

	public void setInt(Integer param) {
		setObject(param);
	}

	public void setString(String param) {
		setObject(param);
	}

	public void setLong(Long param) {
		setObject(param);
	}

	public void setDate(Date date) {
		setObject(date);
	}

	public void setDouble(Double param) {
		setObject(param);
	}

	public void setObject(Object param) {
		this.params.put(Integer.valueOf(this.parameterInd++), param);
	}

	public Map<Integer, Object> getParams() {
		return this.params;
	}
}
