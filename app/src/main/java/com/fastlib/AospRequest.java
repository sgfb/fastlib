package com.fastlib;

import com.fastlib.net.Request;
import com.fastlib.utils.Utils;
import com.google.gson.Gson;

/**
 * Created by sgfb on 17/8/29.
 */
public class AospRequest extends Request{
    final String KEY="yY8FhoBEvWzFhms6oBBlDHkoLQMxJlXu";
    final String aospformat = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:cxf=\"http://cxf.spring.core.sinosoft.com/\">\n"
            + "   <soapenv:Header/>"
            + "   <soapenv:Body>"
            + "      <cxf:%s>"
            + "         <requestParam>%s</requestParam>"
            + "      </cxf:%s>"
            + "   </soapenv:Body>"
            + "</soapenv:Envelope>";

    private String method;

    public AospRequest(String method){
        super();
        setUrl("https://ceshi.zeshukeji.com/wlbx/services/riskAppServer?wsdl");
        this.method=method;
        putHeader("Content-Type","application/xml");
    }

    @Override
    public Request start(boolean forceRefresh){
        Gson gson=new Gson();
        String parmasjson=gson.toJson(getParams());
        String headerStr= Utils.getMd5(parmasjson+KEY,false);
        RequestBean requestBean=new RequestBean(headerStr,getParams());
        String completeRequest=String.format(aospformat,method,gson.toJson(requestBean),method);
        setByteStream(completeRequest.getBytes());
        System.out.println(completeRequest);
        return super.start(forceRefresh);
    }
}
