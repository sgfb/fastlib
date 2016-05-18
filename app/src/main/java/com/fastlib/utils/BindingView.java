package com.fastlib.utils;

import android.content.Context;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatTextView;
import android.util.JsonReader;
import android.util.JsonToken;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by sgfb on 16/4/23.
 * 绑定数据与视图
 */
public class BindingView{
    private Map<String,ViewResolve> mResolves;
    private Context mContext;

    public BindingView(Context context){
        mContext=context;
        mResolves=new HashMap<>();
        TextViewResolve tvResolve=new TextViewResolve();
        CheckBoxResolve cbResolve=new CheckBoxResolve();
        mResolves.put(TextView.class.getCanonicalName(),tvResolve);
        mResolves.put(AppCompatTextView.class.getCanonicalName(),tvResolve);
        mResolves.put(CheckBox.class.getCanonicalName(),cbResolve);
        mResolves.put(AppCompatCheckBox.class.getCanonicalName(),cbResolve);
    }

    public void fromJson(String json,View root){
        Map<String,Integer> map=new HashMap<>();
        getAllChild(map,root);
        JsonReader jr=new JsonReader(new StringReader(json));
        try {
            jr.beginObject();
            while(jr.hasNext()){
                String name=jr.nextName();
                if(checkContain(map, name)){
                    int id=map.get(name);
                    final View view=root.findViewById(id);
                    ViewResolve vr=mResolves.get(view.getClass().getCanonicalName());
                    if(vr!=null)
                        vr.resolve(view,jr);
                    else
                        jr.skipValue();
                    map.remove(name);
                }
            }
            jr.endObject();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void putResolve(String name,ViewResolve resolve){
        mResolves.put(name,resolve);
    }

    /**
     * 获取所有视图的名字和id
     * @param map
     * @param v
     */
    private void getAllChild(Map<String,Integer> map,View v){
        int id=v.getId();
        if(id!=-1)
            map.put(mContext.getResources().getResourceEntryName(v.getId()),v.getId());
        if(v instanceof ViewGroup) {
            ViewGroup vg=(ViewGroup)v;
            for(int i=0;i<vg.getChildCount();i++)
                getAllChild(map,vg.getChildAt(i));
        }
    }

    /**
     * 检查是否包含这个视图
     * @param map
     * @param value
     * @return
     */
    private boolean checkContain(Map<String,Integer> map,String value){
        Iterator<String> iter=map.keySet().iterator();
        while(iter.hasNext()){
            String s=iter.next();
            if(s.equals(value))
                return true;
        }
        return false;
    }

    /**
     * 将数据解析成视图的桥梁
     */
    public interface ViewResolve{
        void resolve(View view,JsonReader reader);
    }

    public final class TextViewResolve implements ViewResolve{

        @Override
        public void resolve(View view,JsonReader reader){
            TextView tv=(TextView)view;
            try {
                final JsonToken token=reader.peek();
                switch (token){
                    case NUMBER:
                    case STRING:
                        tv.setText(reader.nextString());
                        break;
                    case BOOLEAN:
                        tv.setText(Boolean.toString(reader.nextBoolean()));
                        break;
                    case NULL:
                        tv.setText(null);
                        break;
                    default:
                        reader.skipValue();
                        break;
                }
            } catch (IOException e){
                //do noting
            }
        }
    }

    public final class CheckBoxResolve implements ViewResolve{

        @Override
        public void resolve(View view, JsonReader reader) {
            CheckBox cb=(CheckBox)view;
            try {
                JsonToken token=reader.peek();
                switch (token){
                    case BOOLEAN:
                        cb.setChecked(reader.nextBoolean());
                        break;
                    case STRING:
                        String value=reader.nextString().toLowerCase();
                        if(value.equals("true")||value.equals("false"))
                            cb.setChecked(value.equals("true"));
                        break;
                    default:
                        reader.skipValue();
                        break;
                }
            } catch (IOException e) {
                //do noting
            }
        }
    }
}
