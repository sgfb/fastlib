package com.fastlib.net2.param;

/**
 * Created by sgfb on 2019\12\19.
 */
public class DoubleParamParser extends SingleParamParser<Double>{
    @Override
    protected String parseParam(Double param) {
        return Double.toString(param);
    }
}
