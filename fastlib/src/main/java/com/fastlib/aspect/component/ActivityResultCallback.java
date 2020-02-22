package com.fastlib.aspect.component;

import android.content.Intent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sgfb on 2020\02\17.
 * 提供Activity返回时事件监听反回调
 */
public interface ActivityResultCallback{

    void onHandleActivityResult(int requestCode, int resultCode, Intent data);

    final class ActivityResultDelegate{
        List<ActivityResultCallback> callback=new ArrayList<>();

        public List<ActivityResultCallback> getCallback() {
            return callback;
        }

        public void addCallback(ActivityResultCallback callback){
            this.callback.add(callback);
        }
    }
}
