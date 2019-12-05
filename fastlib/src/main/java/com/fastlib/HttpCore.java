package com.fastlib;

import android.text.TextUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sgfb on 2019/12/3
 * E-mail:602687446@qq.com
 * 管理Socket.支持最低程度Http协议连接
 */
public abstract class HttpCore{
    private static final String HTTP_PROTOCOL="HTTP/1.1";
    private static final String CRLF = "\r\n";

    private Socket mSocket;
    private OutputStream mSocketOut;
    private InputStream mSocketIn;
    private ResponseHeader mResponseHeader;
    protected String mUrl;

    /**
     * 生成必要的和自定义的头部
     * @return 请求的HTTP头部
     */
    protected abstract Map<String,String> createHeader();

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

    public void connect() throws IOException {
        mSocket=new Socket(URLUtil.getHost(mUrl),URLUtil.getPort(mUrl));
        mSocketOut=mSocket.getOutputStream();
        mSocketIn=mSocket.getInputStream();
        sendHeader();
        receiveHeader();
        //判断返回码 处理特殊情况
    }

    private void sendHeader() throws IOException {
        Map<String,String> header=createHeader();
        StringBuilder sb=new StringBuilder(256);

        //例：GET /index HTTP/1.1
        sb.append(getRequestMethod()).append(' ').append(URLUtil.getPath(mUrl)).append(' ').append(HTTP_PROTOCOL).append(CRLF);
        for(Map.Entry<String,String> entry:header.entrySet())
            sb.append(entry.getKey()).append(':').append(entry.getValue()).append(CRLF);
        sb.append(CRLF);
        mSocketOut.write(sb.toString().getBytes());
    }

    private void receiveHeader() throws IOException {
        mSocketOut.flush();
        String statusLine=readLine(mSocketIn);
        if(TextUtils.isEmpty(statusLine)) throw new IOException("服务器返回HTTP协议异常");
        String[] status=statusLine.trim().split(" ");
        if(status.length<2) throw new IOException("服务器返回HTTP协议异常");
        try{
            int code=Integer.parseInt(status[1]);
            Map<String, List<String>> header=new HashMap<>();
            String line;

            while(!TextUtils.isEmpty((line=readLine(mSocketIn)))){
                String[] headerSplit=line.trim().split(":");
                if(headerSplit.length==0)
                    continue;

                String key;
                List<String> value;
                if(headerSplit.length==1) key=null;
                else key=headerSplit[0];

                value=header.get(key);
                if(value==null){
                    value=new ArrayList<>();
                    header.put(key,value);
                }
                value.add(headerSplit.length>=2?headerSplit[1]:headerSplit[0]);
                header.put(key,value);
            }
            mResponseHeader=new ResponseHeader(code,status[0],status.length>2?status[2]:"",header);
        }catch (NumberFormatException e){
            throw new IOException("服务器返回状态码异常,状态码为:"+status[1]);
        }
    }

    private String readLine(InputStream inputStream) throws IOException {
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

    public void disconnect() throws IOException {
        if(mSocket!=null) {
            mSocket.close();
            mSocket=null;
        }
    }

    public InputStream getInputStream() throws IOException {
        if(mSocket==null) connect();
        return mSocketIn;
    }

    public OutputStream getOutputStream() throws IOException {
        if(mSocket==null) connect();
        return mSocketOut;
    }

    public boolean isConnected(){
        return mSocket!=null;
    }

    public ResponseHeader getResponseHeader(){
        return mResponseHeader;
    }
}
