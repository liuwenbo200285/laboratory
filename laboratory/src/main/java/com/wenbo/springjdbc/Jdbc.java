package com.wenbo.springjdbc;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;

public abstract interface Jdbc {
	public abstract void setJdbcTemplate(JdbcTemplate paramJdbcTemplate);

	public abstract JdbcTemplate getJdbcTemplate();

	public abstract int update(String paramString,
			SqlParameter paramSqlParameter);

	public abstract int getInt(String paramString,
			SqlParameter paramSqlParameter);

	public abstract <T> T get(String paramString, Class<T> paramClass,
			SqlParameter paramSqlParameter);

	public abstract <T> List<T> getList(String paramString,
			Class<T> paramClass, SqlParameter paramSqlParameter);

	public abstract <T> List<T> getList(String paramString, Class<T> paramClass);

	public abstract <T> int insert(T paramT);

	public abstract <T> int[] insert(List<T> paramList);

	public abstract <T> int insert(String paramString, T paramT);

	public abstract <T> int[] insert(String paramString, List<T> paramList);
}
