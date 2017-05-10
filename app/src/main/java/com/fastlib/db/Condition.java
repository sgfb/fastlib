package com.fastlib.db;

import android.text.TextUtils;

/**
 * Created by sgfb on 17/1/4.
 * 过滤条件
 */
public final class Condition {
    public static final int TYPE_NULL=0;
    public static final int TYPE_NOT_NULL=1;
    public static final int TYPE_BIGER=2;
    public static final int TYPE_SMALLER=3;
    public static final int TYPE_EQUAL=4;
    public static final int TYPE_UNEQUAL=5;
    private int mType;
    private String mField;
    private String mValue;

    private Condition(int type, String value){
        this(type,null,value);
    }

    private Condition(int type, String field, String value){
        mType=type;
        mField=field;
        mValue=value;
    }

    public static Condition biger(String value){
        return new Condition(TYPE_BIGER,value);
    }

    public static Condition biger(String field, String value){
        return new Condition(TYPE_BIGER,field,value);
    }

    public static Condition smaller(String value){
        return new Condition(TYPE_SMALLER,value);
    }

    public static Condition smaller(String field, String value){
        return new Condition(TYPE_SMALLER,field,value);
    }

    public static Condition emptyVaue(){
        return new Condition(TYPE_NULL,null);
    }

    public static Condition emptyValue(String field){
        return new Condition(TYPE_NULL,field,null);
    }

    public static Condition notEmptyValue(){
        return new Condition(TYPE_NOT_NULL,null);
    }

    public static Condition notEmptyValue(String field){
        return new Condition(TYPE_NOT_NULL,field,null);
    }

    public static Condition equal(String value){
        return new Condition(TYPE_EQUAL,value);
    }

    public static Condition equal(String field, String value){
        return new Condition(TYPE_EQUAL,field,value);
    }

    public static Condition unequal(String value){
        return new Condition(TYPE_UNEQUAL,value);
    }

    public static Condition unequal(String field, String value){
        return new Condition(TYPE_UNEQUAL,field,value);
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