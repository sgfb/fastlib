package com.fastlib.local_test;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;

import com.fastlib.R;
import com.fastlib.adapter.MultiAdapter;
import com.fastlib.annotation.Bind;
import com.fastlib.annotation.ContentView;
import com.fastlib.app.FastActivity;

/**
 * Created by sgfb on 2017/9/21.
 */
@ContentView(R.layout.act_main)
public class MainActivity extends FastActivity{
    @Bind(R.id.content)
    EditText mContent;
    @Bind(R.id.list)
    RecyclerView mList;

    @Override
    protected void alreadyPrepared(){
        mList.setAdapter(new MyAdapter3(this));
    }

    @Bind(R.id.bt)
    private void commit(){

    }

    @Bind(R.id.bt2)
    private void commit2(){

    }
}