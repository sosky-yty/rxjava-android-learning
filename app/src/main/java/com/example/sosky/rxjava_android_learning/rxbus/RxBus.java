package com.example.sosky.rxjava_android_learning.rxbus;

import com.jakewharton.rxrelay2.PublishRelay;
import com.jakewharton.rxrelay2.Relay;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;

public class RxBus {
    private final Relay<Object> mbus = PublishRelay.create().toSerialized();

    public void send(Object o ){
        mbus.accept(o);
    }

    public Flowable<Object> asFlowable(){
        return mbus.toFlowable(BackpressureStrategy.LATEST);
    }

    public boolean hasObservers(){
        return mbus.hasObservers();
    }
}
