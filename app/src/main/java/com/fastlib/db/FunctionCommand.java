package com.fastlib.db;

import android.support.annotation.Nullable;

/**
 * Created by sgfb on 17/7/9.
 * 数据库函数命令.指定属性值为某过滤段的指定函数返回
 */
public class FunctionCommand{
    private String mFieldName;
    private FunctionType mType;
    private FilterCommand mFilterCommand;

    public FunctionCommand(String fieldName, FunctionType type){
        mFieldName = fieldName;
        mType = type;
    }

    public FunctionCommand(String fieldName, FunctionType type, @Nullable FilterCommand filterCommand) {
        mFieldName = fieldName;
        mType = type;
        mFilterCommand = filterCommand;
    }

    public String getFieldName() {
        return mFieldName;
    }

    public FunctionType getType() {
        return mType;
    }

    public FilterCommand getFilterCommand() {
        return mFilterCommand;
    }
}