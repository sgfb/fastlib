package com.fastlib;

import com.fastlib.annotation.Bind;
import com.fastlib.annotation.ContentView;
import com.fastlib.app.FastActivity;
import com.fastlib.utils.zip.ZipFileEntity;

import java.io.IOException;

/**
 * Created by Administrator on 2018/5/18.
 */
@ContentView(R.layout.act_main)
public class LauncherActivity extends FastActivity{
    @Override
    protected void alreadyPrepared() {

    }

    @Bind(R.id.bt)
    private void startMainAct(){
        startActivity(MainActivity.class);
    }

    @Bind(R.id.bt2)
    private void commit2(){
        String s="just hello";
        try {
            byte[] compress= ZipFileEntity.memCompress(s.getBytes());
            System.out.println(new String(ZipFileEntity.memUncompress(compress)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
