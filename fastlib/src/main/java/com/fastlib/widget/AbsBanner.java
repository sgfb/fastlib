package com.fastlib.widget;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;

/**
 * Created by sgfb on 16/3/23.
 * 通用轮播.指示器需要额外加装
 */
public abstract class AbsBanner<T> extends ViewPager{
    private boolean isAutoScrolling=false;
    private boolean isInfinite = false;  //无限往前轮播
    private long mScrollInterval = 5000;  //轮播间隔时间
    private BannerAdapter mAdapter;
    private List<T> mData;
    private OnPageChangeListener mPageChangeListener;
    private Runnable mAutoScrolling = new Runnable() {
        @Override
        public void run() {
            setCurrentItem((getCurrentItem() + 1) % mAdapter.getCount(), true);
            if(isAutoScrolling)
                startAutoScroll();
        }
    };
    private SimpleOnPageChangeListener mInfiniteListener = new SimpleOnPageChangeListener() {
        @Override
        public void onPageScrollStateChanged(int state) {
            if(state==ViewPager.SCROLL_STATE_IDLE){
                if (getCurrentItem() == 0)
                    setCurrentItem(mAdapter.getCount() - 2,false);
                else if (getCurrentItem() == mAdapter.getCount() - 1)
                    setCurrentItem(1, false);
            }
        }
    };

    /**
     * 回调数据与视图绑定逻辑
     * @return 绑定逻辑回调
     */
    protected abstract HandlePage<T> dataBindView();

    /**
     * 返回指定布局Id.如果是0，默认给予一个ImageView
     * @return 如果不是0，使用指定布局，否则给予一个ImageView作为item页面
     */
    protected abstract int getItemLayoutId();

    public AbsBanner(Context context) {
        super(context);
        init();
    }

    public AbsBanner(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mAdapter = new BannerAdapter();
        mAdapter.mImageWithEvent = dataBindView();
        setAdapter(mAdapter);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        boolean consume= super.onTouchEvent(ev);

        //针对自动轮播进行触摸监听，如果可能有外力手动轮播，则暂停轮播计时.等手离开轮播触摸区域后再计时
        if(consume&&isAutoScrolling){
            switch (ev.getAction()& MotionEventCompat.ACTION_MASK){
                case MotionEvent.ACTION_DOWN:
                    removeCallbacks(mAutoScrolling);
                    break;
                case MotionEvent.ACTION_UP:
                    startAutoScroll();
                    break;
            }
        }
        return consume;
    }

    /**
     * 开始自动轮播
     */
    private void startAutoScroll() {
        removeCallbacks(mAutoScrolling);
        postDelayed(mAutoScrolling, mScrollInterval);
    }

    /**
     * 是否自动滚动
     * @param autoScrollable true自动滚动，false不自动滚动
     */
    public void setAutoScroll(boolean autoScrollable) {
        if(autoScrollable!=isAutoScrolling){
            isAutoScrolling=autoScrollable;
            if (autoScrollable)
                startAutoScroll();
            else removeCallbacks(mAutoScrolling);
        }
    }

    /**
     * 设置是否无边界(滑动边缘再往边缘划将会划到第一或最后一张)
     * @param infinite true为无边界，false有边界
     */
    public void setInfinite(boolean infinite) {
        if(isInfinite!=infinite){
            isInfinite = infinite;
            if (isInfinite) super.addOnPageChangeListener(mInfiniteListener);
            else removeOnPageChangeListener(mInfiniteListener);
            setAdapter(null);
            setAdapter(mAdapter);
            if(isInfinite&&mAdapter.getCount()>1)
                setCurrentItem(1,false);
        }
    }

    public void setData(List<T> data) {
        mData = data;
        mAdapter.notifyDataSetChanged();
    }

    /**
     * 设置自动轮播间隔
     * @param interval 自动轮播间隔，毫秒值
     */
    public void setAutoScrollingInterval(long interval){
        if(interval<0) interval=0;
        mScrollInterval = interval;
    }

    //TODO
    @Override
    public void addOnPageChangeListener(final OnPageChangeListener listener) {
        super.addOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                listener.onPageScrolled(position,positionOffset,positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                if (mAdapter.getCount() > 1 && isInfinite){
                    if(position==1)
                        listener.onPageSelected(0);
                    else if(position>1&&position<mAdapter.getCount()-1)
                        listener.onPageSelected(position-1);
                }
                else listener.onPageSelected(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                listener.onPageScrollStateChanged(state);
            }
        });
    }

    private class BannerAdapter extends PagerAdapter {
        private HandlePage<T> mImageWithEvent;

        @Override
        public int getCount() {
            int count = mData == null ? 0 : mData.size();
            if (count > 1 && isInfinite) {
                count += 2;
            }
            return count;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            int layoutId = getItemLayoutId();
            View v = layoutId > 0 ? LayoutInflater.from(getContext()).inflate(layoutId, null) : new ImageView(getContext());
            if (mImageWithEvent != null) {
                T data;
                if(isInfinite){
                    if(mData.size()>1){
                        if(position==0)
                            data=mData.get(mData.size()-1);
                        else if(position>mData.size())
                            data=mData.get(0);
                        else data=mData.get(position-1);
                    }
                    else data=mData.get(0);
                }
                else data=mData.get(position);
                mImageWithEvent.handle(v,data);
            }
            container.addView(v);
            return v;
        }
    }

    /**
     * 绑定数据与视图逻辑
     *
     * @param <T> 数据泛型
     */
    public interface HandlePage<T> {
        void handle(View v, T element);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if(isAutoScrolling)
            removeCallbacks(mAutoScrolling);
    }
}