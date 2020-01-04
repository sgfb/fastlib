package com.fastlib.net2.param;

/**
 * Created by sgfb on 2019\12\19.
 */
public class BooleanParamParser extends SingleParamParser<Boolean>{
    @Override
    protected String parseParam(Boolean param) {
        return Boolean.toString(param);
    }
}
