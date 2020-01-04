package com.fastlib.net2.param;

/**
 * Created by sgfb on 2019\12\25.
 */
public class StringParamParser extends SingleParamParser<String>{

    @Override
    protected String parseParam(String param) {
        return param;
    }
}
