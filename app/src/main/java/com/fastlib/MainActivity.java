package com.fastlib;

import android.graphics.BitmapFactory;
import android.os.Environment;
import android.widget.ImageView;
import android.widget.ListView;

import com.fastlib.annotation.Bind;
import com.fastlib.annotation.ContentView;
import com.fastlib.annotation.Event;
import com.fastlib.app.FastActivity;
import com.fastlib.app.PhotoResultListener;
import com.fastlib.app.task.Action;
import com.fastlib.app.task.EmptyAction;
import com.fastlib.app.task.NetAction;
import com.fastlib.app.task.NoReturnAction;
import com.fastlib.app.task.Task;
import com.fastlib.app.task.ThreadType;
import com.fastlib.net.Request;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@ContentView(R.layout.act_main)
public class MainActivity extends FastActivity{
    @Bind(R.id.list)
    ListView mList;

    @Override
    protected void alreadyPrepared(){
        List<String> list=new ArrayList<>();
        for(int i=0;i<10;i++)
            list.add(Integer.toString(i));
        mList.setAdapter(new MyAdapter(list));
    }

    @Bind(R.id.bt)
    private void commit(){

    }

    @Bind(R.id.bt2)
    private void commit2(){

    }
}