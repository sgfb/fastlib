package com.fastlib.utils.local_data;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.util.SparseArray;
import android.view.View;
import android.widget.AdapterView;

import com.fastlib.BuildConfig;
import com.fastlib.annotation.Bind;
import com.fastlib.annotation.LocalData;
import com.fastlib.db.And;
import com.fastlib.db.Condition;
import com.fastlib.db.FastDatabase;
import com.fastlib.db.SaveUtil;
import com.fastlib.utils.ContextHolder;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by sgfb on 17/2/20.
 * 本地数据注入
 */
public class LocalDataInject{
    private List<Pair<Field,LocalData>> mChildActivityGiver = new ArrayList<>(); //子Activity返回时获取Intent中数据
    private List<Pair<Method,LocalData>> mDelayToggleList=new ArrayList<>(); //延迟触发的本地数据注入方法
    private List<Pair<Method,LocalData>> mChildToggle2=new ArrayList<>();  //子模块返回后触发的方法
    private SparseArray<Object[]> mToggleData = new SparseArray<>(); //触发后读取数据缓存点
    private Map<Class<? extends View>,LocalDataViewActive<?>> mLocalDataViewMap=new HashMap<>();
    private Object mHost;

    public LocalDataInject(@Nullable Object host){
        if(host!=null)
            mHost=host;
    }

    public <V extends View> void putLocalDataViewActive(LocalDataViewActive<V> actives, Class<V> cla){
        mLocalDataViewMap.put(cla,actives);
    }

    public void toggleDelayLocalDataMethod(){
        Bundle bundle=null;

        if(mHost instanceof Activity)
            bundle=((Activity)mHost).getIntent().getExtras();
        else if(mHost instanceof Fragment)
            bundle=((Fragment)mHost).getArguments();
        if(bundle==null) return;    //如果bundle为空说明当前host不支持这个方法

        for(Pair<Method,LocalData> pair:mDelayToggleList){
            LocalData ld=pair.second;
            Object[] args=new Object[ld.value().length];

            for(int i=0;i<args.length;i++){
                String key=ld.value()[i];
                args[i]=bundle.get(key);
            }
            try {
                pair.first.invoke(mHost,args);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 处理子Activity返回的Intent中包含所需的数据
     * @param data 数据包裹
     */
    public void injectChildBack(Intent data){
        for (Pair<Field, LocalData> pair : mChildActivityGiver)
            try {
                loadLocalDataFromIntent(data,pair.first, pair.second);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        for(Pair<Method,LocalData> pair:mChildToggle2){
            if(data==null) return;
            LocalData ld=pair.second;
            Object[] args=new Object[ld.value().length];
            Bundle innerBundle=data.getExtras();
            for(int i=0;i<ld.value().length;i++){
                String key=ld.value()[i];
                args[i]=innerBundle.get(key);
            }
            try {
                pair.first.invoke(mHost,args);
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 本地数据注入
     */
    public void localDataInject(){
        Field[] fields = mHost.getClass().getDeclaredFields();
        //属性注入
        if (fields != null && fields.length > 0) {
            for (Field field : fields) {
                field.setAccessible(true);
                LocalData lr = field.getAnnotation(LocalData.class);
                if (lr == null)
                    continue;
                try {
                    switch (lr.from()) {
                        case INTENT_PARENT:
                            loadLocalDataFromIntent(null,field, lr);
                            break;
                        case INTENT_CHILD:
                            mChildActivityGiver.add(new Pair<>(field, lr));
                            break;
                        case SP:
                            loadLocalDataFromSp(field, lr);
                            break;
                        case DATABASE:
                            loadLocalDataFromDatabase(field, lr);
                            break;
                        case ASSETS:
                            loadLocalDataFromFile(field, lr, true);
                            break;
                        case FILE:
                            loadLocalDataFromFile(field, lr, false);
                            break;
                        default:
                            break;
                    }
                } catch (IllegalAccessException | IOException e) {
                    e.printStackTrace();
                }
            }
        }

        View rootView=null;

        if(mHost instanceof Activity)
            rootView=((Activity)mHost).findViewById(android.R.id.content);
        else if(mHost instanceof Fragment)
            rootView=((Fragment)mHost).getView();
        if(rootView==null) return;
        Method[] methods = mHost.getClass().getDeclaredMethods();
        //"触发数据"注入
        if (methods != null && methods.length > 0) {
            for (final Method m : methods) {
                m.setAccessible(true);
                final LocalData ld = m.getAnnotation(LocalData.class);
                final Bind bind = m.getAnnotation(Bind.class);
                if (ld != null){
                    if(bind != null){ //视图触发
                        View v = rootView.findViewById(bind.value()[0]);
                        switch (bind.bindType()) {
                            case CLICK:
                                v.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        invokeToggleCallback(v, m, ld, bind.bindType(), this, null, null, null);
                                    }
                                });
                                break;
                            case LONG_CLICK:
                                v.setOnLongClickListener(new View.OnLongClickListener() {
                                    @Override
                                    public boolean onLongClick(View v) {
                                        invokeToggleCallback(v, m, ld, bind.bindType(), null, this, null, null);
                                        return false;
                                    }
                                });
                                break;
                            case ITEM_CLICK:
                                ((AdapterView) v).setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        invokeToggleCallback(parent, m, ld, bind.bindType(), null, null, this, null);
                                    }
                                });
                                break;
                            case ITEM_LONG_CLICK:
                                ((AdapterView) v).setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                                    @Override
                                    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                                        invokeToggleCallback(parent, m, ld, bind.bindType(), null, null, null, this);
                                        return false;
                                    }
                                });
                        }
                    }
                    else{ //延迟或子模块触发
                        if(ld.from()== LocalData.GiverType.INTENT_CHILD){
                            mChildToggle2.add(Pair.create(m,ld));
                        }
                        else mDelayToggleList.add(Pair.create(m,ld));
                    }
                }
            }
        }
    }

    private void invokeToggleCallback(View v, Method m, LocalData ld, Bind.BindType type, View.OnClickListener clickListener, View.OnLongClickListener longClickListtener,
                                      AdapterView.OnItemClickListener itemClickListener, AdapterView.OnItemLongClickListener itemLongClickListener) {
        Object[] data = mToggleData.get(v.getId());
        Class<?>[] paramTypes = m.getParameterTypes();
        try {
            if (data == null) { //如果没有则读取一份进入缓存
                if (BuildConfig.isShowLog)
                    System.out.println("缓存中没有触发数据");
                //截断触发事件直到数据读取完毕
                switch (type) {
                    case CLICK:
                        v.setOnClickListener(null);
                        break;
                    case LONG_CLICK:
                        v.setOnLongClickListener(null);
                        break;
                    case ITEM_CLICK:
                        ((AdapterView) v).setOnItemClickListener(null);
                        break;
                    case ITEM_LONG_CLICK:
                        ((AdapterView) v).setOnItemLongClickListener(null);
                        break;
                }
                data = loadLocalData(ld, Arrays.copyOfRange(paramTypes,1,paramTypes.length));
                switch (type) {
                    case CLICK:
                        v.setOnClickListener(clickListener);
                        break;
                    case LONG_CLICK:
                        v.setOnLongClickListener(longClickListtener);
                        break;
                    case ITEM_CLICK:
                        ((AdapterView) v).setOnItemClickListener(itemClickListener);
                        break;
                    case ITEM_LONG_CLICK:
                        ((AdapterView) v).setOnItemLongClickListener(itemLongClickListener);
                        break;
                }
                mToggleData.append(v.getId(), data);
                v.setOnClickListener(clickListener);
            } else if (BuildConfig.isShowLog)
                System.out.println("缓存中有触发数据");
            //View必须在第一个，接下来是参数对象数组
            flatInvoke(m, v, data);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    private void flatInvoke(Method m, View v, Object[] data) throws InvocationTargetException, IllegalAccessException {
        switch (data.length){
            case 1:
                m.invoke(mHost, v, data[0]);
                break;
            case 2:
                m.invoke(mHost, v, data[0], data[1]);
                break;
            case 3:
                m.invoke(mHost, v, data[0], data[1], data[2]);
                break;
            case 4:
                m.invoke(mHost, v, data[0], data[1], data[2], data[3]);
                break;
            case 5:
                m.invoke(mHost, v, data[0], data[1], data[2], data[3], data[4]);
                break;
            case 6:
                m.invoke(mHost, v, data[0], data[1], data[2], data[3], data[4], data[5]);
                break;
            case 7:
                m.invoke(mHost, v, data[0], data[1], data[2], data[3], data[4], data[5], data[6]);
                break;
            case 8:
                m.invoke(mHost, v, data[0], data[1], data[2], data[3], data[4], data[5], data[6], data[7]);
                break;
            case 9:
                m.invoke(mHost, v, data[0], data[1], data[2], data[3], data[4], data[5], data[6], data[7], data[8]);
            case 10:
                m.invoke(mHost, v, data[0], data[1], data[2], data[3], data[4], data[5], data[6], data[7], data[8], data[9]);
                break;
            default:
                break;
        }
    }

    /**
     * 读取本地数据，不支持子Activity返回的Intent
     * @param ld
     * @param param
     * @return
     */
    private Object[] loadLocalData(LocalData ld,Class<?>[] param) {
        Object[] datas = new Object[ld.value().length];
        for (int i = 0; i < datas.length; i++) {
            switch (ld.from()){
                case INTENT_PARENT:
                    datas[i] = loadLocalDataFromIntent(i, ld);
                    break;
                case SP:
                    datas[i] = loadLocalDataFromSp(i, ld, param[i]);
                    break;
                case FILE:
                    datas[i] = loadLocalDataFromFile(i, ld, param[i], false);
                    break;
                case ASSETS:
                    datas[i] = loadLocalDataFromFile(i, ld, param[i], true);
                    break;
                case DATABASE:
                    datas[i] = loadLocalDataFromDatabase(i, ld, param[i]);
                    break;
            }
        }
        return datas;
    }

    /**
     * 读取数据库中的数据,仅主键过滤
     * @param position
     * @param ld
     * @param paramsType
     * @return
     */
    private Object loadLocalDataFromDatabase(int position, LocalData ld, Class<?> paramsType){
        return FastDatabase.getDefaultInstance(ContextHolder.getContext(mHost)).setFilter(And.condition(Condition.equal(ld.value()[position]))).getFirst(paramsType);
    }

    /**
     * 读取外磁卡中或者Assets中的文件的数据.自动判断类型
     * @param position
     * @param ld
     * @param paramsType
     * @param fromAssets
     * @return
     */
    private Object loadLocalDataFromFile(int position, LocalData ld, Class<?> paramsType, boolean fromAssets){
        AssetManager am=null;

        if(mHost instanceof Activity) am=((Activity)mHost).getAssets();
        else if(mHost instanceof Fragment) am=((Fragment)mHost).getActivity().getAssets();
        if(am==null) return null;

        try {
            Gson gson = new Gson();
            byte[] data = fromAssets ? SaveUtil.loadAssetsFile(am,ld.value()[position]) : SaveUtil.loadFile(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + ld.value()[0]);
            if (paramsType == byte[].class)
                return data;
            else if (paramsType == String.class)
                return new String(data);
            else
                return gson.fromJson(new String(data), paramsType);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 读取SharedPreferences中的数据
     * @param position
     * @param ld
     * @param paramType
     * @return
     */
    private Object loadLocalDataFromSp(int position, LocalData ld, Class<?> paramType) {
        Context context=ContextHolder.getContext(mHost);
        SharedPreferences sp = context.getSharedPreferences(BuildConfig.DEFAULT_DATA_FILE_NAME,Context.MODE_PRIVATE);
        if (paramType == boolean.class || paramType == Boolean.class)
            return sp.getBoolean(ld.value()[position], false);
        else if (paramType == int.class || paramType == Integer.class)
            return sp.getInt(ld.value()[position], -1);
        else if (paramType == long.class || paramType == Long.class)
            return sp.getLong(ld.value()[position], -1);
        else if (paramType == float.class || paramType == Float.class)
            return sp.getFloat(ld.value()[position], -1.1f);
        else if (paramType == String.class)
            return sp.getString(ld.value()[position], null);
        else if (paramType == Set.class)
            return sp.getStringSet(ld.value()[position], null);
        else
            return null;
    }

    /**
     * 读取在Intent或Bundle中的数据
     * @param position 对应注解下标
     * @param ld 本地数据注解
     * @return 对应包裹里数据
     */
    private Object loadLocalDataFromIntent(int position, LocalData ld){
        Bundle bundle=null;

        if(mHost instanceof Activity) bundle=((Activity)mHost).getIntent().getExtras();
        else if(mHost instanceof Fragment) bundle=((Fragment)mHost).getArguments();
        if(bundle==null) return null;
        return bundle.get(ld.value()[position]);
    }

    /**
     * 从数据库中加载数据到属性中(仅支持FastDatabase)
     * @param field
     * @param lr
     * @throws IllegalAccessException
     */
    private void loadLocalDataFromDatabase(Field field, LocalData lr) throws IllegalAccessException{
        Class<?> type = field.getType();
        Object obj = FastDatabase.getDefaultInstance(ContextHolder.getContext(mHost)).setFilter(And.condition(Condition.equal(lr.value()[0]))).getFirst(type);
        field.set(mHost,obj);
    }

    /**
     * 从SharedPreferences中加载数据到属性中
     * @param field
     * @param lr
     * @throws IllegalAccessException
     */
    private void loadLocalDataFromSp(Field field, LocalData lr) throws IllegalAccessException{
        SharedPreferences sp =ContextHolder.getContext(mHost).getSharedPreferences(BuildConfig.DEFAULT_DATA_FILE_NAME,Context.MODE_PRIVATE);
        field.set(mHost,sp.getAll().get(lr.value()[0]));
    }

    /**
     * 从文件中加载数据到属性中.取文件时默认取外磁卡位置
     * @param field
     * @param lr
     * @param fromAssets
     * @throws IOException
     * @throws IllegalAccessException
     */
    private void loadLocalDataFromFile(Field field, LocalData lr, boolean fromAssets) throws IOException, IllegalAccessException{
        AssetManager am=null;

        if(mHost instanceof Activity) am=((Activity)mHost).getAssets();
        else if(mHost instanceof Fragment) am=((Fragment)mHost).getActivity().getAssets();
        if(am==null&&fromAssets) return;

        byte[] data = fromAssets ? SaveUtil.loadAssetsFile(am, lr.value()[0]) : SaveUtil.loadFile(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + lr.value());
        if (data == null)
            return;
        Class<?> type = field.getType();
        if (type == byte[].class)
            field.set(mHost, data);
        else if (type == String.class)
            field.set(mHost, new String(data));
        else {
            Gson gson = new Gson();
            field.set(mHost, gson.fromJson(new String(data), type));
        }
    }

    /**
     * 从Intent中加载本地数据
     * @param field 字段
     * @param lr 本地数据注解
     * @throws IllegalAccessException
     */
    private void loadLocalDataFromIntent(@Nullable Intent childIntent, Field field, LocalData lr) throws IllegalAccessException{
//        Object host=mActivity==null?mFragment:mActivity;
//        Intent intent=childIntent;
//        Bundle bundle=mFragment!=null?mFragment.getArguments():null;
//        if(intent==null)
//            intent=mActivity!=null?mActivity.getIntent():null;

        Bundle bundle=null;

        if(childIntent!=null) bundle=childIntent.getExtras();
        else if(mHost instanceof Activity) bundle=((Activity)mHost).getIntent().getExtras();
        else if(mHost instanceof Fragment) bundle=((Fragment)mHost).getArguments();
        if(bundle==null) return;

        Object value=bundle.get(lr.value()[0]);
        if (value != null) {
            LocalDataViewActive active=mLocalDataViewMap.get(field.getType());
            if(active!=null){
                View view= (View) field.get(mHost);
                LocalDataViewActiveImpl.inflaterData(active,view,value);
            }
            else field.set(mHost,value);
        }
    }
}