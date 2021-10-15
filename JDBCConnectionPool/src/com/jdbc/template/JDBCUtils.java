package com.jdbc.template;

import com.jdbc.exception.MySqlException;
import com.jdbc.exception.NoParamsException;
import com.jdbc.pool.ConnectionPool;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.*;

public class JDBCUtils {

    //将连接池放着这里，一个Utils拥有一个连接池
    private ConnectionPool connectionPool;

    public JDBCUtils(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    public Integer update(String sql, Object... params) throws MySqlException, SQLException, ClassNotFoundException {
        if(!sql.startsWith("update")) {
            throw new MySqlException("sql is not update");
        }

        UpdateTemplate template = new UpdateTemplate(connectionPool);
        return (Integer) template.execute(sql, params);
    }

    public Integer save(String sql, Object... params) throws MySqlException, SQLException, ClassNotFoundException {
        if(!sql.startsWith("insert")) {
            throw new MySqlException("sql is not insert");
        }

        UpdateTemplate template = new UpdateTemplate(connectionPool);
        return (Integer) template.execute(sql, params);
    }

    public Integer delete(String sql, Object... params) throws MySqlException, SQLException, ClassNotFoundException {
        if(!sql.startsWith("delete")) {
            throw new MySqlException("sql is not delete");
        }

        UpdateTemplate template = new UpdateTemplate(connectionPool);
        return (Integer) template.execute(sql, params);
    }

    public <T> T selectOne(String sql, Class<T> clazz, Object... param) throws MySqlException, NoSuchMethodException, NoSuchFieldException, InstantiationException, IllegalAccessException, InvocationTargetException, SQLException, ClassNotFoundException {
        if(!sql.startsWith("select")) {
            throw new MySqlException("sql is not select");
        }

        QueryTemplate template = new QueryTemplate(connectionPool);
        List<Map<String, Object>> list = (List<Map<String, Object>>) template.execute(sql, param);

        if(list == null || list.size() != 1) {
            throw new MySqlException("result is not one");
        }
        Map<String, Object> map = list.get(0);

        return mapping(clazz, map);
    }

    public <T> List<T> selectList(String sql, Class<T> clazz, Object... param) throws MySqlException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException, NoSuchFieldException, SQLException, ClassNotFoundException {
        if(!sql.startsWith("select")) {
            throw new MySqlException("sql is not select");
        }

        QueryTemplate template = new QueryTemplate(connectionPool);
        List<Map<String, Object>> list = (List<Map<String, Object>>) template.execute(sql, param);

        List<T> res = new ArrayList<>();
        for(Map<String, Object> map : list) {
            T val = mapping(clazz, map);
            res.add(val);
        }
        return res;
    }

    private <T> T mapping(Class<T> clazz, Map<String, Object> map) throws InvocationTargetException, IllegalAccessException, NoSuchFieldException, NoSuchMethodException, InstantiationException {
        Object val = null;

        if(clazz == Map.class) {
            val = map;
        } else if (clazz == String.class) {//name
            for(String key : map.keySet()) {
                val = String.valueOf(map.get(key));
            }
        } else if (clazz == Long.class || clazz == long.class) {//age
            for(String key : map.keySet()) {
                val = ((Number) map.get(key)).longValue();
            }

        } else if(clazz == Integer.class || clazz == int.class) {
            for(String key : map.keySet()) {
                val = ((Number) map.get(key)).intValue();
            }
        } else if(clazz == Short.class || clazz == short.class) {
            for(String key : map.keySet()) {
                val = ((Number) map.get(key)).shortValue();
            }
        } else if(clazz == Byte.class || clazz == byte.class) {
            for(String key : map.keySet()) {
                val = ((Number) map.get(key)).byteValue();
            }
        } else if(clazz == Double.class || clazz == double.class) {//person
            for(String key : map.keySet()) {
                val = ((Number) map.get(key)).doubleValue();
            }
        } else {
            val = clazz.newInstance();
            for(String key : map.keySet()) {
                Object value = map.get(key);
                Field field = clazz.getDeclaredField(key);
                String methodName = "set" + key.substring(0, 1).toUpperCase() + key.substring(1);
                Method method = clazz.getMethod(methodName, field.getType());
                method.invoke(val, value);
            }
        }

        return (T)val;
        
    }

}
