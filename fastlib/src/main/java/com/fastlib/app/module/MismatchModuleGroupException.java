package com.fastlib.app.module;

import java.util.List;

/**
 * Created by sgfb on 2018/7/25.
 * 当有相同module name但是group不匹配时抛出这个异常
 */
public class MismatchModuleGroupException extends Exception{
    private List<String> mOtherGroups;  //同名其他group

    public MismatchModuleGroupException(List<String> otherGroups){
        super("没有匹配的Group");
        mOtherGroups=otherGroups;
    }

    public List<String> getOtherGroups(){
        return mOtherGroups;
    }
}
