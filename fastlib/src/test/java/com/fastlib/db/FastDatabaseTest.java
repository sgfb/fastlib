package com.fastlib.db;

import com.fastlib.bean.DBInnerBean;
import com.fastlib.bean.DBTestBean;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowApplication;

import java.util.ArrayList;

/**
 * Created by sgfb on 2018/5/27.
 * FastDatabase功能测试
 */
@RunWith(RobolectricTestRunner.class)
public class FastDatabaseTest{
    private DBTestBean mTestBean;

    @Before
    public void setUp() throws Exception {
        mTestBean=new DBTestBean();
        mTestBean.age=16;
        mTestBean.address="贝克街66号";
        mTestBean.name="张三";
        mTestBean.score=88.5f;
        mTestBean.mOptionalCourse=new ArrayList<>();

        DBInnerBean innerBean=new DBInnerBean();
        innerBean.id=1;
        innerBean.name="马术";
        innerBean.score=20.2f;
        mTestBean.mOptionalCourse.add(innerBean);
    }

    /**
     * 同时测试存和取，已是否有异常弹出和取出的数据为准
     */
    @Test
    public void saveAndGet(){
        FastDatabase.getDefaultInstance(ShadowApplication.getInstance().getApplicationContext()).saveOrUpdate(mTestBean);

        DBTestBean fromDb=FastDatabase.getDefaultInstance(ShadowApplication.getInstance().getApplicationContext()).getFirst(DBTestBean.class);
        Assert.assertTrue(mTestBean.equals(fromDb));
    }

    /**
     * 测试保存后修改
     */
    @Test
    public void saveAndInsert(){
        FastDatabase.getDefaultInstance(ShadowApplication.getInstance().getApplicationContext()).saveOrUpdate(mTestBean);

        mTestBean.age=8;
        mTestBean.score=0.85f;
        mTestBean.mOptionalCourse.get(0).score=80.1f;
        FastDatabase.getDefaultInstance(ShadowApplication.getInstance().getApplicationContext()).saveOrUpdate(mTestBean);
        DBTestBean fromDB=FastDatabase.getDefaultInstance(ShadowApplication.getInstance().getApplicationContext()).getFirst(DBTestBean.class);
        Assert.assertTrue(mTestBean.equals(fromDB));
    }
}
