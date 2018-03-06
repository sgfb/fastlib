package com.fastlib;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by sgfb on 18/3/6.
 */

public class Bean implements Serializable{
    public int id;
    public String name;

    public Bean(){

    }

    @Override
    public String toString() {
        return "id:"+id+" name:"+name;
    }
}
