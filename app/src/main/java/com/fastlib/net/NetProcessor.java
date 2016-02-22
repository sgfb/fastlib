package com.fastlib.net;

import android.os.Handler;

import com.fastlib.app.AppGlobal;
import com.fastlib.app.FastApplication;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.Executor;

/**
 * 网络请求的具体处理。在结束时会保存一些状态
 */
public class NetProcessor extends Thread{
    private final String BOUNDARY=Long.toHexString(System.currentTimeMillis());
    private final String CRLF="\r\n";
    private final String END="--"+BOUNDARY+"--"+CRLF;

    private Request mRequest;
    private long Tx,Rx;
    private NetStatus mStatus;
    private String mMessage;
    private String mResponse;
    private OnCompleteListener mListener;
    private Executor mResponsePoster;

    public NetProcessor(Request request, OnCompleteListener l, final Handler handler){
        mRequest=request;
        mListener=l;
        mStatus=NetStatus.UNSTART;
        mResponsePoster=new Executor() {
            @Override
            public void execute(Runnable command) {
                handler.post(command);
            }
        };
    }

    @Override
    public void run(){
        try {
            mStatus=NetStatus.RUNNING;
            ByteArrayOutputStream baos=new ByteArrayOutputStream();
            InputStream in;
            OutputStream out;
            URL url=new URL(mRequest.getUrl());
            HttpURLConnection con=(HttpURLConnection)url.openConnection();
            boolean isMulti=false,isPost=false;

            if(mRequest.getMethod().equals("POST")) {
                isPost = true;
                if(mRequest.getFiles()!=null&&mRequest.getFiles().size()>0) {
                    isMulti = true;
                    con.setChunkedStreamingMode(4096);
                }
            }
            con.setRequestMethod(mRequest.getMethod());
            con.setDoInput(true);
            if(isPost) {
                con.setDoOutput(true);
                if(isMulti){
                    con.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);
                    con.setUseCaches(false);
                }
            }
            con.connect();
            if(isPost) {
                out = con.getOutputStream();
                if(isMulti)
                    multipart(mRequest.getParame(), mRequest.getFiles(), out);
                else{
                    StringBuilder sb=new StringBuilder();
                    loadParames(mRequest.getParame(),sb);
                    byte[] data=sb.toString().getBytes();
                    Tx+=data.length;
                    out.write(data);
                }
                out.close();
            }

            in=con.getInputStream();
            int len;
            byte[] data=new byte[1024];
            while((len=in.read(data))!=-1)
                baos.write(data,0,len);
            Rx+=baos.size();
            mResponse = baos.toString();
            baos.close();
            in.close();
            con.disconnect();
            mMessage="成功";
            mStatus=NetStatus.SUCCESS;
        }catch (IOException e) {
            mMessage=e.toString();
            mStatus=NetStatus.ERROR;
        }finally {
            if(mListener!=null)
                mListener.onComplete(this);
            final Listener l=mRequest.getListener();
            if(l!=null){
                mResponsePoster.execute(new Runnable() {
                    @Override
                    public void run() {
                        if(mStatus==NetStatus.SUCCESS){
                            Gson gson=new Gson();
                            Result result=null;

                            if(NetQueue.getInstance().getConfig().isUseStatus()){
                                try{
                                    result=gson.fromJson(mResponse,Result.class);
                                }catch(JsonSyntaxException e){

                                }
                            }
                            if(result==null||result.getCode()==0){
                                result=new Result();
                                result.setBody(mResponse);
                                result.setCode(-1);
                                result.setSuccess(true);
                            }
                            l.onResponseListener(result);
                        }
                        else if(mStatus==NetStatus.ERROR){
                            l.onErrorListener(mMessage);
                        }
                    }
                });
            }
        }
    }

    private void loadParames(Map<String,String> params,StringBuilder sb){
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
            StringBuilder sb=new StringBuilder();
            while(iter.hasNext()){
                String key=iter.next();
                File value=fileParams.get(key);
                if(value!=null&&value.exists()&&value.isFile()){
                    sb.append("--"+BOUNDARY).append(CRLF);
                    sb.append("Content-Disposition:form-data; name=\""+key+"\"").append(CRLF);
                    sb.append("Content-type: "+ URLConnection.guessContentTypeFromName(value.getName())).append(CRLF);
                    sb.append("Content-Transfer-Encoding:binary").append(CRLF + CRLF);
                    out.write(sb.toString().getBytes());
                    copyFileToStream(value,out);
                    out.write(CRLF.getBytes());
                    out.flush();
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
    
    public Request getReqeust(){return mRequest;}

    public interface OnCompleteListener {
        void onComplete(NetProcessor processer);
    }

    public enum NetStatus{
        UNSTART,
        RUNNING,
        ERROR,
        SUCCESS;
    }
}
