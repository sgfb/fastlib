package com.fastlib;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.media.AudioAttributes;
import android.renderscript.Float2;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.graphics.drawable.DrawableUtils;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.fastlib.app.FastDialog;
import com.fastlib.base.ActivityPreviewImage;
import com.fastlib.base.RoundDrawable;
import com.fastlib.db.DataDelegater;
import com.fastlib.db.FastDatabase;
import com.fastlib.interf.AdapterViewState;
import com.fastlib.interf.Delayable;
import com.fastlib.net.Listener;
import com.fastlib.net.Result;
import com.fastlib.test.AutoIncrement;
import com.fastlib.test.TestBean;
import com.fastlib.widget.FastPopupWindow;
import com.fastlib.widget.FastTab;
import com.fastlib.widget.RecycleListView;
import com.fastlib.widget.RoundButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import junit.framework.Test;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity{
    private final int DATABASE_VERSION=3;
    private boolean isAsc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FastDatabase.getInstance().getConfig().setVersion(DATABASE_VERSION);
        Button bt=(Button)findViewById(R.id.bt);

        bt.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                List<AutoIncrement> list;
                if(isAsc){
                    list=FastDatabase.getInstance().orderBy(true,"id").getAll(AutoIncrement.class);
                    isAsc=false;
                }
                else{
                    list=FastDatabase.getInstance().orderBy(false,"id").getAll(AutoIncrement.class);
                    isAsc=true;
                }
                System.out.println("location");
            }
        });
    }
}