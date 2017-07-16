package com.fastlib;

import android.app.ActivityManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.gesture.Gesture;
import android.hardware.Camera;
import android.hardware.SensorManager;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.net.Uri;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.UserManager;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.util.Pair;
import android.text.TextUtils;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.Surface;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ListView;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.widget.TextView;

import com.fastlib.annotation.Bind;
import com.fastlib.annotation.ContentView;
import com.fastlib.annotation.Event;
import com.fastlib.app.EventObserver;
import com.fastlib.app.FastActivity;
import com.fastlib.app.LoadingDialog;
import com.fastlib.app.PhotoResultListener;
import com.fastlib.base.OldViewHolder;
import com.fastlib.db.And;
import com.fastlib.db.Condition;
import com.fastlib.db.FastDatabase;
import com.fastlib.db.FunctionCommand;
import com.fastlib.db.FunctionType;
import com.fastlib.net.Request;
import com.fastlib.net.SimpleListener;
import com.fastlib.net.SimpleMockProcessor;
import com.fastlib.temp.Bean;
import com.fastlib.utils.ImageUtil;
import com.fastlib.utils.N;
import com.fastlib.utils.TimeUtil;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by sgfb on 17/6/26.
 */
@ContentView(R.layout.act_main_2)
public class SecondActivity extends FastActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void alreadyPrepared(){

    }

    @Bind(R.id.bt1)
    private void commit(View v){
        LoadingDialog.show(this);
        v.postDelayed(new Runnable() {
            @Override
            public void run() {
                LoadingDialog.dismissNow(SecondActivity.this);
            }
        },5000);
    }

    @Bind(R.id.bt2)
    private void commit2(final View v){
        final LoadingDialog loading=new LoadingDialog();
        loading.show(getSupportFragmentManager(),"loading");
        loading.setHint("just test");
        v.postDelayed(new Runnable() {
            @Override
            public void run() {
                loading.dismiss();
            }
        },2000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}