package com.fastlib.adapter;

import androidx.core.util.Pair;
import androidx.viewpager.widget.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by sgfb on 18/6/13.
 * ViewPager通用适配器
 */
public class CommonViewPagerAdapter extends PagerAdapter {
    private List<Pair<String,View>> mPages;

    public CommonViewPagerAdapter(List<Pair<String,View>> pages){
        mPages=pages;
    }

    @Override
    public int getCount() {
        return mPages==null?0:mPages.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view==object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view=mPages.get(position).second;
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mPages.get(position).first;
    }

    public void addPage(Pair<String,View> page){
        mPages.add(page);
        notifyDataSetChanged();
    }
}
