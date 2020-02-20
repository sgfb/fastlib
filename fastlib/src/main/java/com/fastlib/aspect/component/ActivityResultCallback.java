package com.fastlib.aspect.component;

import android.content.Intent;

/**
 * Created by sgfb on 2020\02\17.
 * 提供Activity返回时事件监听反回调
 */
public interface ActivityResultCallback{

    void onHandleActivityResult(int requestCode, int resultCode, Intent data);

    final class ActivityResultDelegate{
        ActivityResultCallback callback;

        public ActivityResultCallback getCallback() {
            return callback;
        }

        public void setCallback(ActivityResultCallback callback) {
            this.callback = callback;
        }
    }
}
