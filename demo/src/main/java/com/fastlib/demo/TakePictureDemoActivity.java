package com.fastlib.demo;

import android.Manifest;
import android.app.Activity;
import android.content.Context;

import com.fastlib.app.module.FastActivity;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Options;

import java.util.List;

/**
 * Created by sgfb on 2020\03\05.
 */
public class TakePictureDemoActivity extends FastActivity{

    @Override
    public void alreadyPrepared() {
        Options permissionHelp=AndPermission.with(this);
        permissionHelp.runtime()
                .permission(Manifest.permission.CAMERA)
                .onGranted(new Action<List<String>>() {
                    @Override
                    public void onAction(List<String> data) {

                    }
                })
                .onDenied(new Action<List<String>>() {
                    @Override
                    public void onAction(List<String> data) {

                    }
                })
                .start();
    }
}
