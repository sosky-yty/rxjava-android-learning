package com.example.sosky.rxjava_android_learning.volley;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.example.sosky.rxjava_android_learning.Fragment.BaseFargment;
import com.example.sosky.rxjava_android_learning.LogAdapter;
import com.example.sosky.rxjava_android_learning.R;

import org.json.JSONObject;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subscribers.DisposableSubscriber;
import timber.log.Timber;

public class Volley_fragment extends BaseFargment {

    private static final String TAG = "volley_fragment";
    @BindView(R.id.list_threading_log)
    ListView _logsList;

    private List<String> _logs;
    private LogAdapter _adapter;
    private Unbinder unbinder;

    private CompositeDisposable _disposables = new CompositeDisposable();

    @Override
    public View onCreateView(
            LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_volley, container, false);
        unbinder = ButterKnife.bind(this, layout);
        return layout;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        _setupLogger();
    }

    @Override
    public void onPause() {
        super.onPause();
        _disposables.clear();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    public Flowable<JSONObject> newGetRouteData() {
        return Flowable.defer(
                () -> {
                    try {
                        return Flowable.just(getRouteData());
                    } catch (InterruptedException | ExecutionException e) {
                        Log.e("routes", e.getMessage());
                        return Flowable.error(e);
                    }
                });
    }

    @OnClick(R.id.btn_start_operation)
    void startRequest() {
        startVolleyRequest();
    }

    private void startVolleyRequest() {
        DisposableSubscriber<JSONObject> d =
                new DisposableSubscriber<JSONObject>() {
                    @Override
                    public void onNext(JSONObject jsonObject) {
                        Log.e(TAG, "onNext " + jsonObject.toString());
                        _log("onNext " + jsonObject.toString());
                    }

                    @Override
                    public void onError(Throwable e) {
                        VolleyError cause = (VolleyError) e.getCause();
                        String s = new String(cause.networkResponse.data, Charset.forName("UTF-8"));
                        Log.e(TAG, s);
                        Log.e(TAG, cause.toString());
                        _log("onError " + s);
                    }

                    @Override
                    public void onComplete() {
                        Log.e(TAG, "onCompleted");
                        Timber.d("----- onCompleted");
                        _log("onCompleted ");
                    }
                };

        newGetRouteData()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(d);

        _disposables.add(d);
    }

    private JSONObject getRouteData() throws ExecutionException, InterruptedException {
        RequestFuture<JSONObject> future = RequestFuture.newFuture();
        String url = getString(R.string.url);
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, url, future, future);
        MyVolley.getmRequestQueue().add(req);
        return future.get();
    }

    private void _setupLogger() {
        _logs = new ArrayList<>();
        _adapter = new LogAdapter(getActivity(), new ArrayList<>());
        _logsList.setAdapter(_adapter);
    }

    private void _log(String logMsg) {

        if (_isCurrentlyOnMainThread()) {
            _logs.add(0, logMsg + " (main thread) ");
            _adapter.clear();
            _adapter.addAll(_logs);
        } else {
            _logs.add(0, logMsg + " (NOT main thread) ");

            // You can only do below stuff on main thread.
            new Handler(Looper.getMainLooper())
                    .post(
                            () -> {
                                _adapter.clear();
                                _adapter.addAll(_logs);
                            });
        }
    }

    private boolean _isCurrentlyOnMainThread() {
        return Looper.myLooper() == Looper.getMainLooper();
    }
}
