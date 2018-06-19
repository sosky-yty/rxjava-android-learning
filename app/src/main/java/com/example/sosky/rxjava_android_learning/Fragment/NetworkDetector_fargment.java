package com.example.sosky.rxjava_android_learning.Fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.sosky.rxjava_android_learning.LogAdapter;
import com.example.sosky.rxjava_android_learning.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.processors.PublishProcessor;

public class NetworkDetector_fargment extends BaseFargment {
    @BindView(R.id.network_log)
    ListView listView;

    Unbinder unbinder;
    private LogAdapter adapter;
    private List<String> logs;
    private BroadcastReceiver broadcastReceiver;
    private Disposable disposable;
    private PublishProcessor<Boolean> publishProcessor;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setuplogs();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_networkdetector,container,false);
        unbinder = ButterKnife.bind(this,layout);
        return layout;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


    @Override
    public void onStart() {
        super.onStart();

        publishProcessor = PublishProcessor.create();

        disposable =
                publishProcessor
                        .startWith(getConnectivityStatus(getActivity()))
                        .distinctUntilChanged()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                online -> {
                                    if (online) {
                                        log("你当前在线");
                                    } else {
                                        log("你当前不在线");
                                    }
                                });

        listenToNetworkConnectivity();
    }

    private void listenToNetworkConnectivity() {

        broadcastReceiver =
                new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        publishProcessor.onNext(getConnectivityStatus(context));
                    }
                };

        final IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        getActivity().registerReceiver(broadcastReceiver, intentFilter);
    }

    private boolean getConnectivityStatus(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    private void setuplogs(){
        logs = new ArrayList<>();
        adapter = new LogAdapter(getActivity(),new ArrayList<>());
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
