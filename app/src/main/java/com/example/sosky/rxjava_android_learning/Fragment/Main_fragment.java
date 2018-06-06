package com.example.sosky.rxjava_android_learning.Fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.sosky.rxjava_android_learning.R;

import java.util.Objects;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


public class Main_fragment extends BaseFargment {

    private Unbinder unbinder;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_main_fragment,container,false);
        unbinder = ButterKnife.bind(this,layout);
        return layout;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.btn_schedulers)
    public void scheduler_fragment(){
        clickedOn(new Scheduler_fragment());
    }

    private void clickedOn(@NonNull Fragment fragment) {
        final String tag = fragment.getClass().toString();
        getActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .addToBackStack(tag)
                .replace(android.R.id.content, fragment, tag)
                .commit();

    }
}