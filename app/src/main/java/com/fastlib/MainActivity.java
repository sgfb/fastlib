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
import com.fastlib.annotation.ViewInject;
import com.fastlib.app.FastActivity;
import com.fastlib.base.OldViewHolder;
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
    String path;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final AutofitGridView gridview=(AutofitGridView)findViewById(R.id.autoGridView);
        List<String> list=new ArrayList<>();
        for(int i=0;i<10;i++)
            list.add(Integer.toString(i*111));
        gridview.addString(android.R.layout.simple_list_item_1,list);
        gridview.setOnItemListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                View v=gridview.findViewByPosition(position);
                if(v!=null)
                    System.out.println(v.getTag());
                else
                    System.out.println("v is null");
            }
        });
//        final MyBanner banner=(MyBanner)findViewById(R.id.banner);
//        final PercentIndicator indicato=(PercentIndicator)findViewById(R.id.indicator);
//        List<Object> list=new ArrayList<>();
//        banner.setInfinite(true);
//        for(int i=0;i<3;i++)
//            list.add("http://img3.imgtn.bdimg.com/it/u=2477016780,243579597&fm=21&gp=0.jpg");
//        banner.setData(list);
//        indicato.setItemCount(list.size());
//        banner.addOnPageChangeListener(new ViewPager.OnPageChangeListener(){
//            @Override
//            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//                indicato.setPercent(position,positionOffset);
//            }
//
//            @Override
//            public void onPageSelected(int position) {
//                if(position==0) banner.setCurrentItem(banner.getAdapter().getCount(), false);
//                else if(position==banner.getAdapter().getCount()) banner.setCurrentItem(0,false);
//            }
//
//            @Override
//            public void onPageScrollStateChanged(int state) {
//
//            }
//        });
    }

//    @Override
//    protected void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//        outState.putSerializable("photo",path);
//    }
//
//    @Override
//    protected void onRestoreInstanceState(Bundle savedInstanceState) {
//        super.onRestoreInstanceState(savedInstanceState);
//        path=savedInstanceState.getString("photo");
//

    @Override
    public void onActivityResult(int requestCode,int resultCode,Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if(TextUtils.isEmpty(path))
            System.out.println(requestCode+","+resultCode+","+data);
        else
            System.out.println(requestCode+","+resultCode+","+Uri.fromFile(new File(path)));
    }
}