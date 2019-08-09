package com.fastlib;

import android.Manifest;
import android.content.ContentProvider;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CallLog;
import android.provider.Telephony;

import com.fastlib.annotation.Bind;
import com.fastlib.annotation.ContentView;
import com.fastlib.app.module.FastActivity;

import java.util.Locale;

@ContentView(R.layout.act_main)
public class MainActivity extends FastActivity {

    @Bind(R.id.bt)
    private void startServer(){
        requestPermission(new String[]{Manifest.permission.READ_SMS}, new Runnable() {
            @Override
            public void run() {
                Cursor cursor=getContentResolver().query(Uri.parse("content://sms/"),null,null,null,null);

                if(cursor!=null){
                    while(cursor.moveToNext()){
                        for(int i=0;i<cursor.getColumnCount();i++) {
                            String columnName=cursor.getColumnName(i);
                            System.out.println(String.format(Locale.getDefault(),"%s:%s ",columnName,cursor.getString(i)));
                        }
                        cursor.moveToNext();
                    }
                    cursor.close();
                }
                else System.out.println("未有通话记录或无权限");
            }
        }, new Runnable() {
            @Override
            public void run() {
                System.out.println("未给予权限");
            }
        });
    }

    @Bind(R.id.bt2)
    private void bt2(){

    }


    @Override
    public void alreadyPrepared() {

    }
}