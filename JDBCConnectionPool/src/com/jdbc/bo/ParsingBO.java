package com.jdbc.bo;

import java.io.Serializable;

public class ParsingBO implements Serializable {

    private static final long serialVersionUID = 6695383790847736494L;

    private String sql;

    private Object[] param;

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public Object[] getParam() {
        return param;
    }

    public void setParam(Object[] param) {
        this.param = param;
    }
}
