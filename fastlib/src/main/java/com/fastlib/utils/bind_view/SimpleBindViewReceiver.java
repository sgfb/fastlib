package com.fastlib.utils.bind_view;

/**
 * Created by sgfb on 2020\03\03.
 */
public abstract class SimpleBindViewReceiver implements OnBindViewReceiver{
    protected OnBindViewCallback mCallback;

    @Override
    public void setOnBindViewCallback(OnBindViewCallback callback) {
        mCallback=callback;
    }
}
