package com.fastlib.test.UrlImage.request;

import android.app.Activity;
import android.support.v4.app.Fragment;

/**
 * Created by sgfb on 18/1/23.
 * 图像请求入口
 */
public class BitmapRequestEntrance{

    private BitmapRequestEntrance(){
        //不示例化
    }

    public static BitmapRequestFactory buildBitmapRequestFactory(Activity activity){
        return new BitmapRequestFactory(activity);
    }

    public static BitmapRequestFactory buildBitmapRequestFactory(Fragment fragment){
        return new BitmapRequestFactory(fragment);
    }
}