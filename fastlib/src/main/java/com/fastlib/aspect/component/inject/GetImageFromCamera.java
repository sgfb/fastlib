package com.fastlib.aspect.component.inject;

import android.Manifest;

import com.fastlib.annotation.Permission;
import com.fastlib.aspect.component.GetImageFromCameraAction;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by sgfb on 2020\02\18.
 * 调用照相机拍照并返回图片地址,切面事件实现{@link GetImageFromCameraAction}
 */
@Permission({Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE})
@Retention(RetentionPolicy.RUNTIME)
public @interface GetImageFromCamera {
}
