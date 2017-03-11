package com.fastlib.test.jsonAdvanced;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.util.Pair;
import android.util.SparseArray;
import android.view.View;

import com.fastlib.adapter.JsonAdapter;
import com.fastlib.base.JsonActivity;
import com.fastlib.net.Request;
import com.fastlib.utils.N;
import com.fastlib.utils.json.JsonObject;
import com.fastlib.utils.json.JsonViewBinder;

import java.util.List;

/**
 * Created by sgfb on 17/1/21.
 */
public abstract class JsonActivity2 extends JsonActivity{
    private SparseArray<Pair<Integer,Request>> mContains=new SparseArray<>();

    /**
     * 添加包含容器(如ListView)
     * @param id
     * @param request
     */
    public void addContains(int id,@LayoutRes int layoutId,@Nullable Request request){
        mContains.append(id,new Pair<>(layoutId,request));
    }

    public class ListViewResolve implements JsonViewBinder.ViewResolve{

        @Override
        public void resolve(View view,Object reader){
            Pair<Integer,Request> pair=mContains.get(view.getId());
            if(pair==null)
                return;
            NormalJsonAdapter adapter=new NormalJsonAdapter(JsonActivity2.this,pair.first);
            if(pair.second!=null){
                adapter.setRequst(pair.second);
                adapter.refresh();
            }
            else
                adapter.addData((JsonObject) reader);
        }
    }

    public class NormalJsonAdapter extends JsonAdapter {

        public NormalJsonAdapter(Context context, @LayoutRes int layoutId) {
            super(context,layoutId,false);
        }

        @Override
        public List<JsonObject> translate(JsonObject raw){
            try{
                if(raw.findValue("success"))
                    return raw.findValue("list");
                else
                    N.showShort(JsonActivity2.this,(String)raw.findValue("message"));
            }catch (ClassCastException e){
                System.out.println("数据解析异常");
            }
            return null;
        }

        @Override
        public Request generateRequest(){
            return null;
        }

        @Override
        public void getMoreRequest(){
            mRequest.increment("page",1);
        }

        @Override
        public void getRefreshRequest(){
            mRequest.put("page",1);
        }

        public void setRequst(Request request){
            mRequest=request;
        }
    }
}
