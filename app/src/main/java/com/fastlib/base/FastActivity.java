package com.fastlib.base;

import java.lang.reflect.Field;

import com.fastlib.R;
import com.fastlib.annotation.ViewInject;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ProgressBar;

/**
 * 基础的activity.注解视图绑定id，默认有toolbar
 * 
 * @author sgfb,shf
 */
public abstract class FastActivity extends AppCompatActivity{
	
	protected Toolbar mToolbar;
	private ActivityConfig mConfig=null;
	private ViewGroup mDefaultLayout;
	private View mLoading;
	
	protected abstract ActivityConfig initConfig(ActivityConfig config);
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		mConfig=initConfig(new ActivityConfig());
		mDefaultLayout=(ViewGroup) LayoutInflater.from(this).inflate(R.layout.activity_fast,null).findViewById(R.id.fast_content);
		generateLoadingView();
	}
	
	@Override
	public void setContentView(@LayoutRes int resId){
		if(mConfig==null||mConfig.mToolbarId==0){
			LayoutInflater.from(this).inflate(resId,mDefaultLayout,true);
			super.setContentView((View)mDefaultLayout.getParent());
			if(mConfig==null)
				mConfig=new ActivityConfig();
			mConfig.mToolbarId=R.id.toolbar;
		}
		else
		    super.setContentView(resId);
		injectView();
	}
	
	@Override
	public void setContentView(View v){
		if(mConfig==null||mConfig.mToolbarId==0){
			mDefaultLayout.addView(v);
			super.setContentView((View)mDefaultLayout.getParent());
			if(mConfig==null)
				mConfig=new ActivityConfig();
			mConfig.mToolbarId=R.id.toolbar;
		}
		else
		    super.setContentView(v);
		injectView();
	}
	
	private void injectView(){
		Field[] fields=getClass().getDeclaredFields();
		
		if(fields!=null&&fields.length>0){
			for(Field field:fields){
				try {
					field.setAccessible(true);
					if(field.get(this)!=null)
						continue;
					ViewInject inject=field.getAnnotation(ViewInject.class);
					if(inject!=null)
					    field.set(this,findViewById(inject.id()));
				}catch(IllegalAccessException | IllegalArgumentException e) {
					e.printStackTrace();
				}
			}
		}
		String title=mConfig.getTitle();
		mToolbar=(Toolbar)findViewById(mConfig.getToolbarId());
		if(title!=null)
			mToolbar.setTitle(title);
		setSupportActionBar(mToolbar);
	}
	
	/**
	 * 生成进度提示
	 */
	private void generateLoadingView() {
		FrameLayout loading = new FrameLayout(this);// 加载背景
		loading.setBackgroundColor(Color.BLACK);
		loading.setAlpha(0.5f);
		ProgressBar progressBar = new ProgressBar(this);
		LayoutParams flp = new LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		flp.gravity = Gravity.CENTER;
		progressBar.setLayoutParams(flp);
		int padding = (int) getResources().getDimension(R.dimen.common_middle);
		progressBar.setPadding(padding, padding, padding, padding);
		progressBar.setBackgroundResource(R.drawable.round_rect_white);
		loading.addView(progressBar);
		//进度条应该拦截触摸监听
		loading.setOnTouchListener(new OnTouchListener(){
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return true;
			}			
		});
		mLoading=loading;
	}
	
	/**
	 * 打开/关闭读取进度提示
	 * 
	 * @param state
	 */
	public void setLoadingState(boolean state){
		ViewGroup contentView=(ViewGroup)findViewById(R.id.fast_content);
		if(contentView==null)
			contentView=(ViewGroup)findViewById(android.R.id.content);
		
		if(state)
			contentView.addView(mLoading);
		else
			contentView.removeView(mLoading);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu){ 
		if(mConfig!=null&&mConfig.getMenuId()!=0){
			MenuInflater menuInflater=new MenuInflater(this);
			menuInflater.inflate(mConfig.getMenuId(), menu);
		}
		return true;
	}
	
	public void setLoadingView(View loading){
		mLoading=loading;
	}
	
	public class ActivityConfig{
		//title要在setSupportActionBar前加入
		private String mTitle;
		private int mToolbarId;
		private int mMenuId;
		
		public void setToolbarId(int id){
			mToolbarId=id;
		}
		
		public int getToolbarId(){
			return mToolbarId;
		}
		
		public void setMenuId(int id){
			mMenuId=id;
		}
		
		public int getMenuId(){
			return mMenuId;
		}

		public String getTitle() {
			return mTitle;
		}

		public void setTitle(String title) {
			mTitle = title;
		}
		
		public void setTitle(int res){
			mTitle=FastActivity.this.getString(res);
		}
	}
}