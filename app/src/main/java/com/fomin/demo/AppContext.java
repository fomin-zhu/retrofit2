package com.fomin.demo;

import android.app.Application;

import com.fomin.demo.retrofit.HttpManager;

/**
 * Created by Fomin on 2018/12/13.
 */
public class AppContext extends Application {

    private static final String mServerUrl = "http://192.168.1.101";

    @Override
    public void onCreate() {
        super.onCreate();
        HttpManager.getInstance().init(new RequestInfo(), mServerUrl);
    }
}
