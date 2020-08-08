package com.orin.booruviewer.api;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class ApiRequest  {
    private static ApiRequest instance;
    private static RequestQueue requestQueue;
    private static Context ctx;

    private ApiRequest(Context context) {
        ctx = context.getApplicationContext();
        requestQueue = getRequestQueue();
    }

    public static synchronized void init(Context context) {
        instance = new ApiRequest(context);
    }

    public static synchronized ApiRequest getInstance() {
        if (instance == null) {
            throw new IllegalStateException(ApiRequest.class.getSimpleName() +
                    " is not initialized, call init(...) first");
        }
        return instance;
    }

    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(ctx.getApplicationContext());
        }
        return requestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }
}
