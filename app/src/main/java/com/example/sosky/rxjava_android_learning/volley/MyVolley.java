package com.example.sosky.rxjava_android_learning.volley;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class MyVolley {
    private static RequestQueue mRequestQueue;

    private MyVolley(){

    }

    public static void init(Context context){
        mRequestQueue = Volley.newRequestQueue(context);
    }

    public static RequestQueue getmRequestQueue() {
        if (mRequestQueue== null){
            throw  new IllegalStateException("volle is not inited");
        }
        return mRequestQueue;
    }
}
