package com.fastlib.net;

import android.os.Handler;

import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.Executor;

/**
 * 网络请求的具体处理。在结束时会保存一些状态
 */
public class NetProcessor extends Thread{

    private Request mRequest;
    private int Tx,Rx;
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
            StringBuilder sb=new StringBuilder();
            ByteArrayOutputStream baos=new ByteArrayOutputStream();
            InputStream in;
            OutputStream out;
            URL url=new URL(mRequest.mUrl);
            HttpURLConnection con=(HttpURLConnection)url.openConnection();

            con.setDoOutput(true);
            con.setDoInput(true);
            con.setRequestMethod(mRequest.method);
            loadParames(mRequest.getParame(),sb);
            con.connect();
            out=con.getOutputStream();
            if(sb.length()>0) {
                Tx+=sb.toString().getBytes().length;
                out.write(sb.toString().getBytes());
            }
            in=con.getInputStream();
            int len;
            byte[] data=new byte[1024];
            while((len=in.read(data))!=-1){
                baos.write(data,0,len);
            }
            Rx+=baos.size();
            mResponse=baos.toString();
            baos.close();
            out.close();
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
                            //TODO need testing
                            Gson gson=new Gson();
                            StringBuilder sb=new StringBuilder(mResponse);
                            String bodyJson=sb.substring(sb.indexOf("{",2))+sb.substring(sb.indexOf("}"));
                            String statusJson=sb.delete(sb.indexOf("{",2),sb.indexOf("}")).toString();
                            Result result=gson.fromJson(statusJson,Result.class);
                            result.setBody(bodyJson);
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
        if(params==null)
            return;
        Iterator<String> iter=params.keySet().iterator();

        while(iter.hasNext()){
            String key=iter.next();
            String value=params.get(key);

            sb.append(key).append("=").append(value).append("&");
        }
        if(params.size()>0)
            sb.deleteCharAt(sb.length() - 1);
    }

    @Override
    public String toString(){
        String txt="status:"+mStatus+" message:"+mMessage+" tx:"+Tx+" rx:"+Rx;
        return txt;
    }

    public int getTx() {
        return Tx;
    }

    public int getRx() {
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
