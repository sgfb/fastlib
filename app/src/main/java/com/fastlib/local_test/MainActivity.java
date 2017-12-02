package com.fastlib.local_test;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fastlib.R;
import com.fastlib.adapter.CommonFragmentViewPagerAdapter;
import com.fastlib.annotation.Bind;
import com.fastlib.annotation.ContentView;
import com.fastlib.app.FastActivity;
import com.fastlib.app.task.Action;
import com.fastlib.app.task.NoReturnAction;
import com.fastlib.app.task.Task;
import com.fastlib.net.Request;
import com.fastlib.net.SimpleListener;
import com.fastlib.utils.N;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sgfb on 2017/9/21.
 */
@ContentView(R.layout.act_main2)
public class MainActivity extends FastActivity {

    @Override
    protected void alreadyPrepared() {

    }

    @Bind(R.id.bt)
    private void commit(View view) {

    }

    @Bind(R.id.bt2)
    private void commit2(View view) {

    }

    @Bind(android.R.id.content)
    private void commit3(){

    }
}