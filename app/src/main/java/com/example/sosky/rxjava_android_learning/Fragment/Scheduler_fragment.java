package com.example.sosky.rxjava_android_learning.Fragment;


import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.example.sosky.rxjava_android_learning.LogAdapter;
import com.example.sosky.rxjava_android_learning.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class Scheduler_fragment extends BaseFargment {
    @BindView(R.id.scheduler_progressBar)
    ProgressBar mProgressBar;

    @BindView(R.id.scheduler_list)
    ListView logListView;

    private LogAdapter adapter;
    private List<String> logs;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private Unbinder unbinder;

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
        compositeDisposable.clear();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setupLogger();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View  layout = inflater.inflate(R.layout.fragment_scheduler,container,false);
        unbinder = ButterKnife.bind(this,layout);
        return layout;
    }

    @OnClick(R.id.scheduler_btn)
    public void startbackground(){
        log("按钮事件触发");
        mProgressBar.setVisibility(View.VISIBLE);
        DisposableObserver<Boolean> d = getDisposableObserver();
        getObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(d);

        compositeDisposable.add(d);
    }

    private Observable<Boolean> getObservable(){
        //通过just创建的observable,将按照参数顺序调用observe的onNext()
        return Observable.just(true,false)
                .map(aBoolen->{
                    log("后台耗时工作");
                    background_operation();
                    return aBoolen;
                });
    }


    private DisposableObserver<Boolean> getDisposableObserver(){
        return new DisposableObserver<Boolean>() {

            @Override
            public void onNext(Boolean aBoolean) {
                log(String.format("onNext with return value \"%b\"",aBoolean));
            }

            @Override
            public void onError(Throwable e) {
                Timber.e(e,"rajava is error");
                log("error:"+e.getMessage());
                mProgressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onComplete() {
                log("后台任务完成");
                mProgressBar.setVisibility(View.INVISIBLE);
            }
        };
    }

    /**
     * 模拟后台耗时操作
     */
    private void background_operation(){
        log("后台进行耗时操作...");
        try{
            Thread.sleep(1000);
        }catch(InterruptedException e ){
            log("后台操作出错");
        }
    }

    /**
     * 设置listview适配器
     */
    private void setupLogger(){
        logs = new ArrayList<>();
        adapter = new LogAdapter(getActivity(),new ArrayList<String>());
        logListView.setAdapter(adapter);
    }

    /**
     * 判断当前是否为主线程
     * @return
     */
    private boolean isMainThread(){
        return Looper.myLooper()== Looper.getMainLooper();
    }

    private void log(String message){
        if(isMainThread()){
            logs.add(0,message+"  (main thread) ");
            adapter.clear();
            adapter.addAll(logs);
        }else{
            logs.add(0, message + " (NOT main thread) ");
            new Handler(Looper.getMainLooper())
                    .post(
                            () -> {
                                adapter.clear();
                                adapter.addAll(logs);
                            });
        }
    }
}
