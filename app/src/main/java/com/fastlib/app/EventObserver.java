package com.fastlib.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.BatteryManager;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 将常用的系统广播集中起来，动态启动广播监听（只要有一个监听者就会开启对应广播，否则关闭广播）
 * 本地的事件传输.
 */
public class EventObserver {
    public static String TYPE_NETWORK=ConnectivityManager.CONNECTIVITY_ACTION;
    public static String TYPE_BATTERY_CHANGED =Intent.ACTION_BATTERY_CHANGED;
    public static String TYPE_BATTERY_LOW=Intent.ACTION_BATTERY_LOW;
    public static String TYPE_BATTERY_OKAY=Intent.ACTION_BATTERY_OKAY;
    public static String TYPE_HEADSET_PLUG=Intent.ACTION_HEADSET_PLUG;
    public static String TYPE_TIME_TICK=Intent.ACTION_TIME_TICK;
    public static String TYPE_BOOT_COMPLETED=Intent.ACTION_BOOT_COMPLETED;
    public static String TYPE_PACKAGE_ADDED=Intent.ACTION_PACKAGE_ADDED;
    public static String TYPE_PACKAGE_REMOVED=Intent.ACTION_PACKAGE_REMOVED;
    public static String[] TYPE_ALL={
            TYPE_NETWORK,TYPE_BATTERY_CHANGED,TYPE_BATTERY_LOW,TYPE_BATTERY_OKAY,
            TYPE_HEADSET_PLUG, TYPE_TIME_TICK,TYPE_BOOT_COMPLETED,TYPE_PACKAGE_ADDED,
            TYPE_PACKAGE_REMOVED};

    public static String VALUE_HEADSET_NAME="name";
    public static String VALUE_HEADSET_STATE="state";
    public static String VALUE_HEADSET_MICROPHONE="microphone";
    public static String VALUE_BATTERY_STATUS=BatteryManager.EXTRA_STATUS;
    public static String VALUE_BATTERY_HEALTH=BatteryManager.EXTRA_HEALTH;
    public static String VALUE_BATTERY_PRESENT=BatteryManager.EXTRA_PRESENT;
    public static String VALUE_BATTERY_LEVEL=BatteryManager.EXTRA_LEVEL;
    public static String VALUE_BATTERY_SCALE=BatteryManager.EXTRA_SCALE;
    public static String VALUE_BATTERY_SMALLICON=BatteryManager.EXTRA_ICON_SMALL;
    public static String VALUE_BATTERY_PLUGGED=BatteryManager.EXTRA_PLUGGED;
    public static String VALUE_BATTERY_VOLTAGE=BatteryManager.EXTRA_VOLTAGE;
    public static String VALUE_BATTERY_TEMPERATURE=BatteryManager.EXTRA_TEMPERATURE;
    public static String VALUE_BATTERY_TECHNOLOGY=BatteryManager.EXTRA_TECHNOLOGY;

    private static Map<String,ReceiverWrapper> mObserver;
    private static Map<String,List<LocalReceiver>> mLocalObserver;
    private static EventObserver mOwer;
    private static Context mContext;

    private EventObserver(Context context){
        mContext=context;
        mObserver =new HashMap<>();
        mLocalObserver=new HashMap<>();
        for(String type:TYPE_ALL){
            mObserver.put(type, new ReceiverWrapper(new Receiver(), new ArrayList<OnEventListener>()));
        }
    }

    public static synchronized EventObserver getInstance(){
        return mOwer;
    }

    public static synchronized void build(Context context){
        if(mOwer==null)
            mOwer=new EventObserver(context);
    }

    public void addObserver(String type,OnEventListener l){
        ReceiverWrapper wrapper= mObserver.get(type);
        if(wrapper.getListeners().size()==0){
            IntentFilter filter=new IntentFilter(type);
            mContext.registerReceiver(wrapper.getReceiver(),filter);
        }
        wrapper.getListeners().add(l);
    }

    public void removeObserver(String type,Context context,OnEventListener l){
        ReceiverWrapper wrapper= mObserver.get(type);
        List<OnEventListener> list=wrapper.getListeners();
        if(list.remove(l)&&list.size()==0)
            context.unregisterReceiver(wrapper.getReceiver());
    }

    public void removeAllObserver(){
        Iterator<String> iter= mObserver.keySet().iterator();

        while(iter.hasNext()){
            String type=iter.next();
            ReceiverWrapper wrapper= mObserver.get(type);
            //如果有监听，那么广播一定是开启的
            if(wrapper.getListeners().size()>0)
                mContext.unregisterReceiver(wrapper.getReceiver());
        }
    }

    /**
     * 订阅本地事件
     * @param cls
     * @param event
     */
    public void subscribe(Class cls,OnLocalEvent event){
        String name=cls.getCanonicalName();
        LocalBroadcastManager lbm=LocalBroadcastManager.getInstance(mContext);
        LocalReceiver receiver=new LocalReceiver(event);
        IntentFilter filter=new IntentFilter(name);
        List<LocalReceiver> list=mLocalObserver.get(name);

        if(list==null){
            list=new ArrayList<>();
            mLocalObserver.put(name,list);
        }
        list.add(receiver);
        lbm.registerReceiver(receiver,filter);
    }

    /**
     * 发送本地事件
     * @param subEvent
     */
    public void sendLocalEvent(Object subEvent){
        LocalBroadcastManager lbm=LocalBroadcastManager.getInstance(mContext);
        String name=subEvent.getClass().getCanonicalName();
        Intent intent=new Intent(name);
        EntityWrapper entity=new EntityWrapper(subEvent);
        intent.putExtra("entity", entity);
        lbm.sendBroadcast(intent);
    }

    public void clear(){
        removeAllObserver();
        removeAllLocalObserer();
    }

    /**
     * 清理所有本地事件订阅(这个方法必须在程序结束之前被调用)
     */
    public void removeAllLocalObserer(){
        Iterator<String> iter=mLocalObserver.keySet().iterator();
        LocalBroadcastManager lbm=LocalBroadcastManager.getInstance(mContext);

        while(iter.hasNext()){
            String key=iter.next();
            List<LocalReceiver> list=mLocalObserver.get(key);
            for(LocalReceiver r:list)
                lbm.unregisterReceiver(r);
            list.clear();
        }
        mLocalObserver.clear();
    }

    public interface OnEventListener{

    }

    public static abstract class OnHeadsetListener implements OnEventListener{
        public abstract void onEvent(Context context,int state,int microphone,String name);
    }

    public static abstract class OnNetworkListener implements OnEventListener{
        public abstract void onEvent(Context context,NetworkInfo info);
    }

    public static abstract class OnBatteryListener implements OnEventListener{
        public abstract void onEvent(boolean present,int status,int health,int level,
                                     int scale,int smallIcon,int plugged,int voltage,int temperature,String technology);
    }

    class ReceiverWrapper{
        List<OnEventListener> listeners;
        Receiver receiver;

        public ReceiverWrapper(Receiver r,List<OnEventListener> ls){
            receiver=r;
            listeners=ls;
        }

        public List<OnEventListener> getListeners() {
            return listeners;
        }

        public void setListeners(List<OnEventListener> listeners) {
            this.listeners = listeners;
        }

        public Receiver getReceiver() {
            return receiver;
        }

        public void setReceiver(Receiver receiver) {
            this.receiver = receiver;
        }
    }

    class Receiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent){
            String action=intent.getAction();
            if(action.equals(TYPE_NETWORK)){
                ConnectivityManager cm=(ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo info=cm.getActiveNetworkInfo();
                Iterator<OnEventListener> iter=getListenerIter(TYPE_NETWORK);

                while(iter.hasNext()){
                    OnNetworkListener l=(OnNetworkListener)iter.next();
                    l.onEvent(context,info);
                }
            }
            else if(action.equals(TYPE_HEADSET_PLUG)){
                Bundle bundle=intent.getExtras();
                Iterator<OnEventListener> iter=getListenerIter(TYPE_HEADSET_PLUG);

                while(iter.hasNext()){
                    OnHeadsetListener l=(OnHeadsetListener)iter.next();
                    l.onEvent(context,bundle.getInt(VALUE_HEADSET_STATE),bundle.getInt(VALUE_HEADSET_MICROPHONE),bundle.getString(VALUE_HEADSET_NAME));
                }
            }
            else if(action.equals(TYPE_BATTERY_CHANGED)){
                Bundle bundle=intent.getExtras();
                Iterator<OnEventListener> iter=getListenerIter(TYPE_BATTERY_CHANGED);

                while(iter.hasNext()){
                    OnBatteryListener l=(OnBatteryListener)iter.next();
                    boolean present=bundle.getBoolean(VALUE_BATTERY_PRESENT);
                    int health=bundle.getInt(VALUE_BATTERY_HEALTH);
                    int status=bundle.getInt(VALUE_BATTERY_STATUS);
                    int level=bundle.getInt(VALUE_BATTERY_LEVEL);
                    int scale=bundle.getInt(VALUE_BATTERY_SCALE);
                    int smallIcon=bundle.getInt(VALUE_BATTERY_SMALLICON);
                    int plugged=bundle.getInt(VALUE_BATTERY_PLUGGED);
                    int voltage=bundle.getInt(VALUE_BATTERY_VOLTAGE);
                    int temperature=bundle.getInt(VALUE_BATTERY_TEMPERATURE);
                    String technology=bundle.getString(VALUE_BATTERY_TECHNOLOGY);

                    l.onEvent(present,status,health,level,scale,smallIcon,plugged,voltage,temperature,technology);
                }
            }
        }
    }

    public Iterator getListenerIter(String type){
        ReceiverWrapper wrapper= mObserver.get(type);
        List<OnEventListener> list=wrapper.getListeners();
        return list.iterator();
    }

    public interface OnLocalEvent {
        void onEvent(Object subEvent);
    }

    public class EntityWrapper implements Serializable {
        public Object obj;

        public EntityWrapper(Object obj){
            this.obj=obj;
        }
    }

    public class LocalReceiver extends BroadcastReceiver{
        OnLocalEvent event;

        public LocalReceiver(OnLocalEvent event){
            this.event=event;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            EntityWrapper wrapper= (EntityWrapper) intent.getSerializableExtra("entity");
            event.onEvent(wrapper.obj);
        }
    }
}
