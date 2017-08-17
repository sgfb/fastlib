package com.fastlib;

import java.util.Map;

/**
 * Created by sgfb on 17/8/15.
 */
public class RequestBean{
    public RequestHead riskAppHeader;
    public Map<String,String> riskAppContent;

    public RequestBean(String sign,Map<String, String> riskAppContent) {
        this.riskAppContent = riskAppContent;
        riskAppHeader=new RequestHead();
        riskAppHeader.signMsg=sign;
    }

    public class RequestHead{
        public String signMsg;
    }
}