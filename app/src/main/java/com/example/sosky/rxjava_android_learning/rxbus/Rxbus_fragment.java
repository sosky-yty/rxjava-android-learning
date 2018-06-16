package com.example.sosky.rxjava_android_learning.rxbus;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.sosky.rxjava_android_learning.Fragment.BaseFargment;
import com.example.sosky.rxjava_android_learning.R;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public class Rxbus_fragment extends BaseFargment {

    private Unbinder unbinder;

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rxbus,container,false);
        unbinder = ButterKnife.bind(this,view);
        return  view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.demo_rxbus_frag_1, new RxBusDemo_TopFragment())
                .replace(R.id.demo_rxbus_frag_2, new RxBusDemo_BottomFragment())
                .commit();
    }

    public static class TapEvent {}
}
