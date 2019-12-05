package com.fastlib;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sgfb on 2019/12/3
 * E-mail:602687446@qq.com
 * 服务器返回的Header
 */
public class ResponseHeader{
    private int mCode;
    private String mProtocol;
    private String mMessage;
    private Map<String, List<String>> mHeader;

    public ResponseHeader(int code, String protocol, String message, Map<String, List<String>> header) {
        mCode = code;
        mProtocol = protocol;
        mMessage = message;
        mHeader = header;
        if(mHeader==null) mHeader=new HashMap<>();
    }

    public int getCode() {
        return mCode;
    }

    public String getProtocol() {
        return mProtocol;
    }

    public String getMessage() {
        return mMessage;
    }

    public Map<String, List<String>> getHeader() {
        return new HashMap<>(mHeader);
    }
}
