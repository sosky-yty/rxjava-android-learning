package com.example.sosky.rxjava_android_learning.Fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;

import com.example.sosky.rxjava_android_learning.LogAdapter;
import com.example.sosky.rxjava_android_learning.R;
import com.jakewharton.rxbinding2.view.RxView;
import com.jakewharton.rxbinding2.widget.RxTextView;
import com.jakewharton.rxbinding2.widget.TextViewTextChangeEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import timber.log.Timber;

import static co.kaush.core.util.CoreNullnessUtils.isNotNullOrEmpty;
import static java.lang.String.format;

public class Debounce_fragment extends BaseFargment {
    @BindView(R.id.list_debounce)
    ListView listView;

    @BindView(R.id.edit_debounce)
    EditText editText;

    private Disposable disposable;
    private Unbinder unbinder;
    private LogAdapter adapter;
    private List<String> logs;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setuplogs();
        disposable = RxTextView.textChangeEvents(editText)
                .debounce(400, TimeUnit.MICROSECONDS)
                .filter(changes -> isNotNullOrEmpty(changes.text().toString()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(getObserver());
    }

    private DisposableObserver<TextViewTextChangeEvent> getObserver(){
        return new DisposableObserver<TextViewTextChangeEvent>(){

            @Override
            public void onNext(TextViewTextChangeEvent textViewTextChangeEvent) {
                log(format("Searching for %s", textViewTextChangeEvent.text().toString()));
            }

            @Override
            public void onError(Throwable e) {
                Timber.e("Rxjava 出错");
                log("rxjava 出错");
            }

            @Override
            public void onComplete() {
                Timber.d("edittext事件完成");
            }
        };
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_debounce,container,false);
        unbinder = ButterKnife.bind(this,layout);
        return layout;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        disposable.dispose();
    }

    @OnClick(R.id.btn_debounce)
    public void onClickClear(){
        logs =  new ArrayList<String>();
        adapter.clear();
    }

    private void setuplogs(){
        logs = new ArrayList<String>();
        adapter = new LogAdapter(getActivity(),new ArrayList<String>());
        listView.setAdapter(adapter);
    }

    private Boolean isMainThrad(){
        return (Looper.myLooper()==Looper.getMainLooper());
    }

    private void log(String msg){
        if (isMainThrad()){
            logs.add(0,msg+" (main thread) ");
            adapter.clear();
            adapter.addAll(logs);
        }else{
            logs.add(0,msg+" (not main thread) ");
            new Handler(Looper.getMainLooper())
                    .post(
                            ()->{
                                adapter.clear();
                                adapter.addAll(logs);
                            }
                    );
        }
    }

}
