package org.crudboy.toolbar.db;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.ArrayListHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import static org.crudboy.toolbar.db.JDBCDriverMap.*;

public class SimpleDBClient {

    private String dbType;

    private DataSource dataSource;

    private QueryRunner run;

    public SimpleDBClient(String dbType, String ip, String port, String dbName, String userName, String password) {
        this.dbType = dbType;
        BasicDataSource ds = new BasicDataSource();
        String driverClassName = JDBCDriverMap.getDriverClassName(dbType);
        String url = JDBCDriverMap.getURL(dbType, ip, port, dbName);
        ds.setDriverClassName(driverClassName);
        ds.setUrl(url);
        ds.setUsername(userName);
        ds.setPassword(password);
        dataSource = ds;
        run = new QueryRunner(dataSource);
    }

    public List<Map<String, Object>> queryAsMap(String sql) throws SQLException {
        List<Map<String, Object>> maps = run.query(sql, new MapListHandler());
        return maps;
    }

    public List<Object[]> queryAsObject(String sql) throws SQLException {
        List<Object[]> arrays = run.query(sql, new ArrayListHandler());
        return arrays;
    }

    public <T> List<T> queryAsBean(String sql, Class<T> tClass) throws SQLException {
        ResultSetHandler<List<T>> resultHandler = new BeanListHandler<T>(tClass);
        List<T> results = run.query(sql, resultHandler);
        return results;
    }

    public <T> List<T> queryAsBean(String sql, Page page, Class<T> tClass) throws SQLException {
        ResultSetHandler<List<T>> resultHandler = new BeanListHandler<T>(tClass);
        StringBuilder pageSQL = new StringBuilder();
        switch (this.dbType) {
            case DB_ORACLE: {
                int startNum = (page.getCurrentPageNum()-1)*page.getSize() + 1;
                int endNum = startNum + page.getSize() - 1;
                pageSQL.append("select * from (select t.*, rownum rn from (")
                        .append(sql).append(") t where rownum<=").append(endNum)
                        .append(") where rn>=").append(startNum);
                break;
            }
            case DB_MYSQL: {
                int startNum = (page.getCurrentPageNum()-1)*page.getSize();
                pageSQL.append(sql).append(" limit ").append(startNum).append(" ").append(page.getSize());
                break;
            }
            case DB_POSTGRESQL:
                break;
            case DB_MSSQL:
                break;
            default:
                break;
        }
        return queryAsBean(pageSQL.toString(), tClass);
    }

    public void update(String sql, List<Object> params) throws SQLException {
        run.update(sql, params.toArray());
    }

    public void batchUpdate(String sql, List<List<Object>> params) throws SQLException {
        Object[][] parameters = new Object[params.size()][];
        for (int i=0; i<params.size(); i++) {
            parameters[i] = params.get(i).toArray();
        }
        run.update(sql, parameters);
    }
}
