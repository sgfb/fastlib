package com.fastlib;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;

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

    @Override
    protected void alreadyPrepared(){

    }

    @Bind(R.id.bt)
    private void commit(){

    }

    @Bind(R.id.bt2)
    private void commit2(){

    }
}