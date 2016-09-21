package com.fastlib;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by sgfb on 16/9/20.
 * 顶层循环进度视图,默认居中
 */
public class LoadingDialog extends DialogFragment{
    private int mShowingCount; //每个activity仅显示一个进度视图,当count等于0时关闭进度视图

    public LoadingDialog(){
        mShowingCount=0;
        setStyle(STYLE_NO_TITLE,0);
    }

    @Override
    public void onStart(){
        super.onStart();
        Window window = getDialog().getWindow();
        window.setLayout(WindowManager.LayoutParams.WRAP_CONTENT,getDialog().getWindow().getAttributes().height);
        window.setBackgroundDrawableResource(android.R.color.transparent);
        window.setGravity(Gravity.CENTER);
    }

    public static void show(AppCompatActivity activity){
        show(activity,false);
    }

    public static void show(AppCompatActivity activity,boolean cancelable){
        String tag=activity.getClass().getCanonicalName();
        LoadingDialog loading=new LoadingDialog();
        if(loading.mShowingCount==0){
            loading.mShowingCount++;
            loading.setCancelable(cancelable);
            loading.show(activity.getSupportFragmentManager(),tag);
        }
        else
            loading.mShowingCount++;
    }

    /**
     * 只有在当前activity的showCount等于0时才关闭进度视图
     * @param activity
     */
    public static void dismiss(AppCompatActivity activity){
        String tag=activity.getClass().getCanonicalName();
        Fragment fragment=activity.getSupportFragmentManager().findFragmentByTag(tag);
        if(fragment instanceof LoadingDialog){
            LoadingDialog ld= (LoadingDialog) fragment;
            if(ld.mShowingCount<=1)
                ld.dismiss();
            else
                ld.mShowingCount--;
        }
    }

    /**
     * 无视计数，直接关闭进度视图
     * @param activity
     */
    public static void dismissNow(AppCompatActivity activity){
        String tag=activity.getClass().getCanonicalName();
        Fragment fragment=activity.getSupportFragmentManager().findFragmentByTag(tag);
        if(fragment instanceof LoadingDialog)
            ((LoadingDialog)fragment).dismiss();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_loading,null);
    }
}
