package com.fastlib.test;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.TextView;

import com.fastlib.R;
import com.fastlib.adapter.BindingAdapter;
import com.fastlib.base.OldViewHolder;
import com.fastlib.net.Request;
import com.fastlib.net.Result;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by sgfb on 16/3/21.
 */
public class TestAdapter extends BindingAdapter<TestBean>{
    int limit=5;

    public TestAdapter(Context context,Request request, @NonNull int resId) {
        super(context,request, resId);
    }

    @Override
    public void binding(int position, TestBean data, OldViewHolder holder) {
        holder.setText(R.id.textView,data.getData());
    }

    @Override
    public List<TestBean> translate(Result result){
        Gson gson=new Gson();
        Type type=new TypeToken<List<TestBean>>(){}.getType();
        List<TestBean> list=gson.fromJson(result.getBody(),type);
        if(list==null||list.size()<=0)
            System.out.println("没有更多数据");
        else
            System.out.println(list.size());
        return list;
    }

    @Override
    public void getMoreDataRequest(Request request){
        int start=Integer.parseInt(request.getParame().get("start"));
        request.put("start",Integer.toString(start+limit));
    }

    @Override
    public void getRefreshDataRequest(Request request) {
        request.put("start","0");
    }
}
