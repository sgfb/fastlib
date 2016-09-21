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

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
     * 订阅本地事件
     * @param subscriber 订阅者
     * @param cla 订阅事件
     */
    public void subscribe(Object subscriber,Class<?> cla){
        String eventName=baseClassUpper(cla);
        String subscriberName=subscriber.getClass().getCanonicalName();
        LocalBroadcastManager lbm=LocalBroadcastManager.getInstance(mContext);
        LocalReceiver receiver=mLocalObserver.get(eventName);
        IntentFilter filter=new IntentFilter(eventName);
        List<String> eventNames=mLocalObserverMap.get(subscriberName);
        //检测是否有注解的事件方法,如果没有则不加入订阅事件
        Method eventMethod=findEventMethod(subscriber,cla);
        if(eventMethod==null){
            if(DEBUG)
                Log.d(TAG,"订阅者"+subscriberName+"没有注解EventMethod,无法订阅事件"+eventName);
            return;
        }

        if(receiver==null){
            receiver = new LocalReceiver();
            mLocalObserver.put(eventName,receiver);
            lbm.registerReceiver(receiver,filter);
        }
        if(eventNames==null)
            eventNames=new ArrayList<>();
        if(!eventNames.contains(eventName))
            eventNames.add(eventName);
        mLocalObserverMap.put(subscriber.getClass().getCanonicalName(), eventNames);
        receiver.mSubscribes.put(subscriber,eventMethod);
        if(DEBUG)
            Log.d(TAG,"订阅者"+subscriberName+" 订阅事件"+eventName);
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
     * 遍历寻找存在的参数是订阅事件类的事件方法(注解EventMethod)
     * @param obj 订阅者
     * @param event 订阅事件
     * @return
     */
    private Method findEventMethod(Object obj,Class<?> event){
        Method method=null;
        Method[] methods=obj.getClass().getDeclaredMethods();
        if(methods==null||methods.length<=0)
            return null;
        for(Method m:methods){
            Annotation anno=m.getAnnotation(Event.class);
            if(anno!=null){
                Class<?>[] params=m.getParameterTypes();
                if(params!=null&&params.length>0&&params[0]==event){
                    method=m;
                    break;
                }
            }
        }
        return method;
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

    public class LocalReceiver extends BroadcastReceiver{
        public Map<Object,Method> mSubscribes; //订阅者->事件方法

        public LocalReceiver(){
            mSubscribes=new HashMap<>();
        }

        @Override
        public void onReceive(Context context, Intent intent){
            EntityWrapper wrapper= (EntityWrapper) intent.getSerializableExtra("entity");
            List<Object> invisibleSubscriber=new ArrayList<>();
            Iterator<Object> iter=mSubscribes.keySet().iterator();
            while(iter.hasNext()){
                Object subscribe=iter.next();
                boolean visible=checkVisible(subscribe);
                if(!visible){
                    invisibleSubscriber.add(subscribe);
                    continue;
                }
                try {
                    mSubscribes.get(subscribe).invoke(subscribe,wrapper.obj);
                } catch (IllegalAccessException e) {
                    //do noting
                } catch (InvocationTargetException e) {
                    // do noting
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