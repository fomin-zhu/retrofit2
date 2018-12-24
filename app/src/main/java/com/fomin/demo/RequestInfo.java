package com.fomin.demo;

import com.fomin.demo.retrofit.bean.PBDevice;
import com.fomin.demo.retrofit.bean.PBNetworkType;
import com.fomin.demo.retrofit.model.IRequest;

/**
 * Created by Fomin on 2018/12/13.
 */
public class RequestInfo implements IRequest {

    @Override
    public String getVersionName() {
        return null;
    }

    @Override
    public Integer getVersionCode() {
        return null;
    }

    @Override
    public PBNetworkType getNetworkType() {
        return null;
    }

    @Override
    public PBDevice getDevice() {
        return null;
    }
}
