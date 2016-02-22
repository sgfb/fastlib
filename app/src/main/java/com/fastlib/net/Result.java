package com.fastlib.net;

/**
 * 网络数据返回时的一些包装，默认返回的数据是json
 * Created by sgfb on 16/2/15.
 */
public class Result {
    private String message;
    private String body;
    private int code;
    private boolean success;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    @Override
    public String toString(){
        return "success:"+Boolean.toString(success)+" message:"+message+
                " code:"+Integer.toString(code)+" body:"+body;
    }
}
