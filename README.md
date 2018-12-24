# retrofit2
### 使用
初始化
```
HttpManager.getInstance().init(new RequestInfo(), mServerUrl);
```
使用MVP或者MVVM的架构，在P或者VM层调用请求
```
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
```
