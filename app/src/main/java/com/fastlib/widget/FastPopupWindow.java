package com.fastlib.widget;

import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.PopupWindow;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by sgfb on 16/2/24.
 *
 * 一个简化的PopupWindow,支持多个Anchor
 */
public class FastPopupWindow {
    private PopupWindow mPopupWindow;
    private View mAnchor;
    private Orientation mOrientation=Orientation.BOTTOM;

    public FastPopupWindow(View contentView){
        this(contentView, null);
    }

    public FastPopupWindow(View contentView, View anchor){
        mAnchor=anchor;
        mPopupWindow=new PopupWindow(contentView,LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT,true);

        mPopupWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
    }

    public void toggle(){
        if(mPopupWindow.isShowing())
            mPopupWindow.dismiss();
        else {
            switch(mOrientation){
                case LEFT:
                    mPopupWindow.showAsDropDown(mAnchor,mPopupWindow.getContentView().getWidth()*-1,mAnchor.getHeight()*-1);
                    break;
                case RIGHT:
                    mPopupWindow.showAsDropDown(mAnchor,mAnchor.getWidth(),mAnchor.getHeight()*-1);
                    break;
                case ABOVE:
                    mPopupWindow.showAsDropDown(mAnchor,0,mAnchor.getHeight()*-1-mPopupWindow.getContentView().getHeight());
                    break;
                case BOTTOM:
                    mPopupWindow.showAsDropDown(mAnchor);
                    break;
                default:
                    break;
            }
        }
    }

    public void toggle(View newAnchor){
        if(mPopupWindow.isShowing())
            mPopupWindow.dismiss();
        mAnchor=newAnchor;
        toggle();
    }

    /**
     * 反射调用setTouchModal。如果为false，touch事件将会被传递到Popupwindow的下面
     * @param touchModal
     */
    public void setTouchModal(boolean touchModal){
        try {
            Method method=mPopupWindow.getClass().getDeclaredMethod("setTouchModal",boolean.class);
            method.setAccessible(true);
            method.invoke(mPopupWindow,touchModal);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置方向，默认是Bottom.必须已经设置了Anchor
     *
     * @param orientation
     */
    public void setOrientation(Orientation orientation){
        mOrientation=orientation;
    }

    public void setAnchor(View anchor){
        mAnchor=anchor;
    }

    public View getAnchor(){
        return mAnchor;
    }

    public PopupWindow getPopupWindow(){
        return mPopupWindow;
    }

    public enum Orientation{
        LEFT,
        RIGHT,
        ABOVE,
        BOTTOM;
    }
}
