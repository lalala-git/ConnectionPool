package com.jdbc.proxy;

import com.jdbc.annotation.*;
import com.jdbc.exception.MySqlException;
import com.jdbc.exception.NoParamsException;
import com.jdbc.exception.NoSQLException;
import com.jdbc.factory.MySQLFactory;
import com.jdbc.template.JDBCSession;
import com.jdbc.template.JDBCUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.sql.SQLException;

public class DynamicProxy implements InvocationHandler {

    //获取代理对象
    public <T> T getProxy(Class<T> inter) {
        return (T) Proxy.newProxyInstance(inter.getClassLoader(), new Class[] {inter}, this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        Annotation[] annotations = method.getDeclaredAnnotations();
        for(Annotation annotation : annotations) {
            Class annotationType = annotation.annotationType();
            if(annotationType == Insert.class) {
                return insertHandle(annotation, args);
            } else if(annotationType == Update.class) {
                return updateHandle(annotation, args);
            } else if(annotationType == Delete.class) {
                return deleteHandle(annotation, args);
            } else if(annotationType == Select.class) {
                return selectHandle(annotation, args);
            }
        }
        throw new NoSQLException("your annotation is no sql");
    }

    public Object insertHandle(Annotation annotation, Object[] args) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, MySqlException, SQLException, ClassNotFoundException {
        String sql = (String) annotation.getClass().getMethod("value").invoke(annotation);
        JDBCSession session = MySQLFactory.getMySQLFactory().getJdbcSession();
        JDBCUtils utils = MySQLFactory.getMySQLFactory().getJdbcUtils();
        if(args == null || args.length == 0) {
            return utils.save(sql);
        }
        Class param = args[0].getClass();
        return session.save(sql, args[0], param);

    }
    public Object updateHandle(Annotation annotation, Object[] args) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, MySqlException, SQLException, ClassNotFoundException {
        String sql = (String) annotation.getClass().getMethod("value").invoke(annotation);
        JDBCSession session = MySQLFactory.getMySQLFactory().getJdbcSession();
        JDBCUtils utils = MySQLFactory.getMySQLFactory().getJdbcUtils();
        if(args == null || args.length == 0) {
            return utils.update(sql);
        }
        Class param = args[0].getClass();
        return session.update(sql, args[0], param);
    }
    public Object deleteHandle(Annotation annotation, Object[] args) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, MySqlException, SQLException, ClassNotFoundException {
        String sql = (String) annotation.getClass().getMethod("value").invoke(annotation);
        JDBCSession session = MySQLFactory.getMySQLFactory().getJdbcSession();
        JDBCUtils utils = MySQLFactory.getMySQLFactory().getJdbcUtils();
        if(args == null || args.length == 0) {
            return utils.delete(sql);
        }
        Class param = args[0].getClass();
        return session.delete(sql, args[0], param);
    }
    public Object selectHandle(Annotation annotation, Object[] args) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, MySqlException, NoSuchFieldException, InstantiationException, NoParamsException, SQLException, ClassNotFoundException {
        String sql = (String) annotation.getClass().getMethod("value").invoke(annotation);
        JDBCSession session = MySQLFactory.getMySQLFactory().getJdbcSession();
        JDBCUtils utils = MySQLFactory.getMySQLFactory().getJdbcUtils();

        if(args == null || args.length == 0) {
            throw new NoParamsException("no param find");
        }
        Class param = args[0].getClass();
        return session.selectOne(sql, args[0], param);
    }
}
