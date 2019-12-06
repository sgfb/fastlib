package com.fastlib;

import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by sgfb on 2019/12/3
 * E-mail:602687446@qq.com
 * 管理Socket.支持最低程度Http协议连接
 */
public abstract class HttpCore{
    private static String TAG=HttpCore.class.getSimpleName();
    private static final String HTTP_PROTOCOL="HTTP/1.1";
    private static final String CRLF = "\r\n";

    private Socket mSocket;
    private OutputStream mSocketOut;
    private InputStream mSocketIn;
    protected ResponseHeader mResponseHeader;
    protected String mUrl;

    /**
     * 生成必要的和自定义的头部
     * @return 请求的HTTP头部
     */
    protected abstract Map<String,List<String>> createHeader() throws IOException;

    /**
     * 发送请求体给服务器
     */
    protected abstract void onSendData() throws IOException;

    /**
     * 获取请求方法
     * @return  请求方法(GET,POST等)
     */
    protected abstract String getRequestMethod();

    public HttpCore(String url){
        if(!URLUtil.validUrl(url))
            throw new IllegalArgumentException("url不正确或者不支持的协议");
        mUrl=url;
    }

    public void begin() throws IOException {
        mSocket=new Socket(URLUtil.getHost(mUrl),URLUtil.getPort(mUrl));
        Log.d(TAG,"Socket已连接");
        mSocketOut=mSocket.getOutputStream();
        mSocketIn=mSocket.getInputStream();
        sendHeader();
        onSendData();
        receiveHeader();
        //判断返回码 处理特殊情况
    }

    /**
     * 发送头部
     */
    private void sendHeader() throws IOException {
        Map<String,List<String>> header=createHeader();
        StringBuilder sb=new StringBuilder(256);

        //例：GET /index HTTP/1.1
        sb.append(getRequestMethod()).append(' ').append(URLUtil.getPath(mUrl)).append(' ').append(HTTP_PROTOCOL).append(CRLF);
        for(Map.Entry<String,List<String>> entry:header.entrySet()) {
            for(String headerValue:entry.getValue()){
                sb.append(entry.getKey()).append(':').append(headerValue).append(CRLF);
            }
        }
        sb.append(CRLF);
        mSocketOut.write(sb.toString().getBytes());
    }

    /**
     * 接收头部
     */
    private void receiveHeader() throws IOException {
        mSocketOut.flush();
        String statusLine=readLine(mSocketIn);
        if(TextUtils.isEmpty(statusLine)) throw new IOException("服务器返回HTTP协议异常");
        String[] status=statusLine.trim().split(" ");
        if(status.length<2) throw new IOException("服务器返回HTTP协议异常");
        try{
            int code=Integer.parseInt(status[1].trim());
            Map<String, List<String>> header=new HashMap<>();
            String line;

            while(!TextUtils.isEmpty((line=readLine(mSocketIn)))){
                String[] headerSplit=line.trim().split(":");
                if(headerSplit.length==0)
                    continue;

                String key;
                List<String> value;
                if(headerSplit.length==1) key=null;
                else key=headerSplit[0].trim();

                value=header.get(key);
                if(value==null){
                    value=new ArrayList<>();
                    header.put(key,value);
                }
                value.add(headerSplit.length>=2?headerSplit[1].trim():headerSplit[0].trim());
                header.put(key,value);
            }
            mResponseHeader=new ResponseHeader(code,status[0],status.length>2?status[2]:"",header);
            Log.d(TAG,String.format(Locale.getDefault(),"code:%d message:%s",mResponseHeader.getCode(),mResponseHeader.getMessage()));
            for(Map.Entry<String,List<String>> entry:mResponseHeader.getHeaders().entrySet()){
                StringBuilder sb=new StringBuilder();
                sb.append('[');
                if(entry.getValue()!=null){
                    for(String value:entry.getValue())
                        sb.append(value).append(',');
                }
                if(sb.length()>2)
                    sb.deleteCharAt(sb.length()-1);
                sb.append(']');
                Log.d(TAG,String.format(Locale.getDefault(),"header:%s,%s",entry.getKey(),sb.toString()));
            }
        }catch (NumberFormatException e){
            throw new IOException("服务器返回状态码异常,状态码为:"+status[1]);
        }
    }

    protected String readLine(InputStream inputStream) throws IOException {
        int lastChar=0;
        int currChar=0;
        StringBuilder sb=new StringBuilder(100);

        while(lastChar!='\t'&&currChar!='\n'){
            lastChar=currChar;
            currChar=inputStream.read();

            if(currChar==-1) break;
            sb.append((char)currChar);
        }

        if(sb.length()>=2&&sb.substring(sb.length()-2).equals(CRLF))
            sb.delete(sb.length()-2,sb.length());
        return sb.toString();
    }

    protected OutputStream getSocketOutputStream() throws IOException {
        if(mSocket==null) begin();
        return mSocketOut;
    }

    public void end() throws IOException {
        if(mSocket!=null) {
            mSocket.close();
            mSocket=null;
            Log.d(TAG,"socket已关闭");
        }
    }

    public InputStream getInputStream() throws IOException {
        if(mSocket==null) begin();
        return mSocketIn;
    }

    public boolean isConnected(){
        return mSocket!=null;
    }

    public ResponseHeader getResponseHeader(){
        return mResponseHeader;
    }
}
