package com.fastlib;

import android.widget.ImageView;

import com.fastlib.adapter.BaseRecyAdapter;
import com.fastlib.annotation.Bind;
import com.fastlib.annotation.ContentView;
import com.fastlib.base.CommonViewHolder;
import com.fastlib.url_image.request.Callback2ImageView;
import com.fastlib.url_image.request.ImageRequest;

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
        for(int i=0;i<100;i++)
            list.add("https://static.oschina.net/uploads/img/201901/31055503_3yCJ.png");
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
