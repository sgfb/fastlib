package com.fastlib.url_image.request;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;

/**
 * Created by sgfb on 18/1/23.
 * 图像请求入口
 */
public class BitmapRequestEntrance{

    private BitmapRequestEntrance(){
        //不实例化
    }

    public static BitmapRequestFactory factory(Context context){
        return new BitmapRequestFactory((Activity) context);
    }

    public static BitmapRequestFactory factory(Fragment fragment){
        return new BitmapRequestFactory(fragment);
    }
}