package com.fastlib;

import com.fastlib.base.OldViewHolder;

/**
 * Created by sgfb on 17/5/22.
 */
public class TitleDv implements MultiAdapter.ItemMVC<String> {
    private String mData;

    public TitleDv(String data) {
        mData = data;
    }

    @Override
    public int getType() {
        return 0;
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
