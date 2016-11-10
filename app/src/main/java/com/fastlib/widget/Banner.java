package com.fastlib.widget;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sgfb on 16/3/23.
 * 通用轮播.指示器需要额外加装
 */
public abstract class Banner extends ViewPager{
    private BannerAdapter mAdapter;
    private List<Object> mData;
    private boolean autoScroll=false; //初始化设置false才能自动轮播
    private long scrollInterval=5000; //轮播间隔时间

    protected abstract HandlePage getHandleImageWithEvent();

    public Banner(Context context){
        super(context);
        init();
    }

    public Banner(Context context, AttributeSet attrs){
        super(context, attrs);
        init();
    }

    private void init(){
        mAdapter=new BannerAdapter();
        mAdapter.mImageWithEvent=getHandleImageWithEvent();
        setAdapter(mAdapter);
    }

    private void startAutoScroll(){
        postDelayed(new Runnable() {
            @Override
            public void run() {
                if (autoScroll) {
                    setCurrentItem((getCurrentItem() + 1) % mAdapter.getCount(), true);
                    startAutoScroll();
                }
            }
        },scrollInterval);
    }

    public void setAutoScroll(boolean autoScroll){
        if(!this.autoScroll&&autoScroll){
            this.autoScroll=true;
            postDelayed(new Runnable(){
                @Override
                public void run(){
                    startAutoScroll();
                }
            },scrollInterval);
        }
    }

    public void setData(List<Object> data){
        if(data==null||data.size()==0)
            return;
        mData=data;
        mAdapter.notifyDataSetChanged();
        if(!autoScroll){ //如果没有自动轮播,延迟一个轮播间隔启动自动轮播
            postDelayed(new Runnable(){
                @Override
                public void run(){
                    autoScroll=true;
                    startAutoScroll();
                }
            },scrollInterval);
        }
    }

    public void setInterval(long interval){
        scrollInterval=interval;
    }

    private class BannerAdapter extends PagerAdapter{
        private HandlePage mImageWithEvent;

        @Override
        public int getCount(){
            return mData==null?0:mData.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view==object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object){
            container.removeView((View) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position){
            ImageView iv=new ImageView(getContext());
            if(mImageWithEvent!=null)
                mImageWithEvent.handle(iv,mData.get(position));
            container.addView(iv);
            return iv;
        }
    }

    public interface HandlePage{
        void handle(ImageView iv,Object element);
    }
}