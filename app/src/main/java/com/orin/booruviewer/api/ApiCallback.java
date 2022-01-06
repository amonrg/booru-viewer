package com.orin.booruviewer.api;

public interface ApiCallback {
    void onSuccess(Object response);
    void onError(String errorMsg);
}
