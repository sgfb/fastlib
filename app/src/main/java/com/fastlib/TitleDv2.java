package com.fastlib;

import com.fastlib.base.OldViewHolder;

/**
 * Created by sgfb on 17/5/22.
 */

public class TitleDv2 implements MultiAdapter.ItemMVC<String> {
    private String mData;

    public TitleDv2(String data) {
        mData = data;
    }

    @Override
    public int getType() {
        return 3;
    }

    public int getLayoutId() {
        return R.layout.test;
    }

    @Override
    public String getData() {
        return mData;
    }

    @Override
    public void controlDataToView(int position, int type, OldViewHolder holder) {
        holder.setText(R.id.text,mData);
    }

    public void setData(String data){
        mData=data;
    }
}
