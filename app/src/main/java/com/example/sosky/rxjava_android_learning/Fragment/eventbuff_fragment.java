package com.example.sosky.rxjava_android_learning.Fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.example.sosky.rxjava_android_learning.LogAdapter;
import com.example.sosky.rxjava_android_learning.R;
import com.jakewharton.rxbinding2.view.RxView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import timber.log.Timber;

public class eventbuff_fragment extends BaseFargment{
    @BindView(R.id.list_eventbuff)
    ListView listView;

    @BindView(R.id.btn_eventbuff)
    Button button;

    private Unbinder unbinder;
    private LogAdapter adapter;
    private List<String> logs;
    //用于取消订阅关系
    private Disposable disposable;

    @Override
    public void onResume() {
        super.onResume();
        disposable = getDisposable();
    }

    @Override
    public void onPause() {
        super.onPause();
        disposable.dispose();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setuplog();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_buffer,container,false);
        unbinder = ButterKnife.bind(this,layout);
        return layout;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    private Disposable getDisposable(){
        return RxView.clicks(button)
                .map(
                        onClickEvent ->{
                            Timber.d("点击了一尺");
                            log("点击一次");
                            return 1;
                        }
                ).buffer(3, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(
                        new DisposableObserver<List<Integer>>(){

                            @Override
                            public void onNext(List<Integer> integers) {
                                Timber.d("onNext");
                                if ((integers.size()>0)){
                                    log("点击了"+integers.size());
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                Timber.e("Rx出错,检查log");
                            }

                            @Override
                            public void onComplete() {
                                Timber.d("事件完成");
                            }
                        }
                );
    }


    private void setuplog(){
        logs = new ArrayList<>();
        adapter = new LogAdapter(getActivity(),new ArrayList<String>());
        listView.setAdapter(adapter);
    }

    private Boolean isMainThread(){
        return (Looper.myLooper() == Looper.getMainLooper());
    }

    private void log(String msg){
        if (isMainThread()){
            logs.add(0,msg+"(main thread)");
            adapter.clear();
            adapter.addAll(logs);
        }else{
            logs.add(0,msg+"(not main thread)");
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
