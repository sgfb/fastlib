package com.fastlib.demo.aspect;

import com.fastlib.annotation.Bind;
import com.fastlib.annotation.ContentView;
import com.fastlib.aspect.base.AspectActivity;
import com.fastlib.aspect.ThreadOn;
import com.fastlib.aspect.exception.ExceptionHandler;
import com.fastlib.demo.R;

/**
 * Created by sgfb on 2020\03\05.
 */
@ContentView(R.layout.act_aspect_demo)
public class AspectDemoActivity extends AspectActivity<AspectView,AspectController> implements ExceptionHandler {

    @Override
    protected void onReady() {

    }

    @ThreadOn(ThreadOn.ThreadType.WORK)
    @Bind(R.id.bt)
    private void bt(){
        mController.getCameraPermission();
        System.out.println("camera 权限获取完毕");
    }

    @Override
    public void onException(Exception e) {
        e.printStackTrace();
    }
}
