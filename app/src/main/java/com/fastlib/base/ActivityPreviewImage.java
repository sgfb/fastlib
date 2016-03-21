package com.fastlib.base;

import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.fastlib.R;
import com.fastlib.utils.N;
import com.fastlib.widget.PinchImageView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.w3c.dom.Text;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by sgfb on 16/3/18.
 * 预览照片模块。支持多图和索引样式替换
 */
public abstract class ActivityPreviewImage extends AppCompatActivity implements ViewPager.OnPageChangeListener{
    public static final String KEY_IMAGES="IMAGES";
    public static final String KEY_INDEX="INDEX";

    private ViewPager mViewPager;
    private TextView mIndicator;
    private List<String> mData;

    protected abstract void loadImage(ImageView imageView,String data);
    protected abstract void indexChanged(int index);

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview_image);

        mViewPager=(ViewPager)findViewById(R.id.viewPager);
        mIndicator=(TextView)findViewById(R.id.indicator);
        mData=getIntent().getStringArrayListExtra(KEY_IMAGES);
        int index=getIntent().getIntExtra(KEY_INDEX,0);
        mViewPager.setAdapter(new SimpleAdapter());
        mViewPager.setCurrentItem(index, false);
        mViewPager.addOnPageChangeListener(this);
        if(mData==null||mData.size()<=0)
            N.showShort(this,"没有可以显示的图像");
        else
            mIndicator.setText(Integer.toString(index+1)+"/"+Integer.toString(mData.size()));
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position){
        mIndicator.setText(Integer.toString(position+1)+"/"+Integer.toString(mData.size()));
        indexChanged(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private class SimpleAdapter extends PagerAdapter{

        @Override
        public int getCount() {
            return mData==null?0:mData.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view==object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position){
            PinchImageView imageView=new PinchImageView(ActivityPreviewImage.this);
            loadImage(imageView,mData.get(position));
            container.addView(imageView);
            return imageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }
}
