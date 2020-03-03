package com.fastlib.utils.fitout;

/**
 * Created by sgfb on 2020\03\03.
 * 附件自动装配
 */
public interface AttachmentFitout{

    /**
     * 主件上装配附件
     * @param fieldInstance 主件
     * @param attachment    附件
     */
    void fitout(Object fieldInstance,Object attachment);
}
