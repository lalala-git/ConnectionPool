package com.test;

import com.jdbc.annotation.Delete;
import com.jdbc.annotation.Insert;
import com.jdbc.annotation.Select;

public interface MyTest {

    @Insert("insert into person values ('wuwu', '11', 'nan')")
    Integer insert();

    @Delete("delete from person where name = #{name}")
    Integer delete(Person person);

    @Select("select * from person where name = #{name}")
    Person select(Person person);
}
