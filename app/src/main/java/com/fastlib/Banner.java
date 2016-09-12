package com.fastlib;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sgfb on 16/3/23.
 * 轮播.指示器需要额外加装
 */
public abstract class Banner extends ViewPager{
    private BannerAdapter mAdapter;
    private List<Object> mData;
    private boolean autoScroll=true;
    private boolean infinite;
    private long scrollInterval=5000; //轮播间隔时间

    protected abstract HandlePage getHandleImageWithEvent();

    public Banner(Context context){
        this(context, null);
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
        }, scrollInterval);
    }

    public void setAutoScroll(boolean autoScroll){
        this.autoScroll=autoScroll;
        if(autoScroll)
            startAutoScroll();
    }

    public void setData(List<Object> data){
        if(data==null||data.size()==0)
            return;
        if(infinite){
            mData=new ArrayList<>(data);
            mData.add(0,data.get(data.size()-1));
            mData.add(data.get(0));
        }
        else
            mData=data;
        mAdapter.notifyDataSetChanged();
        autoScroll=true;
        startAutoScroll();
    }

    @Override
    public void setCurrentItem(int position){
        super.setCurrentItem(position);
        if(!infinite||mData==null||mData.size()<=0)
            return;
//        if(position==0) setCurrentItem(mAdapter.getCount(),false);
//        else if(position==mAdapter.getCount()) setCurrentItem(0,false);

    }

    public void setInterval(long interval){
        scrollInterval=interval;
    }

    public void setInfinite(boolean infinite){
        this.infinite=infinite;
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