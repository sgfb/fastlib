package com.fastlib.demo.net;

import com.fastlib.demo.base.Response;
import com.fastlib.net2.utils.NetResultTransformer;

/**
 * Created by sgfb on 2020\03\02.
 */
public class ResponseTransformer implements NetResultTransformer<Response>{

    @Override
    public Object transform(Response result) {
        return result.data;
    }
}
