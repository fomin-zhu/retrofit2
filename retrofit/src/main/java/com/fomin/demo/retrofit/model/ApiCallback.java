package com.fomin.demo.retrofit.model;

import com.squareup.wire.Message;

/**
 * Created by Fomin on 2018/12/11.
 */
public class ApiCallback<T extends Message> {
    private boolean isDisposed = false;

    public boolean isDisposed() {
        return isDisposed;
    }

    public void setDisposed(boolean disposed) {
        isDisposed = disposed;
    }

    public void onStart() {

    }

    public void onSuccess(T response) {

    }

    public void onFailure(int code, String message) {

    }

    public void onCompleted() {

    }
}
