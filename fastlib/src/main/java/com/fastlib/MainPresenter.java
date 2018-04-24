package com.fastlib;

/**
 * Created by Administrator on 2018/4/24.
 */

public class MainPresenter extends UserModel_G{

    public void toLogin(String phone,String password){
        login(phone,password);
    }

    @Override
    public void loginCallback(String result) {
        super.loginCallback(result);
        System.out.println("result:"+result);
    }
}
