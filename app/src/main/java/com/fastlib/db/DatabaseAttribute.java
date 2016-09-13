package com.fastlib.db;

import android.graphics.Point;

/**
 * Created by sgfb on 16/3/21.
 * 数据库运行时一些参数
 */
public final class DatabaseAttribute{
    private boolean asc;
    private String orderBy;
    private String whichDatabase; //不转换数据库而保存数据到某个数据库.如果这个数据库不存在,这条语句将被丢弃不会抛出异常
    private int saveMax; //最大保存数,如果超出了这个值删除历史直到符合这个值
    private int start, size; //合起来就是limit

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
}