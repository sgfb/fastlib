package com.fastlib;

import android.app.Activity;
import android.widget.ImageView;

import com.fastlib.adapter.BaseRecyAdapter;
import com.fastlib.adapter.CommonBaseAdapter;
import com.fastlib.annotation.ContentView;
import com.fastlib.base.CommonViewHolder;
import com.fastlib.url_image.request.RequestFactory;

import java.util.List;

/**
 * Created by Administrator on 2018/5/18.
 */
@ContentView(R.layout.item)
public class MyAdapter2 extends BaseRecyAdapter<String>{

    public MyAdapter2(List<String> initList) {
        super(initList);
    }

    @Override
    public void binding(int position, String data, CommonViewHolder holder) {
        System.out.println(position);
        RequestFactory.host(holder.getConvertView().getContext())
                .byUrl(data)
                .setImageView((ImageView) holder.getView(R.id.image))
//                .setCompressInMemory(true)
                .start();
    }
}
