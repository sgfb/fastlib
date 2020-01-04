package com.fastlib.net2.param;

/**
 * Created by sgfb on 2019\12\18.
 */
public class IntParamParser extends SingleParamParser<Integer>{

    @Override
    protected String parseParam(Integer param){
        return Integer.toString(param);
    }
}
