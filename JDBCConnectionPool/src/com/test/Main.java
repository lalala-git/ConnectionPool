package com.test;

import com.jdbc.proxy.DynamicProxy;

public class Main {
    public static void main(String[] args) throws Exception {

        MyTest myTest = new DynamicProxy().getProxy(MyTest.class);
        for(int i = 0; i < 100; i ++) {
            Thread thread = new Thread(() -> {
                Person val = myTest.select(new Person("王小二", 10, "女"));
                System.out.println(val);
            });
            thread.join();
            thread.start();
        }

//
//        MyTest myTest = new DynamicTest().getProxy(MyTest.class);
//        myTest.test();


//        InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("jdbc.properties");
//        Properties properties = new Properties();
//        properties.load(inputStream);
//        JDBCSession jdbcSession = new JDBCSession(properties);
//        List<Person> personList = jdbcSession.selectList("select * from person where name = #{name}", new Person("王小二", 19, "男"), Person.class);
//        for(Person person : personList) {
//            System.out.println(person);
//        }


//        int res = jdbcSession.save("insert into person values (#{name}, #{age}, #{sex})", new Person("王小二", 19, "男"), Person.class);
//        String insertSql = "insert into person values(?, ?, ?)";
//        String deleteSql = "delete from person where name = ?";
//        String updateSql = "update person set sex = ? where name = ?";
//        String selectSql = "select * from person where name = ?";
//        String selectAll = "select * from person";
//        String url = "jdbc:mysql://182.42.113.36:3306/jdbc?characterEncoding=utf8&serverTimezone=CST";
//        String username = "root";
//        String password = "123456";
//        String driver = "com.mysql.cj.jdbc.Driver";
//


//        List<Person> personList = jdbcUtils.selectAll(selectAll, Person.class);
//        for(Person person : personList) {
//            System.out.println(person);
//        }

//        int res = jdbcUtils.update(updateSql, "22", "王宇");
//        System.out.println(res);

//        Class.forName("com.mysql.cj.jdbc.Driver");
//
//        Connection conn = DriverManager.getConnection(url, username, password);
//
//        PreparedStatement state = conn.prepareStatement(sql);
//        state.setString(1, "王宇");
//        state.setInt(2, 21);
//        state.setString(3, "女");
//
//        state.executeUpdate();
//
//        state.close();
//        conn.close();
    }
}
