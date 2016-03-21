package com.fastlib.app;

import android.app.Dialog;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.fastlib.R;

import java.util.List;

/**
 * Dialog集合
 */
public class FastDialog {

    private FastDialog(){}

    /**
     * 普通文本dialog，有确定监听
     *
     * @param activity
     * @param message
     * @param l
     */
    public static void showMessageDialog(final AppCompatActivity activity,final String message,final OnClickListener l){
        showMessageDialog(activity, message, l, true);
    }

    /**
     * 普通文本dialog，显示取消按键，有监听
     * @param activity
     * @param message
     * @param l
     * @param displayCancel
     */
    public static void showMessageDialog(final AppCompatActivity activity, final String message,final OnClickListener l,
                                         final boolean displayCancel){
        DialogFragment dialog=new DialogFragment(){

            @NonNull
            @Override
            public Dialog onCreateDialog(Bundle savedInstanceState){
                AlertDialog dialog;
                AlertDialog.Builder builder=new AlertDialog.Builder(activity)
                        .setMessage(message)
                        .setPositiveButton("确定",l);

                if(displayCancel)
                    builder.setNegativeButton("取消",null);
                dialog=builder.create();
                return dialog;
            }
        };
        dialog.show(activity.getSupportFragmentManager(), null);
    }

    /**
     * 列表Dialog
     *
     * @param activity
     * @param items
     * @param l
     */
    public static void showListDialog(final AppCompatActivity activity,final String[] items,final OnClickListener l){
        DialogFragment dialog=new DialogFragment(){

            @NonNull
            @Override
            public Dialog onCreateDialog(Bundle savedInstanceState){
                ListView lv=new ListView(activity);
                ArrayAdapter<String> adapter=new ArrayAdapter<>(activity,android.R.layout.simple_list_item_1,items);
                lv.setAdapter(adapter);
                final AlertDialog dialog=new AlertDialog
                        .Builder(activity)
                        .setView(lv).create();
                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if(l!=null)
                            l.onClick(dialog,position);
                        dialog.dismiss();
                    }
                });
                return dialog;
            }
        };
        dialog.show(activity.getSupportFragmentManager(),null);
    }

    /**
     * 列表dialog
     * @param activity
     * @param items
     * @param l
     */
    public static void showListDialog(AppCompatActivity activity,final List<String> items,OnClickListener l){
        showListDialog(activity,items.toArray(new String[]{}),l);
    }
}