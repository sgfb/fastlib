package com.fastlib;

import android.Manifest;
import android.os.Environment;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.fastlib.annotation.Bind;
import com.fastlib.annotation.ContentView;
import com.fastlib.app.module.FastActivity;
import com.fastlib.db.FastDatabase;
import com.fastlib.db.SaveUtil;
import com.fastlib.net.Request;
import com.fastlib.net.listener.SimpleListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.IOException;
import java.util.List;

@ContentView(R.layout.act_main)
public class MainActivity extends FastActivity{

    @Override
    public void alreadyPrepared(){

    }

    @Bind(R.id.bt)
    private void bt(){
        Gson gson=new Gson();
        try {
            List<MyTemplate> list=gson.fromJson(new String(SaveUtil.loadAssetsFile(getAssets(),"response.json")),new TypeToken<List<MyTemplate>>(){}.getType());
            for(MyTemplate template:list)
                System.out.println(template.categoryId);
            FastDatabase.getDefaultInstance(this).saveOrUpdate(list);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Bind(R.id.bt2)
    private void bt2(){
        List<MyTemplate> list=FastDatabase.getDefaultInstance(this).get(MyTemplate.class);
        if(list!=null)
        for(MyTemplate template:list)
            System.out.println(template.categoryId);
    }
}