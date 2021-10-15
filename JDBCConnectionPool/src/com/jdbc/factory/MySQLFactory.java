package com.jdbc.factory;

import com.jdbc.exception.NoParamsException;
import com.jdbc.pool.ConnectionPool;
import com.jdbc.template.JDBCSession;
import com.jdbc.template.JDBCUtils;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Properties;

public class MySQLFactory {

    private JDBCUtils jdbcUtils;
    private JDBCSession jdbcSession;

    //默认jdbc.properties配置文件
    private MySQLFactory() throws IOException, NoParamsException, SQLException, ClassNotFoundException {
        InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("jdbc.properties");
        Properties properties = new Properties();
        properties.load(inputStream);

        if(!properties.containsKey("driver")) {
            throw new NoParamsException("no param driver");
        }
        if(!properties.containsKey("url")) {
            throw new NoParamsException("no param url");
        }
        if(!properties.containsKey("username")) {
            throw new NoParamsException("no param username");
        }
        if(!properties.containsKey("password")) {
            throw new NoParamsException("no param password");
        }
        String driver = properties.getProperty("driver");
        String url = properties.getProperty("url");
        String username = properties.getProperty("username");
        String password = properties.getProperty("password");

        ConnectionPool connectionPool = new ConnectionPool(driver, url, username, password);
        this.jdbcUtils = new JDBCUtils(connectionPool);
        this.jdbcSession = new JDBCSession(jdbcUtils);

        assert inputStream != null;
        inputStream.close();
    }

    //内部类静态方法
    private static class Factory {
        static MySQLFactory mySQLFactory;

        static {
            try {
                mySQLFactory = new MySQLFactory();
            } catch (IOException | NoParamsException | SQLException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
    public static MySQLFactory getMySQLFactory() {
        return Factory.mySQLFactory;
    }


    //提供session和utils的get和set方法
    public JDBCUtils getJdbcUtils() {
        return jdbcUtils;
    }

    public void setJdbcUtils(JDBCUtils jdbcUtils) {
        this.jdbcUtils = jdbcUtils;
    }

    public JDBCSession getJdbcSession() {
        return jdbcSession;
    }

    public void setJdbcSession(JDBCSession jdbcSession) {
        this.jdbcSession = jdbcSession;
    }
}
