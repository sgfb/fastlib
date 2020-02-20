package com.fastlib.aspect.component;

import android.support.annotation.NonNull;

/**
 * Created by sgfb on 2020\02\17.
 */
public interface PermissionCallback {

    void onPermissionResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults);

    final class PermissionDelegate{
        PermissionCallback callback;

        public PermissionCallback getCallback() {
            return callback;
        }

        public void setCallback(PermissionCallback mCallback) {
            this.callback = mCallback;
        }
    }
}
