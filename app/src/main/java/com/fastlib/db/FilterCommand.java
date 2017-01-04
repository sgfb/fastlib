package com.fastlib.db;

/**
 * Created by sgfb on 17/1/4.
 * 过滤命令
 */
public final class FilterCommand{
    public static final int TYPE_NULL=0;
    public static final int TYPE_BIGER=1;
    public static final int TYPE_SMALLER=2;
    public static final int TYPE_EQUAL=3;
    public static final int TYPE_UNEQUAL=4;
    private int mType;
    private String mValue;

    private FilterCommand(int type,String value){
        mType=type;
        mValue=value;
    }

    public static FilterCommand biger(String value){
        return new FilterCommand(TYPE_BIGER,value);
    }

    public static FilterCommand smaller(String value){
        return new FilterCommand(TYPE_SMALLER,value);
    }

    public static FilterCommand emptyVaue(){
        return new FilterCommand(TYPE_NULL,null);
    }

    public static FilterCommand equal(String value){
        return new FilterCommand(TYPE_EQUAL,value);
    }

    public static FilterCommand unequal(String value){
        return new FilterCommand(TYPE_UNEQUAL,value);
    }

    public int getType() {
        return mType;
    }

    public String getValue() {
        return mValue;
    }

    /**
     * 根据类型转换表达式字符串
     * @return 表达式字符串
     */
    public String getExpression(){
        switch(mType){
            case TYPE_BIGER:
                return ">?";
            case TYPE_EQUAL:
                return "=?";
            case TYPE_SMALLER:
                return "<?";
            case TYPE_UNEQUAL:
                return "!=?";
            case TYPE_NULL:
                return "is null";
            default:return "";
        }
    }
}
