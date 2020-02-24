package com.fastlib;

import com.fastlib.annotation.Bind;
import com.fastlib.annotation.ContentView;
import com.fastlib.annotation.Event;
import com.fastlib.app.EventObserver;
import com.fastlib.aspect.AspectActivity;
import com.fastlib.aspect.AspectManager;
import com.fastlib.aspect.ExceptionHandler;
import com.fastlib.aspect.ThreadOn;
import com.fastlib.net2.Request;
import com.fastlib.utils.ContextHolder;

@ContentView(R.layout.act_main)
public class MainActivity extends AspectActivity<MainView,MainController> implements ExceptionHandler {

    @ThreadOn(value = ThreadOn.ThreadType.WORK)
    @Bind(R.id.bt)
    private void bt() {
        mView.showToast("hello,world");
    }

    @ThreadOn(value = ThreadOn.ThreadType.WORK,weight = ThreadOn.ThreadWeight.LIGHT)
    @Bind(R.id.bt2)
    private void bt2(){
        mController.testGetImage();
    }

    @Override
    protected void onReady() {
        ContextHolder.init(getApplicationContext());
        AspectManager am=AspectManager.getInstance();
        am.init(this);
    }

    @Override
    public void onException(Exception e) {
        e.printStackTrace();
    }
}