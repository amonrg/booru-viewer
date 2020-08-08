package com.orin.booruviewer.api;

import org.json.JSONArray;

public interface ApiCallback {
    void onSuccess(JSONArray response);
    void onError(String errorMsg);
}
