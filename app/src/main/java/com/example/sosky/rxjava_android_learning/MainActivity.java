package com.example.sosky.rxjava_android_learning;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.sosky.rxjava_android_learning.Fragment.Main_fragment;
import com.example.sosky.rxjava_android_learning.rxbus.RxBus;

public class MainActivity extends AppCompatActivity {

    private RxBus _rxBus = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState == null){
                 getSupportFragmentManager()
                    .beginTransaction()
                    .replace(android.R.id.content, new Main_fragment(), this.toString())
                    .commit();
        }
    }


    public RxBus getRxBusSingleton() {
        if (_rxBus == null) {
            _rxBus = new RxBus();
        }

        return _rxBus;
    }

}
