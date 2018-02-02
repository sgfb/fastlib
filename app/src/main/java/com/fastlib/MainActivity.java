package com.fastlib;

import android.os.Environment;
import android.view.View;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.EditText;
import android.widget.ImageView;

import com.fastlib.annotation.Bind;
import com.fastlib.annotation.ContentView;
import com.fastlib.app.FastActivity;
import com.fastlib.net.Request;
import com.fastlib.net.SimpleListener;
import com.fastlib.test.UrlImage.request.BitmapRequest;
import com.fastlib.test.UrlImage.FastImage;
import com.fastlib.test.UrlImage.FastImageConfig;
import com.fastlib.test.UrlImage.request.BitmapRequestEntrance;
import com.fastlib.widget.AutofitGridView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@ContentView(R.layout.act_main)
public class MainActivity extends FastActivity{
    @Bind(R.id.image)
    ImageView mImage;
    @Bind(R.id.image2)
    ImageView mImage2;
    @Bind(R.id.image3)
    ImageView mImage3;
    @Bind(R.id.duration)
    EditText mDuration;

    @Override
    protected void alreadyPrepared(){
        FastImageConfig config=FastImage.getInstance().getConfig();
        config.mSaveFolder=getExternalCacheDir();
        FastImage.getInstance().setConfig(config);
    }

    @Bind(R.id.bt)
    private void commit(){
        Request request=new Request("http://www.wiboson.com/fz/api/upload");
        request.put("token","f4f75049e13346178f2ea7f9b225955b");
        request.put("file",new File(Environment.getExternalStorageDirectory(),"temp.jpg"));
        request.setListener(new SimpleListener<String>(){

            @Override
            public void onResponseListener(Request r, String result) {
                System.out.println("result:"+result);
            }
        });
        net(request);
//        BitmapRequest br=BitmapRequestEntrance.factory(this)
//                .bitmapRequestByUrl("https://static.oschina.net/uploads/space/2018/0106/134811_VkaD_347223.jpg")
//                .setImageView(mImage)
//                .setStoreStrategy(FastImageConfig.STRATEGY_STORE_SAVE_DISK)
//                .setSpecifiedStoreFile(new File(Environment.getExternalStorageDirectory(),"temp.jpg"));
//        FastImage.getInstance().startRequest(br);
    }

    @Bind(R.id.bt2)
    private void commit2(){
        BitmapRequest br=BitmapRequestEntrance.factory(this)
                .bitmapRequestByUrl("https://static.oschina.net/uploads/space/2018/0106/134811_VkaD_347223.jpg")
                .setImageView(mImage2)
                .setRequestWidth(100)
                .setRequestHeight(100)
                .setSpecifiedStoreFile(new File(Environment.getExternalStorageDirectory(),"temp.jpg"));
        FastImage.getInstance().startRequest(br);
    }

    @Bind(R.id.bt3)
    private void commit3(){
        BitmapRequest br=BitmapRequestEntrance.factory(this)
                .bitmapRequestByUrl("http://c.hiphotos.baidu.com/image/pic/item/bd315c6034a85edfef0cf9e940540923dc547573.jpg")
                .setAnimator(new BitmapRequest.ViewAnimator() {
                    @Override
                    public void animator(View v) {
                        v.setScaleX(0);
                        v.setScaleY(0);
                        v.animate().scaleX(1).scaleY(1).setInterpolator(new OvershootInterpolator()).setDuration(Long.parseLong(mDuration.getText().toString()));
                    }
                })
                .setImageView(mImage3);
        FastImage.getInstance().startRequest(br);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FastImage.getInstance().clearMemory();
    }
}