package org.crudboy.toolbar.db;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

public class JDBCDriverMap {

    public final static String DB_ORACLE = "ORACLE";
    public final static String DB_MYSQL = "MySQL";
    public final static String DB_POSTGRESQL = "PostgreSQL";
    public final static String DB_MSSQL = "MSSQL";

    private static void checkDBType(String dbType) {
        Preconditions.checkArgument(
                dbType.equals(DB_ORACLE) ||
                        dbType.equals(DB_MYSQL) ||
                        dbType.equals(DB_POSTGRESQL) ||
                        dbType.equals(DB_MSSQL)
                , Strings.lenientFormat("db type is not correct"));
    }

    public static String getDriverClassName(String dbType) {
        checkDBType(dbType);
        String driverClassName = null;
        switch (dbType) {
            case DB_ORACLE:
                driverClassName = "oracle.jdbc.driver.OracleDriver";
                break;
            case DB_MYSQL:
                driverClassName = "com.mysql.jdbc.Driver";
                break;
            case DB_POSTGRESQL:
                driverClassName = "org.postgresql.Driver";
                break;
            case DB_MSSQL:
                driverClassName = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
                break;
            default:
                break;
        }
        return driverClassName;
    }

    public static String getURL(String dbType, String ip, String port, String databaseName) {
        checkDBType(dbType);
        String url = null;
        switch (dbType) {
            case DB_ORACLE:
                url = "jdbc:oracle:thin:@" + ip + ":" + port + ":" + databaseName;
                break;
            case DB_MYSQL:
                url = "jdbc:mysql://" + ip + ":" + port + "/" + databaseName;
                break;
            case DB_POSTGRESQL:
                url = "jdbc:postgresql://" + ip + ":" + port + "/" + databaseName;
                break;
            case DB_MSSQL:
                url = "jdbc:sqlserver://" + ip + ":" + port + ";database=" + databaseName;
                break;
            default:
                break;
        }
        return url;
    }
}
