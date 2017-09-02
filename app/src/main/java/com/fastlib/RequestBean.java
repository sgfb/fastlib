package com.fastlib;

import java.util.Map;

/**
 * Created by sgfb on 17/8/29.
 */
public class RequestBean{
    public Header riskAppHeader;
    public Map<String,String> riskAppContent;

    public RequestBean(String headerStr, Map<String, String> riskAppContent) {
        riskAppHeader=new Header(headerStr);
        this.riskAppContent = riskAppContent;
    }

    public class Header{
        public String signMsg;

        public Header(String signMsg) {
            this.signMsg = signMsg;
        }
    }
}