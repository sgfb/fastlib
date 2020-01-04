package com.fastlib.net2;

import android.os.Environment;
import android.support.v4.util.Pair;

import com.fastlib.annotation.NetCallback;
import com.fastlib.db.SaveUtil;
import com.fastlib.net2.core.HeaderDefinition;
import com.fastlib.net2.core.MethodDefinition;
import com.fastlib.net2.core.SimpleHttpCoreImpl;
import com.fastlib.net2.param.interpreter.FormDataInterpreter;
import com.fastlib.net2.param.interpreter.ParamInterpreter;
import com.fastlib.net2.param.interpreter.ParamInterpreterFactor;
import com.fastlib.utils.Reflect;
import com.google.gson.Gson;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 * Created by sgfb on 2019/12/10
 * E-mail:602687446@qq.com
 * 此类对Http进行请求.通过{@link Request}给定的参数经过解释调用{@link SimpleHttpCoreImpl}达到请求和回调
 */
public class HttpProcessor implements Runnable{
    private Request mRequest;
    private Type mCallbackType;
    private File mDownloadFile;
    private InputStream mRawDataInputStream;
    private Exception mException;

    public HttpProcessor(Request request) {
        mRequest = request;
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
        SimpleHttpCoreImpl httpCore= new SimpleHttpCoreImpl(urlWithParam,mRequest.getMethod());

        //填充头部
        for(Map.Entry<String, List<String>> entry:mRequest.getHeader().entrySet()){
            for(String header:entry.getValue()){
                httpCore.addHeader(entry.getKey(),header);
            }
        }

        boolean needClientBody=MethodDefinition.POST.equals(method)||MethodDefinition.PUT.equals(method);
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
            InputStream in=httpCore.getInputStream();
            Downloadable downloadable=mRequest.getDownloadable();
            mCallbackType=resolveResultType(mRequest.getListener());

            //下载类型
            if(downloadable!=null||mCallbackType==File.class){
                if(downloadable!=null){
                    if(downloadable.getTargetFile().isFile())
                        mDownloadFile=downloadable.getTargetFile();
                    else if(downloadable.getTargetFile().isDirectory()){
                        String name;
                        if("GET".equals(mRequest.getMethod().toUpperCase())){
                            String url=mRequest.getUrl();
                            int lastSlash=url.lastIndexOf("/");
                            if(lastSlash!=-1&&lastSlash!=url.length()-1)
                                name=url.substring(lastSlash,url.length());
                            else name="download"+System.currentTimeMillis();
                            mDownloadFile=new File(downloadable.getTargetFile(),name);
                        }
                    }
                }
                if(mDownloadFile==null)
                    mDownloadFile=new File(Environment.getExternalStorageDirectory(),"download"+System.currentTimeMillis());
                SaveUtil.saveToFile(mDownloadFile,in,false);
                mRawDataInputStream=new FileInputStream(mDownloadFile);
            }
            else mRawDataInputStream=new ByteArrayInputStream(SaveUtil.loadInputStream(in,false));
            httpCore.end();
        } catch (Exception e) {
            mException=e;
        }finally {
            callbackProcess();
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
        final Listener listener=mRequest.getListener();
        //如果跳过全局监听回调则置空这个回调
        final GlobalListener globalListener=mRequest.getSkipGlobalListener()?
                HttpGlobalConfig.getInstance().getEmptyGlobalListener():HttpGlobalConfig.getInstance().getGlobalListener();

        if(listener!=null){
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
        }
        globalListener.onRequestComplete();
    }

    /**
     * 解析回调指定类型.如果是Object或byte[]就返回原始字节流,String返回字符,File则联合{@link Request#mDownloadable}来做处理,其它类型就尝试使用gson解析
     * @param listener  监听回调
     * @return  需要回调的类型
     */
    private Type resolveResultType(Listener listener){
        NetCallback netCallback=Reflect.findAnnotation(listener.getClass(),NetCallback.class,true);
        if(netCallback==null) throw new IllegalStateException("NetCallback annotation can't be null!");

        Method[] ms = listener.getClass().getDeclaredMethods();
        for (Method m : ms) {
            String methodFullDescription=m.toString();
            if (netCallback.value().equals(m.getName())&&!methodFullDescription.contains("volatile")){
                //所有参数都必须不是Object,否则当无类型使用
                Type[] paramsType=m.getGenericParameterTypes();
                for(Type type:paramsType){
                    if(type!=Request.class&&type!=Object.class) return type;
                }
            }
        }
        return null;
    }
}
