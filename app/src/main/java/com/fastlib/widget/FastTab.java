package com.fastlib.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.design.widget.TabLayout.Tab;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.support.v4.view.ViewPager.OnPageChangeListener;

import com.fastlib.R;
import com.fastlib.interf.Delayable;

/**
 * Created by sgfb on 16/2/25.
 * 快速集合式的TabLayout.支持视图延迟加载
 */
public class FastTab extends FrameLayout implements ViewPager.OnPageChangeListener{
    private ViewPager mViewPager;
    private FragmentPagerAdapter mAdapter;
    private OnPageChangeListener mListener;
    private TabLayout mTabLayout;
    private TabPage[] mTabPages;
    private boolean isAlignTop=true;

    public FastTab(Context context){
        this(context, null);
    }

    public FastTab(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public void setPages(CharSequence[] titles, int icons[], Fragment[] fragments){
        int min=Math.min(titles.length,icons.length);
        TabPage[] pages;

        min=Math.min(min,fragments.length);
        pages=new TabPage[min];
        for(int i=0;i<min;i++){
            TabPage page;
            Tab tab=mTabLayout.newTab();
            tab.setText(titles[i]);
            tab.setIcon(icons[i]);
            page=new TabPage(tab,fragments[i]);
            pages[i]=page;
        }
        setPages(pages);
    }

    public void setPages(@NonNull TabPage[] tabPages){
        mTabPages=tabPages;
        mViewPager.setOffscreenPageLimit(mTabPages.length);
        generateTab();
    }

    private void init(){
        ViewGroup mainView= (ViewGroup) LayoutInflater.from(getContext()).inflate(R.layout.fasttab_relative,this);
        mViewPager=(ViewPager)mainView.findViewById(R.id.viewPager);
        mTabLayout=(TabLayout)mainView.findViewById(R.id.tabLayout);
        mAdapter=new DefaultAdapter(((AppCompatActivity)getContext()).getSupportFragmentManager());

        mViewPager.setAdapter(mAdapter);
        mViewPager.addOnPageChangeListener(this);
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.setTabsFromPagerAdapter(mAdapter);
    }

    private void generateTab(){
        mTabLayout.removeAllTabs();
        for(TabPage page:mTabPages)
            mTabLayout.addTab(page.getTab());
        mAdapter.notifyDataSetChanged();
    }

    public void setAlignTop(boolean alignTop){
        if(isAlignTop!=alignTop){
            isAlignTop=alignTop;
            if(alignTop){
                RelativeLayout.LayoutParams lpTop=new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
                RelativeLayout.LayoutParams lpBottom=new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.MATCH_PARENT);

                lpTop.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                lpBottom.addRule(RelativeLayout.BELOW,R.id.tabLayout);
                mTabLayout.setLayoutParams(lpTop);
                mViewPager.setLayoutParams(lpBottom);
            }
            else{
                RelativeLayout.LayoutParams lpTop=new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.MATCH_PARENT);
                RelativeLayout.LayoutParams lpBottom=new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.WRAP_CONTENT);

                lpTop.addRule(RelativeLayout.ABOVE,R.id.tabLayout);
                lpBottom.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                mViewPager.setLayoutParams(lpTop);
                mTabLayout.setLayoutParams(lpBottom);
            }
        }
    }

    public boolean isAlignTop(){
        return isAlignTop;
    }

    public TabLayout getTabLayout(){
        return mTabLayout;
    }

    public TabPage[] getTabPages(){return mTabPages;}

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if(mListener!=null)
            mListener.onPageScrolled(position,positionOffset,positionOffsetPixels);
    }

    @Override
    public void onPageSelected(int position) {
        Fragment fragment=mTabPages[position].getFragment();
        if(fragment instanceof Delayable)
            ((Delayable)fragment).startLoad();
        if(mListener!=null)
            mListener.onPageSelected(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        if(mListener!=null)
            mListener.onPageScrollStateChanged(state);
    }

    public void setOnPageChangedListener(OnPageChangeListener l){
        mListener=l;
    }

    class DefaultAdapter extends FragmentPagerAdapter{

        public DefaultAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mTabPages[position].getFragment();
        }

        @Override
        public int getCount() {
            return mTabPages==null?0:mTabPages.length;
        }
    }

    public static class TabPage{
        private Fragment fragment;
        private Tab tab;

        public TabPage(Tab tab,Fragment fragment){
            this.tab=tab;
            this.fragment=fragment;
        }

        public Tab getTab() {
            return tab;
        }

        public void setTab(Tab tab) {
            this.tab = tab;
        }

        public Fragment getFragment(){
            return fragment;
        }

        public void setFragment(Fragment fragment) {
            this.fragment = fragment;
        }
    }
}
