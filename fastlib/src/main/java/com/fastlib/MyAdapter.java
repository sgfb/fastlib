package com.fastlib;

import android.widget.ImageView;

import com.fastlib.adapter.BaseRecyAdapter;
import com.fastlib.annotation.ContentView;
import com.fastlib.base.CommonViewHolder;
import com.fastlib.image_manager.request.Callback2ImageView;
import com.fastlib.image_manager.request.ImageRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sgfb on 19/2/14.
 * E-mail: 602687446@qq.com
 */
@ContentView(R.layout.item_test)
public class MyAdapter extends BaseRecyAdapter<String>{

    public MyAdapter() {
        super();
        List<String> list=new ArrayList<>();
        for(int i=0;i<1000;i++)
            list.add("http://cn.best-wallpaper.net/wallpaper/3840x2160/1705/Earth-our-home-planet-space-black-background_3840x2160.jpg");
        setData(list);
    }

    @Override
    public void binding(int position, String data, CommonViewHolder holder) {
        ImageRequest.create(data)
                .bindOnHostLifeCycle(mContext)
                .setCallbackParcel(new Callback2ImageView((ImageView)holder.getView(R.id.image)))
                .start();
    }
}
