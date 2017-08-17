package com.fastlib;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import com.fastlib.adapter.MultiAdapter;
import com.fastlib.base.OldViewHolder;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

/**
 * Created by sgfb on 17/8/15.
 */
public class MyAdapter extends MultiAdapter{

    public MyAdapter(Context context) {
        super(context);
    }

    public void addData(int type,String data){
        addData(type==0?new Item1(data):new Item2(data));
    }

    public class Item1 extends ItemMVC<String>{

        public Item1(String data) {
            super(data);
        }

        @Override
        public int getType() {
            return 0;
        }

        @Override
        protected int getLayoutId() {
            return R.layout.item;
        }

        @Override
        protected void controlDataToView(int position, int type, OldViewHolder holder) {
            holder.setText(R.id.text,mData);
        }
    }

    public class Item2 extends ItemMVC<String>{

        public Item2(String data) {
            super(data);
        }

        @Override
        public int getType() {
            return 1;
        }

        @Override
        protected int getLayoutId() {
            return R.layout.item2;
        }

        @Override
        protected void controlDataToView(int position, int type, OldViewHolder holder) {
            ImageView iv=holder.getView(R.id.image);
            iv.setImageBitmap(BitmapFactory.decodeFile(mData));
        }
    }
}