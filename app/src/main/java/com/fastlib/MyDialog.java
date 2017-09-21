package com.fastlib;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.WindowManager;

/**
 * Created by sgfb on 17/9/14.
 */
public class MyDialog extends DialogFragment{

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
        builder.setView(R.layout.act_main);
        Dialog dialog=builder.show();
        WindowManager.LayoutParams lp=dialog.getWindow().getAttributes();
        if(lp==null) {
            lp=new WindowManager.LayoutParams();
            lp.width=WindowManager.LayoutParams.WRAP_CONTENT;
            lp.height=WindowManager.LayoutParams.MATCH_PARENT;
        }
        else lp.height=WindowManager.LayoutParams.MATCH_PARENT;
        dialog.getWindow().setAttributes(lp);
        return dialog;
    }
}
