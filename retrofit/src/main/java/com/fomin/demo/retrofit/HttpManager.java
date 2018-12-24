package com.fomin.demo.retrofit;

import android.annotation.SuppressLint;

import com.fomin.demo.retrofit.bean.PBAppInfo;
import com.fomin.demo.retrofit.bean.PBRequest;
import com.fomin.demo.retrofit.bean.PBResponse;
import com.fomin.demo.retrofit.model.ApiCallback;
import com.fomin.demo.retrofit.model.ApiResult;
import com.fomin.demo.retrofit.model.IRequest;
import com.fomin.demo.retrofit.core.RetrofitHandler;
import com.squareup.wire.Message;
import com.squareup.wire.ProtoAdapter;

import java.io.IOException;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okio.ByteString;

/**
 * Created by Fomin on 2018/12/11.
 */
public final class HttpManager {

    private IRequest mRequest;
    private RetrofitHandler mHandler;

    private static class Holder {
        private static final HttpManager INSTANCE = new HttpManager();
    }

    private HttpManager() {

    }

    public static HttpManager getInstance() {
        return HttpManager.Holder.INSTANCE;
    }

    public void init(IRequest request, String url){
        mRequest = request;
        mHandler = RetrofitHandler.getInstance(url);
    }

    /**
     * 组合请求体
     *
     * @param request     请求体
     * @param messageType 请求类型
     * @param <T>         Message
     * @return PBRequest
     */
    private <T extends Message> PBRequest buildBody(final T request, final int messageType) {
        return new PBRequest.Builder()
                .type(messageType)
                .timestamp(System.currentTimeMillis())
                .messageData(ByteString.of(request.encode()))
                .appInfo(new PBAppInfo.Builder()
                        .network(mRequest.getNetworkType())
                        .versionCode(mRequest.getVersionCode())
                        .versionName(mRequest.getVersionName())
                        .device(mRequest.getDevice())
                        .build())
                .build();
    }

    @SuppressLint("CheckResult")
    public <T extends Message, P extends Message> Disposable request(final T request, final int messageType,
                                                                     final Class<P> pClass, final ApiCallback<P> apiCallback) {
        Observable observable = mHandler.send(buildBody(request, messageType))
                .map(new Function<PBResponse, ApiResult<? extends Message>>() {
                    @Override
                    public ApiResult<? extends Message> apply(PBResponse pbResponse) throws Exception {
                        return apiResult(pClass, pbResponse);
                    }
                });
        observable.doOnDispose(new Action() {
            @Override
            public void run() throws Exception {
                apiCallback.setDisposed(true);
            }
        });
        return toSubscribe(observable, new Consumer<ApiResult<P>>() {
            @Override
            public void accept(ApiResult<P> apiResult) throws Exception {
                if (apiCallback == null || apiCallback.isDisposed()) return;
                try {
                    if (apiResult.getCode() == 200) {
                        apiCallback.onSuccess(apiResult.getResponse());
                    } else {
                        apiCallback.onFailure(apiResult.getCode(), apiResult.getMessage());
                    }
                } catch (Exception ex) {
                    apiCallback.onFailure(1004, "客户端处理异常");
                } finally {
                    apiCallback.onCompleted();
                }
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) {
                if (apiCallback == null || apiCallback.isDisposed()) return;
                //客户端本地的异常，如断网等
                try {
                    apiCallback.onFailure(1005, "网络连接错误");
                } catch (Exception e) {
                } finally {
                    apiCallback.onCompleted();
                }
            }
        }, new Action() {
            @Override
            public void run() {
                if (apiCallback == null || apiCallback.isDisposed()) return;

                //onCompleted will not be called when occurs network exception, like disconnected/timeout, replace invoking at onNext/onError
            }
        }, new Consumer<Disposable>() {
            @Override
            public void accept(Disposable disposable) {
                if (apiCallback == null || apiCallback.isDisposed()) return;
                apiCallback.onStart();
            }
        });
    }

    private <T> Disposable toSubscribe(Observable<T> o, Consumer<T> onNext, Consumer<Throwable> onError, Action onComplete, Consumer<Disposable> onSubscribe) {
        return o.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onNext, onError, onComplete, onSubscribe);
    }

    private <P extends Message> ApiResult<? extends Message> apiResult(Class<P> pClass, PBResponse pbResponse) {
        try {
            if (pbResponse.resultCode != 200) {
                if (pbResponse.messageData == null || pClass == null) {
                    return new ApiResult<>(pbResponse.resultCode, pbResponse.resultInfo, null);
                }
                return getApiResult(pClass, pbResponse);
            }
            if (pClass == null || pClass == Message.class || pbResponse.messageData == null) {
                return new ApiResult<>(pbResponse.resultCode, pbResponse.resultInfo, null);
            } else {
                return getApiResult(pClass, pbResponse);
            }
        } catch (Exception ex) {
            return new ApiResult<>(1003, "数据处理异常", null);
        }
    }

    private <P extends Message> ApiResult<? extends Message> getApiResult(Class<P> pClass, PBResponse pbResponse) {
        byte[] bytes = pbResponse.messageData != null ? pbResponse.messageData.toByteArray() : null;
        P response = getResponse(pClass, bytes);
        if (response == null) {
            return new ApiResult<>(1002, "数据转换错误", null);
        } else {
            return new ApiResult<>(pbResponse.resultCode, pbResponse.resultInfo, pbResponse);
        }
    }

    private <P extends Message> P getResponse(Class<P> pClass, byte[] bytes) {
        try {
            return ProtoAdapter.get(pClass).decode(bytes);
        } catch (IOException e) {
            return null;
        }
    }
}
