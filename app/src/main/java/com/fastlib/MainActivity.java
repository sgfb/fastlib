package com.fastlib;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.JsonReader;
import android.util.JsonToken;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import com.bumptech.glide.Glide;
import com.fastlib.adapter.BindingAdapter2;
import com.fastlib.annotation.DatabaseInject;
import com.fastlib.annotation.ViewInject;
import com.fastlib.app.FastActivity;
import com.fastlib.base.OldViewHolder;
import com.fastlib.db.FastDatabase;
import com.fastlib.net.Listener;
import com.fastlib.net.NetQueue;
import com.fastlib.net.Request;
import com.fastlib.utils.FastJson;
import com.fastlib.utils.ImageUtil;
import com.fastlib.widget.Indicator;
import com.fastlib.widget.PercentIndicator;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by sgfb on 16/5/10.
 */
public class MainActivity extends FastActivity{

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @ViewInject(id=R.id.bt)
    public void onclick(View v){
        Bean b=new Bean();
        for(int i=0;i<10;i++){
            b.name=Integer.toString(i*1111);
            b.extraField=i;
            FastDatabase.getDefaultInstance().saveOrUpdate(b);
        }
    }

    @ViewInject(id=R.id.bt2)
    public void onclick2(View v){
        List<Bean> list=FastDatabase.getDefaultInstance().limit(5,2).getAll(Bean.class);
        if(list==null||list.size()<=0)
            System.out.println("data empty");
        for(Bean b:list)
            System.out.println(b);
    }

    @ViewInject(id=R.id.bt3)
    public void onclick3(View v){
        FastDatabase.getDefaultInstance().delete(Bean.class,"2");
    }
}