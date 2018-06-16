package com.example.sosky.rxjava_android_learning.rxbus;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.sosky.rxjava_android_learning.Fragment.BaseFargment;
import com.example.sosky.rxjava_android_learning.MainActivity;
import com.example.sosky.rxjava_android_learning.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

@SuppressLint("ValidFragment")
class RxBusDemo_TopFragment extends BaseFargment {
    private RxBus _rxBus;

    @Override
    public View onCreateView(
            LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_rxbux_top, container, false);
        ButterKnife.bind(this, layout);
        return layout;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        _rxBus = ((MainActivity) getActivity()).getRxBusSingleton();
    }

    @OnClick(R.id.btn_demo_rxbus_tap)
    public void onTapButtonClicked() {
        if (_rxBus.hasObservers()) {
            _rxBus.send(new Rxbus_fragment.TapEvent());
        }
    }
}
