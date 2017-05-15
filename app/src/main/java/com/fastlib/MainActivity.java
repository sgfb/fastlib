package com.fastlib;

import android.os.Bundle;
import android.os.Environment;
import android.os.PersistableBundle;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.fastlib.annotation.Bind;
import com.fastlib.annotation.ContentView;
import com.fastlib.app.EventObserver;
import com.fastlib.app.FastActivity;
import com.fastlib.db.And;
import com.fastlib.db.Condition;
import com.fastlib.db.FastDatabase;
import com.fastlib.net.DefaultDownload;
import com.fastlib.net.DefaultMockProcessor;
import com.fastlib.net.NetManager;
import com.fastlib.net.Request;
import com.fastlib.net.SimpleListener;
import com.fastlib.utils.TimeUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

/**
 * Created by sgfb on 17/5/10.
 */
@ContentView(R.layout.activity_main)
public class MainActivity extends FastActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState){
        EventObserver.build(this);
        super.onCreate(savedInstanceState);
    }

    @Bind(R.id.bt)
    private void commit(){

    }

    long count=0;

    @Bind(R.id.bt2)
    private void commit2(){
        count+=6000;
        System.out.println(TimeUtil.formatTimeLag(System.currentTimeMillis()-count));
    }

    @Bind(R.id.bt3)
    private void commit3(View view){
        count+=3600000;
        System.out.println(TimeUtil.formatTimeLag(System.currentTimeMillis()-count));
    }

    @Bind(R.id.bt4)
    private void commit4(View view){
        count+=100000000;
        System.out.println(TimeUtil.formatTimeLag(System.currentTimeMillis()-count));
    }

    @Override
    protected void alreadyPrepared() {
        NetManager.getInstance().setGlobalHead(Pair.create("head1","sgfb"),Pair.create("head2","123456"));
        NetManager.getInstance().setGlobalParams(Pair.create("params1","111"),Pair.create("params2","222"));
    }
}