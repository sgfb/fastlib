package com.fastlib;

import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.ToggleButton;

import com.fastlib.annotation.Bind;
import com.fastlib.annotation.ContentView;
import com.fastlib.app.FastActivity;
import com.fastlib.app.task.Task;
import com.fastlib.app.task.TaskLauncher;


@ContentView(R.layout.act_main)
public class MainActivity extends FastActivity{
    final int DURATION_CHANGE_COLOR=20;
    long mTimer;
    TaskLauncher mTl;
    Task mTask;
    @Bind(R.id.toggleButton)
    ToggleButton mToggleButton;
    @Bind(R.id.imageView)
    ImageView mImageView;
    ColorDrawable mColorDrawable;

    @Override
    protected void alreadyPrepared(){
        mColorDrawable=new ColorDrawable();
//		final StringBuilder sb=new StringBuilder();
//		mTl=new TaskLauncher.Builder(this,mThreadPool).setExceptionHandler(new NoReturnAction<Throwable>() {
//			@Override
//			public void executeAdapt(Throwable param) {
//				param.printStackTrace();
//			}
//		}).build();
//		mTask=Task.begin(20)
//				.cycleList(new Action<Integer,List<Request>>(){
//
//					@Override
//					protected List<Request> execute(Integer param) throws Throwable{
//						List<Request> list=new ArrayList<>();
//						for(int i=0;i<param;i++) list.add(new Request("http://www.baidu.com"));
//						return list;
//					}
//				})
//				.next(new Action<Request,String>(){
//
//					@Override
//					protected String execute(Request param) throws Throwable {
//						return new String(NetManager.getInstance().netRequestPromptlyBack(param));
//					}
//				})
//				.again(new NoReturnAction<List<String>>() {
//					@Override
//					public void executeAdapt(List<String> param) {
//						System.out.println("ending task result length:"+param.size());
//					}
//				});
    }

//    @Bind(R.id.commit)
    public void onCommit(View view) {
//		mTl=mTl.reboot(mTask);
    }

//    @Bind(R.id.cancel)
    private void onCancel() {
//		mTl.stopNow(true);
    }
}