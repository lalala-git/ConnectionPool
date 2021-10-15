package com.jdbc.template;

import com.jdbc.pool.ConnectionPool;

import java.sql.SQLException;

class UpdateTemplate extends JDBCTemplate {

    public UpdateTemplate(ConnectionPool connectionPool) throws SQLException, ClassNotFoundException {
        super(connectionPool);
    }

    @Override
    public Integer executeSql() {
        try {
            return preparedStatement.executeUpdate();
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
        return null;
    }
}
