package com.fastlib.local_test;

import android.content.Context;

import com.fastlib.R;
import com.fastlib.adapter.FastAdapterForRecycler;
import com.fastlib.base.CommonViewHolder;

import java.util.List;

/**
 * Created by sgfb on 17/10/17.
 */
public class MyAapter3 extends FastAdapterForRecycler<String>{

    public MyAapter3(Context context, List<String> data) {
        super(context, R.layout.item5, data);
    }

    @Override
    public void binding(int position, String data, CommonViewHolder holder){

    }
}