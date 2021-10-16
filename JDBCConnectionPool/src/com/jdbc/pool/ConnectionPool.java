package com.jdbc.pool;

import com.jdbc.exception.ConnectionPoolException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 获取连接：锁住proxy，保证一个连接只能被一个open获取
 * 扩充连接：锁住pool，保证一个池子里只能被一个
 * 回收连接
 */
public class ConnectionPool {

    private List<ConnectionProxy> connectionPool = new ArrayList<>();

    private final String url;
    private final String username;
    private final String password;

    private static final Integer CORE_CONNECTION_COUNT = 10;

    private static final Integer MAX_CONNECTION_COUNT = 20;

    private static final Integer RECYCLE_CONNECTION_COUNT = 3;

    private static void defaultHandle() throws ConnectionPoolException {
        throw new ConnectionPoolException("get connection is timeout");
    }

    public ConnectionPool(String driver,
                          String url,
                          String username,
                          String password) throws SQLException, ClassNotFoundException {
        //加载驱动
        Class.forName(driver);

        //初始化连接
        for(int i = 0; i < CORE_CONNECTION_COUNT; i ++) {
            Connection connection = DriverManager.getConnection(url, username, password);
            ConnectionProxy connectionProxy = new ConnectionProxy(connection);
            connectionPool.add(connectionProxy);
        }
        System.out.println("【log】pool初始化完成");

        //初始化参数
        this.username = username;
        this.password = password;
        this.url = url;

        //开启连接回收机制
        new RecycleHandle().start();
    }

    public Connection open() throws InterruptedException, ConnectionPoolException, SQLException {
        int second = 0;
        ok: while(true) {
            //获取connection连接，不能通过迭代器获取，modCount
            for (int i = 0; i < connectionPool.size(); i ++) {
                ConnectionProxy connectionProxy = connectionPool.get(i);
                if (connectionProxy.isUsing()) continue;
                synchronized (connectionProxy) {
                    if (!connectionProxy.isUsing()) {
                        System.out.println("【log】" + Thread.currentThread().getName() + " get the connection");
                        connectionProxy.setUsing(true);
                        return connectionProxy;
                    }
                }
                continue ok;
            }

            //增加连接数量
            if(connectionPool.size() < MAX_CONNECTION_COUNT) {
                synchronized (connectionPool) {
                    if(connectionPool.size() < MAX_CONNECTION_COUNT) {
                        System.out.println("【log】a connection is created");
                        System.out.println("【log】" + Thread.currentThread().getName() + " get the connection");

                        Connection connection = DriverManager.getConnection(url, username, password);
                        ConnectionProxy connectionProxy = new ConnectionProxy(connection);
                        connectionPool.add(connectionProxy);
                        connectionProxy.setUsing(true);
                        return connectionProxy;
                    }
                }
            }

            //拒绝策略
            System.out.println("【log】" + Thread.currentThread().getName() + " is waiting ...");
            second += 100;
            Thread.sleep(100);
            if(second >= 2000) {
                defaultHandle();
            }
        }
    }

    //回收策略
    private class RecycleHandle extends Thread {
        public void run() {
            while(true) {
                if (connectionPool.size() > CORE_CONNECTION_COUNT) {
                    //判断是否回收
                    int using = 0;
                    for (int i = 0; i < connectionPool.size(); i ++) {
                        ConnectionProxy connectionProxy = connectionPool.get(i);
                        if (connectionProxy.isUsing()) {
                            using++;
                        }
                    }
                    //触发回收机制
                    if (using <= RECYCLE_CONNECTION_COUNT) {
                        System.out.println("【log】recycle is running");
                        synchronized (connectionPool) {
                            Iterator<ConnectionProxy> iterator = connectionPool.iterator();
                            while(iterator.hasNext() && connectionPool.size() > CORE_CONNECTION_COUNT) {
                                ConnectionProxy proxy = iterator.next();
                                if(!proxy.isUsing()) {
                                    synchronized (proxy) {
                                        if (!proxy.isUsing()) {
                                            //即将关闭的连接设置为不可用
                                            proxy.setUsing(true);
                                            iterator.remove();
                                            try {
                                                proxy.shutDown();
                                                System.out.println("【log】a connection is recycled");
                                            } catch (SQLException throwables) {
                                                throwables.printStackTrace();
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                try {
                    //100秒进行一次回收
                    Thread.sleep(100000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
