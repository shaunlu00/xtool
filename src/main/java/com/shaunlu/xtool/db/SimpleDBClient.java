package com.shaunlu.xtool.db;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.ArrayListHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import static com.shaunlu.xtool.db.JDBCDriverMap.*;

public class SimpleDBClient {

    private String dbType;

    private DataSource dataSource;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

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
    }

    public List<Map<String, Object>> queryAsMap(String sql) throws SQLException {
        QueryRunner run = new QueryRunner();
        try (Connection conn = dataSource.getConnection()) {
            List<Map<String, Object>> maps = run.query(conn, sql, new MapListHandler());
            return maps;
        }
    }

    public List<Object[]> queryAsObject(String sql) throws SQLException {
        QueryRunner run = new QueryRunner();
        try (Connection conn = dataSource.getConnection()) {
            List<Object[]> arrays = run.query(conn, sql, new ArrayListHandler());
            return arrays;
        }
    }

    public <T> List<T> queryAsBean(String sql, Class<T> tClass) throws SQLException {
        QueryRunner run = new QueryRunner();
        try (Connection conn = dataSource.getConnection()) {
            ResultSetHandler<List<T>> resultHandler = new BeanListHandler<T>(tClass);
            List<T> results = run.query(conn, sql, resultHandler);
            return results;
        }
    }

    public <T> List<T> queryAsBean(String sql, Page page, Class<T> tClass) throws SQLException {
        StringBuilder pageSQL = new StringBuilder();
        switch (this.dbType) {
            case DB_ORACLE: {
                int startNum = (page.getCurrentPageNum() - 1) * page.getSize() + 1;
                int endNum = startNum + page.getSize() - 1;
                pageSQL.append("select * from (select t.*, rownum rn from (")
                        .append(sql).append(") t where rownum<=").append(endNum)
                        .append(") where rn>=").append(startNum);
                break;
            }
            case DB_MYSQL: {
                int startNum = (page.getCurrentPageNum() - 1) * page.getSize();
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

    /**
     * @param sql
     * @param params
     * @return 1) The number of rows updated.
     * 2) -1 when exception happens
     */
    public int update(String sql, List<Object> params) {
        int ret = -1;
        QueryRunner run = new QueryRunner();
        try (Connection conn = dataSource.getConnection()) {
            ret = run.update(conn, sql, params.toArray());
        } catch (SQLException e) {
            logger.error("db update error", e);
        }
        return ret;
    }

    public boolean multipleUpdate(List<String> sqls) {
        boolean ret = false;
        QueryRunner run = new QueryRunner();
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);
            for (String sql : sqls) {
                run.update(conn, sql);
            }
            conn.commit();
            ret = true;
        } catch (SQLException e) {
            logger.error("multiple db update error", e);
            try {
                conn.rollback();
            } catch (SQLException e1) {
                logger.error("multiple db update-rollback error", e);
            }
        } finally {
            try {
                conn.close();
            } catch (SQLException e) {
                logger.error("multiple db update-close connection error", e);
            }
        }
        return ret;
    }

    /**
     * @param sql
     * @param params The query replacement parameters.
     * @throws SQLException
     *
     * for example:
     *    sql - update status=? where id=?
     *    params - {"deleted", "123"}
     *           - {"created", "456"}
     */
    public boolean batchUpdate(String sql, List<List<Object>> params) throws SQLException {
        boolean ret = false;
        QueryRunner run = new QueryRunner();
        Connection conn = null;
        Object[][] parameters = new Object[params.size()][];
        for (int i = 0; i < params.size(); i++) {
            parameters[i] = params.get(i).toArray();
        }
        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);
            run.update(conn, sql, parameters);
        } catch (SQLException e) {
            logger.error("batchUpdate error", e);
            try {
                conn.rollback();
            } catch (SQLException e1) {
                logger.error("batch db update-rollback error", e);
            }
        } finally {
            try {
                conn.close();
            } catch (SQLException e) {
                logger.error("batch db update-close connection error", e);
            }
        }
        return ret;
    }
}
