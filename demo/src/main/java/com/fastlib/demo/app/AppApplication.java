package com.fastlib.demo.app;

import android.app.Application;
import android.support.v7.widget.RecyclerView;

import com.fastlib.aspect.AspectManager;
import com.fastlib.demo.aspect.CustomerPermissionHandler;
import com.fastlib.demo.list_view.RemoteBindAdapterDemo;
import com.fastlib.demo.net.TestInterface;
import com.fastlib.net2.utils.RequestAgentFactory;
import com.fastlib.utils.fitout.AttachmentFitout;
import com.fastlib.utils.fitout.FitoutFactory;
import com.fastlib.utils.fitout.InstanceMaker;

/**
 * Created by sgfb on 2020\03\02.
 */
public class AppApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        AspectManager.getInstance().init(this);
        AspectManager.getInstance().addStaticEnv(CustomerPermissionHandler.class);
        CustomViewInject.inflaterCustomViewInject();
        initCustomFitout();
    }

    private void initCustomFitout(){
        FitoutFactory.getInstance().putInstanceMaker(new InstanceMaker() {
            @Override
            public Object makeInstance(Class cla) {
                return RequestAgentFactory.genAgent(cla);
            }
        },TestInterface.class);
        FitoutFactory.getInstance().putAttachmentFitout(new AttachmentFitout() {
            @Override
            public void fitout(Object fieldInstance, Object attachment) {
                ((RecyclerView)fieldInstance).setAdapter((RecyclerView.Adapter) attachment);
            }
        },RemoteBindAdapterDemo.class);
    }
}
