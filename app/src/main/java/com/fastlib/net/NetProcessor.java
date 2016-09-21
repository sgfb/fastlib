package com.fastlib.net;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.TextUtils;

import com.fastlib.app.EventObserver;
import com.fastlib.bean.EventDownloading;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.Executor;

/**
 * 网络请求的具体处理.在结束时会保存一些状态<br/>
 * 这个类可以上传和下载文件,支持中断,下载文件时每秒发送一次进度广播
 */
public class NetProcessor implements Runnable{
    private final String BOUNDARY=Long.toHexString(System.currentTimeMillis());
    private final String CRLF="\r\n";
    private final String END="--"+BOUNDARY+"--"+CRLF;
    private final int BUFF_LENGTH=1024;
    private final int CHUNK_LENGTH=4096;

    private long Tx,Rx;
    private Request mRequest;
    private NetStatus mStatus;
    private String mMessage;
    private String mResponse;
    private OnCompleteListener mListener;
    private Executor mResponsePoster;
    private HttpURLConnection mConnection;

    public NetProcessor(Request request, OnCompleteListener l, final Handler handler){
        mRequest=request;
        mListener=l;
        mStatus=NetStatus.UNSTART;
        mResponsePoster=new Executor(){
            @Override
            public void execute(@NonNull Runnable command){
                handler.post(command);
            }
        };
        if(mRequest.getHost() instanceof Activity){
            final Activity act= (Activity) mRequest.getHost();
            act.getApplication().registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
                @Override
                public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

                }

                @Override
                public void onActivityStarted(Activity activity) {

                }

                @Override
                public void onActivityResumed(Activity activity) {

                }

                @Override
                public void onActivityPaused(Activity activity) {

                }

                @Override
                public void onActivityStopped(Activity activity) {
                    if(act==activity){
                        act.getApplication().unregisterActivityLifecycleCallbacks(this);
                        stop();
                    }
                }

                @Override
                public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

                }

                @Override
                public void onActivityDestroyed(Activity activity) {

                }
            });
        }
    }

    @Override
    public void run(){
        if(mStatus==NetStatus.STOP)
            return;
        try {
            mStatus=NetStatus.RUNNING;
            ByteArrayOutputStream baos=new ByteArrayOutputStream();
            File downloadFile=null;
            InputStream in;
            OutputStream out;
            URL url=new URL(mRequest.getUrl());
            mConnection =(HttpURLConnection)url.openConnection();
            boolean isMulti=false,isPost=false;
            long existsLength=0;

            //检测是否可保存为文件
            if(mRequest.downloadable()) {
                downloadFile = mRequest.getDownloadable().getTargetFile();
                existsLength=downloadFile.length();
                if(existsLength>0&&mRequest.getDownloadable().supportBreak()) //如果支持中断并且文件已部分存在,跳过部分流
                    mConnection.addRequestProperty("Range:bytes",Long.toString(existsLength)+"-");
            }
            if(mRequest.getMethod().equals("POST")){
                isPost = true;
                if(mRequest.getFiles()!=null&&mRequest.getFiles().size()>0){
                    isMulti = true;
                    mConnection.setChunkedStreamingMode(CHUNK_LENGTH);
                }
            }
            mConnection.setRequestMethod(mRequest.getMethod());
            mConnection.setDoInput(true);
            if(isPost) {
                mConnection.setDoOutput(true);
                if(isMulti){
                    mConnection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);
                    mConnection.setUseCaches(false);
                }
            }
            String[] sessions=mRequest.getSession();
            if(sessions!=null&&sessions.length>0){
                mConnection.setRequestProperty("Cookie",sessions[0]); //当前仅取首个session
            }
            if(mStatus==NetStatus.STOP)
                return;
            mConnection.connect();
            mRequest.startProcess(this);
            if(isPost){
                out = mConnection.getOutputStream();
                if(isMulti)
                    multipart(mRequest.getParams(), mRequest.getFiles(),out);
                else{
                    StringBuilder sb=new StringBuilder();
                    loadParams(mRequest.getParams(),sb);
                    byte[] data=sb.toString().getBytes();
                    Tx+=data.length;
                    out.write(data);
                }
                out.close();
            }

            in= mConnection.getInputStream();
            String session=mConnection.getHeaderField("Set-Cookie");
            if(!TextUtils.isEmpty(session))
                mRequest.setSession(session.split(";"));
            int len;
            byte[] data=new byte[BUFF_LENGTH];
            if(downloadFile!=null){
                OutputStream fileOut=new FileOutputStream(downloadFile,mRequest.getDownloadable().supportBreak());
//                String disposition=mConnection.getHeaderField("Content-Disposition");
//                String filename= URLDecoder.decode(disposition.substring(disposition.indexOf("filename=") + 9),"UTF-8");
                int maxCount=mConnection.getContentLength();
                int speed=0;
                long timer=System.currentTimeMillis();
                while((len=in.read(data))!=-1){
                    fileOut.write(data, 0, len);
                    Rx+=len;
                    speed+=len;
                    if((System.currentTimeMillis()-timer)>1000){ //每秒发送一次广播
                        EventObserver.getInstance().sendEvent(new EventDownloading(maxCount,speed,downloadFile.getAbsolutePath()));
                        speed=0;
                        timer=System.currentTimeMillis();
                    }
                }
                EventObserver.getInstance().sendEvent(new EventDownloading(maxCount,speed,downloadFile.getAbsolutePath())); //下载结束发一次广播
                fileOut.close();
                mResponse=downloadFile.getAbsolutePath();
            }
            else{
                while((len=in.read(data))!=-1)
                    baos.write(data,0,len);
                Rx+=baos.size();
                mResponse = baos.toString();
            }
            baos.close();
            in.close();
            mConnection.disconnect();
            mMessage=mConnection.getResponseMessage();
            mStatus=NetStatus.SUCCESS;
        }catch (IOException e) {
            mMessage=e.toString();
            mStatus=NetStatus.ERROR;
        } finally{
            if(mListener!=null)
                mListener.onComplete(this);
            final Listener l=mRequest.getListener();
            if(l!=null){
                Object host=mRequest.getHost();
                boolean hostAvailable=true; //宿主是否状态正常.需要request里有宿主引用.如果没有宿主默认为正常
                if(host instanceof Fragment){
                    Fragment fragment= (Fragment) host;
                    if((fragment.isRemoving()||fragment.isDetached()))
                        hostAvailable=false;
                }
                else if(host instanceof Activity){
                    Activity activity=(Activity)host;
                    if(activity.isFinishing())
                        hostAvailable=false;
                }
                if(hostAvailable){
                    mResponsePoster.execute(new Runnable(){
                        @Override
                        public void run(){
                            if(mStatus==NetStatus.SUCCESS)
                                l.onResponseListener(mRequest,mResponse);
                            else if(mStatus==NetStatus.ERROR)
                                l.onErrorListener(mRequest,mMessage);
                        }
                    });
                }
            }
        }
    }

    private void loadParams(Map<String, String> params, StringBuilder sb){
        if(params==null||params.size()<=0)
            return;
        Iterator<String> iter=params.keySet().iterator();

        while(iter.hasNext()){
            String key=iter.next();
            String value=params.get(key);
            sb.append(key).append("=").append(value).append("&");
        }
        sb.deleteCharAt(sb.length() - 1);
    }

    private void multipart(Map<String,String> strParams,Map<String,File> fileParams,OutputStream out) throws IOException {
        if(strParams!=null&&strParams.size()>0){
            Iterator<String> iter=strParams.keySet().iterator();
            StringBuilder sb=new StringBuilder();
            while(iter.hasNext()){
                String key=iter.next();
                String value=strParams.get(key);
                sb.append("--"+BOUNDARY).append(CRLF);
                sb.append("Content-Disposition:form-data; name=\""+key+"\"").append(CRLF);
                sb.append("Content-Type:text/plain charset=utf-8").append(CRLF+CRLF);
                sb.append(value).append(CRLF);
                Tx+=sb.toString().getBytes().length;
                out.write(sb.toString().getBytes());
            }
        }

        if(fileParams!=null&&fileParams.size()>0){
            Iterator<String> iter=fileParams.keySet().iterator();
            while(iter.hasNext()){
                StringBuilder sb=new StringBuilder();
                String key=iter.next();
                File value=fileParams.get(key);
                if(value!=null&&value.exists()&&value.isFile()){
                    sb.append("--"+BOUNDARY).append(CRLF);
                    sb.append("Content-Disposition:form-data; name=\""+key+"\";filename=\""+value.getName()+"\"").append(CRLF);
                    sb.append("Content-type: "+ URLConnection.guessContentTypeFromName(value.getName())).append(CRLF);
                    sb.append("Content-Transfer-Encoding:binary").append(CRLF + CRLF);
                    out.write(sb.toString().getBytes());
                    copyFileToStream(value, out);
                    out.write(CRLF.getBytes());
                    Tx+=sb.toString().getBytes().length;
                    Tx+=value.length();
                }
            }
        }
        out.write(END.getBytes());
        out.flush();
    }

    private void copyFileToStream(File file,OutputStream out) throws IOException {
        if(file==null||!file.exists())
            return;
        InputStream in=new FileInputStream(file);
        byte[] data=new byte[1024];
        int len;

        while((len=in.read(data))!=-1)
            out.write(data, 0, len);
    }

    @Override
    public String toString(){
        String txt="status:"+mStatus+" message:"+mMessage+" tx:"+Tx+" rx:"+Rx;
        return txt;
    }

    public long getTx() {
        return Tx;
    }

    public long getRx() {
        return Rx;
    }

    public NetStatus getStatus() {
        return mStatus;
    }

    public String getMessage() {
        return mMessage;
    }

    public Request getRequest(){return mRequest;}

    public boolean stop(){
        mStatus=NetStatus.STOP;
        if(mConnection==null)
            return false;
        new Thread(){
            @Override
            public void run(){
                mConnection.disconnect();
            }
        }.start();
        return true;
    }

    public interface OnCompleteListener {
        void onComplete(NetProcessor processer);
    }

    public enum NetStatus{
        UNSTART,
        RUNNING,
        ERROR,
        SUCCESS,
        STOP,
        OTHER
    }
}