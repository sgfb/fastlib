package com.fastlib.db;

/**
 * Created by sgfb on 17/1/6.
 * 数据库与条件过滤
 */
public class And extends FilterCommand{

    public And(FilterCondition condition){
        super(condition);
    }

    @Override
    public int getType(){
        return TYPE_AND;
    }
}