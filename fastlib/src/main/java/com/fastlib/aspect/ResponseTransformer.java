package com.fastlib.aspect;

/**
 * Created by sgfb on 2020\02\28.
 */
public class ResponseTransformer implements NetResultTransformer<Response> {

    @Override
    public Object transform(Response result) {
        return result.data;
    }
}
