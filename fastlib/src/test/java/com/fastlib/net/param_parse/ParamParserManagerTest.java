package com.fastlib.net.param_parse;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.robolectric.RobolectricTestRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2018/5/16.
 * 参数解析管理器测试
 */
@RunWith(JUnit4.class)
public class ParamParserManagerTest{
    ParamParserManager mParamParserManager;

    @Before
    public void setUp() throws Exception {
        mParamParserManager=new ParamParserManager();
        mParamParserManager.putParser(new JsonParamParser());
        mParamParserManager.putParser(new MapParamParser());
        mParamParserManager.putParser(new NetBeanWrapperParser());
        mParamParserManager.putParser(new PrimitiveParamParser());
    }

    /**
     * 重复测试
     * 结果要求不能重复
     */
    @Test
    public void duplicationTest(){
        mParamParserManager.putParser(new PrimitiveParamParser());
        int primitiveParserCount=0;

        for(Map.Entry<ParamParserManager.NetParamParserClass,NetParamParser> entry:mParamParserManager.mNetParamParserMap.entrySet()){
            if(entry.getKey().mCla==PrimitiveParamParser.class)
                primitiveParserCount++;
        }
        Assert.assertEquals(1,primitiveParserCount);
    }

    /**
     * 排序测试
     * 结果要求{@link NetParamParser} 返回的priority越小越在前面
     */
    @Test
    public void sortTest(){
        List<NetParamParser> list=new ArrayList<>();
        for(Map.Entry<ParamParserManager.NetParamParserClass,NetParamParser> entry:mParamParserManager.mNetParamParserMap.entrySet()){
            list.add(entry.getValue());
        }
        Assert.assertEquals(PrimitiveParamParser.class,list.get(0).getClass());
        Assert.assertEquals(JsonParamParser.class,list.get(list.size()-1).getClass());
    }
}