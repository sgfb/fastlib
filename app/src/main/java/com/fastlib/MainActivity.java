package com.fastlib;

import android.os.Environment;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.fastlib.annotation.Bind;
import com.fastlib.annotation.ContentView;
import com.fastlib.app.FastActivity;
import com.fastlib.net.Request;
import com.fastlib.net.SimpleListener;
import com.fastlib.test.UrlImage.BitmapRequest;
import com.fastlib.test.UrlImage.FastImage;
import com.fastlib.test.UrlImage.FastImageConfig;
import com.fastlib.test.UrlImage.ImageProcessingManager;

import java.io.File;
import java.util.List;
import java.util.Map;

@ContentView(R.layout.act_main)
public class MainActivity extends FastActivity{
    @Bind(R.id.image)
    ImageView mImage;
    @Bind(R.id.image2)
    ImageView mImage2;
    @Bind(R.id.image3)
    ImageView mImage3;

    @Override
    protected void alreadyPrepared(){
        FastImageConfig config=FastImage.getInstance(this).getConfig();

        config.mSaveFolder=getExternalCacheDir();
        FastImage.getInstance(this).setConfig(config);
    }

    @Bind(R.id.bt)
    private void commit(){
        BitmapRequest request=new BitmapRequest();
        request.setUrl("https://static.oschina.net/uploads/space/2018/0106/134811_VkaD_347223.jpg");
        request.setSpecifiedStoreFile(new File(Environment.getExternalStorageDirectory(),"temp.jpg"));
        FastImage.getInstance(this).startRequest(this,request,mImage);
    }

    @Bind(R.id.bt2)
    private void commit2(){
        BitmapRequest request=new BitmapRequest();
        request.setUrl("https://static.oschina.net/uploads/space/2018/0106/134811_VkaD_347223.jpg");
        request.setSpecifiedStoreFile(new File(Environment.getExternalStorageDirectory(),"temp.jpg"));
        request.setRequestWidth(100);
        request.setRequestHeight(100);
        FastImage.getInstance(this).startRequest(this,request,mImage2);
    }

    @Bind(R.id.bt3)
    private void commit3(){
        BitmapRequest request=new BitmapRequest();
        request.setUrl("http://c.hiphotos.baidu.com/image/pic/item/bd315c6034a85edfef0cf9e940540923dc547573.jpg");
        FastImage.getInstance(this).startRequest(this,request,mImage3);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FastImage.getInstance(this).clearMemory();
    }
}