package com.fastlib.aspect;

import android.support.annotation.NonNull;
import android.util.SparseArray;

import com.fastlib.aspect.exception.EnvMissingException;
import com.fastlib.aspect.exception.LockNotFoundException;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by sgfb on 2020\02\17.
 * 切面事件基类
 */
public abstract class AspectAction<T extends Annotation>{
    private static final List<CrossLock> sIdleLock=new ArrayList<>();
    private static final SparseArray<CrossLock> sUsedLock =new SparseArray<>();
    private List mEnvs;
    private ActionResult mActionResult=new ActionResult();

    protected static class CrossLock{
        private static AtomicInteger autoIncrease=new AtomicInteger();

        private int id;

        public CrossLock() {
            id = autoIncrease.addAndGet(1);
        }

        public int getId(){
            return id;
        }

        public void lock(){
            synchronized (CrossLock.this){
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        public void unlock(){
            synchronized (CrossLock.this){
                backLock(id);
                notify();
            }
        }

        private void reset(){
            id = autoIncrease.addAndGet(1);
        }

        private void backLock(int id){
            CrossLock crossLock=sUsedLock.get(id);
            if(crossLock!=null){
                sUsedLock.remove(id);
                sIdleLock.add(crossLock);
            }
            else throw new LockNotFoundException();
        }
    }

    protected CrossLock obtainLock(){
        CrossLock resultLock;
        if(!sIdleLock.isEmpty()){
            resultLock=sIdleLock.remove(sIdleLock.size()-1);
            resultLock.reset();
        }
        else resultLock=new CrossLock();

        sUsedLock.put(resultLock.id,resultLock);
        return resultLock;
    }

    public ActionResult handleAction(T anno,Method method,List environment, Object[] args){
        mActionResult.reset();
        mEnvs=environment;
        handleAction(anno,method,args);
        return mActionResult;
    }

    protected abstract void handleAction(T anno, Method method,Object[] args);

    /**
     * 获取环境参数
     */
    @SuppressWarnings("unchecked")
    protected @NonNull <E> E getEnv(Class<E> cla){
        if(mEnvs!=null){
            for(Object env:mEnvs){
                if(cla.isInstance(env)){
                    return (E) env;
                }
            }
        }
        throw new EnvMissingException("事件"+getClass().getSimpleName()+"环境参数缺失"+cla.getSimpleName());
    }

    protected void setPassed(boolean passed){
        mActionResult.isPassed=passed;
    }

    protected void setResult(Object result){
        mActionResult.result=result;
    }

    protected void setActionCallback(MethodResultCallback callback){
        mActionResult.rawResultCallback=callback;
    }
}
