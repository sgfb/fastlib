package com.fastlib.bean;

/**
 * Created by sgfb on 2018/5/27.
 * 数据库测试用内部数据模型(选修课)
 */
public class DBInnerBean{
    public int id;
    public float score;
    public String name;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DBInnerBean)) return false;

        DBInnerBean innerBean = (DBInnerBean) o;

        if (id != innerBean.id) return false;
        if (Float.compare(innerBean.score, score) != 0) return false;
        return name.equals(innerBean.name);
    }
}