package com.wenbo.springjdbc;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.ResultSetExtractor;


public class JdbcImpl implements Jdbc {
	private static Logger logger = Logger.getLogger(JdbcImpl.class);
	private JdbcTemplate jdbcTemplate;

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public JdbcTemplate getJdbcTemplate() {
		String name = DataSourceContextHolder.getDataSource();
		DataSource dataSource = null;
		if(StringUtils.isNotBlank(name)){
			dataSource = SpringInit.getApplicationContext().getBean(name,DataSource.class);
			if(dataSource != jdbcTemplate.getDataSource()){
				jdbcTemplate.setDataSource(dataSource);
			}
		}else{
			dataSource = SpringInit.getApplicationContext().getBean(DataSourceContextHolder.DEFAULT_DATASOURCE,DataSource.class);
			if(jdbcTemplate.getDataSource() != dataSource){
				jdbcTemplate.setDataSource(dataSource);
			}
		}
		return this.jdbcTemplate;
	}

	public int update(String sql, final SqlParameter parameter) {
		long start = System.currentTimeMillis();

		int effectRow = this.getJdbcTemplate().update(sql,
				new PreparedStatementSetter() {
					public void setValues(PreparedStatement ps)
							throws SQLException {
						if (parameter != null)
							for (Map.Entry<Integer, Object> entry : parameter
									.getParams().entrySet())
								ps.setObject(entry.getKey().intValue(),entry.getValue());
					}
				});
		if (logger.isDebugEnabled()) {
			long time = System.currentTimeMillis() - start;
			logger.debug("execute.time[" + time + "], sql["
					+ getSql(sql, parameter) + "]");
		}

		return effectRow;
	}

	private String getSql(String sql, SqlParameter parameter) {
		if ((parameter == null) || (parameter.getParams() == null)) {
			return sql;
		}

		for (Iterator<Object> i$ = parameter.getParams().values().iterator(); i$
				.hasNext();) {
			Object obj = i$.next();
			if (obj == null) {
				obj = "null";
			}
			sql = sql.replaceFirst("\\?", "`" + obj.toString() + "`");
		}

		return sql;
	}

	public <T> T get(String sql, Class<T> cls, SqlParameter parameter) {
		long start = System.currentTimeMillis();

		List<T> list = getList(sql, cls, parameter);
		if (list != null) {
			if (list.size() == 1)
				return list.get(0);
			if (list.size() > 0) {
				throw new RuntimeException(
						"return more than one record while get one object!!!");
			}
		}

		if (logger.isDebugEnabled()) {
			long time = System.currentTimeMillis() - start;
			logger.debug("execute.time[" + time + "], sql["
					+ getSql(sql, parameter) + "]");
		}

		return null;
	}

	public <T> List<T> getList(String sql, Class<T> cls) {
		return getList(sql, cls, null);
	}

	public <T> List<T> getList(String sql, Class<T> cls,
			final SqlParameter parameter) {
		long start = System.currentTimeMillis();

		ColumnBeanRowMapper<T> mapper = new ColumnBeanRowMapper<T>();
		mapper.setMappedClass(cls);

		List<T> list = this.getJdbcTemplate().query(sql,
				new PreparedStatementSetter() {
					public void setValues(PreparedStatement ps)
							throws SQLException {
						if (parameter != null)
							for (Map.Entry<Integer, Object> entry : parameter
									.getParams().entrySet()) {
								Object value = entry.getValue();
								if ((value instanceof String))
									ps.setString(entry.getKey().intValue(),
											value != null ? value.toString()
													: null);
								else
									ps.setObject(entry.getKey().intValue(), value);
							}
					}
				}, mapper);

		if (logger.isDebugEnabled()) {
			long time = System.currentTimeMillis() - start;
			logger.debug("execute.time[" + time + "], sql["
					+ getSql(sql, parameter) + "]");
		}

		return list;
	}

	public int getInt(String sql, final SqlParameter parameter) {
		long start = System.currentTimeMillis();
		int value = this.getJdbcTemplate().query(sql,
				new PreparedStatementSetter() {
					public void setValues(PreparedStatement ps)
							throws SQLException {
						if (parameter != null)
							for (Map.Entry<Integer, Object> entry : parameter
									.getParams().entrySet())
								ps.setObject(entry.getKey().intValue(),entry.getValue());
					}
				}, new ResultSetExtractor<Integer>() {
					public Integer extractData(ResultSet rs)
							throws SQLException {
						if (rs.next()) {
							return Integer.valueOf(rs.getInt(1));
						}
						return Integer.valueOf(0);
					}
				}).intValue();

		if (logger.isDebugEnabled()) {
			long time = System.currentTimeMillis() - start;
			logger.debug("execute.time[" + time + "], sql["
					+ getSql(sql, parameter) + "]");
		}

		return value;
	}

	public <T> int insert(T t) {
		long start = System.currentTimeMillis();

		BeanSQL beanSql = SqlHelper.createBeanSql(t);
		int effectCount = update(beanSql.getSql(), beanSql.getParams());

		if (logger.isDebugEnabled()) {
			long time = System.currentTimeMillis() - start;
			logger.debug("execute.time[" + time + "], sql["
					+ getSql(beanSql.getSql(), beanSql.getParams()) + "]");
		}

		return effectCount;
	}

	public <T> int[] insert(final List<T> list) {
		long start = System.currentTimeMillis();

		BeansSQL beansSql = SqlHelper.crateBeanSql(list);
		final List<SqlParameter> parameters = beansSql.getParams();

		int[] effectCounts = this.getJdbcTemplate().batchUpdate(beansSql.getSql(),
				new BatchPreparedStatementSetter() {
					public void setValues(PreparedStatement ps, int i)
							throws SQLException {
						SqlParameter parameter = (SqlParameter) parameters
								.get(i);
						for (Map.Entry<Integer,Object> entry : parameter.getParams().entrySet())
							ps.setObject(entry.getKey().intValue(),
									entry.getValue());
					}

					public int getBatchSize() {
						return list.size();
					}
				});
		if (logger.isDebugEnabled()) {
			long time = System.currentTimeMillis() - start;
			logger.debug("execute.time[" + time + "], sql[" + beansSql.getSql()
					+ "]");
		}

		return effectCounts;
	}

	public <T> int insert(String tableName, T t) {
		long start = System.currentTimeMillis();

		BeanSQL beanSql = SqlHelper.createBeanSql(tableName, t);
		int effectCount = update(beanSql.getSql(), beanSql.getParams());

		if (logger.isDebugEnabled()) {
			long time = System.currentTimeMillis() - start;
			logger.debug("execute.time[" + time + "], sql["
					+ getSql(beanSql.getSql(), beanSql.getParams()) + "]");
		}

		return effectCount;
	}

	public <T> int[] insert(String tableName, final List<T> list) {
		long start = System.currentTimeMillis();

		BeansSQL beansSql = SqlHelper.crateBeanSql(tableName, list);
		final List<SqlParameter> parameters = beansSql.getParams();

		int[] effectCounts = this.getJdbcTemplate().batchUpdate(beansSql.getSql(),
				new BatchPreparedStatementSetter() {
					public void setValues(PreparedStatement ps, int i)
							throws SQLException {
						SqlParameter parameter = (SqlParameter) parameters
								.get(i);
						for (Map.Entry<Integer,Object> entry : parameter.getParams().entrySet())
							ps.setObject(entry.getKey().intValue(),
									entry.getValue());
					}

					public int getBatchSize() {
						return list.size();
					}
				});
		if (logger.isDebugEnabled()) {
			long time = System.currentTimeMillis() - start;
			logger.debug("execute.time[" + time + "], sql[" + beansSql.getSql()
					+ "]");
		}

		return effectCounts;
	}
}
