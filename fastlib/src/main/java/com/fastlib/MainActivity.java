package com.fastlib;

import android.graphics.BitmapFactory;
import android.widget.ImageView;

import com.fastlib.annotation.Bind;
import com.fastlib.annotation.ContentView;
import com.fastlib.app.FastActivity;
import com.fastlib.utils.ImageUtil;

@ContentView(R.layout.act_main)
public class MainActivity extends FastActivity {
    @Bind(R.id.image)
    ImageView mImage;

    @Override
    protected void alreadyPrepared(){

    }
}