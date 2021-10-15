package com.jdbc.pool;

import java.sql.Connection;
import java.sql.SQLException;

public class ConnectionProxy extends AbstractConnection {

    private boolean isUsing;

    public ConnectionProxy(Connection connection) {
        super(connection);
    }

    public boolean isUsing() {
        return isUsing;
    }

    public void setUsing(boolean using) {
        isUsing = using;
    }

    public void close() {
        setUsing(false);
    }

    public void shutDown() throws SQLException {
        connection.close();
    }

}
