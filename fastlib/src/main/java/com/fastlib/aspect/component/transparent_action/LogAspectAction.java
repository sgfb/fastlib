package com.fastlib.aspect.component.transparent_action;

import android.support.annotation.Nullable;
import android.util.Log;

import com.fastlib.aspect.AspectTransparentAction;
import com.fastlib.aspect.component.inject.Logcat;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Locale;

/**
 * Created by sgfb on 2020\02\21.
 * 日志输出事件
 */
public class LogAspectAction implements AspectTransparentAction<Logcat> {
    private final static String TAG=LogAspectAction.class.getSimpleName();

    @Override
    public void before(Object host,Logcat anno, Method method, @Nullable List<Annotation> aspectActions) throws Throwable {
        String hostName=host.getClass().getSuperclass().getName();
        String methodName=method.getName();
        String aspectActionsStr=aspectActions==null||aspectActions.isEmpty()?"[]":concatSimpleClassName(aspectActions);
        printLog(String.format(Locale.getDefault(),"%s调用%s和切面事件%s",hostName,methodName,aspectActionsStr));
    }

    @Override
    public void after(Object host,Logcat anno, Method method, @Nullable Object result, @Nullable Exception exception) {
        String hostName=host.getClass().getSuperclass().getName();
        String methodName=method.getName();
        String success=exception==null?"成功":"失败";
        String resultStr=result!=null?"("+result.getClass()+")"+result:"无返回";
        String exceptionStr=exception!=null?"抛出异常 "+exception.getClass():"";
        printLog(String.format(Locale.getDefault(),"%s调用%s%s %s%s",hostName,methodName,success,resultStr,exceptionStr));
    }

    private String concatSimpleClassName(List<Annotation> list){
        StringBuilder sb=new StringBuilder();
        sb.append('[');
        for(Annotation a:list)
            sb.append(a.annotationType().getSimpleName()).append(',');
        if(sb.length()>0)
            sb.deleteCharAt(sb.length()-1);
        sb.append(']');
        return sb.toString();
    }

    private void printLog(String log){
        Log.d(TAG,log);
    }
}
