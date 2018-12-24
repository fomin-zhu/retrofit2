package com.fomin.demo.retrofit.core;

import com.fomin.demo.retrofit.bean.PBRequest;
import com.fomin.demo.retrofit.bean.PBResponse;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.functions.Function;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.wire.WireConverterFactory;

/**
 * Created by Fomin on 2018/12/4.
 */
public final class RetrofitHandler {
    private Service mService;
    private static String mServiceUrl;

    private static class Holder {
        private static final RetrofitHandler INSTANCE = new RetrofitHandler();
    }

    private RetrofitHandler() {
        init();
    }

    public static RetrofitHandler getInstance(String serviceUrl) {
        mServiceUrl = serviceUrl;
        return Holder.INSTANCE;
    }

    private void init() {
        assert mServiceUrl != null;
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(mServiceUrl)
                .client(createClient())
                .addConverterFactory(WireConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        mService = retrofit.create(Service.class);
    }

    private OkHttpClient createClient() {
        return new OkHttpClient.Builder()
                .retryOnConnectionFailure(true)
                .connectionPool(new ConnectionPool(5, 1, TimeUnit.MINUTES))
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .writeTimeout(5, TimeUnit.SECONDS)
                .build();

    }

    public Observable<PBResponse> send(final PBRequest request) {
        return mService.sendMessage(request)
                .map(new Function<Response<PBResponse>, PBResponse>() {
                    @Override
                    public PBResponse apply(Response<PBResponse> response) throws Exception {
                        if (response == null) {
                            return failed(request.type, 1001, "未知错误");
                        }
                        if (response.code() != 200 || response.body() == null) {
                            return failed(request.type, response.code(), response.message());
                        }
                        return response.body();
                    }
                });
    }

    private PBResponse failed(int type, int code, String info) {
        return new PBResponse.Builder()
                .type(type)
                .resultCode(code)
                .resultInfo(info)
                .build();
    }
}
