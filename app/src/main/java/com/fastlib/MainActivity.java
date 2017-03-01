package com.fastlib;

import android.os.Environment;
import android.text.format.Formatter;
import android.view.View;
import android.widget.ImageView;

import com.fastlib.annotation.Bind;
import com.fastlib.annotation.ContentView;
import com.fastlib.app.FastActivity;
import com.fastlib.test.UrlImage.ImageLoader;

import java.io.File;

/**
 * Created by sgfb on 16/12/29.
 */
@ContentView(R.layout.activity_main)
public class MainActivity extends FastActivity{
    @Bind(R.id.image1)
    ImageView mImage1;
    @Bind(R.id.image2)
    ImageView mImage2;

    @Bind(R.id.bt)
    private void commit(View v){
        startActivity(SecondActivity.class);
    }

    @Bind(R.id.bt2)
    private void commit2(View v){
        ImageLoader.getInstance().clear();
    }

    @Bind(R.id.bt3)
    private void commit3(View v){
        System.out.println(Formatter.formatFileSize(this,ImageLoader.getInstance().useMemory())+" "+Formatter.formatFileSize(this,ImageLoader.getConfig().mMaxCacheSize));
    }

    @Override
    protected void alreadyPrepared(){
        ImageLoader.build(this);
        ImageLoader.getInstance().setRootDirectory(new File(Environment.getExternalStorageDirectory(),"AImage"));
    }
}