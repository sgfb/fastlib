package com.fastlib.app;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.fastlib.annotation.Event;
import com.fastlib.net.NetQueue;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by sgfb on 16/9/1.
 */
public class EventObserver {
    public static final String TAG=EventObserver.class.getSimpleName();
    public static boolean DEBUG=true;

    private static EventObserver mOwer;
    private Map<String,LocalReceiver> mLocalObserver;   //订阅事件名->订阅广播
    private Map<String,List<String>> mLocalObserverMap; //订阅者->订阅事件名
    private Context mContext;

    private EventObserver(Context context){
        mContext=context;
        mLocalObserver=new HashMap<>();
        mLocalObserverMap=new HashMap<>();
    }

    public static synchronized EventObserver getInstance(){
        return mOwer;
    }

    public static synchronized void build(Context context){
        if(mOwer==null)
            mOwer=new EventObserver(context);
    }

    /**
     * 订阅注册者所有有Event注解的本地事件.这个方法将会遍历订阅者所有方法效率略低于subscribe(Object,Class)
     * @param subscriber 订阅者
     */
    public void subscribe(Object subscriber){
        String subscriberName=subscriber.getClass().getCanonicalName();
        LocalBroadcastManager lbm=LocalBroadcastManager.getInstance(mContext);
        List<String> eventNames=mLocalObserverMap.get(subscriberName);
        List<Method> eventMethods=findEventMethods(subscriber);

        if(eventMethods==null||eventMethods.isEmpty()){
            if(DEBUG)
                Log.d(TAG,"订阅者"+subscriberName+"没有广播接受方法,请检查是否添加了Event注解和广播方法参数");
            return;
        }
        if(eventNames==null)
            eventNames=new ArrayList<>();
        for(Method m:eventMethods){
            String eventName=baseClassUpper(m.getParameterTypes()[0]); //仅取第一个参数作为广播目标
            LocalReceiver receiver=mLocalObserver.get(eventName);
            IntentFilter filter=new IntentFilter(eventName);
            if(receiver==null){
                receiver = new LocalReceiver();
                mLocalObserver.put(eventName,receiver);
                lbm.registerReceiver(receiver,filter);
            }
            if(!eventNames.contains(eventName))
                eventNames.add(eventName);
            mLocalObserverMap.put(subscriber.getClass().getCanonicalName(),eventNames);
            receiver.mSubscribes.put(subscriber,m);
            if(DEBUG)
                Log.d(TAG,"订阅者"+subscriberName+"订阅事件"+eventName);
        }
    }

    /**
     * 返回类型名，如果是基本类型转换为引用类
     * @param cla
     * @return
     */
    private String baseClassUpper(Class<?> cla){
        String name=cla.getCanonicalName();
        if(name.equals(int.class.getCanonicalName()))
            return Integer.class.getCanonicalName();
        else if(name.equals(long.class.getCanonicalName()))
            return Long.class.getCanonicalName();
        else if(name.equals(float.class.getCanonicalName()))
            return Float.class.getCanonicalName();
        else if(name.equals(double.class.getCanonicalName()))
            return Double.class.getCanonicalName();
        else if(name.equals(short.class.getCanonicalName()))
            return Short.class.getCanonicalName();
        return name;
    }

    /**
     * 遍历注解Event的接受广播方法
     * @param obj
     * @return
     */
    private List<Method> findEventMethods(Object obj){
        List<Method> eventMethods=new ArrayList<>();
        Method[] allMethod=obj.getClass().getDeclaredMethods();
        if(allMethod==null||allMethod.length==0)
            return null;
        for(Method m:allMethod){
            Annotation anno=m.getAnnotation(Event.class);
            if(anno!=null){
                Class<?>[] params=m.getParameterTypes();
                if(params!=null&&params.length>0) //判断广播接收事件参数是否正常
                    eventMethods.add(m);
            }
        }
        return eventMethods;
    }

    /**
     * 移除某订阅者所有订阅事件
     * @param subscribe 订阅者
     */
    public void unsubscribe(Object subscribe){
        String subscribeName=subscribe.getClass().getCanonicalName();
        List<String> events=mLocalObserverMap.get(subscribeName);
        if(events==null||events.size()<=0)
            return;
        List<String> temp =new ArrayList<>(events);
        for(String event:temp)
            unsubscribe(subscribe,event);
    }

    public void unsubscribe(Object subscriber,Class<?> event){
        unsubscribe(subscriber,event.getCanonicalName());
    }

    /**
     * 移除某订阅者的某个订阅事件
     * @param subscriber 订阅者
     * @param eventName 订阅事件名
     */
    public void unsubscribe(Object subscriber,String eventName){
        String subscribeName=subscriber.getClass().getCanonicalName();
        List<String> events=mLocalObserverMap.get(subscribeName);
        LocalReceiver receiver=mLocalObserver.get(eventName);
        LocalBroadcastManager lbm=LocalBroadcastManager.getInstance(mContext);

        if(events==null) //如果这个订阅者还没订阅过事件就跳出
            return;
        events.remove(eventName);
        receiver.mSubscribes.remove(subscriber);
        if(receiver.mSubscribes.size()==0){
            lbm.unregisterReceiver(receiver);
            mLocalObserver.remove(eventName);
        }
        if(events.size()==0)
            mLocalObserverMap.remove(subscribeName);
        if(DEBUG)
            Log.d(TAG, "订阅者" + subscribeName + "移除事件"+eventName);
    }

    /**
     * 发送本地事件
     * @param event
     */
    public void sendEvent(Object event){
        if(event==null){
            if(DEBUG)
                Log.d(TAG,"无法发送null事件");
            return;
        }
        LocalBroadcastManager lbm=LocalBroadcastManager.getInstance(mContext);
        String name=event.getClass().getCanonicalName();
        Intent intent=new Intent(name);
        EntityWrapper entity=new EntityWrapper(event);
        intent.putExtra("entity",entity);
        lbm.sendBroadcast(intent);
        if(DEBUG)
            Log.d(TAG,"广播事件"+name);
    }

    /**
     * 事件广播中转.运行在主线程中
     */
    public class LocalReceiver extends BroadcastReceiver{
        public Map<Object,Method> mSubscribes; //订阅者->事件方法

        public LocalReceiver(){
            mSubscribes=new HashMap<>();
        }

        @Override
        public void onReceive(Context context, Intent intent){
            final EntityWrapper wrapper= (EntityWrapper) intent.getSerializableExtra("entity");
            List<Object> invisibleSubscriber=new ArrayList<>();
            Iterator<Object> iter=mSubscribes.keySet().iterator();
            while(iter.hasNext()){
                final Object subscribe=iter.next();
                boolean visible=checkVisible(subscribe);
                if(!visible){
                    invisibleSubscriber.add(subscribe);
                    continue;
                }
                try{
                    final Method m=mSubscribes.get(subscribe);
                    Event anno=m.getAnnotation(Event.class);

                    m.setAccessible(true);
                    if(anno.value()) //是否在主线程调用,如果不是进入线程池
                        m.invoke(subscribe,wrapper.obj);
                    else
                        NetQueue.sRequestPool.execute(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    m.invoke(subscribe,wrapper.obj);
                                } catch (IllegalAccessException e) {
                                    Log.w(TAG,"方法调起失败:"+e.getMessage());
                                } catch (InvocationTargetException e) {
                                    Log.w(TAG,"方法调起失败:"+e.getMessage());
                                }
                            }
                        });
                } catch (IllegalAccessException e) {
                    Log.w(TAG,"方法调起失败:"+e.getMessage());
                } catch (InvocationTargetException e) {
                    Log.w(TAG,"方法调起失败:"+e.getMessage());
                }
            }
            for(Object obj:invisibleSubscriber)
                unsubscribe(obj);
        }
    }

    /**
     * 检查订阅是否状态正常(这个是不通用的方法)
     * @param subscriber
     * @return 是否正常 默认为true
     */
    private boolean checkVisible(Object subscriber){
        if(subscriber instanceof Activity){
            Activity activity= (Activity) subscriber;
            if(activity.isFinishing())
                return false;
        }else if(subscriber instanceof Fragment){
            Fragment fragment=(Fragment)subscriber;
            if(fragment.isRemoving()||fragment.isDetached())
                return false;
        }
        return true;
    }

    /**
     * 应用退出时应该调用这个方法清理所有数据
     */
    public void clear(){
        if(DEBUG)
            Log.d(TAG,"清理所有数据");
        clearReceiver();
        mLocalObserver.clear();
        mLocalObserverMap.clear();
        mContext=null;
        mOwer=null;
        System.gc();
    }

    /**
     * 清理所有广播
     */
    private void clearReceiver(){
        LocalBroadcastManager lbm=LocalBroadcastManager.getInstance(mContext);
        Iterator<String> iter=mLocalObserver.keySet().iterator();
        while(iter.hasNext()){
            String key=iter.next();
            LocalReceiver receiver=mLocalObserver.get(key);
            lbm.unregisterReceiver(receiver);
        }
    }

    public class EntityWrapper implements Serializable {
        public Object obj;
        public EntityWrapper(Object obj){
            this.obj=obj;
        }
    }
}