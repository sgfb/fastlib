package com.fastlib.db;

/**
 * Created by sgfb on 16/3/21.
 * 数据库运行时一些参数
 */
public final class DatabaseAttribute{
    private boolean asc;
    private String orderBy;

    public void defaultAttribute(){
        asc=true;
        orderBy=null;
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
}