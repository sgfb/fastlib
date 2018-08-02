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
import java.util.List;
import java.util.Random;

/**
 * Created by sgfb on 2018/5/27.
 * FastDatabase功能测试
 */
@RunWith(RobolectricTestRunner.class)
public class FastDatabaseTest{
    private FastDatabase mFastDatabase;
    private DBTestBean mTestBean;

    @Before
    public void setUp() throws Exception {
        mFastDatabase=FastDatabase.getDefaultInstance(ShadowApplication.getInstance().getApplicationContext());
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
        mFastDatabase.saveOrUpdate(mTestBean);

        DBTestBean fromDb=mFastDatabase.getFirst(DBTestBean.class);
        Assert.assertTrue(mTestBean.equals(fromDb));
    }

    /**
     * 测试保存后修改
     */
    @Test
    public void saveAndInsert(){
        mFastDatabase.saveOrUpdate(mTestBean);

        mTestBean.age=8;
        mTestBean.score=0.85f;
        mTestBean.mOptionalCourse.get(0).score=80.1f;
        mFastDatabase.saveOrUpdate(mTestBean);
        DBTestBean fromDb=mFastDatabase.getFirst(DBTestBean.class);
        Assert.assertTrue(mTestBean.equals(fromDb));
    }

    /**
     * 保存后删除测试
     */
    @Test
    public void saveAndDelete(){
        mFastDatabase.saveOrUpdate(mTestBean);
        mFastDatabase.delete(mTestBean);

        DBTestBean fromDb=mFastDatabase.getFirst(DBTestBean.class);
        Assert.assertNull(fromDb);
    }

    @Test
    public void saveListAndGetList(){
        List<DBTestBean> list=new ArrayList<>();
        for(int i=0;i<20;i++)
            list.add(mTestBean.clone());
        mFastDatabase.saveOrUpdate(list);

        List<DBTestBean> fromDb=mFastDatabase.get(DBTestBean.class);
        Assert.assertFalse(fromDb==null||fromDb.isEmpty());
        Assert.assertEquals(list.size(),fromDb.size());
    }

    /**
     * 直接清空整张表
     */
    @Test
    public void dropTable(){
        List<DBTestBean> list=new ArrayList<>();
        for(int i=0;i<20;i++)
            list.add(mTestBean.clone());
        mFastDatabase.saveOrUpdate(list);
        mFastDatabase.dropTable(DBTestBean.class);

        List<DBTestBean> fromDb=mFastDatabase.get(DBTestBean.class);
        Assert.assertTrue(fromDb==null||fromDb.isEmpty());
    }

    /**
     * 过滤取测试(整形)
     */
    @Test
    public void getByAgeFilter(){
        List<DBTestBean> list=new ArrayList<>();
        Random random=new Random();
        int expectCount=0;
        for(int i=0;i<1000;i++){
            DBTestBean testBean=mTestBean.clone();
            testBean.age=random.nextInt(100);
            list.add(testBean);
        }
        for(DBTestBean testBean:list){
            if(testBean.age>mTestBean.age)
                expectCount++;
        }
        mFastDatabase.saveOrUpdate(list);
        List<DBTestBean> fromDb=mFastDatabase.setFilter(And.condition(Condition.bigger("age",mTestBean.age))).get(DBTestBean.class);
        Assert.assertEquals(expectCount,fromDb.size());
    }

    /**
     * 过滤取测试（单精浮点）
     */
    @Test
    public void getByScore(){
        List<DBTestBean> list=new ArrayList<>();
        Random random=new Random();
        int expectCount=0;
        for(int i=0;i<1000;i++){
            DBTestBean testBean=mTestBean.clone();
            testBean.score=random.nextFloat()*100;
            list.add(testBean);
        }
        for(DBTestBean testBean:list){
            if(testBean.score<mTestBean.score)
                expectCount++;
        }
        mFastDatabase.saveOrUpdate(list);

        List<DBTestBean> fromDb=mFastDatabase.setFilter(And.condition(Condition.smaller("score",mTestBean.score))).get(DBTestBean.class);
        Assert.assertEquals(expectCount,fromDb.size());
    }

    /**
     * 过滤取（字符串）
     */
    @Test
    public void getByName(){
        List<DBTestBean> list=new ArrayList<>();
        Random random=new Random();
        int expectCount=0;
        final String fExpectName="50";
        for(int i=0;i<1000;i++){
            DBTestBean testBean=mTestBean.clone();
            testBean.name=Integer.toString(random.nextInt(100));
            list.add(testBean);
        }
        for(DBTestBean testBean:list){
            if(testBean.name.equals(fExpectName))
                expectCount++;
        }
        mFastDatabase.saveOrUpdate(list);

        List<DBTestBean> fromDb=mFastDatabase.setFilter(And.condition(Condition.equal("name",fExpectName))).get(DBTestBean.class);
        Assert.assertEquals(expectCount,fromDb.size());
    }

    /**
     * 获取第10到20条数据
     */
    @Test
    public void getMiddleData(){
        List<DBTestBean> list=new ArrayList<>();
        List<DBTestBean> expectList=new ArrayList<>();
        Random random=new Random();
        for(int i=0;i<1000;i++){
            DBTestBean testBean=mTestBean.clone();
            testBean.age=random.nextInt(100);
            if(i>=10&&i<20){
                expectList.add(testBean);
            }
            list.add(testBean);
        }
        mFastDatabase.saveOrUpdate(list);

        List<DBTestBean> fromDb=mFastDatabase.limit(10,10).get(DBTestBean.class);
        Assert.assertEquals(expectList.size(),fromDb.size());
        for(int i=0;i<fromDb.size();i++){
            Assert.assertEquals(expectList.get(i),fromDb.get(i));
        }
    }

    /**
     * 获取到数据以年龄为标准从小到大排序
     */
    @Test
    public void getOrderData(){
        List<DBTestBean> list=new ArrayList<>();
        Random random=new Random();
        for(int i=0;i<1000;i++){
            DBTestBean testBean=mTestBean.clone();
            testBean.age=random.nextInt(100);
            list.add(testBean);
        }
        mFastDatabase.saveOrUpdate(list);

        List<DBTestBean> fromDb=mFastDatabase.orderBy(true,"age").get(DBTestBean.class);
        Assert.assertTrue(fromDb!=null&&!fromDb.isEmpty());
        int previousAge=fromDb.get(0).age;
        for(DBTestBean testBean:fromDb) {
            Assert.assertTrue(previousAge==testBean.age||previousAge<testBean.age);
            previousAge=testBean.age;
        }
    }
}
