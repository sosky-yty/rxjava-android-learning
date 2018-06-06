package com.example.sosky.rxjava_android_learning;

import android.annotation.SuppressLint;
import android.app.Application;
import android.support.multidex.MultiDexApplication;

import com.example.sosky.rxjava_android_learning.volley.MyVolley;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

import timber.log.Timber;

public class Myapp extends MultiDexApplication {
    private static Myapp mMyapp;
    private RefWatcher mRefWatcher;

    public static Myapp getmMyapp(){
        return mMyapp;
    }

    public static RefWatcher getWatcher(){
        return mMyapp.getmMyapp().getmRefWatcher();
    }

    public RefWatcher getmRefWatcher() {
        return mRefWatcher;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        if(LeakCanary.isInAnalyzerProcess(this)){
            return;
        }

        mMyapp = (Myapp) getApplicationContext();
        mRefWatcher = LeakCanary.install(this);

        MyVolley.init(this);

        Timber.plant(new Timber.DebugTree());
    }
}
