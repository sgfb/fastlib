package com.fastlib;

import android.graphics.BitmapFactory;
import android.widget.ImageView;

import com.fastlib.base.AbsPreviewImageActivity;

/**
 * Created by sgfb on 17/8/17.
 */
public class ImagePreviewActivity extends AbsPreviewImageActivity{

    @Override
    protected void loadImage(ImageView imageView, String data){
        imageView.setImageBitmap(BitmapFactory.decodeFile(data));
    }

    @Override
    protected void indexChanged(int index) {

    }
}
