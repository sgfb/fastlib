package com.fastlib.local_test;

import com.fastlib.net.Request;
import com.fastlib.utils.Utils;
import com.google.gson.Gson;

/**
 * Created by sgfb on 2017/9/25.
 */

public class AospRequest extends Request{
    public static final String URL="https://ceshi.zeshukeji.com/wlbx02/services/riskAppServer?wsdl";
    public static final String KEY = "yY8FhoBEvWzFhms6oBBlDHkoLQMxJlXu";//生产
    public static final String aospformat = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:cxf=\"http://cxf.spring.core.sinosoft.com/\">\n"
            + "<soapenv:Header/>\n"
            + "<soapenv:Body>\n"
            + "<cxf:%s>\n"
            + "<requestParam>%s</requestParam>\n"
            + "</cxf:%s>\n"
            + "</soapenv:Body>\n"
            + "</soapenv:Envelope>";
    private String mMethod;

    public AospRequest(String method){
        mMethod=method;
    }

    @Override
    public byte[] start(boolean forceRefresh){
        Gson gson=new Gson();
        RequestBean bean=new RequestBean(Utils.getMd5(gson.toJson(getParams())+KEY,false),getParams());
        String beanJson=gson.toJson(bean);
        String json=String.format(aospformat,mMethod,beanJson,mMethod);
        setByteStream(json.getBytes());
        setUrl(URL);
        setMethod("POST");
        putHeader("Content-Type","application/json");
        return super.start(forceRefresh);
    }
}