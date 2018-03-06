package com.fastlib.utils.local_data;

import android.support.v7.widget.AppCompatTextView;

/**
 * Created by sgfb on 18/3/6.
 */
public class AppcompatTextViewDataActive extends LocalDataViewActiveImpl<AppCompatTextView> {

    @Override
    public void bind(AppCompatTextView view, int arg) {
        view.setText(arg);
    }

    @Override
    public void bind(AppCompatTextView view, String arg) {
        view.setText(arg);
    }
}
