package com.fastlib.local_test;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.fastlib.R;
import com.fastlib.base.CommonViewHolder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by sgfb on 17/10/7.
 */
public class MyAdapter  extends MultiTypeAdapter{
    private Group1 mGroup1;
    private Group2 mGroup2;
    private Group3 mGroup3;

    public MyAdapter(Context context) {
        super(context);
        addAllType();
    }

    public void removeFirstGroup(){
        removeGroup(mGroup1);
    }

    public void addFirstGroup(){
        addGroup(mGroup1);
    }

    private void addAllType(){
        mGroup1=new Group1();
        mGroup2=new Group2();
        mGroup3=new Group3();
        List<List<Bean2>> list=new ArrayList<>();
        List<Bean2> element=new ArrayList<>();
        List<Bean3> list2=new ArrayList<>();
        list.add(element);
        Bean2 bean2=new Bean2();
        bean2.name="万科东城水均匀";
        bean2.type="2室2厅 102平米";
        bean2.cover="https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1507321347708&di=511bd62eb61cfcc124027f6090fb345e&imgtype=0&src=http%3A%2F%2Fimgsrc.baidu.com%2Fimgad%2Fpic%2Fitem%2F00e93901213fb80eea15b7f33cd12f2eb83894ca.jpg";
        for(int i=0;i<10;i++)
            element.add(bean2);
        for(int i=0;i<10;i++)
            list2.add(new Bean3());
        mGroup2.setData(list);
        mGroup3.setData(list2);
        addGroup(mGroup1);
        addGroup(mGroup2);
        addGroup(mGroup3);
    }

    public class Group1 extends RecyclerGroup<Void>{

        public Group1() {
            super(R.layout.item);
        }

        @Override
        protected void binding(int position, int type, Void data, CommonViewHolder holder) {

        }
    }

    public class Group2 extends RecyclerGroup<List<Bean2>>{

        public Group2() {
            super(R.layout.item2);
        }

        @Override
        protected void binding(int position, int type, List<Bean2> data, CommonViewHolder holder) {
            RecyclerView list=holder.getView(R.id.list);
            list.setLayoutManager(new LinearLayoutManager(mContext,LinearLayoutManager.HORIZONTAL,false));
            list.setAdapter(new MyAdapter2(mContext,data));
        }
    }

    public class Group3 extends RecyclerGroup<Bean3>{

        public Group3() {
            super(R.layout.item4);
        }

        @Override
        protected void binding(int position, int type,Bean3 data, CommonViewHolder holder) {
            ImageView iv=holder.getView(R.id.cover);
            Glide.with(mContext).load("http://pic117.nipic.com/file/20161211/18980004_085810664000_2.jpg").into(iv);
        }
    }
}