package com.fastlib.bean;

import com.fastlib.annotation.Database;

import java.util.List;

/**
 * Created by sgfb on 2018/5/27.
 * 数据库测试用数据模型(学生数据)
 */
public class DBTestBean{
    @Database(keyPrimary = true,autoincrement = true)
    public int id;
    public int age;
    public float score;
    public String name;
    public String address;
    public List<DBInnerBean> mOptionalCourse;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DBTestBean)) return false;

        DBTestBean that = (DBTestBean) o;

        if (id != that.id) return false;
        if (age != that.age) return false;
        if (Float.compare(that.score, score) != 0) return false;
        if (!name.equals(that.name)) return false;
        if (!address.equals(that.address)) return false;
        if((mOptionalCourse==null||mOptionalCourse.isEmpty())&&(that.mOptionalCourse==null||that.mOptionalCourse.isEmpty())) return true;
        else if(mOptionalCourse.size()==that.mOptionalCourse.size()){
            for(DBInnerBean innerBean:mOptionalCourse)
                if(!that.mOptionalCourse.contains(innerBean)) return false;
            return true;
        }
        return false;
    }
}