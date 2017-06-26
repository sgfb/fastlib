package com.fastlib.temp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.util.Pair;
import android.text.format.Formatter;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.fastlib.R;
import com.fastlib.adapter.FastAdapter;
import com.fastlib.annotation.Bind;
import com.fastlib.annotation.ContentView;
import com.fastlib.app.EventObserver;
import com.fastlib.app.FastActivity;
import com.fastlib.base.OldViewHolder;
import com.fastlib.utils.N;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by sgfb on 17/5/10.
 */
@ContentView(R.layout.act_main)
public class MainActivity extends FastActivity{
    public static final int REQ_ENABLE_BLUETOOTH=2; //蓝牙启动请求值
    public static final int REQ_FINDABLE_BLUETOOTH=3; //蓝牙可检测性请求值
    @Bind(R.id.list)
    ListView mList;
    @Bind(R.id.message)
    EditText mMessage;
    @Bind(R.id.receiveMessage)
    TextView mReceiveMessage;
    BluetoothChat mBluetoothChat;
    BluetoothChatClient mBluetoothChatClient;
    FastAdapter<Pair<Boolean,BluetoothDevice>> mAdapter=new FastAdapter<Pair<Boolean,BluetoothDevice>>(this,R.layout.item){
        @Override
        public void binding(int position,Pair<Boolean,BluetoothDevice> data, OldViewHolder holder) {
            holder.setText(R.id.name,data.second.getName());
            holder.setText(R.id.address,data.second.getAddress());
            holder.setText(R.id.history,data.first?"已配对":"可配对");
        }
    };
    BroadcastReceiver mBluetoothScanReceiver =new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent){
            if(BluetoothDevice.ACTION_FOUND.equals(intent.getAction())){
                BluetoothDevice bd=intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                mAdapter.addIfNotExist(Pair.create(false,bd));
            }
        }
    };
    BroadcastReceiver mBluetoothStatusReceiver=new BroadcastReceiver(){
        @Override
        public void onReceive(Context context, Intent intent){
            if(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED.equals(intent.getAction())){
                int oldStatus=intent.getIntExtra(BluetoothAdapter.EXTRA_PREVIOUS_SCAN_MODE,-1);
                int nowStatus=intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE,0);
                String oldStatusTxt;
                String nowStatusTxt="未知状态";
                if(BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE==oldStatus)
                    oldStatusTxt="启用并且可被检测";
                else if(BluetoothAdapter.SCAN_MODE_CONNECTABLE==oldStatus)
                    oldStatusTxt="启用但不可被检测";
                else if(BluetoothAdapter.SCAN_MODE_NONE==oldStatus)
                    oldStatusTxt="未启用";
                else oldStatusTxt="未知状态值"+oldStatus;
                if(BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE==nowStatus)
                    nowStatusTxt="启用并且可被检测";
                else if(BluetoothAdapter.SCAN_MODE_CONNECTABLE==nowStatus)
                    nowStatusTxt="启用但不可被检测";
                else if(BluetoothAdapter.SCAN_MODE_NONE==nowStatus)
                    nowStatusTxt="未启用";
                N.showShort(context,"扫描模式从 "+oldStatusTxt+"变化为 "+nowStatusTxt);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState){
        EventObserver.build(this);
        super.onCreate(savedInstanceState);
    }

    @Bind(R.id.sendMessage)
    private void sendMessage(){
        mThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                File file=new File(Environment.getExternalStorageDirectory(),mMessage.getText().toString());

                if(file.exists()){
                    try {
                        InputStream in=new FileInputStream(file);
                        byte[] buff=new byte[4096];
                        int len;
                        long lenCount=0;
                        long timer=System.currentTimeMillis();
                        while((len=in.read(buff))!=-1){
                            lenCount+=len;
                            if(mBluetoothChat!=null)
                                mBluetoothChat.sendMessage(buff,0,len);
                            else if(mBluetoothChatClient!=null)
                                mBluetoothChatClient.sendMessage(buff,0,len);
                            if(System.currentTimeMillis()-timer>1000){
                                final long finalLenCount = lenCount;
                                lenCount=0;
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mReceiveMessage.setText(Formatter.formatFileSize(MainActivity.this,finalLenCount));
                                    }
                                });
                                timer=System.currentTimeMillis();
                            }
                        }
                        in.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else N.showLong(MainActivity.this,"文件不存在");
            }
        });
    }

    @Bind(R.id.bt)
    private void commit(TextView v){
        final BluetoothAdapter ba=BluetoothAdapter.getDefaultAdapter();
        if(ba==null){
            N.showShort(this,"你没有蓝牙设备");
            return;
        }
        if(!ba.startDiscovery()){
            N.showShort(this,"蓝牙扫描异常");
            return;
        }
        v.postDelayed(new Runnable() {
            @Override
            public void run() {
                ba.cancelDiscovery();
            }
        },8000);
    }

    @Bind(R.id.bt2)
    private void commit2(View v){
        Intent intent=new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        intent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION,80);
        startActivityForResult(intent,REQ_FINDABLE_BLUETOOTH);
    }

    @Bind(R.id.bt3)
    private void commit3(View view){
        if(mBluetoothChat==null){
            mBluetoothChat=new BluetoothChat();
            mBluetoothChat.setListener(new BluetoothChat.BluetoothInputListener() {
                @Override
                public void dataResult(final InputStream data) {
                    mThreadPool.execute(new Runnable() {
                        @Override
                        public void run(){
                            final byte[] buff=new byte[4096];
                            int len;
                            File file=new File(Environment.getExternalStorageDirectory(),"newFile");
                            try {
                                file.createNewFile();
                                OutputStream out=new FileOutputStream(file);
                                while((len=data.read(buff))!=-1){
                                    out.write(buff,0,len);
                                }
                                out.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            });
            mThreadPool.execute(mBluetoothChat);
        }
    }

    private void initBluetooth(){
        BluetoothAdapter ba=BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> set=ba.getBondedDevices();
        if(set.size()>0){
            List<Pair<Boolean,BluetoothDevice>> list=new ArrayList<>();
            for(BluetoothDevice bd:set)
                list.add(Pair.create(true,bd));
            mAdapter.setData(list);
        }
        else N.showShort(this,"no bonded device");
    }

    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if(requestCode==REQ_ENABLE_BLUETOOTH){
            if(resultCode==RESULT_OK){
                initBluetooth();
            }
            else if(resultCode==RESULT_CANCELED){
                N.showShort(this,"用户取消了蓝牙设置或者其他异常导致蓝牙打开失败");
            }
        }
        if(requestCode==REQ_FINDABLE_BLUETOOTH){
            if(resultCode==RESULT_CANCELED)
                N.showShort(this,"用户不同意开启蓝牙可检测性");
            else
                N.showShort(this,resultCode+"秒可被检测");
        }
    }

    @Override
    protected void alreadyPrepared(){
        mList.setAdapter(mAdapter);
        IntentFilter filter=new IntentFilter(BluetoothDevice.ACTION_FOUND);
        IntentFilter filter2=new IntentFilter(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
        registerReceiver(mBluetoothScanReceiver,filter);
        registerReceiver(mBluetoothStatusReceiver,filter2);
        BluetoothAdapter ba=BluetoothAdapter.getDefaultAdapter();
        if(ba!=null){
            if(ba.isEnabled()){
                initBluetooth();
            }
            else{
                Intent intent=new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(intent,REQ_ENABLE_BLUETOOTH);
            }
        }
        else{
            N.showShort(this,"你没有蓝牙设备");
        }
        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                final Pair<Boolean,BluetoothDevice> pair=mAdapter.getItem(position);
                if(mBluetoothChatClient==null){
                    mBluetoothChatClient=new BluetoothChatClient(pair.second);
                    mBluetoothChatClient.setListener(new BluetoothChat.BluetoothInputListener() {
                        @Override
                        public void dataResult(final InputStream data){
                            mThreadPool.execute(new Runnable() {
                                @Override
                                public void run() {
                                    final byte[] buff=new byte[4096];
                                    int len;
                                    File file=new File(Environment.getExternalStorageDirectory(),"newFile");
                                    try {
                                        file.createNewFile();
                                        OutputStream out=new FileOutputStream(file);
                                        while((len=data.read(buff))!=-1){
                                            out.write(buff,0,len);
                                        }
                                        out.close();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }
                    });
                    mThreadPool.execute(mBluetoothChatClient);
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBluetoothScanReceiver);
        unregisterReceiver(mBluetoothStatusReceiver);
    }
}