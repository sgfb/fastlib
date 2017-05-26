package com.fastlib;

import android.widget.ImageView;

import com.fastlib.base.OldViewHolder;

/**
 * Created by sgfb on 17/5/22.
 */
public class ContentDv implements MultiAdapter.ItemMVC<Integer> {
    private int mImageId;

    public ContentDv(int imageId) {
        mImageId = imageId;
    }

    @Override
    public int getType() {
        return 1;
    }

    public int getLayoutId() {
        return R.layout.item;
    }

    @Override
    public Integer getData() {
        return mImageId;
    }

    @Override
    public void controlDataToView(int position, int type, OldViewHolder holder) {
        ((ImageView)holder.getView(R.id.image)).setImageResource(mImageId);
    }
}