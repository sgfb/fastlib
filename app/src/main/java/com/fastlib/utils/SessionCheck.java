package com.fastlib.utils;

/**
 * Created by sgfb on 18/3/9.
 * 接口单元测试，业务测试判断
 */
public interface SessionCheck<T>{

    /**
     * 检查接口业务层是否正常
     * @param entity 数据实体
     * @return 如果为空代表正常，否则就是异常提示语
     */
    String check(T entity);
}