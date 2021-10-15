package com.jdbc.template;

import com.jdbc.pool.ConnectionPool;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class QueryTemplate extends JDBCTemplate {

    public QueryTemplate(ConnectionPool connectionPool) throws SQLException, ClassNotFoundException {
        super(connectionPool);
    }

    @Override
    public List<Map<String, Object>> executeSql() {
        try {
            ResultSet rs =  preparedStatement.executeQuery();
            List<Map<String, Object>> list = new ArrayList<>();
            while(rs.next()) {
                Map<String, Object> map = new HashMap<>();
                for(int i = 1; i <= rs.getMetaData().getColumnCount(); i ++) {
                    String countName = rs.getMetaData().getColumnName(i);
                    Object countVal = rs.getObject(i);
                    map.put(countName, countVal);
                }
                list.add(map);
            }
            return list;
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
        return null;
    }
}
