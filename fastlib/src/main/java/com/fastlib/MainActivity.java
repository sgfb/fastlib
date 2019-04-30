package com.fastlib;

import android.support.v4.view.ViewPager;

import com.fastlib.annotation.Bind;
import com.fastlib.annotation.ContentView;
import com.fastlib.app.module.FastActivity;
import com.fastlib.db.FastDatabase;
import com.fastlib.image_manager.ImageManager;
import com.fastlib.image_manager.bean.ImageConfig;
import com.fastlib.net.DefaultDownload;
import com.fastlib.net.NetManager;
import com.fastlib.net.Request;
import com.fastlib.net.listener.SimpleListener;
import com.fastlib.utils.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@ContentView(R.layout.act_main)
public class MainActivity extends FastActivity {
    @Bind(R.id.banner)
    Banner mBanner;

    @Bind(R.id.bt)
    private void startServer(){
        Request request=new Request("get","https://ss0.bdstatic.com/70cFvHSh_Q1YnxGkpoWK1HF6hhy/it/u=2726102536,2091908784&fm=26&gp=0.jpg");
        request.setCallbackByWorkThread(true).setCustomRootAddress("");
        File file=new File(ImageManager.getInstance().getConfig().mSaveFolder, Utils.getMd5("https://ss0.bdstatic.com/70cFvHSh_Q1YnxGkpoWK1HF6hhy/it/u=2726102536,2091908784&fm=26&gp=0.jpg",false));
        request.setDownloadable(new DefaultDownload(file).setDownloadSegment(true));
        request.setListener(new SimpleListener<String>(){

            @Override
            public void onResponseListener(Request r, String result) {
                System.out.println(result);
            }
        });
        request.start();
        String.format("");
    }

    @Bind(R.id.bt2)
    private void bt2(){
        new FaceDetectUd().startDetect(this);
    }

    @Override
    public void alreadyPrepared() {
        ImageConfig config=ImageManager.getInstance().getConfig();
        config.mSaveFolder=new File(getExternalCacheDir(),"image");
        config.mSaveFolder.mkdir();
        ImageManager.getInstance().setConfig(config);

        List<String> list=new ArrayList<>();
        list.add("https://ss0.bdstatic.com/70cFvHSh_Q1YnxGkpoWK1HF6hhy/it/u=2726102536,2091908784&fm=26&gp=0.jpg");
        list.add("https://ss0.bdstatic.com/70cFuHSh_Q1YnxGkpoWK1HF6hhy/it/u=3134892362,855082142&fm=26&gp=0.jpg");
        list.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1555909927491&di=4af0e5d6687b73ffe8f4d7d0e32d5710&imgtype=0&src=http%3A%2F%2Fimg.9553.com%2Fuploadfile%2F2016%2F0328%2F20160328021250785.jpg");
        mBanner.setData(list);
        mBanner.setInfinite(true);
        mBanner.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener(){
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                System.out.println("position:"+position);
            }
        });
    }
}