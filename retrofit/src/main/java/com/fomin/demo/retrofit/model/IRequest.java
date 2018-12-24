package com.fomin.demo.retrofit.model;

import com.fomin.demo.retrofit.bean.PBDevice;
import com.fomin.demo.retrofit.bean.PBNetworkType;

/**
 * Created by Fomin on 2018/12/11.
 */
public interface IRequest {
    String getVersionName();

    Integer getVersionCode();

    PBNetworkType getNetworkType();

    PBDevice getDevice();

}
