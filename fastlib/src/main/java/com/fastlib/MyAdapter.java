package com.fastlib;

import android.app.Activity;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.fastlib.adapter.FastAdapter;
import com.fastlib.annotation.ContentView;
import com.fastlib.base.OldViewHolder;
import com.fastlib.url_image.request.ImageRequest;
import com.fastlib.url_image.request.RequestFactory;

/**
 * Created by Administrator on 2018/5/17.
 */
@ContentView(R.layout.item)
public class MyAdapter extends FastAdapter<String>{

    @Override
    public void binding(int position, String data, OldViewHolder holder) {
        System.out.println(position);
        ImageView image=holder.getView(R.id.image);

        RequestFactory.host((Activity) holder.getConvertView().getContext()).byUrl(data).setImageView(image).start();
    }
}