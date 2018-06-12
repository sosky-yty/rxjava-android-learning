package com.example.sosky.rxjava_android_learning.Fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.example.sosky.rxjava_android_learning.R;

import java.util.List;
import java.util.Objects;

import butterknife.BindView;
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

    @OnClick({R.id.eventbuff_btn})
    public void eventbuff_fragment(){
        clickedOn(new eventbuff_fragment());
    }

    @OnClick(R.id.debounce_btn)
    public void debounce_frament(){
        clickedOn(new Debounce_fragment());
    }


    @OnClick(R.id.btn_retrofit)
    public void retrofit_fragment(){clickedOn(new Retrofit_fragment());}

    @OnClick(R.id.btn_double_binding)
    public void doublebinding_fragment(){clickedOn(new DoubleBindTextview_fragment());}

    @OnClick(R.id.btn_polling)
    public void polling_fragment(){clickedOn(new Polling_fragment());}
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
