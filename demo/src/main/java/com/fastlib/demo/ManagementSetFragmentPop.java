package com.fastlib.demo;

import android.content.Context;
import android.graphics.Typeface;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.lxj.xpopup.core.BottomPopupView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * author ：Administrator
 * date : 2020/3/3 19:30
 * package：com.kyle.myapplication
 * description :
 */
class ManagementSetFragmentPop extends BottomPopupView {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private final String[] titles = {"踢出列表", "踢出记录"};
    private List<Fragment> listFragments = new ArrayList<>();

    FragmentManager fragmentManager;
    Context mContext;

    public ManagementSetFragmentPop(@NonNull Context context, FragmentManager fragmentManager) {
        super(context);
        this.mContext = context;
        this.fragmentManager = fragmentManager;
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.pop_testb;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        initView();
    }

    protected void initView() {
        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.viewpager);

        ArrayList<String> titleList = new ArrayList<>(Arrays.asList(titles));
        ListFragment listFragment = ListFragment.newInstance();
        RecordFragment recordFragment = RecordFragment.newInstance();
        listFragments.add(listFragment);
        listFragments.add(recordFragment);
        TabLayoutViewPagerAdapter adapter = new TabLayoutViewPagerAdapter(fragmentManager, mContext, listFragments, titleList);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(listFragments.size());

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                View view = tab.getCustomView();
                if (null == view) {
                    tab.setCustomView(R.layout.custom_tab_layout_text);
                }
                TextView textView = tab.getCustomView().findViewById(android.R.id.text1);
                textView.setTextColor(tabLayout.getTabTextColors());
                textView.setTextSize(20);
                textView.setTypeface(Typeface.DEFAULT_BOLD);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                View view = tab.getCustomView();
                if (null == view) {
                    tab.setCustomView(R.layout.custom_tab_layout_text);
                }
                TextView textView = tab.getCustomView().findViewById(android.R.id.text1);
                textView.setTextSize(14);
                textView.setTypeface(Typeface.DEFAULT);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        tabLayout.setupWithViewPager(viewPager);
    }

    public class TabLayoutViewPagerAdapter extends FragmentPagerAdapter {

        private Context mContext;
        private List<Fragment> listFragments;
        private List<String> titles;

        public TabLayoutViewPagerAdapter(FragmentManager fm, Context context, List<Fragment> listFragments, List<String> titles) {
            super(fm);
            this.mContext = context;
            this.listFragments = listFragments;
            this.titles = titles;
        }

        @Override
        public Fragment getItem(int i) {
            return listFragments.get(i);
        }

        @Override
        public int getCount() {
            return listFragments.size();
        }

        @Override
        public int getItemPosition(@NonNull Object object) {
            return super.getItemPosition(object);
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            super.destroyItem(container, position, object);
        }
    }

    @Override
    protected void onDismiss() {
        super.onDismiss();
    }
}
