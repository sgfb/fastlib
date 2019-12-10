package com.fastlib.net2;

/**
 * Created by sgfb on 2019/12/7 0007
 * E-mail:602687446@qq.com
 * Http全流程计时器
 */
public final class HttpTimer{
    /**
     * 初始化
     * 开始连接
     * TTFB
     * 开始下载
     * 结束
     */
    private long[] mProcessTime=new long[5];
    private int mIndex=0;

    public HttpTimer(){
        nextProcess();
    }

    public void nextProcess(){
        if(mIndex>=mProcessTime.length) return;
        mProcessTime[mIndex++]=System.currentTimeMillis();
    }

    public long getInitConsume(){
        if(mProcessTime[0]==0||mProcessTime[1]==0)
            return -1;
        return mProcessTime[1]-mProcessTime[0];
    }

    public long getConnectionConsume(){
        if(mProcessTime[1]==0||mProcessTime[2]==0)
            return -1;
        return mProcessTime[2]-mProcessTime[1];
    }

    public long getTTFB(){
        if(mProcessTime[2]==0||mProcessTime[3]==0)
            return -1;
        return mProcessTime[3]-mProcessTime[2];
    }

    public long getDownloadConsume(){
        if(mProcessTime[3]==0||mProcessTime[4]==0)
            return -1;
        return mProcessTime[4]-mProcessTime[3];
    }
}
