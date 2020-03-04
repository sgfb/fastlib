package com.fastlib.utils.fitout;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by sgfb on 2020\03\02.
 * 对打上{@link AutoFit}注解的字段自动装配.如果有指定生成方法则使用指定方法如果没有仅直接Class.newInstance()
 */
public final class FitoutFactory {
    private Map<Class,InstanceMaker> mInstanceMakerMap;
    private Map<Class,AttachmentFitout> mAttachmentFitoutMap;   //附件类-->附件装配类
    private static FitoutFactory mInstance;

    private FitoutFactory(){
        mInstanceMakerMap=new HashMap<>();
        mAttachmentFitoutMap=new HashMap<>();
    }

    public static FitoutFactory getInstance(){
        if(mInstance==null){
            synchronized (FitoutFactory.class){
                mInstance=new FitoutFactory();
            }
        }
        return mInstance;
    }

    public <T> T fitout(Class<T> cla) throws IllegalAccessException, InstantiationException {
        InstanceMaker<T> maker=mInstanceMakerMap.get(cla);
        if(maker!=null) return maker.makeInstance(cla);

        return cla.newInstance();
    }

    public static void autoFitout(Object host) throws IllegalAccessException, InstantiationException {
        autoFitout(host,host.getClass());
    }

    public static void autoFitout(Object host,Class cla) throws InstantiationException, IllegalAccessException {
        FitoutFactory fitoutFactory=getInstance();
        for(Field field:cla.getDeclaredFields()){
            AutoFit autoFit=field.getAnnotation(AutoFit.class);
            if(autoFit!=null){
                if(autoFit.attachment()!=void.class){
                    Class attachmentClass=autoFit.attachment();
                    Class upwardClass=autoFit.attachment();
                    AttachmentFitout attachmentFitout;

                    do{
                        attachmentFitout=fitoutFactory.mAttachmentFitoutMap.get(upwardClass);
                        upwardClass=upwardClass.getSuperclass();
                    }
                    while(attachmentFitout==null&&upwardClass!=null);

                    if(attachmentFitout==null)
                        throw new IllegalArgumentException("附件("+attachmentClass.getSimpleName()+")没有对应的装配类");

                    field.setAccessible(true);
                    Object fieldInstance=field.get(host);
                    if(fieldInstance==null)
                        throw new IllegalArgumentException("字段"+field.getName()+"在附件装配的时候不能为空");

                    attachmentFitout.fitout(fieldInstance,fitoutFactory.fitout(attachmentClass));
                }
                else{
                    Object newInstance=fitoutFactory.fitout(field.getType());
                    field.setAccessible(true);
                    field.set(host,newInstance);
                }
            }
        }
    }

    public void putInstanceMaker(InstanceMaker maker,Class... classes){
        if(classes==null||classes.length==0) throw new IllegalArgumentException("装配指定类不能为空");

        for(Class cla:classes)
            mInstanceMakerMap.put(cla,maker);
    }

    public void removeInstanceMaker(Class cla){
        mInstanceMakerMap.remove(cla);
    }

    public void putAttachmentFitout(AttachmentFitout fitout,Class... classes){
        if(classes==null||classes.length==0) throw new IllegalArgumentException("附件装配指定类不能为空");

        for(Class cla:classes)
            mAttachmentFitoutMap.put(cla,fitout);
    }

    public void removeAttachmentFitout(Class cla){
        mAttachmentFitoutMap.remove(cla);
    }
}
