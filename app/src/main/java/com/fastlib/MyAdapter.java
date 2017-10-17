package com.fastlib;

import android.content.Context;
import android.graphics.Color;
import android.view.View;

import com.fastlib.adapter.FastAdapterForRecycler;
import com.fastlib.base.CommonViewHolder;

import java.util.List;

/**
 * Created by sgfb on 2017/10/14.
 */

public class MyAdapter extends FastAdapterForRecycler<MyAdapter.Bean>{

    public MyAdapter(Context context, List<Bean> data){
        super(context,R.layout.item, data);
    }

    @Override
    public void binding(int position, Bean data, CommonViewHolder holder){
        BrokenLineColumn brokenLineColumn=holder.getView(R.id.brokenLine);
        brokenLineColumn.setmRowCount(data.lineCount);
        brokenLineColumn.setmMax(data.max);
        brokenLineColumn.setmStartValue(data.value);
        brokenLineColumn.setmEndVale(data.value2);
        brokenLineColumn.setmBrokenLineColor(Color.YELLOW);
        brokenLineColumn.setmSemiCircleColor(Color.YELLOW);
        if(position==0) brokenLineColumn.isDrawFullLeftCircle=true;
        else if(position==getItemCount()-1) brokenLineColumn.isDrawFullRightCircle=true;
        else{
            brokenLineColumn.isDrawFullLeftCircle=false;
            brokenLineColumn.isDrawFullRightCircle=false;
        }
        brokenLineColumn.invalidate();
        holder.setText(R.id.text,data.title);
    }

    public static class Bean{
        public String title;
        public int lineCount;
        public int max;
        public int value;
        public int value2;
    }
}