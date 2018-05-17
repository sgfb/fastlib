package com.fastlib;

import android.widget.ImageView;

import com.fastlib.adapter.FastAdapter;
import com.fastlib.annotation.ContentView;
import com.fastlib.base.OldViewHolder;
import com.fastlib.url_image.FastImage;
import com.fastlib.url_image.request.BitmapRequestEntrance;

/**
 * Created by Administrator on 2018/5/17.
 */
@ContentView(R.layout.item)
public class MyAdapter extends FastAdapter<String>{

    @Override
    public void binding(int position, String data, OldViewHolder holder) {
        ImageView image=holder.getView(R.id.image);

        FastImage.getInstance().startRequest(
                BitmapRequestEntrance.factory(holder.getConvertView().getContext())
                        .bitmapRequestByUrl(data)
                        .setImageView(image)
        );
    }
}
