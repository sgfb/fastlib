package com.fastlib.net2.param.interpreter;

import com.fastlib.net2.param.RequestParam;

import java.io.InputStream;
import java.util.List;

/**
 * Created by sgfb on 2019\12\24.
 * 参数解析器.解析{@link com.fastlib.net2.param.RequestParam}为Http请求时支持的参数（InputStream列表）
 */
public interface ParamInterpreter {

    List<InputStream> interpreter(RequestParam param);
}
