package com.fastlib.widget;

import android.content.Context;
import android.net.NetworkInfo;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import com.fastlib.app.EventObserver;
import com.fastlib.utils.NetUtils;

import java.util.Observable;

/**
 * Created by sgfb on 16/4/21.
 * 根据网络状态变换视图
 */
public abstract class AutoReconnectLayout extends FrameLayout{
    protected boolean isLoaded;
    protected OnNetChangedListener mListener;
    private View mCurrentView;
    private EventObserver.OnNetworkListener mObserver=new EventObserver.OnNetworkListener(){

        @Override
        public void onEvent(Context context,NetworkInfo info){
            //如果读取后，不进行视图转换
            if(isLoaded&&mListener!=null) {
                mListener.changed(info!=null&&info.isAvailable());
                return;
            }
            if(mCurrentView!=null)
                removeView(mCurrentView);
            if(info!=null&&info.isAvailable())
                mCurrentView = startLoading();
            else
                mCurrentView= getUnconnectedView();
            addView(mCurrentView);
        }
    };

    public AutoReconnectLayout(Context context){
        this(context, null);
    }

    public AutoReconnectLayout(Context context, AttributeSet attrs){
        super(context, attrs);
        EventObserver.getInstance().addObserver(EventObserver.TYPE_NETWORK,mObserver);
        if(NetUtils.isConnected(context))
            mCurrentView=startLoading();
        else
            mCurrentView=getUnconnectedView();
        addView(mCurrentView);
    }

    /**
     * 未连接或错误的页面
     * @return
     */
    protected abstract View getUnconnectedView();

    /**
     * 读取中页面
     * @return
     */
    protected abstract View startLoading();

    public void clear(){
        EventObserver.getInstance().removeObserver(EventObserver.TYPE_NETWORK,mObserver);
    }

    public interface OnNetChangedListener{
        void changed(boolean available);
    }
}
