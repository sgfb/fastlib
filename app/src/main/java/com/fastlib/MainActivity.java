package com.fastlib;

import android.app.ActionBar;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.JsonReader;
import android.util.JsonToken;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.fastlib.app.FastDialog;
import com.fastlib.db.FastDatabase;
import com.fastlib.net.Downloadable;
import com.fastlib.net.Listener;
import com.fastlib.net.NetQueue;
import com.fastlib.net.Request;
import com.fastlib.net.Result;
import com.fastlib.test.ImageCache;
import com.fastlib.utils.BindingView;
import com.fastlib.utils.ImageUtil;
import com.fastlib.utils.ScreenUtils;
import com.fastlib.widget.PercentView;
import com.fastlib.widget.RecycleListView;
import com.fastlib.widget.RoundImageView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sgfb on 16/5/10.
 *
 */
public class MainActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button bt=(Button)findViewById(R.id.bt);
        bt.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                File f=new File(Environment.getExternalStorageDirectory()+File.separator+"whatthefuck");
                try {
                    f.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                ImageUtil.openCamera(MainActivity.this);
            }
        });
//        RecyclerView list=(RecyclerView)findViewById(R.id.list);
//        list.setLayoutManager(new LinearLayoutManager(this));
//        list.setItemAnimator(new DefaultItemAnimator());
//        list.setAdapter(new MyAdapter());
//        ItemTouchHelper ith=new ItemTouchHelper(new MyCallBack(ItemTouchHelper.UP|ItemTouchHelper.DOWN,ItemTouchHelper.LEFT|ItemTouchHelper.RIGHT));
//        ith.attachToRecyclerView(list);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri uri=ImageUtil.getImageFromActive(requestCode,resultCode,data);
        String path=ImageUtil.getImagePath(this,uri);
        File f=new File(path);
        System.out.println(path+" length:"+f.length());
    }

    class MyAdapter extends RecyclerView.Adapter<Holder>{

        @Override
        public Holder onCreateViewHolder(ViewGroup parent, int viewType){
            TextView tv=new TextView(MainActivity.this);
            tv.setText("hello,world");
            tv.setPadding(40,40,40,40);
            return new Holder(tv);
        }

        @Override
        public void onBindViewHolder(Holder holder, int position) {

        }

        @Override
        public int getItemCount() {
            return 20;
        }
    }

    class Holder extends RecyclerView.ViewHolder{

        public Holder(View itemView){
            super(itemView);
        }
    }

    class MyCallBack extends ItemTouchHelper.SimpleCallback{

        public MyCallBack(int dragDirs, int swipeDirs) {
            super(dragDirs, swipeDirs);
        }

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target){
            System.out.println("onMove:"+viewHolder.getAdapterPosition()+","+target.getAdapterPosition());
            return true;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction){
            System.out.println("onSwiped:"+direction);
//            mAdapter.notifyItemRemoved(viewHolder.getAdapterPosition());
        }

        @Override
        public boolean isLongPressDragEnabled() {
            return super.isLongPressDragEnabled();
        }

        @Override
        public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive){
            if(actionState!=ItemTouchHelper.ACTION_STATE_IDLE){
                final float alpha=1-Math.abs(dX)/ ScreenUtils.getScreenWidth(MainActivity.this);
                viewHolder.itemView.setAlpha(alpha);
            }
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    }
}