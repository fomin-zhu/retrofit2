package com.fomin.demo.retrofit.model;

import com.squareup.wire.Message;

/**
 * Created by Fomin on 2018/12/11.
 */
public class ApiResult<T extends Message> {
    private int code;
    private String message;
    private T response;

    public ApiResult(int code, String message, T response) {
        this.code = code;
        this.message = message;
        this.response = response;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public T getResponse() {
        return response;
    }
}
