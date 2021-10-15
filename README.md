* 首先通过模板模式确定JDBC固定流程模板
* 由于executeUpdate和executeQuery分成两个模板
* 创建Util类通过crud四个方法将两个模板隐藏
* 将Util的查询方法的结果进行反射处理生成domain对象
* 创建Session类将crud四个方法的入参进行解析，调用Util
* 添加CRUD注解，通过动态代理执行session和util中的方法
* 通过静态代理自定义连接close()方法，创建连接池获取连接
* 优化连接池，增加回收机制，扩充机制，处理策略

---

**JDBCUtil**

* sql：select * from person where name = ?
* Class（指定出参格式）：可以为Domain，基本类型和包装类，Map类型

**JDBCSession**

* sql：select * from person where name = #{name}
* Domain（指定出参入参格式）

**Annotation**

* sql：select * from person where name = #{name}
* Domain（指定出参入参格式）







资料：https://blog.csdn.net/qq_31859365/article/details/82902349