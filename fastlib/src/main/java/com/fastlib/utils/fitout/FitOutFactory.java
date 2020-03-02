package com.fastlib.utils.fitout;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by sgfb on 2020\03\02.
 * 对打上{@link AutoFit}注解的字段自动装配.如果有指定生成方法则使用指定方法如果没有仅直接Class.newInstance()
 */
public final class FitOutFactory{
    private Map<Class,InstanceMaker> mInstanceMakerMap;
    private static FitOutFactory mInstance;

    private FitOutFactory(){
        mInstanceMakerMap=new HashMap<>();
    }

    public static FitOutFactory getInstance(){
        if(mInstance==null){
            synchronized (FitOutFactory.class){
                mInstance=new FitOutFactory();
            }
        }
        return mInstance;
    }

    public void putInstanceMaker(InstanceMaker maker,Class... classes){
        if(classes==null||classes.length==0) throw new IllegalArgumentException("装配指定类不能为空");

        for(Class cla:classes)
            mInstanceMakerMap.put(cla,maker);
    }

    public void removeInstanceMaker(Class cla){
        mInstanceMakerMap.remove(cla);
    }

    public <T> T fitout(Class<T> cla) throws IllegalAccessException, InstantiationException {
        InstanceMaker<T> maker=mInstanceMakerMap.get(cla);
        if(maker!=null) return maker.makeInstance(cla);

        return cla.newInstance();
    }
}
