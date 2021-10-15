package com.jdbc.template;

import com.jdbc.exception.ConnectionPoolException;
import com.jdbc.pool.ConnectionPool;

import java.sql.*;

abstract class JDBCTemplate {

    //每次使用都要new template，所以pool来自Factory比较好
    private ConnectionPool connectionPool;

    public JDBCTemplate(ConnectionPool connectionPool) throws SQLException, ClassNotFoundException {
        this.connectionPool = connectionPool;
    }

    //模板
    public Object execute(String sql, Object[] params) {
        try {
            getConnection();
            getPreparedStatement(sql, params);
            return executeSql();
        } catch (SQLException | ConnectionPoolException | InterruptedException throwable) {
            throwable.printStackTrace();
        } finally {
            try {
                closeAll();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        return null;
    }

    //加载驱动，一个模板只需要执行一次，改为在pool中加载
    public void loadDriver(String driver) throws ClassNotFoundException {
//        Class.forName(driver);
    }

    //创建连接，连接池中获取
    Connection connection;
    public void getConnection() throws ConnectionPoolException, InterruptedException, SQLException {
        connection = connectionPool.open();
    }

    //创建预处理对象，动态参数
    PreparedStatement preparedStatement;
    public void getPreparedStatement(String sql, Object[] params) throws SQLException {
        preparedStatement = connection.prepareStatement(sql);
        for(int i = 0; i < params.length; i ++) {
            preparedStatement.setObject(i + 1, params[i]);
        }
    }

    //执行sql，结果集处理
    ResultSet resultSet;
    public abstract Object executeSql();

    //关闭连接
    public void closeAll() throws SQLException {
        if(resultSet != null) resultSet.close();
        if(preparedStatement != null) preparedStatement.close();
        if(connection != null) connection.close();
    }

}
