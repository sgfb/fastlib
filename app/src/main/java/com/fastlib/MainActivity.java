package com.fastlib;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.util.Pair;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.fastlib.annotation.Bind;
import com.fastlib.annotation.Database;
import com.fastlib.app.FastActivity;
import com.fastlib.app.TaskChain;
import com.fastlib.db.FastDatabase;
import com.fastlib.db.FilterCommand;
import com.fastlib.db.SaveUtil;
import com.fastlib.db.ServerCache;
import com.fastlib.net.Listener;
import com.fastlib.net.Request;
import com.fastlib.utils.N;
import com.fastlib.utils.ZipUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.zip.ZipInputStream;

/**
 * Created by sgfb on 16/12/29.
 */
public class MainActivity extends FastActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Bind(R.id.bt)
    public void commit1(View v){
        Random random=new Random();
        final List<Bean> list=new ArrayList<>();
        for(int i=0;i<15;i++){
            Bean b=new Bean();
            b.sex=random.nextBoolean()?"男":"女";
            b.name="no name";
            list.add(b);
        }
        startTasks(new TaskChain(new Runnable() {
            @Override
            public void run() {
                FastDatabase.getDefaultInstance(MainActivity.this).saveOrUpdate(list);
            }
        }).next(TaskChain.TYPE_THREAD_ON_MAIN, new Runnable() {
            @Override
            public void run() {
                N.showShort(MainActivity.this,"保存完毕");
            }
        }));
    }

    @Bind(R.id.bt2)
    public void commit2(View v){
        FastDatabase.getDefaultInstance(this).delete(Bean.class,"id",FilterCommand.biger("20"));
    }

    @Bind(R.id.bt3)
    public void commit3(View v){
        List<Bean> list=FastDatabase.getDefaultInstance(this).getAll(Bean.class);
        if(list!=null&&!list.isEmpty()){
            for(Bean b:list)
                System.out.println(b);
        }
        else
            System.out.println("list is empty");
    }
}