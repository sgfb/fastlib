package com.fastlib.local_test;

import android.content.Context;
import android.view.View;

import com.fastlib.R;
import com.fastlib.base.CommonViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sgfb on 2017/10/17.
 */
public class MyAdapter3 extends MultiTypeAdapter{
    private GroupTitle mGroupTitle;
    private GroupText mGroupText;

    public MyAdapter3(Context context) {
        super(context);
        init();
    }

    private void init(){
        GroupTitle title2=new GroupTitle();
        GroupText text2=new GroupText();
        mGroupTitle=new GroupTitle();
        mGroupText=new GroupText();


        mGroupTitle.addData("title1");
        mGroupText.setSuspandEnable(true);
        List<String> list=new ArrayList<>();
        for(int i=0;i<10;i++)
            list.add(Integer.toString(i));
        mGroupText.setData(list);
        title2.addData("title2");
        text2.setData(list);
        addGroup(mGroupTitle);
        addGroup(mGroupText);
        addGroup(title2);
        addGroup(text2);
    }

    public class GroupTitle extends RecyclerGroup<String>{

        public GroupTitle() {
            super(R.layout.item5);
        }

        @Override
        protected void binding(int position, final int type, String data, CommonViewHolder holder) {
            holder.setText(R.id.text,data);
            holder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getGroupByType(type+1).suspend();
                }
            });
        }
    }

    public class GroupText extends RecyclerGroup<String>{

        public GroupText(){
            super(android.R.layout.simple_list_item_1);
        }

        @Override
        protected void binding(int position, int type, String data, CommonViewHolder holder) {
            holder.setText(android.R.id.text1,data);
        }
    }
}