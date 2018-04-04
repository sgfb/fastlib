package com.fastlib.utils.local_data;

import android.widget.TextView;

/**
 * Created by sgfb on 18/3/6.
 */
public class TextViewDataActive extends LocalDataViewActiveImpl<TextView>{

    @Override
    public void bind(TextView view, int arg) {
        view.setText(arg);
    }

    @Override
    public void bind(TextView view, String arg) {
        view.setText(arg);
    }
}