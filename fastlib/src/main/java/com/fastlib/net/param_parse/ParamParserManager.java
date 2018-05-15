package com.fastlib.net.param_parse;

import com.fastlib.net.Request;

/**
 * Created by sgfb on 18/5/2.
 * 参数解析管理器
 */
public class ParamParserManager{
    private ParamParserNode mNode;
    private NetParamParser mLastParser;

    public void addParser(NetParamParser parser){
        ParamParserNode newNode=new ParamParserNode();

        newNode.parser=parser;
        if(mNode==null)
            mNode=newNode;
        else{
            ParamParserNode nextNode=mNode.next;

            mNode.next=newNode;
            newNode.next=nextNode;
        }
    }

    /**
     * 增加末尾参数解析器
     * @param lastParser 参数解析器
     */
    public void setParserLast(NetParamParser lastParser){
        if(mLastParser!=null)
            addParser(mLastParser);
        mLastParser=lastParser;
    }

    public void parserParam(boolean duplication,Request request,String key,Object obj){
        ParamParserNode node=mNode;
        boolean handled=false;

        while (node!=null){
            if(node.parser.canParse(request,key,obj)&& node.parser.parseParam(duplication,request,key,obj)) {
                handled=true;
                break;
            }
            else node=node.next;
        }
        if(!handled&&mLastParser!=null) mLastParser.parseParam(duplication,request,key,obj);
    }

    class ParamParserNode{
        NetParamParser parser;
        ParamParserNode next;
    }
}
