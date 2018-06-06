package com.example.sosky.rxjava_android_learning.BaseFragment;

import android.app.Fragment;

import com.example.sosky.rxjava_android_learning.Myapp;
import com.squareup.leakcanary.RefWatcher;

public class BaseFargment  extends Fragment{
    @Override
    public void onDestroy() {
        super.onDestroy();
        RefWatcher refWatcher = Myapp.getWatcher();
        refWatcher.watch(this);
    }
}
