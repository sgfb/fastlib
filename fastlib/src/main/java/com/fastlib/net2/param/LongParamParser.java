package com.fastlib.net2.param;

/**
 * Created by sgfb on 2019\12\18.
 */
public class LongParamParser extends SingleParamParser<Long>{

    @Override
    protected String parseParam(Long param) {
        return Long.toString(param);
    }
}
