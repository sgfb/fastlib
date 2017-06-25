package com.fastlib;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.util.Pair;
import android.util.SparseArray;
import android.view.View;
import android.widget.Spinner;
import android.widget.TextView;

import com.fastlib.adapter.FastAdapter;
import com.fastlib.annotation.Bind;
import com.fastlib.annotation.ContentView;
import com.fastlib.app.EventObserver;
import com.fastlib.app.FastActivity;
import com.fastlib.base.OldViewHolder;
import com.fastlib.db.And;
import com.fastlib.db.Condition;
import com.fastlib.db.FastDatabase;
import com.fastlib.net.Request;
import com.fastlib.net.ResponseStatus;
import com.fastlib.net.SimpleListener;
import com.fastlib.utils.N;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by sgfb on 17/5/10.
 */
@ContentView(R.layout.act_main)
public class MainActivity extends FastActivity{
    public static final int REQ_ENABLE_BLUETOOTH=2;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        EventObserver.build(this);
        super.onCreate(savedInstanceState);
    }
    @Bind(R.id.bt)
    private void commit(TextView v){
        BluetoothAdapter ba=BluetoothAdapter.getDefaultAdapter();
        if(ba!=null){
            if(ba.isEnabled()){
                useBlueTooth();
            }
            else{
                Intent intent=new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(intent,REQ_ENABLE_BLUETOOTH);
            }
        }
        else{
            N.showShort(this,"你没有蓝牙设备");
        }
    }

    private void useBlueTooth(){
        BluetoothAdapter ba=BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> set=ba.getBondedDevices();
        if(set.size()>0){
            for(BluetoothDevice bd:set){
                N.showLong(this,bd.getName()+","+bd.getAddress());
            }
        }
        else System.out.println("no bonded device");
    }

    @Bind(R.id.bt2)
    private void commit2(){

    }

    @Bind(R.id.bt3)
    private void commit3(View view){

    }

    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if(requestCode==REQ_ENABLE_BLUETOOTH){
            if(resultCode==RESULT_OK){
                useBlueTooth();
            }
            else if(resultCode==RESULT_CANCELED){
                N.showShort(this,"用户取消了蓝牙设置或者其他异常导致蓝牙打开失败");
            }
        }
    }

    @Override
    protected void alreadyPrepared(){

    }
}