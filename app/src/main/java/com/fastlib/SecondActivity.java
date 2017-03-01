package com.fastlib;

import android.view.View;
import android.widget.ImageView;

import com.fastlib.annotation.Bind;
import com.fastlib.annotation.ContentView;
import com.fastlib.app.FastActivity;
import com.fastlib.test.UrlImage.ImageLoader;

/**
 * Created by sgfb on 17/3/1.
 */
@ContentView(R.layout.activity_main)
public class SecondActivity extends FastActivity{
    @Bind(R.id.image1)
    ImageView mImage1;
    @Bind(R.id.image2)
    ImageView mImage2;

    @Bind(R.id.bt)
    private void commit(View v){
        ImageLoader.getInstance()
                .load("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1488347225470&di=d05530b06bb945d562936de4c06df357&imgtype=0&src=http%3A%2F%2Fimg1.2345.com%2Fduoteimg%2FzixunImg%2Flocal%2F2017%2F02%2F24%2F14879053082332.png")
                .into(mImage1);
    }

    @Bind(R.id.bt2)
    private void commit2(View v){
        ImageLoader.getInstance().load("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1488347256260&di=566657f53578ec2b0c90b242ee6166e6&imgtype=0&src=http%3A%2F%2Fwww.touxiang.cn%2Fuploads%2F20130725%2F25-011254_691.jpg").into(mImage2);
    }

    @Override
    protected void alreadyPrepared() {

    }
}
