package com.fastlib.demo.net;

import com.fastlib.annotation.Bind;
import com.fastlib.annotation.ContentView;
import com.fastlib.aspect.AspectActivity;
import com.fastlib.aspect.ThreadOn;
import com.fastlib.demo.R;
import com.fastlib.demo.route.Empty;
import com.fastlib.net2.Request;
import com.fastlib.net2.core.HeaderDefinition;
import com.fastlib.net2.core.Method;
import com.fastlib.net2.listener.SimpleListener;
import com.fastlib.net2.utils.RequestAgentFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by sgfb on 2020\03\04.
 */
@ContentView(R.layout.act_net)
public class NetDemoActivity extends AspectActivity<Empty,Empty>{

    @Override
    protected void onReady(){

    }

    @ThreadOn(ThreadOn.ThreadType.WORK)
    @Bind(R.id.bt)
    private void bt(){
        NoTransformerInterface model=RequestAgentFactory.genAgent(NoTransformerInterface.class);
        System.out.println(model.testJsonRequest("application/json",0,1,10));
    }
}
