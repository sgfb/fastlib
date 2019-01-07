package com.fastlib.net;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.fastlib.BuildConfig;
import com.fastlib.net.listener.GlobalListener;
import com.fastlib.net.param_parse.NetParamParser;
import com.fastlib.net.param_parse.ParamParserManager;

import java.io.IOException;
import java.util.concurrent.Executors;
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
    public static ThreadPoolExecutor sRequestPool =(ThreadPoolExecutor) Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()+2); //公共网络请求池
    private String mRootAddress;
    private GlobalListener mGlobalListener; //一个全局的事件回调监听，所有网络回调给具体回调之前做一次回调
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
        } catch (IOException e) {
            //不会被触发，丢弃异常处理
        }
    }

    /**
     * 立即返回模式请求网络任务
     * @param request 网络请求
     * @return 服务器返回数据
     * @throws IOException 触发的异常
     */
    public byte[] netRequestPromptlyBack(Request request) throws IOException {
        request=prepareRequest(request);
        return enqueue(request,true);
    }

    /**
     * 网络请求内部入队列处理，只有立即返回模式才会触发异常
     * @param request 网络请求
     * @param promptlyBackMode 标识立即返回模式
     */
    private byte[] enqueue(Request request,boolean promptlyBackMode)throws IOException{
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
        else sRequestPool.execute(processor);
        return null;
    }

    private Request prepareRequest(Request request){
//        if(mGlobalData!=null&&request.isUseFactory()){
//            if(mGlobalData.mParams!=null&&mGlobalData.mParams.length>0){
//                List<Pair<String,String>> params=request.getParamsRaw();
//                if(params==null){
//                    params=new ArrayList<>();
//                    Collections.addAll(params,mGlobalData.mParams);
//                    request.setParams(params);
//                }
//                else
//                    for(Pair<String,String> pair:mGlobalData.mParams)
//                        if(!params.contains(pair))
//                            params.add(pair);
//            }
//            if(mGlobalData.mHeads!=null&&mGlobalData.mHeads.length>0){
//                List<Request.ExtraHeader> heads=request.getSendHeadExtra();
//                if(heads==null){
//                    heads=new ArrayList<>();
//                    Collections.addAll(heads,mGlobalData.mHeads);
//                    request.setSendHeader(heads);
//                }
//                else{
//                    for(Request.ExtraHeader header:mGlobalData.mHeads)
//                        if(!heads.contains(header))
//                            heads.add(header);
//                }
//            }
//            if(mGlobalData.mCookies!=null&&!mGlobalData.mCookies.isEmpty()){ //全局预加载Cookies
//                List<Pair<String, String>> cookies=request.getSendCookies();
//                if(cookies==null)
//                    request.setSendCookies(mGlobalData.mCookies);
//                else{
//                    for(Pair<String,String> newCookie:mGlobalData.mCookies)
//                        if(!cookies.contains(newCookie))
//                            cookies.add(newCookie);
//                }
//            }
//        }
        if(mGlobalListener!=null)
            mGlobalListener.onLauncherRequestBefore(request);
        if(!TextUtils.isEmpty(mRootAddress)&&!request.isHadRootAddress()){ //添加根地址，如果需要的话
            request.setUrl(mRootAddress + request.getUrl());
            request.setHadRootAddress(true);
        }
        return request;
    }

    public void close(){
        sRequestPool.shutdownNow();
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