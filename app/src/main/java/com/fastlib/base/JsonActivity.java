package com.fastlib.base;

import android.os.Bundle;
import android.support.v4.util.Pair;
import android.view.View;

import com.fastlib.app.FastActivity;
import com.fastlib.app.TaskAction;
import com.fastlib.app.TaskChain;
import com.fastlib.app.TaskChainHead;
import com.fastlib.utils.json.JsonObject;
import com.fastlib.utils.json.JsonViewBinder;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by sgfb on 16/9/21.
 * 数据填充来自 1.Intent数据传递 2.服务器 3.特殊
 */
public abstract class JsonActivity extends FastActivity{
    protected JsonViewBinder mJsonViewBinder;
    protected OldViewHolder mViewHolder;

    public JsonActivity(){
        mPreparedTaskRemain=3;
    }

    public Pair<JsonViewBinder.ViewResolve,Class<? extends View>>[] generateViewResolves(){
        return null;
    }

    @Override
    protected void afterSetContentView(){
        super.afterSetContentView();
        mViewHolder=OldViewHolder.get(findViewById(android.R.id.content));
        startTasks(TaskChainHead.begin(0).next(new TaskAction<Integer,Integer>(){

            @Override
            public Integer call(Integer t){ //初始化有遍历View过程，放入工作线程来处理
                mJsonViewBinder=new JsonViewBinder(JsonActivity.this);
                Pair<JsonViewBinder.ViewResolve,Class<? extends View>>[] extraViewResolves=generateViewResolves();
                if(extraViewResolves!=null&&extraViewResolves.length>0)
                    for(Pair<JsonViewBinder.ViewResolve,Class<? extends View>> pair:extraViewResolves)
                        mJsonViewBinder.putResolve(pair.first,pair.second);
                return 0;
            }
        })
        .next(new TaskAction<Integer,Integer>(){ //任务初始化阶段完毕

            @Override
            public Integer call(Integer t){
                prepareTask();
                return 0;
            }
        },TaskChain.TYPE_THREAD_ON_MAIN)
        .next(new TaskAction<Integer,Map<String,Object>>(){ //将Intent中的参数全部取出，尝试填充对应视图

            @Override
            public Map<String, Object> call(Integer t){
                Map<String,Object> map=new HashMap<>();
                Bundle bundle=getIntent().getExtras();
                Set<String> keys=bundle.keySet();
                for(String key:keys)
                    map.put(key,bundle.get(key));
                return map;
            }
        })
        .next(new TaskAction<Map<String,Object>,Integer>(){ //填充数据到视图
            @Override
            public Integer call(Map<String,Object> t){
                mJsonViewBinder.bindDataToView(t);
                return 0;
            }
        },TaskChain.TYPE_THREAD_ON_MAIN));
    }

    /**
     * 填充json数据到视图
     * @param json json字符串
     */
    protected void inflaterJsonToView(String json){
        try {
            mJsonViewBinder.bindDataToView(json);
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    protected void inflaterJsonToView(JsonObject jo){
        mJsonViewBinder.bindDataToView(jo);
    }
}