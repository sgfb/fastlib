package com.fastlib.net.param_parse;

import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.fastlib.bean.InterfaceCheckTestBean;
import com.fastlib.net.Request;
import com.google.gson.Gson;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowApplication;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2018/5/16.
 * 网络参数解析器有效性
 */
@RunWith(RobolectricTestRunner.class)
public class NetParamParserValidTest{
    private final String KEY="key";
    private Request mRequest;

    @Before
    public void setUp() throws Exception {
        mRequest=new Request();
    }

    /**
     * json化解析器测试
     */
    @Test
    public void checkJsonParamParserTest(){
        InterfaceCheckTestBean bean=new InterfaceCheckTestBean();

        bean.id=10;
        bean.name="just test";
        mRequest.getParamParserManager().putParser(new JsonParamParser());
        mRequest.put(KEY,bean);

        Gson gson=new Gson();
        InterfaceCheckTestBean deserializationBean=gson.fromJson(mRequest.getParams().get(KEY),InterfaceCheckTestBean.class);
        Assert.assertTrue(bean.id==deserializationBean.id&& TextUtils.equals(bean.name,deserializationBean.name));
    }

    /**
     * Map解析器测试
     */
    @Test
    public void checkMapParamParserTest(){
        final String VALUE="just test";
        Map<String,String> map=new HashMap<>();
        map.put(KEY,VALUE);
        mRequest.getParamParserManager().putParser(new MapParamParser());
        mRequest.put(map);

        String realResult=mRequest.getParams().get(KEY);
        Assert.assertEquals(VALUE,realResult);
    }

    /**
     * 网络数据包裹解析器测试
     */
    @Test
    public void checkNetBeanParserTest(){
        InterfaceCheckTestBean bean=new InterfaceCheckTestBean();

        bean.id=10;
        bean.name="just test";
        mRequest.getParamParserManager().putParser(new PrimitiveParamParser()); //NetBeanParser依赖PrimitiveParser
        mRequest.getParamParserManager().putParser(new NetBeanWrapperParser());
        mRequest.put(bean);
        Assert.assertEquals(Integer.toString(bean.id),mRequest.getParams().get("id"));
        Assert.assertEquals(bean.name,mRequest.getParams().get("name"));
    }

    /**
     * 基本类型解析器测试
     */
    @Test
    public void checkPrimitiveParamParser(){
        final Object VALUE_STR="valueStr";
        final Object VALUE_INT=10;

        mRequest.getParamParserManager().putParser(new PrimitiveParamParser());
        mRequest.put("key1",VALUE_STR);
        mRequest.put("key2",VALUE_INT);
        Assert.assertEquals(VALUE_STR,mRequest.getParams().get("key1"));
        Assert.assertEquals(VALUE_INT.toString(),mRequest.getParams().get("key2"));
    }

    /**
     * TextView解析器测试
     */
    @Test
    public void checkTextViewParamParser(){
        final String VALUE="just test";
        TextView tv=new TextView(ShadowApplication.getInstance().getApplicationContext());

        tv.setText(VALUE);
        mRequest.getParamParserManager().putParser(new TextViewParamParser());
        mRequest.put(KEY,tv);
        Assert.assertEquals(VALUE,mRequest.getParams().get(KEY));
    }

    /**
     * Spinner解析器测试
     */
    @Test
    public void checkSpinnerParamParser(){
        final String[] VALUE={"test1","test2"};
        final int INDEX=0;
        Spinner spinner=new Spinner(ShadowApplication.getInstance().getApplicationContext());
        ArrayAdapter<String> adapter=new ArrayAdapter<>(ShadowApplication.getInstance().getApplicationContext(),android.R.layout.simple_list_item_1,VALUE);

        spinner.setAdapter(adapter);
        spinner.performItemClick(spinner,INDEX,1);
        mRequest.getParamParserManager().putParser(new SpinnerParamParser());
        mRequest.put(KEY,spinner);
        Assert.assertEquals(VALUE[INDEX],mRequest.getParams().get(KEY));
    }
}