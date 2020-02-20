package com.fastlib.aspect.component;

import android.app.Activity;

import com.fastlib.aspect.ThreadOn;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by sgfb on 2020\02\19.
 * 同步调起新Activity并且等待返回
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@ThreadOn(ThreadOn.ThreadType.WORK)
public @interface SycStartActivityForResult{

    Class<? extends Activity> value();

    String resultKey();
}
