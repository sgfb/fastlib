package com.fastlib.app.module;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2018/7/25.
 * 模块跳转请求.主要中转模块跳转一些参数
 */
public class ModuleRequest{
    private boolean isPassGlobalControl;
    private int mContainLayoutId=-1;  //fragment用填充父id，如果不为-1则尝试将context转换为AppcompatActivity并且插入Fragment
    private String mModuleName;
    private String mGroup="";   //group一定不能为null
    private Class mModuleClass; /**优先级高于 {@link #mModuleName}*/
    private Bundle mParams=new Bundle();

    public ModuleRequest() {
    }

    public ModuleRequest(String moduleName) {
        mModuleName = moduleName;
    }

    public ModuleRequest(Class moduleClass){
        mModuleClass=moduleClass;
    }

    public Class getModuleClass(){
        return mModuleClass;
    }

    public ModuleRequest setModuleClass(Class moduleClass){
        mModuleClass=moduleClass;
        return this;
    }

    public String getModuleName() {
        return mModuleName;
    }

    public ModuleRequest setModuleName(String mModuleName) {
        this.mModuleName = mModuleName;
        return this;
    }

    public String getGroup() {
        return mGroup;
    }

    public ModuleRequest setGroup(String mGroup) {
        this.mGroup = mGroup;
        if(this.mGroup==null)
            this.mGroup="";
        return this;
    }

    public ModuleRequest setContainLayoutId(@IdRes int layoutId){
        mContainLayoutId=layoutId;
        return this;
    }

    public int getContainLayoutId(){
        return mContainLayoutId;
    }

    public ModuleRequest setIsPassGlobalControl(boolean isPassGlobalControl){
        this.isPassGlobalControl=isPassGlobalControl;
        return this;
    }

    public boolean isPassGlobalControl(){
        return isPassGlobalControl;
    }

    public String getPath(){
        return mModuleName+"$"+mGroup;
    }

    public ModuleRequest putInt(String key,int value){
        mParams.putInt(key,value);
        return this;
    }

    public ModuleRequest putLong(String key,long value){
        mParams.putLong(key,value);
        return this;
    }

    public ModuleRequest putFloat(String key,float value){
        mParams.putFloat(key,value);
        return this;
    }

    public ModuleRequest putDouble(String key,double value){
        mParams.putDouble(key,value);
        return this;
    }

    public ModuleRequest putString(String key,String value){
        mParams.putString(key,value);
        return this;
    }

    public ModuleRequest putSerializable(String key,Serializable value){
        mParams.putSerializable(key,value);
        return this;
    }

    public Bundle getParams(){
        return mParams;
    }

    public ModuleRequest start(Context context){
        ModuleLauncher.getInstance().start(context,this);
        return this;
    }

    public ModuleRequest startForResult(Activity activity,int requestCode){
        ModuleLauncher.getInstance().startForResult(activity,this,requestCode);
        return this;
    }

    @Override
    public String toString() {
        return "ModuleRequest{" +
                "isPassGlobalControl=" + isPassGlobalControl +
                ", mContainLayoutId=" + mContainLayoutId +
                ", mModuleName='" + mModuleName + '\'' +
                ", mGroup='" + mGroup + '\'' +
                ", mModuleClass=" + mModuleClass +
                ", mParams=" + mParams +
                '}';
    }
}