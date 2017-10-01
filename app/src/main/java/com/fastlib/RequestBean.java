package com.fastlib;

import java.util.Map;

/**
 * Created by sgfb on 2017/9/25.
 */
public class RequestBean{
    public RequestHead riskAppHeader;
    public Map<String,String> riskAppContent;

    public RequestBean(String signMsg,Map<String,String> content){
        riskAppHeader=new RequestHead();

        riskAppHeader.signMsg=signMsg;
        riskAppContent=content;
    }

    public class RequestHead {
        public String signMsg;
    }
}
