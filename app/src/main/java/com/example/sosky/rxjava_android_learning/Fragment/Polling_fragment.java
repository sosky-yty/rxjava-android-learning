package com.example.sosky.rxjava_android_learning.Fragment;

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

import org.reactivestreams.Publisher;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.Flowable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import timber.log.Timber;

public class Polling_fragment extends BaseFargment {

    private static final int INITIAL_DELAY = 0;
    private static final int POLLING_INTERVAL = 1000;
    private static final int POLL_COUNT = 8;

    @BindView(R.id.list_polling)
    ListView loglist;

    private int mcount = 0;
    private LogAdapter adapter;
    private List<String> logs;
    private CompositeDisposable disposables;
    private Unbinder unbinder;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        disposables = new CompositeDisposable();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setuplog();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_polling,container,false);
        unbinder = ButterKnife.bind(this,layout);
        return layout;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        disposables.clear();
        unbinder.unbind();
    }

    @OnClick(R.id.simple_polling)
    public void onSimple_polling(){
        final int count = POLL_COUNT;

        Disposable d =  Flowable.interval(INITIAL_DELAY, POLLING_INTERVAL,TimeUnit.MILLISECONDS)
                .map(this::doNetWorkbackground)
                //迭代器调用几次
                .take(count)
                .doOnSubscribe(
                subscription -> {
                    log(String.format("Start simple polling - %s", mcount));
                }).subscribe(taskName -> {
                    log(
                            String.format(
                                    Locale.US,
                                    "Executing polled task [%s] now time : [xx:%02d]",
                                    taskName,
                                    _getSecondHand()));
                });
        disposables.add(d);
    }

    @OnClick(R.id.start_increasingly_delayed_polling)
    public void start_increasingly_polling(){
        final int pollingInterval = POLLING_INTERVAL;
        final int pollCount = POLL_COUNT;

        log(
                String.format(
                        Locale.US, "Start increasingly delayed polling now time: [xx:%02d]", _getSecondHand()));

        disposables.add(
                Flowable.just(1L)
                        .repeatWhen(new RepeatWithDelay(pollCount, pollingInterval))
                        .subscribe(
                                o -> log(String.format(Locale.US, "Executing polled task now time : [xx:%02d]",
                                                        _getSecondHand())),
                                e -> Timber.d(e, "arrrr. Error")));
    }

    private String doNetWorkbackground(long attem){
        try {
            if (attem == 4) {
                Thread.sleep(2000);
            } else {
                Thread.sleep(1000);
            }
        }catch (Exception e){
            Timber.e(e.getMessage());
            e.printStackTrace();
        }

        mcount++;
        return String.valueOf(mcount);
    }


    private int _getSecondHand() {
        long millis = System.currentTimeMillis();
        return (int)
                (TimeUnit.MILLISECONDS.toSeconds(millis)
                        - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
    }

    public class RepeatWithDelay implements Function<Flowable<Object>, Publisher<Long>> {

        private final int _repeatLimit;
        private final int _pollingInterval;
        private int _repeatCount = 1;

        RepeatWithDelay(int repeatLimit, int pollingInterval) {
            _pollingInterval = pollingInterval;
            _repeatLimit = repeatLimit;
        }

        @Override
        public Publisher<Long> apply(Flowable<Object> inputFlowable) throws Exception {
            return inputFlowable.flatMap(
                    (Function<Object, Publisher<Long>>) o -> {
                        if (_repeatCount >= _repeatLimit) {
                            log("Completing sequence");
                            return Flowable.empty();
                        }
                        _repeatCount++;
                        return Flowable.timer(_repeatCount * _pollingInterval, TimeUnit.MILLISECONDS);
                    });
        }
    }

    private void setuplog(){
        logs = new ArrayList<String>();
        adapter = new LogAdapter(getActivity(),new ArrayList<String>());
        loglist.setAdapter(adapter);
    }

    private Boolean isMainthread(){
        return (Looper.myLooper()==Looper.getMainLooper());
    }


    private void log(String msg){
        if (isMainthread()){
            logs.add(0,msg+"( main thread) ");
            adapter.clear();
            adapter.addAll(logs);
        }else{
            logs.add(0,msg+"( not main thread) ");
            new Handler(Looper.getMainLooper()).post(
                    ()->{
                        adapter.clear();
                        adapter.addAll(logs);
                    }
            );
        }
    }
}
