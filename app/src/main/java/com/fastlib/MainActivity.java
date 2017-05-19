package com.fastlib;

import android.os.Bundle;
import android.os.Environment;
import android.os.PersistableBundle;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
import com.fastlib.test.jsonAdvanced.Bean;
import com.fastlib.utils.ImageUtil;
import com.fastlib.utils.TimeUtil;
import com.fastlib.widget.TitleBarWithProgress;

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
    private void commit(View v){
        net(Request.obtain("get","http://www.baidu.com").setListener(new SimpleListener<Bean>(){
            @Override
            public void onResponseListener(Request r,Bean result){
                System.out.println("result:"+result);
            }
        }));
    }

    @Bind(R.id.bt2)
    private void commit2(){

    }

    @Bind(R.id.bt3)
    private void commit3(View view){

    }

    @Override
    protected void alreadyPrepared() {

    }
}