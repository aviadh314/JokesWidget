package com.my.jokeswidget.httpclient;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import android.content.Context;

public class VolleyRQSingleton {
    private static VolleyRQSingleton mInstance;
    private RequestQueue mRequestQueue;
    private static Context mCtx;

    private VolleyRQSingleton(Context context) {
        mCtx = context;
        mRequestQueue = getRequestQueue();
    }

    public static synchronized VolleyRQSingleton getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new VolleyRQSingleton(context);
        }
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {

            mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }
}
