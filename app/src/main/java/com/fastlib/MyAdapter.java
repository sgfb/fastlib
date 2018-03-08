package com.fastlib;

import android.view.View;
import android.widget.ImageView;

import com.fastlib.adapter.FastAdapter;
import com.fastlib.annotation.ContentView;
import com.fastlib.base.CommonViewHolder;
import com.fastlib.base.OldViewHolder;
import com.fastlib.base.ViewTagReuse;

import java.util.List;

/**
 * Created by sgfb on 18/3/7.
 */
@ContentView(R.layout.item)
public class MyAdapter extends FastAdapter<String>{

    public MyAdapter(List<String> initData) {
        super(initData);
    }

    @Override
    public void binding(int position, String data, final OldViewHolder holder) {
        holder.setText(R.id.text,data);
        ((ImageView)holder.getView(R.id.image)).setImageResource(R.mipmap.ic_launcher);
        holder.setOnClickListener(R.id.image, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println(holder.getConvertView().getTag());
            }
        });
        holder.useViewTagCache(new ViewTagReuse<String>(){

            @Override
            public String reuse(String tag){
                if(tag==null)
                    return "sgfb";
                else return tag;
            }
        });
    }
}