package com.fastlib.image_manager.request;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;

import com.fastlib.R;

/**
 * Created by sgfb on 19/2/12.
 * E-mail: 602687446@qq.com
 * ImageView回调包裹
 */
public class Callback2ImageView implements CallbackParcel{
    private ImageView mImageView;

    public Callback2ImageView(ImageView imageView){
        mImageView=imageView;
    }

    @Override
    public void prepareLoad(ImageRequest request){
        Object tag=mImageView.getTag(R.id.urlImage);
        if(tag instanceof ImageRequest)
            ((ImageRequest) tag).cancel();
        mImageView.setTag(R.id.urlImage,request);
        //当ImageView的width和height不是match_parent和wrap_content时获取宽高
        if(request.getRequestWidth()==0&&request.getRequestHeight()==0){
            LayoutParams lp=mImageView.getLayoutParams();
            if(lp!=null&&lp.width>=0&&lp.height>=0){
                request.setRequestWidth(lp.width);
                request.setRequestHeight(lp.height);
            }
        }
        mImageView.setImageDrawable(request.getReplaceDrawable());
    }

    @Override
    public void success(final ImageRequest request, final Bitmap bitmap){
        Handler handler=new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                mImageView.setImageBitmap(bitmap);
                if(request.getAnimator()!=null) request.getAnimator().animator(mImageView);
            }
        });
    }

    @Override
    public void failure(ImageRequest request, Exception exception){
        if(!request.isCanceled()) mImageView.setImageDrawable(request.getErrorDrawable());
    }
}
