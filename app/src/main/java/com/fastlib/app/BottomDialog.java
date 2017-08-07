package com.fastlib.app;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.fastlib.R;

/**
 * Created by sgfb on 16/9/20.
 * 底部dialog，带动画
 */
public abstract class BottomDialog extends DialogFragment{
    public static final String ARG_INT_LAYOUT_ID ="layoutId"; //必传的布局id

    /**
     * 绑定视图
     * @param v
     */
    protected abstract void bindView(View v);

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        View view=LayoutInflater.from(getContext()).inflate(getArguments().getInt(ARG_INT_LAYOUT_ID),null);
        AlertDialog.Builder builder=new AlertDialog.Builder(getContext()).setView(view);
        Dialog dialog=builder.create();
        bindView(view);

        Window window=dialog.getWindow();
        WindowManager.LayoutParams attr=new WindowManager.LayoutParams();

        attr.width=WindowManager.LayoutParams.MATCH_PARENT;
        attr.height=WindowManager.LayoutParams.WRAP_CONTENT;
        attr.gravity=Gravity.BOTTOM;
        attr.windowAnimations=R.style.BottomDialogStyle;
        window.setBackgroundDrawableResource(android.R.color.transparent);
        window.setAttributes(attr);
        return dialog;
    }
}