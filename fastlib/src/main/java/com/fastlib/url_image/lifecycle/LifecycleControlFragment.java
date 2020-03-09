package com.fastlib.url_image.lifecycle;

import androidx.fragment.app.Fragment;

/**
 * Created by sgfb on 18/1/15.
 * 控制生命周期回调的一个空Fragment
 */
public class LifecycleControlFragment extends Fragment{
    private HostLifecycle mLifecycle;

    @Override
    public void onResume() {
        super.onResume();
        mLifecycle.onStart(getContext());
    }

    @Override
    public void onPause() {
        super.onPause();
        mLifecycle.onPause(getContext());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mLifecycle.onDestroy(getContext());
    }

    public void setHostLifecycle(HostLifecycle lifecycle){
        mLifecycle=lifecycle;
    }
}
