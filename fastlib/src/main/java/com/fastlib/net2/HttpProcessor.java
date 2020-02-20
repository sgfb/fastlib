package com.fastlib.net2;

import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.util.Pair;

import com.fastlib.db.SaveUtil;
import com.fastlib.net2.core.HeaderDefinition;
import com.fastlib.net2.core.MethodDefinition;
import com.fastlib.net2.core.SimpleHttpCoreImpl;
import com.fastlib.net2.param.interpreter.FormDataInterpreter;
import com.fastlib.net2.param.interpreter.ParamInterpreter;
import com.fastlib.net2.param.interpreter.ParamInterpreterFactor;
import com.google.gson.Gson;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

/**
 * Created by sgfb on 2019/12/10
 * E-mail:602687446@qq.com
 * 此类对Http进行请求.通过{@link Request}给定的参数经过解释调用{@link SimpleHttpCoreImpl}达到请求和回调
 */
public class HttpProcessor implements Runnable{
    private Request mRequest;
    private Type mCallbackType;
    private InputStream mRawDataInputStream;
    private Exception mException;
    private Executor mCallbackExecutor;
    private Object mResultData;
    private File mDownloadFile;

    public HttpProcessor(Request request) {
        mRequest = request;
        mCallbackExecutor=new Executor() {
            @Override
            public void execute(@NonNull Runnable command) {
                if(!mRequest.getCallbackOnWorkThread())
                    new Handler(Looper.getMainLooper()).post(command);
                else command.run();
            }
        };
    }

    @Override
    public void run(){
        String rootAddress=HttpGlobalConfig.getInstance().getRootAddress();
        String urlWithParam=mRequest.getSkipRootAddress()?mRequest.getUrl():rootAddress+mRequest.getUrl();
        String method=mRequest.getMethod().toUpperCase();
        if("GET".equals(method)){
            try {
                urlWithParam=urlWithParam.concat(concatUrlParam());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        SimpleHttpCoreImpl httpCore= new SimpleHttpCoreImpl(urlWithParam,method);

        //填充头部
        for(Map.Entry<String, List<String>> entry:mRequest.getHeader().entrySet()){
            for(String header:entry.getValue()){
                httpCore.addHeader(entry.getKey(),header);
            }
        }

        boolean needClientBody=MethodDefinition.POST.equals(method)||MethodDefinition.PUT.equals(method);
        boolean needServerBody=!MethodDefinition.HEAD.equals(method);
        if(needClientBody){
            //填充输出体
            String bodyType=checkBodyType();
            String contentType=genContentType(bodyType);
            if(contentType!=null)
                httpCore.addHeader(HeaderDefinition.KEY_CONTENT_TYPE,contentType);
            ParamInterpreter interpreter=ParamInterpreterFactor.getInterpreter(bodyType);
            List<InputStream> inputStreamList=interpreter.interpreter(mRequest.getRequestParam());
            for(InputStream inputStream:inputStreamList) {
                httpCore.addPendingInputStream(inputStream);
            }
        }

        //开始连接
        try {
            if(!mRequest.getSkipGlobalListener())
                HttpGlobalConfig.getInstance().getGlobalListener().onLaunchRequestBefore(mRequest);
            httpCore.begin();
            if(needServerBody){
                InputStream in=httpCore.getInputStream();
                mCallbackType=mRequest.getResultType();
                DownloadStreamController downloadController=mRequest.getDownloadable();

                if(downloadController==null&&mCallbackType==File.class){
                    File randomFile=createRandomFile();
                    if(randomFile==null) throw new IOException("创建文件时异常");
                    downloadController=new SingleDownloadController(randomFile);
                }

                //下载类型
                if(downloadController!=null){
                    long fileLength;
                    String filename=null;

                    String fileLengthHeader=httpCore.getResponseHeader().getHeaderFirst(HeaderDefinition.KEY_CONTENT_LENGTH);
                    fileLength=Long.parseLong(fileLengthHeader);
                    String contentDisposition=httpCore.getResponseHeader().getHeaderFirst(HeaderDefinition.KEY_CONTENT_DISPOSITION);
                    if(contentDisposition!=null){
                        int filenameIndex=contentDisposition.indexOf("filename=\"");
                        if(filenameIndex!=-1)
                            filename=new String(contentDisposition.substring(filenameIndex+10,contentDisposition.length()-1).getBytes("ISO_8859_1"),"utf-8");
                    }
                    downloadController.onStreamReady(in,filename,fileLength);
                    mDownloadFile=downloadController.getSavedFile();
                    mRawDataInputStream=new FileInputStream(mDownloadFile);
                }
                else mRawDataInputStream=new ByteArrayInputStream(SaveUtil.loadInputStream(in,false));
            }
            else mRawDataInputStream=new ByteArrayInputStream(new byte[]{});
            httpCore.end();

            int sendLength=httpCore.getSendHeaderLength()+httpCore.getSendBodyLength();
            int receivedLength=httpCore.getReceivedHeaderLength()+httpCore.getReceivedBodyLength();
            mRequest.setResponseHeader(httpCore.getResponseHeader());
            mRequest.setStatistical(new SimpleStatistical(0,httpCore.getHttpTimer(), new Statistical.ContentLength(sendLength,receivedLength)));
        } catch (Exception e) {
            mException=e;
            e.printStackTrace();
        }finally {
            mCallbackExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    callbackProcess();
                }
            });
        }
    }

    /**
     * 转换参数为url参数
     * @return url地址参数
     */
    private String concatUrlParam() throws IOException {
        List<InputStream> streamList=ParamInterpreterFactor.getInterpreter(ParamInterpreterFactor.BODY_URL_PARAM).interpreter(mRequest.getRequestParam());
        return streamList!=null&&!streamList.isEmpty()?new String(SaveUtil.loadInputStream(streamList.get(0),true)):"";
    }

    /**
     * 自动检查body类型返回.依次检查form-data、json、如果都没有返回form-urlencoded
     * @return body类型
     */
    private @ParamInterpreterFactor.ParamInterpreterType String checkBodyType(){
        List<Pair<String,Object>> bottomParam=mRequest.getRequestParam().getBottomParam();

        for(Pair<String,Object> pair:bottomParam){
            if(pair.second instanceof File)
                return ParamInterpreterFactor.BODY_FORM_DATA;
        }
        if(!bottomParam.isEmpty())
            return ParamInterpreterFactor.BODY_RAW_JSON;
        return ParamInterpreterFactor.BODY_FORM_URLENCODED;
    }

    /**
     * 根据body类型返回ContentType
     * @return Content-Type头部值 如果是null则为空
     */
    private String genContentType(String bodyType){
        switch (bodyType){
            case ParamInterpreterFactor.BODY_FORM_URLENCODED:return "application/x-www-form-urlencoded";
            case ParamInterpreterFactor.BODY_RAW_JSON:return "application/json";
            case ParamInterpreterFactor.BODY_FORM_DATA:return "multipart/form-data; boundary="+FormDataInterpreter.BOUNDARY;
        }
        return null;
    }

    /**
     * 回调回请求方
     */
    @SuppressWarnings("unchecked")
    private void callbackProcess() {
        final Listener listener=mRequest.getListener()!=null?mRequest.getListener():getEmptyListener();
        //如果跳过全局监听回调则置空这个回调
        final GlobalListener globalListener=mRequest.getSkipGlobalListener()?
                HttpGlobalConfig.getInstance().getEmptyGlobalListener():HttpGlobalConfig.getInstance().getGlobalListener();

        //此包裹监听将回调先经过globalListener然后走原监听
        Listener wrapperListener=new Listener() {
            @Override
            public void onRawCallback(Request request, InputStream outputStream) {
                InputStream handledInputStream=globalListener.onRawData(request,outputStream);
                listener.onRawCallback(request,handledInputStream);
            }

            @Override
            public void onResponseSuccess(Request request, Object result) {
                Object handledResult=globalListener.onResponseListener(request,result);
                listener.onResponseSuccess(request,handledResult);
                mResultData=handledResult;
            }

            @Override
            public void onError(Request request, Exception error) {
                Exception handledException=globalListener.onErrorListener(request,error);
                listener.onError(request,handledException);
            }
        };
        if(mException==null){
            try{
                wrapperListener.onRawCallback(mRequest,mRawDataInputStream);
                if(mCallbackType==null||mCallbackType==Object.class||mCallbackType==byte[].class)
                    wrapperListener.onResponseSuccess(mRequest,SaveUtil.loadInputStream(mRawDataInputStream,false));
                else if(mCallbackType==File.class)
                    wrapperListener.onResponseSuccess(mRequest,mDownloadFile);
                else if(mCallbackType==String.class)
                    wrapperListener.onResponseSuccess(mRequest,new String(SaveUtil.loadInputStream(mRawDataInputStream,false)));
                else{
                    Gson gson=new Gson();
                    String json=new String(SaveUtil.loadInputStream(mRawDataInputStream,false));
                    wrapperListener.onResponseSuccess(mRequest,gson.fromJson(json,mCallbackType));
                }
            }catch (IOException e) {
                //这里仅关闭流时可能出现的异常，不处理
            }finally {
                try {
                    mRawDataInputStream.close();
                } catch (IOException e) {
                    //这里仅关闭流时可能出现的异常，不处理
                }
            }
        }
        else wrapperListener.onError(mRequest,mException);
        globalListener.onRequestComplete();
    }

    private Listener getEmptyListener(){
        return new Listener() {
            @Override
            public void onRawCallback(Request request, InputStream outputStream) {

            }

            @Override
            public void onResponseSuccess(Request request, Object result) {

            }

            @Override
            public void onError(Request request, Exception error) {

            }
        };
    }

    public Object getResultData(){
        return mResultData;
    }

    /**
     * 生成一个随机文件
     */
    private File createRandomFile()throws IOException{
        File downloadDirectory=new File(Environment.getExternalStorageDirectory(),Environment.DIRECTORY_DOWNLOADS);
        if(!downloadDirectory.exists())
            downloadDirectory=Environment.getExternalStorageDirectory();
        File file=new File(downloadDirectory,Long.toString(System.currentTimeMillis()));

        boolean success;
        if(file.exists())
            success=file.delete();
        else success=file.createNewFile();
        return success?file:null;
    }
}
