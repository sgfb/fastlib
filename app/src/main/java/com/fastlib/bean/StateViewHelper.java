package com.fastlib.bean;

import android.view.View;

/**
 * Created by sgfb on 16/8/24.
 */
public class StateViewHelper{
    public int position;
    public ViewHelper helper;

    public StateViewHelper(int position,ViewHelper helper){
        this.position=position;
        this.helper=helper;
    }

    public interface ViewHelper{
        View getView();
    }
}
