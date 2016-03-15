package com.fastlib;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
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
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.fastlib.app.FastDialog;
import com.fastlib.base.RoundDrawable;
import com.fastlib.db.FastDatabase;
import com.fastlib.interf.AdapterViewState;
import com.fastlib.interf.Delayable;
import com.fastlib.test.TestBean;
import com.fastlib.widget.FastPopupWindow;
import com.fastlib.widget.FastTab;
import com.fastlib.widget.RecycleListView;
import com.fastlib.widget.RoundButton;
import com.google.gson.Gson;

import junit.framework.Test;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final RoundButton bt=(RoundButton)findViewById(R.id.bt);

        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TestBean tb=new TestBean();
                tb.setData("hello");
                FastDatabase.getInstance().saveOrUpdate(tb);
                List<TestBean> tbs=FastDatabase.getInstance().getAll(TestBean.class);
                System.out.println("location");
            }
        });
    }
}