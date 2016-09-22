package com.fastlib.app;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import com.fastlib.R;

/**
 * Created by sgfb on 16/9/20.
 * 底部dialog，带动画
 */
public class BottomDialog extends DialogFragment{

    public BottomDialog getInstance(@LayoutRes int layoutId){
        BottomDialog dialog=new BottomDialog();
        Bundle bundle=new Bundle();
        bundle.putInt("id",layoutId);
        dialog.setArguments(bundle);
        return dialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        AlertDialog.Builder builder=new AlertDialog.Builder(getContext()).setView(getArguments().getInt("id"));
        Dialog dialog=builder.create();
        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawableResource(android.R.color.transparent);
        window.setGravity(Gravity.BOTTOM);
        window.getAttributes().windowAnimations= R.style.DialogStyle;
        return dialog;
    }
}
