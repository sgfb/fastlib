package com.fastlib.net.param_parse;

import com.fastlib.net.Request;

/**
 * Created by sgfb on 18/5/1.
 * 解析请求参数的非基本类型字段
 */
public interface NetParamParser{

    /**
     * 判断是否能解析
     * @param request 网络请求
     * @param key 键
     * @param obj 值
     * @return true能解析，false不能
     */
    boolean canParse(Request request, String key, Object obj);


    /**
     * 尝试解析参数
     * @param duplication 参数重复
     * @param request 网络请求
     * @param key 键
     * @param obj 值
     * @return true解析成功，false解析失败
     */
    boolean parseParam(boolean duplication,Request request,String key,Object obj);

    /**
     * 优先级，影响执行顺序,越小越早执行
     * @return 优先级
     */
    int priority();
}