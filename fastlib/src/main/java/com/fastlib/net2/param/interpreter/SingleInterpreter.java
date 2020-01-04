package com.fastlib.net2.param.interpreter;

import com.fastlib.net2.param.RequestParam;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sgfb on 2019\12\24.
 */
public abstract class SingleInterpreter implements ParamInterpreter{

    protected abstract InputStream interpreterAdapter(RequestParam param);

    @Override
    public List<InputStream> interpreter(RequestParam param) {
        List<InputStream> list=new ArrayList<>();
        list.add(interpreterAdapter(param));
        return list;
    }
}
