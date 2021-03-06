package com.fastlib.net;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.fastlib.BuildConfig;
import com.fastlib.app.task.ThreadPoolManager;
import com.fastlib.net.listener.GlobalListener;
import com.fastlib.net.param_parse.NetParamParser;
import com.fastlib.net.param_parse.ParamParserManager;

import java.io.IOException;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by sgfb on 16/9/1.
 * 这个类不具体处理网络事务，只分发任务和数据统计，调整网络配置，注重任务调配和任务处理结果统计<br/>
 * 网络在类被实现的时候开始工作（调配任务），在断网或者需要保存请求缓存的时候可以正确的保存请求等待下次使用
 */
public class NetManager{
    private static NetManager mOwer;
    public int mRequestCount=0;
    public long Tx,Rx;
    private String mRootAddress;
    private GlobalListener mGlobalListener;         //一个全局的事件回调监听，所有网络回调给具体回调之前做一次回调
    private ParamParserManager mGlobalParamParserManager;

    private NetManager(){
        mGlobalParamParserManager=new ParamParserManager();
    }

    public static synchronized NetManager getInstance(){
        if(mOwer==null)
            mOwer = new NetManager();
        return mOwer;
    }

    /**
     * 网络任务入队列
     * @param request 网络请求
     */
    public void netRequest(Request request){
        request=prepareRequest(request);
        try {
            enqueue(request,false);
        } catch (Exception e) {
            //不会被触发，丢弃异常处理
        }
    }

    /**
     * 立即返回模式请求网络任务
     * @param request 网络请求
     * @return 服务器返回数据
     * @throws IOException 触发的异常
     */
    public byte[] netRequestPromptlyBack(Request request) throws Exception {
        request=prepareRequest(request);
        return enqueue(request,true);
    }

    /**
     * 网络请求内部入队列处理，只有立即返回模式才会触发异常
     * @param request 网络请求
     * @param promptlyBackMode 标识立即返回模式
     */
    private byte[] enqueue(Request request,boolean promptlyBackMode)throws Exception{
        ThreadPoolExecutor pool=request.getExecutor();
        NetProcessor processor=new NetProcessor(request,new NetProcessor.OnCompleteListener(){
            @Override
            public void onComplete(NetProcessor processor1){
                mRequestCount++;
                Tx+=processor1.getTx();
                Rx+=processor1.getRx();
                if(BuildConfig.isShowLog)
                    System.out.println(processor1);
            }
        },new Handler(Looper.getMainLooper()));
        if(promptlyBackMode){ //如果是立即返回模式，不进入线程池直接运行后返回数据
            processor.run();
            return processor.getResponse();
        }
        if(pool!=null) pool.execute(processor);
        else ThreadPoolManager.sSlowPool.execute(processor);
        return null;
    }

    private Request prepareRequest(Request request){
        if(mGlobalListener!=null&&request.isAcceptGlobalCallback())
            mGlobalListener.onLaunchRequestBefore(request);
        if(!TextUtils.isEmpty(mRootAddress)&&request.getCustomRootAddress()==null){ //根地址替换，如果需要的话
            request.setCustomRootAddress(mRootAddress);
        }
        return request;
    }

    public void close(){
        ThreadPoolManager.sSlowPool.shutdownNow();
        mOwer=null;
    }

    public String getRootAddress() {
        return mRootAddress;
    }

    public void setRootAddress(String rootAddress) {
        mRootAddress = rootAddress;
    }

    public GlobalListener getGlobalListener() {
        return mGlobalListener;
    }

    public void setGlobalListener(GlobalListener globalListener) {
        mGlobalListener = globalListener;
    }

    public void putParamParser(NetParamParser netParamParser){
        mGlobalParamParserManager.putParser(netParamParser);
    }

    public void removeParamParser(NetParamParser netParamParser){
        mGlobalParamParserManager.removeParser(netParamParser);
    }

    public ParamParserManager getGlobalParamParserManager(){
        return mGlobalParamParserManager;
    }
}