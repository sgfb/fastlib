package com.fastlib.demo.route;

import android.text.TextUtils;
import android.widget.EditText;

import com.fastlib.annotation.Bind;
import com.fastlib.annotation.ContentView;
import com.fastlib.aspect.base.AspectActivity;
import com.fastlib.demo.R;
import com.fastlib.utils.router.Router;

/**
 * Created by sgfb on 2020\03\03.
 * 路径辅助跳转示例
 */
@ContentView(R.layout.act_route)
public class RouteDemoActivity extends AspectActivity<Empty,Empty>{
    @Bind(R.id.param)
    EditText mParam;
    RouterPathDemo mRouteLink;

    @Override
    protected void onReady() {
        mRouteLink=  Router.createRouter(RouterPathDemo.class,this).getRouterLink();
    }

    @Bind(R.id.jumpWithoutParams)
    private void jumpWithoutParams(){
        mRouteLink.startSecondActivity();
    }

    @Bind(R.id.jumpWithParams)
    private void jumpWithParams(){
        String text=mParam.getText().toString();

        if(TextUtils.isEmpty(text)){
            mParam.setError("输入要传给下个Activity的字符串");
            mParam.requestFocus();
            return;
        }
        mRouteLink.startSecondActivityWithParam(text);
    }
}
