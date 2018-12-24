package com.fomin.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.fomin.demo.retrofit.HttpManager;
import com.fomin.demo.retrofit.bean.PBAppInfo;
import com.fomin.demo.retrofit.bean.PBResponse;
import com.fomin.demo.retrofit.model.ApiCallback;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        HttpManager.getInstance().request(new PBAppInfo.Builder().build(), 1001, PBResponse.class, new ApiCallback<PBResponse>() {

            @Override
            public void onSuccess(PBResponse response) {
                super.onSuccess(response);
            }

            @Override
            public void onFailure(int code, String message) {
                super.onFailure(code, message);
            }
        });
    }
}
