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
