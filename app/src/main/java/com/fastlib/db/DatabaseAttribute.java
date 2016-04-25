package com.fastlib.db;

/**
 * Created by sgfb on 16/3/21.
 * 数据库运行时一些参数
 */
public final class DatabaseAttribute{
    private boolean asc;
    private String orderBy;
    private int ceil;

    public void defaultAttribute(){
        asc=true;
        orderBy=null;
        ceil=Integer.MAX_VALUE;
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

    public int getCeil() {
        return ceil;
    }

    public void setCeil(int ceil) {
        this.ceil = ceil;
    }
}