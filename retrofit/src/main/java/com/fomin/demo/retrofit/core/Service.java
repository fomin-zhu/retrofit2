package com.fomin.demo.retrofit.core;

import com.fomin.demo.retrofit.bean.PBRequest;
import com.fomin.demo.retrofit.bean.PBResponse;

import io.reactivex.Observable;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by Fomin on 2018/12/3.
 */
public interface Service {

    @POST("users/new")
    Observable<Response<PBResponse>> sendMessage(@Body PBRequest request);
}
