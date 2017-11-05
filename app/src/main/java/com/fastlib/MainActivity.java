package com.fastlib;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;
import android.widget.ListView;

import com.fastlib.annotation.Bind;
import com.fastlib.annotation.ContentView;
import com.fastlib.app.FastActivity;

@ContentView(R.layout.act_main)
public class MainActivity extends FastActivity{
    @Bind(R.id.list)
    ListView mList;

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