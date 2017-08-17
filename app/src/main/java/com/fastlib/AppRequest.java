package com.fastlib;

import com.fastlib.net.Request;
import com.fastlib.utils.Utils;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sgfb on 17/8/15.
 */
public class AppRequest extends Request{
    String aospformat = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:cxf=\"http://cxf.spring.core.sinosoft.com/\">\n"
            + "   <soapenv:Header/>\n"
            + "   <soapenv:Body>\n"
            + "      <cxf:%s>\n"
            + "         <!--Optional:-->\n"
            + "         <requestParam>%s</requestParam>\n"
            + "      </cxf:%s>\n"
            + "   </soapenv:Body>\n"
            + "</soapenv:Envelope>";
    public final static String URL="https://ceshi.zeshukeji.com/wlbx/services/riskAppServer?wsdl";
    public final static String MD5KEY = "yY8FhoBEvWzFhms6oBBlDHkoLQMxJlXu";//生产
    private String mMethod;

    public AppRequest(String method){
        setUrl(URL);
        mMethod=method;
    }

    @Override
    public Request start(boolean forceRefresh){
        Gson gson=new Gson();
        String params=gson.toJson(getParams());
        String singMsg= Utils.getMd5(params+ MD5KEY,false);
        RequestBean bean=new RequestBean(singMsg,getParams());
        String beanJson=gson.toJson(bean);
        System.out.println(beanJson);
        setByteStream(String.format(aospformat,mMethod,beanJson,mMethod).getBytes());
        List<ExtraHeader> list=new ArrayList<>();
        list.add(new ExtraHeader(false,"Content-Type","application/xml"));
        setSendHeader(list);
//        paramObject.add("riskAppHeader", headerObject);
//        paramObject.add("riskAppContent", contentObject);
        return super.start(forceRefresh);
    }
}
