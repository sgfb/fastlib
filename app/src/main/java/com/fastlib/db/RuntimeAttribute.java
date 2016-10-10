package com.fastlib.db;

import android.graphics.Point;

/**
 * Created by sgfb on 16/3/21.
 * 数据库运行时一些参数
 */
public final class RuntimeAttribute{
    private boolean asc;
    private int saveMax; //最大保存数,如果超出了这个值删除历史直到符合这个值
    private int start, size; //合起来就是limit
    private String orderBy;
    private String whichDatabase; //仅本次操作,保存数据到指定数据库.如果这个数据库不存在,这条语句将被丢弃不会抛出异常
    private String[] selectColumn;
    private String[] unselectColumn;//如果这个字段为空取再判断selectColumn，如果selectColumn也是空，取所有列。如果这个字段不为空将不使用selectColumn字段

    public void defaultAttribute(){
        asc=true;
        orderBy=null;
        whichDatabase =null;
        saveMax=Integer.MAX_VALUE;
        start=0;
        size =Integer.MAX_VALUE;
    }

    public boolean isAsc() {
        return asc;
    }

    public void setAsc(boolean asc) {
        this.asc = asc;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    public int getSaveMax() {
        return saveMax;
    }

    public void setSaveMax(int saveMax) {
        this.saveMax = saveMax;
    }

    public String getWhichDatabase() {
        return whichDatabase;
    }

    public void setWhichDatabase(String whichDatabase) {
        this.whichDatabase = whichDatabase;
    }

    public void limit(int start,int end){
        this.start=start;
        this.size =end;
    }

    public Point getLimit(){
        return new Point(start, size);
    }

    public void select(String... selectColumn){
        this.selectColumn=selectColumn;
    }

    public String[] getSelectColumn(){
        return selectColumn;
    }

    public void unselect(String... unselectColumn){
        this.unselectColumn=unselectColumn;
    }

    public String[] getUnselectColumn(){
        return unselectColumn;
    }
}