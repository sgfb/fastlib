package com.fastlib;

import android.os.Build;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.fastlib.net.ExtraHeader;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by sgfb on 2019\12\03.
 * 基础实现HttpCore功能.添加便利功能可快速安全使用Http协议与服务器交互
 */
public class SimpleHttpCoreImpl extends HttpCore{
    private Map<String,List<String>> mAdditionHeader=new HashMap<>();
    private List<InputStream> mPendingInputStream=new ArrayList<>();

    public SimpleHttpCoreImpl(String url) {
        super(url);
    }

    @Override
    protected Map<String, List<String>> getHeader() throws IOException {
        Map<String,List<String>> totalHeader=new HashMap<>(mAdditionHeader);

        putIfNotExist(totalHeader,HeaderDefinition.KEY_HOST,URLUtil.getHost(mUrl));
        putIfNotExist(totalHeader,HeaderDefinition.KEY_ACCEPT,"*/*");
        putIfNotExist(totalHeader,HeaderDefinition.KEY_AGENT,String.format(Locale.getDefault(),"%s %s","android", Build.VERSION.SDK));
        putIfNotExist(totalHeader,HeaderDefinition.KEY_CONNECTION,"keep-alive");
        putIfNotExist(totalHeader,HeaderDefinition.KEY_CACHE_CONTROL,"no-cache");
        putIfNotExist(totalHeader,HeaderDefinition.KEY_ACCEPT_ENCODING,"gzip,deflate");
        if(!totalHeader.containsKey(HeaderDefinition.KEY_TRANSFER_ENCODING))
            putIfNotExist(totalHeader,HeaderDefinition.KEY_CONTENT_LENGTH,Long.toString(calContentLength()));
        return totalHeader;
    }

    @Override
    protected void onSendData() throws IOException {
        byte[] buffer=new byte[8096];
        int len;
        for(InputStream inputStream:mPendingInputStream){
            OutputStream out=getSocketOutputStream();

            while((len=inputStream.read(buffer))!=-1){
                out.write(buffer,0,len);
            }
            inputStream.close();
        }
    }

    /**
     * 对一些必要的头部如果不存在就写入默认值
     * @param map               头部群
     * @param key               必要头部键
     * @param defaultValue      必要头部值
     */
    private void putIfNotExist(Map<String,List<String>> map,String key,String defaultValue){
        if(!map.containsKey(key)){
            List<String> list=new ArrayList<>();
            list.add(defaultValue);
            map.put(key,list);
        }
    }

    private long calContentLength() throws IOException {
        long length=0;
        for(InputStream in:mPendingInputStream){
            length+=in.available();
        }
        return length;
    }

    @Override
    protected String getRequestMethod() {
        return "GET";
    }

    @Override
    protected HttpOption getHttpOption() {
        return new HttpOption();
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return new HttpInputStream(super.getInputStream(), new StreamRemainCounter(){
            private static final int NOT_INIT=-2;

            int mRemain=NOT_INIT;

            @Override
            public int getRemainCount() throws IOException {
                if(mRemain==NOT_INIT){
                    readLengthByContentLength();
                    readLengthByChunk();
                }
                if(mRemain==0){
                    readLengthByChunk();
                    if(mRemain==0)
                        mRemain=-1;
                }
                if(mRemain==-1&&isConnected()){
                    end();
                }
                return mRemain;
            }

            private void readLengthByContentLength(){
                String contentLength=mResponseHeader.getHeaderFirst(HeaderDefinition.KEY_CONTENT_LENGTH);

                if(!TextUtils.isEmpty(contentLength)){
                    try{
                        mRemain=Integer.parseInt(contentLength);
                    }catch (NumberFormatException e){
                        //如果不能转换成正常数字,无视这个参数
                    }
                }
            }

            private void readLengthByChunk(){
                String transferEncoding=mResponseHeader.getHeaderFirst(HeaderDefinition.KEY_TRANSFER_ENCODING);
                if(HeaderDefinition.VALUE_TRANSFER_ENCODING_CHUNKED.equals(transferEncoding)){
                    try {
                        //可能是一个CRLF,如果是空的再尝试读一行
                        String line=readLine(SimpleHttpCoreImpl.super.getInputStream());
                        if(TextUtils.isEmpty(line)){
                            line=readLine(SimpleHttpCoreImpl.super.getInputStream());
                        }
                        mRemain=Integer.parseInt(line,16);
                    } catch (IOException|NumberFormatException e){
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void readStream(int readBytes) {
                if(readBytes==-1)
                    mRemain=-1;
                else mRemain-=readBytes;
            }
        },isKeepAlive());
    }

    /**
     * 提供一个流在网络连接时发送给服务器的数据.这个流的{@link InputStream#available()}必须是可用的,在使用完后会被关闭
     * @param inputStream 预计发送给服务器的数据
     */
    public void addPendingInputStream(@NonNull InputStream inputStream){
        if(isBegin)
            throw new IllegalStateException("不允许在begin()后添加输入流");
        mPendingInputStream.add(inputStream);
    }

    public void addHeader(String key,String value){
        if(isBegin)
            throw new IllegalStateException("不允许在begin()后设置头部参数");
        List<String> valueList=mAdditionHeader.get(key);

        if(valueList==null) {
            valueList = new ArrayList<>();
            mAdditionHeader.put(key,valueList);
        }
        valueList.add(value);
    }

    public void setHeader(String key,String value){
        if(isBegin)
            throw new IllegalStateException("不允许在begin()后设置头部参数");
        List<String> valueList=mAdditionHeader.get(key);

        if(valueList==null) {
            valueList = new ArrayList<>();
            mAdditionHeader.put(key,valueList);
        }
        else valueList.clear();
        valueList.add(value);
    }
}
