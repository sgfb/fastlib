package com.fastlib;

import android.os.Environment;
import android.widget.EditText;

import com.fastlib.annotation.Bind;
import com.fastlib.annotation.ContentView;
import com.fastlib.app.FastActivity;
import com.fastlib.net.Request;
import com.fastlib.net.SimpleListener;
import com.fastlib.utils.Utils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by sgfb on 2017/9/21.
 */
@ContentView(R.layout.act_main2)
public class MainActivity extends FastActivity{
    @Bind(R.id.content)
    EditText mContent;

    @Override
    protected void alreadyPrepared(){

    }

    @Bind(R.id.bt)
    private void commit(){
        AospRequest request=new AospRequest("queryMemberBaseInfo");
        request.put("mobile","13353353534");
        request.setListener(new SimpleListener<String>(){

            @Override
            public void onResponseListener(Request r, String result){
                System.out.println("result:"+result);
            }
        });
        net(request);
    }

    @Bind(R.id.bt2)
    private void commit2(){
        Gson gson=new Gson();
        Request request=Request.obtain("https://ceshi.zeshukeji.com/wlbx02/activity/uploadImgPoster");
        Map<String,Integer> params=new HashMap<>();
        RequestBean bean;

        params.put("agentId",36662);
        String signMsg= Utils.getMd5(gson.toJson(params)+AospRequest.KEY,false);
        System.out.println(gson.toJson(params));
        System.out.println(signMsg);
//        bean=new RequestBean(signMsg,params);
//        request.put("requestParam",gson.toJson(bean));
        request.put("image",new File(Environment.getExternalStorageDirectory(),"1.jpeg"));
        request.setListener(new SimpleListener<String>(){

            @Override
            public void onResponseListener(Request r, String result) {
                System.out.println("result:"+result);
            }
        });
//        net(request);
    }

    @Bind(R.id.bt3)
    private void commit3(){

    }
}
