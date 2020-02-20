package com.fastlib.aspect.component;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.fastlib.aspect.AspectAction;

/**
 * Created by sgfb on 2020\02\19.
 * 同步调起新Activity并且等待返回
 */
public class SycStartActivityForResultAction extends AspectAction<SycStartActivityForResult>{

    @Override
    protected void handleAction(final SycStartActivityForResult anno, Object[] args) {
        ActivityResultCallback.ActivityResultDelegate delegate=getEnv(ActivityResultCallback.ActivityResultDelegate.class);
        Activity activity=getEnv(Activity.class);

        delegate.setCallback(new ActivityResultCallback() {
            @Override
            public void onHandleActivityResult(int requestCode, int resultCode, Intent data) {
                if(resultCode==Activity.RESULT_OK&&requestCode==1){
                    Bundle bundle=data.getExtras();
                    setResult(bundle!=null?bundle.get(anno.resultKey()):null);
                }
                synchronized (SycStartActivityForResultAction.this){
                    SycStartActivityForResultAction.this.notifyAll();
                }
            }
        });
        activity.startActivityForResult(new Intent(activity,anno.value()),1);
        synchronized (this){
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        setPassed(true);
    }
}
