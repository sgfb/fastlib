package com.fastlib.net;

import android.app.Activity;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.text.TextUtils;

import com.fastlib.app.EventObserver;
import com.fastlib.app.GlobalConfig;
import com.fastlib.bean.EventDownloading;
import com.fastlib.bean.EventUploading;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * 网络请求的具体处理.在结束时会保存一些状态<br/>
 * 这个类可以上传和下载文件,支持中断,下载文件时每秒发送一次进度广播(EventDownloading)
 */
public class NetProcessor implements Runnable{
    private final String BOUNDARY = Long.toHexString(System.currentTimeMillis());
    private final String CRLF = "\r\n";
    private final String END = "--" + BOUNDARY + "--" + CRLF;
    private final int BUFF_LENGTH = 4096;
    private final int CHUNK_LENGTH = 4096;

    public static long mDiffServerTime; //与服务器时间差

    private boolean isSuccess=true;
    private long Tx, Rx;
    private byte[] mResponse;
    private Request mRequest;
    private String mMessage = null;
    private OnCompleteListener mListener;
    private Executor mResponsePoster;

    public NetProcessor(Request request, OnCompleteListener l, final Handler handler){
        mRequest = request;
        mListener = l;
        mResponsePoster = new Executor() {
            @Override
            public void execute(@NonNull Runnable command) {
                handler.post(command);
            }
        };
    }

    @Override
    public void run(){
        if(mRequest.getMock()!=null){
            mResponse=mRequest.getMock().dataResponse();
            mMessage="模拟数据";
            toggleCallback();
            if(mListener!=null)
                mListener.onComplete(this);
            return;
        }
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            File downloadFile = null;
            InputStream in;
            OutputStream out;
            URL url = new URL(mRequest.getUrl());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            boolean isMulti = false, isPost = false;
            long existsLength;

            //添加额外信息到头部
            if (mRequest.getHeadExtra() != null) {
                for (Pair<String, String> pair : mRequest.getHeadExtra())
                    connection.addRequestProperty(pair.first, pair.second);
            }
            //检测是否可保存为文件
            if (mRequest.downloadable()) {
                downloadFile = mRequest.getDownloadable().getTargetFile();
                existsLength = downloadFile.length();
                if (existsLength > 0 && mRequest.getDownloadable().supportBreak()) //如果支持中断并且文件已部分存在,跳过部分流
                    connection.addRequestProperty("Range", "bytes=" + Long.toString(existsLength) + "-");
            }
            if (mRequest.getMethod().equals("POST")) {
                isPost = true;
                if (mRequest.getFiles() != null && mRequest.getFiles().size() > 0) {
                    isMulti = true;
                    connection.setChunkedStreamingMode(CHUNK_LENGTH);
                }
            }
            connection.setRequestMethod(mRequest.getMethod());
            connection.setDoInput(true);
            if (isPost){
                connection.setDoOutput(true);
                if (isMulti) {
                    connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);
                    connection.setUseCaches(false);
                }
            }
            if (mRequest.getSendCookies() != null)
                connection.setRequestProperty(mRequest.getSendCookies().first, mRequest.getSendCookies().second);
            if(Thread.currentThread().isInterrupted()) //在连接前判断线程是否已关闭
                return;
            connection.connect();
            if (isPost){
                out=mRequest.isSendGzip()?new GZIPOutputStream(connection.getOutputStream()):connection.getOutputStream();
                if (isMulti)
                    multipart(mRequest.getParams(), mRequest.getFiles(), out);
                else{
                    StringBuilder sb = new StringBuilder();
                    loadParams(mRequest.getParams(), sb);
                    byte[] data = sb.toString().getBytes();
                    Tx += data.length;
                    out.write(data);
                }
                out.close();
            }

            in=mRequest.isReceiveGzip()?new GZIPInputStream(connection.getInputStream()):connection.getInputStream();
            if (mRequest.isSaveCookies()) {
                String cookies = connection.getHeaderField("Set-Cookie");
                if (!TextUtils.isEmpty(cookies))
                    mRequest.setCookies(cookies.split(";"));
            }
            int len;
            byte[] data = new byte[BUFF_LENGTH];
            //如果支持,修改下载的文件名
            if (downloadFile != null){
                OutputStream fileOut = new FileOutputStream(downloadFile, mRequest.getDownloadable().supportBreak());
                String disposition = connection.getHeaderField("Content-Disposition");
                if (!TextUtils.isEmpty(disposition) && disposition.length() > 9 && mRequest.getDownloadable().changeIfHadName()){
                    String filename = URLDecoder.decode(disposition.substring(disposition.indexOf("filename=") + 9), "UTF-8");
                    if (!TextUtils.isEmpty(filename))
                        downloadFile.renameTo(new File(downloadFile.getParent() + File.separator + filename));
                }
                int maxCount = connection.getContentLength();
                int speed = 0;
                long timer = System.currentTimeMillis();
                while ((len = in.read(data)) != -1&&!Thread.currentThread().isInterrupted()){
                    fileOut.write(data, 0, len);
                    Rx += len;
                    speed += len;
                    if ((System.currentTimeMillis() - timer) > 1000) { //每秒发送一次广播
                        EventObserver.getInstance().sendEvent(new EventDownloading(maxCount, speed, downloadFile.getAbsolutePath(),mRequest));
                        speed = 0;
                        timer = System.currentTimeMillis();
                    }
                }
                EventObserver.getInstance().sendEvent(new EventDownloading(maxCount, speed, downloadFile.getAbsolutePath(),mRequest)); //下载结束发一次广播
                fileOut.close();
                mResponse = downloadFile.getAbsolutePath().getBytes();
            } else {
                while((len = in.read(data))!=-1&&!Thread.currentThread().isInterrupted())
                    baos.write(data, 0, len);
                Rx += baos.size();
                mResponse = baos.toByteArray();
            }
            baos.close();
            in.close();
            List<String> trustHost = NetQueue.getInstance().getConfig().getTrustHost(); //调整信任服务器时间差
            if (trustHost != null) {
                for (String host : trustHost) {
                    if (url.getHost().equals(host)) {
                        mDiffServerTime = connection.getDate() - System.currentTimeMillis();
                        break;
                    }
                }
            }
            connection.disconnect();
            mMessage=connection.getResponseMessage();
            toggleCallback();
        } catch (IOException e){
            isSuccess=false;
            mMessage = e.toString();
            toggleCallback();
        } finally {
            if (mListener != null)
                mListener.onComplete(this);
        }
    }

    private void toggleCallback(){
        final ExtraListener l = mRequest.getListener();
        if(l==null||Thread.currentThread().isInterrupted())
            return;
        Object host = mRequest.getHost();
        boolean hostAvailable = true; //宿主是否状态正常.需要request里有宿主引用.如果没有宿主默认为在安全环境
        if (host instanceof Fragment) {
            Fragment fragment = (Fragment) host;
            if ((fragment.isRemoving() || fragment.isDetached()))
                hostAvailable = false;
        } else if (host instanceof Activity){
            Activity activity = (Activity) host;
            if (activity.isFinishing())
                hostAvailable = false;
        }
        if (hostAvailable){
            l.onRawData(mResponse);
            Type realType = mRequest.getGenericType();
            Gson gson = new Gson();
            try {
                final Object responseObj;
                if (realType == null || realType == Object.class||realType==byte[].class)
                    responseObj = mResponse;
                else if (realType == String.class){
                    responseObj = new String(mResponse);
                    l.onTranslateJson((String) responseObj);
                }else{
                    String json = new String(mResponse);
                    l.onTranslateJson(json);
                    responseObj = gson.fromJson(json, realType);
                }
                if(isSuccess){
                    mResponsePoster.execute(new Runnable(){
                        @Override
                        public void run() {
                            l.onResponseListener(mRequest, responseObj);
                        }
                    });
                }
            } catch (JsonParseException e){
                if(GlobalConfig.SHOW_LOG)
                    System.out.println("解析时出现异常:" + e.getMessage());
                mMessage = e.getMessage();
                isSuccess=false;
            }
            if (!isSuccess){
                mResponsePoster.execute(new Runnable() {
                    @Override
                    public void run() {
                        l.onErrorListener(mRequest, mMessage);
                    }
                });
            }
        }
    }

    private void loadParams(Map<String, String> params, StringBuilder sb) {
        if (params == null || params.size() <= 0)
            return;
        Iterator<String> iter = params.keySet().iterator();

        while (iter.hasNext()) {
            String key = iter.next();
            String value = params.get(key);
            sb.append(key).append("=").append(value).append("&");
        }
        sb.deleteCharAt(sb.length() - 1);
    }

    private void multipart(Map<String, String> strParams,List<Pair<String,File>> fileParams, OutputStream out) throws IOException {
        if (strParams != null && strParams.size() > 0) {
            Iterator<String> iter = strParams.keySet().iterator();
            StringBuilder sb = new StringBuilder();
            while (iter.hasNext()&&!Thread.currentThread().isInterrupted()){
                String key = iter.next();
                String value = strParams.get(key);
                sb.append("--" + BOUNDARY).append(CRLF);
                sb.append("Content-Disposition:form-data; name=\"" + key + "\"").append(CRLF);
                sb.append("Content-Type:text/plain charset=utf-8").append(CRLF + CRLF);
                sb.append(value).append(CRLF);
                Tx += sb.toString().getBytes().length;
                out.write(sb.toString().getBytes());
            }
        }

        if (fileParams != null && fileParams.size() > 0) {
            Iterator<Pair<String,File>> iter = fileParams.iterator();
            while (iter.hasNext()&&!Thread.currentThread().isInterrupted()){
                StringBuilder sb = new StringBuilder();
                Pair<String,File> pair = iter.next();
                if (pair.second != null && pair.second.exists() && pair.second.isFile()) {
                    sb.append("--" + BOUNDARY).append(CRLF);
                    sb.append("Content-Disposition:form-data; name=\"" + pair.first + "\";filename=\"" + pair.second.getName() + "\"").append(CRLF);
                    sb.append("Content-type: " + URLConnection.guessContentTypeFromName(pair.second.getName())).append(CRLF);
                    sb.append("Content-Transfer-Encoding:binary").append(CRLF + CRLF);
                    out.write(sb.toString().getBytes());
                    Tx += sb.toString().getBytes().length;
                    copyFileToStream(pair.second,out);
                    out.write(CRLF.getBytes());
                }
            }
        }
        out.write(END.getBytes());
        out.flush();
    }

    /**
     * 复制文件到输出流
     * @param file
     * @param out
     * @throws IOException
     */
    private void copyFileToStream(File file, OutputStream out) throws IOException {
        if (file == null || !file.exists())
            return;
        OutputStream outDelegate=mRequest.isSendGzip()?new GZIPOutputStream(out):out;
        InputStream in = new FileInputStream(file);
        byte[] data = new byte[BUFF_LENGTH];
        int len;
        long time = System.currentTimeMillis();
        long count = 0;
        int speed = 0;

        while ((len = in.read(data)) != -1&&!Thread.currentThread().isInterrupted()){
            outDelegate.write(data, 0, len);
            count += len;
            speed += len;
            Tx += len;
            if ((System.currentTimeMillis() - time) > 1000) {
                EventObserver.getInstance().sendEvent(new EventUploading(speed, count, file.getAbsolutePath()));
                speed = 0;
                time = System.currentTimeMillis();
            }
        }
        if(mRequest.isSendGzip())
            ((GZIPOutputStream)outDelegate).finish();
    }

    @Override
    public String toString() {
        return "message:" + mMessage + " tx:" + Tx + " rx:" + Rx;
    }

    public long getTx() {
        return Tx;
    }

    public long getRx() {
        return Rx;
    }

    public String getMessage() {
        return mMessage;
    }

    public Request getRequest() {
        return mRequest;
    }

    public interface OnCompleteListener {
        void onComplete(NetProcessor processer);
    }
}