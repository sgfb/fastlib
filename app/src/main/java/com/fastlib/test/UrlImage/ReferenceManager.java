package com.fastlib.test.UrlImage;

import android.widget.ImageView;

import com.fastlib.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sgfb on 2017/11/4.
 * 引用管理器.ImageView对Uri引用
 */
public class ReferenceManager{
    private Map<BitmapRequest,List<ImageView>> mReference; //Bitmap请求键ImageView值
    private BitmapPool mBitmapPool;

    private ReferenceManager(){
        mReference=new HashMap<>();
        mBitmapPool=new BitmapPool();
    }

    public void addBitmapRequest(BitmapRequest request,ImageView imageView){
        List<ImageView> list=mReference.get(request);

        if(list==null) { //list为空说明映射中不存在这个请求
            list = new ArrayList<>();
            mReference.put(request,list);
        }
        else{
            if(!list.contains(imageView)) {
                list.add(imageView);
                imageView.setTag(R.id.urlImage,request);
            }
        }

    }

    public BitmapPool getBitmapPool(){
        return mBitmapPool;
    }
}