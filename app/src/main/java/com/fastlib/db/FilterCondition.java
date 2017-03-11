package com.fastlib.db;

import android.text.TextUtils;

/**
 * Created by sgfb on 17/1/4.
 * 过滤条件
 */
public final class FilterCondition{
    public static final int TYPE_NULL=0;
    public static final int TYPE_NOT_NULL=1;
    public static final int TYPE_BIGER=2;
    public static final int TYPE_SMALLER=3;
    public static final int TYPE_EQUAL=4;
    public static final int TYPE_UNEQUAL=5;
    private int mType;
    private String mField;
    private String mValue;

    private FilterCondition(int type,String value){
        this(type,null,value);
    }

    private FilterCondition(int type,String field,String value){
        mType=type;
        mField=field;
        mValue=value;
    }

    public static FilterCondition biger(String value){
        return new FilterCondition(TYPE_BIGER,value);
    }

    public static FilterCondition biger(String field,String value){
        return new FilterCondition(TYPE_BIGER,field,value);
    }

    public static FilterCondition smaller(String value){
        return new FilterCondition(TYPE_SMALLER,value);
    }

    public static FilterCondition smaller(String field,String value){
        return new FilterCondition(TYPE_SMALLER,field,value);
    }

    public static FilterCondition emptyVaue(){
        return new FilterCondition(TYPE_NULL,null);
    }

    public static FilterCondition emptyValue(String field){
        return new FilterCondition(TYPE_NULL,field,null);
    }

    public static FilterCondition notEmptyValue(){
        return new FilterCondition(TYPE_NOT_NULL,null);
    }

    public static FilterCondition notEmptyValue(String field){
        return new FilterCondition(TYPE_NOT_NULL,field,null);
    }

    public static FilterCondition equal(String value){
        return new FilterCondition(TYPE_EQUAL,value);
    }

    public static FilterCondition equal(String field,String value){
        return new FilterCondition(TYPE_EQUAL,field,value);
    }

    public static FilterCondition unequal(String value){
        return new FilterCondition(TYPE_UNEQUAL,value);
    }

    public static FilterCondition unequal(String field,String value){
        return new FilterCondition(TYPE_UNEQUAL,field,value);
    }

    public int getType() {
        return mType;
    }

    public String getValue() {
        return mValue;
    }

    /**
     * 根据类型转换表达式字符串
     * @param key 主键名
     * @return 表达式字符串
     */
    public String getExpression(String key){
        String fieldName= TextUtils.isEmpty(mField)?key:mField;
        switch(mType){
            case TYPE_BIGER:
                return fieldName+">?";
            case TYPE_EQUAL:
                return fieldName+"=?";
            case TYPE_SMALLER:
                return fieldName+"<?";
            case TYPE_UNEQUAL:
                return fieldName+"!=?";
            case TYPE_NULL:
                return fieldName+" is null";
            case TYPE_NOT_NULL:
                return fieldName+" not null";
            default:return "";
        }
    }
}