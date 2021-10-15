package com.jdbc.template;

import com.jdbc.bo.ParsingBO;
import com.jdbc.exception.MySqlException;
import com.jdbc.exception.NoParamsException;
import com.jdbc.pool.ConnectionPool;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class JDBCSession {

    private JDBCUtils jdbcUtils;

    public JDBCSession(JDBCUtils jdbcUtils) {
        this.jdbcUtils = jdbcUtils;
    }

    public <T> Integer save(String sql, T domain, Class<T> clazz) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, MySqlException, SQLException, ClassNotFoundException {
        ParsingBO parsingBO = parsing(sql, domain, clazz);

        if(parsingBO.getParam().length == 0) {
            return jdbcUtils.save(parsingBO.getSql());
        }
        return jdbcUtils.save(parsingBO.getSql(), parsingBO.getParam());
    }

    public <T> Integer delete(String sql, T domain, Class<T> clazz) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, MySqlException, SQLException, ClassNotFoundException {
        ParsingBO parsingBO = parsing(sql, domain, clazz);

        if(parsingBO.getParam().length == 0) {
            return jdbcUtils.delete(parsingBO.getSql());
        }
        return jdbcUtils.delete(parsingBO.getSql(), parsingBO.getParam());
    }

    public <T> Integer update(String sql, T domain, Class<T> clazz) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, MySqlException, SQLException, ClassNotFoundException {
        ParsingBO parsingBO = parsing(sql, domain, clazz);

        if(parsingBO.getParam().length == 0) {
            return jdbcUtils.update(parsingBO.getSql());
        }
        return jdbcUtils.update(parsingBO.getSql(), parsingBO.getParam());
    }

    public <T> T selectOne(String sql, T domain, Class<T> clazz) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, MySqlException, NoSuchFieldException, InstantiationException, SQLException, ClassNotFoundException {
        ParsingBO parsingBO = parsing(sql, domain, clazz);

        if(parsingBO.getParam().length == 0) {
            return jdbcUtils.selectOne(parsingBO.getSql(), clazz);
        }
        return jdbcUtils.selectOne(parsingBO.getSql(), clazz, parsingBO.getParam());
    }

    public <T> List<T> selectList(String sql, T domain, Class<T> clazz) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, MySqlException, NoSuchFieldException, InstantiationException, SQLException, ClassNotFoundException {
        ParsingBO parsingBO = parsing(sql, domain, clazz);

        if(parsingBO.getParam().length == 0) {
            return jdbcUtils.selectList(parsingBO.getSql(), clazz);
        }
        return jdbcUtils.selectList(parsingBO.getSql(), clazz, parsingBO.getParam());
    }

    private <T> ParsingBO parsing(String sql, T domain, Class<T> clazz) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        ParsingBO result = new ParsingBO();

        StringBuilder builder = new StringBuilder();
        List<Object> objectList = new ArrayList<>();
        for(int i = 0; i < sql.length(); i ++) {
            char ch = sql.charAt(i);
            if(ch == '#') {
                builder.append("?");
                StringBuilder param = new StringBuilder();
                i += 2;
                for(;; i ++) {
                    ch = sql.charAt(i);
                    if(ch == '}') break;
                    param.append(ch);
                }
                String methodName = "get" + param.substring(0, 1).toUpperCase() + param.substring(1);
                Method method = clazz.getMethod(methodName);
                Object obj = method.invoke(domain);
                objectList.add(obj);
                continue;
            }
            builder.append(ch);
        }
        result.setParam(objectList.toArray());
        result.setSql(builder.toString());

        return result;
    }

}
