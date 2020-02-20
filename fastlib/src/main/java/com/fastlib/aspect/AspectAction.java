package com.fastlib.aspect;

import java.lang.annotation.Annotation;
import java.util.List;

/**
 * Created by sgfb on 2020\02\17.
 * 切面事件基类
 */
public abstract class AspectAction<T extends Annotation>{
    private List mEnvs;
    private ActionResult mActionResult=new ActionResult();

    public ActionResult handleAction(T anno, List environment, Object[] args){
        mEnvs=environment;
        handleAction(anno,args);
        return mActionResult;
    }

    protected abstract void handleAction(T anno, Object[] args);

    /**
     * 获取环境参数
     * TODO 是否在这里找不到参数就弹出参数缺失异常？
     * @param cla
     * @param <E>
     * @return
     */
    @SuppressWarnings("unchecked")
    protected  <E> E getEnv(Class<E> cla){
        if(mEnvs!=null){
            for(Object env:mEnvs){
                if(cla.isInstance(env)){
                    return (E) env;
                }
            }
        }
        return null;
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
