package com.fastlib.net2.param;

/**
 * Created by sgfb on 2019\12\19.
 */
public class FloatParamParser extends SingleParamParser<Float>{
    @Override
    protected String parseParam(Float param) {
        return Float.toString(param);
    }
}
