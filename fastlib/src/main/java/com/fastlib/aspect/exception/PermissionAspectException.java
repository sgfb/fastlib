package com.fastlib.aspect.exception;

/**
 * Created by sgfb on 2020\01\13.
 * 注解流程获取权限被拒绝
 */
public class PermissionAspectException extends Exception{
    private String[] mPermissions;

    public PermissionAspectException(String[] mPermissions) {
        this.mPermissions = mPermissions;
    }

    public String[] getmPermissions() {
        return mPermissions;
    }
}
