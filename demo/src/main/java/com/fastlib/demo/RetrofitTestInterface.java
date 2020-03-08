package com.fastlib.demo;

import com.fastlib.demo.base.Response;

import java.io.File;

import retrofit2.http.Field;
import retrofit2.http.POST;
import retrofit2.http.Part;
import rx.Observable;

/**
 * Created by sgfb on 2020\03\05.
 */
public interface RetrofitTestInterface{

    @POST("a")
    Observable<Response<String>> uploadFile(@Part("file")File file);

    @POST("b")
    Observable<Response<Boolean>> updateUserAvatar(@Field("AvatarUrl")String avatarUrl);
}
