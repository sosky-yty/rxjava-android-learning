package com.example.sosky.rxjava_android_learning.Fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;

import com.example.sosky.rxjava_android_learning.LogAdapter;
import com.example.sosky.rxjava_android_learning.R;
import com.example.sosky.rxjava_android_learning.retrofit.Contributor;
import com.example.sosky.rxjava_android_learning.retrofit.GithubApi;
import com.example.sosky.rxjava_android_learning.retrofit.GithubService;
import com.example.sosky.rxjava_android_learning.retrofit.User;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

import static android.text.TextUtils.isEmpty;
import static java.lang.String.format;

public class Retrofit_fragment extends BaseFargment {

    @BindView(R.id.c_comname)
    EditText c_comname;

    @BindView(R.id.c_libname)
    EditText c_libname;

    @BindView(R.id.u_comname)
    EditText u_comname;

    @BindView(R.id.u_libname)
    EditText u_libname;

    @BindView(R.id.list_retrofit)
    ListView log_list;

    private Unbinder unbinder;
    private LogAdapter adapter;
    private List<String> logs;
    private GithubApi githubService;
    private CompositeDisposable compositeDisposable;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String githubToken = getResources().getString(R.string.github_oauth_token);
        githubService =  GithubService.createGithubService(githubToken);
        compositeDisposable = new CompositeDisposable();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setuplog();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.retrofit_fragment,container,false);
        unbinder = ButterKnife.bind(this,layout);
        return layout;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        compositeDisposable.dispose();
    }

    @OnClick(R.id.retrofit_btn)
    public void onclick_com_btn(){
        compositeDisposable.add(
                githubService.contributors(c_comname.getText().toString(),c_libname.getText().toString())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(
                                new DisposableObserver<List<Contributor>>(){

                                    @SuppressLint("DefaultLocale")
                                    @Override
                                    public void onNext(List<Contributor> contributors) {
                                        for (Contributor c :contributors){
                                            log(format("%s has made %d contributions to %s",
                                                    c.login, c.contributions, c_libname.getText().toString()));
                                            Timber.d(
                                                    "%s has made %d contributions to %s",
                                                    c.login, c.contributions, c_libname.getText().toString());
                                        }
                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        Timber.e(e, "woops we got an error while getting the list of contributors");
                                    }

                                    @Override
                                    public void onComplete() {
                                        Timber.d("Retrofit call 1 completed");
                                    }
                                }
                        )
        );
    }

    @OnClick(R.id.retrofit_btn1)
    public void onListContributorsWithFullUserInfoClicked() {
        adapter.clear();
        compositeDisposable.add(
                githubService
                        .contributors(u_comname.getText().toString(), u_libname.getText().toString())
                        .flatMap(Observable::fromIterable)
                        .flatMap(
                                contributor -> {
                                    Observable<User> _userObservable =
                                            githubService
                                                    .user(contributor.login)
                                                    .filter(user -> !isEmpty(user.name ) && !isEmpty(user.email));

                                    return Observable.zip(_userObservable, Observable.just(contributor), Pair::new);
                                })
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(
                                new DisposableObserver<Pair<User, Contributor>>() {
                                    @Override
                                    public void onComplete() {
                                        Timber.d("Retrofit call 2 completed ");
                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        Timber.e(
                                                e,
                                                "error while getting the list of contributors along with full " + "names");
                                    }

                                    @SuppressLint("DefaultLocale")
                                    @Override
                                    public void onNext(Pair<User, Contributor> pair) {
                                        User user = pair.first;
                                        Contributor contributor = pair.second;

                                        adapter.add(
                                                format(
                                                        "%s(%s) has made %d contributions to %s",
                                                        user.name,
                                                        user.email,
                                                        contributor.contributions,
                                                        u_libname.getText().toString()));

                                        adapter.notifyDataSetChanged();

                                        Timber.d(
                                                "%s(%s) has made %d contributions to %s",
                                                user.name,
                                                user.email,
                                                contributor.contributions,
                                                u_libname.getText().toString());
                                    }
                                }));
    }


    private void setuplog(){
        adapter = new LogAdapter(getActivity(),new ArrayList<String>());
        logs = new ArrayList<String>();
        log_list.setAdapter(adapter);
    }

    private Boolean isMainThread(){
        return (Looper.myLooper()==Looper.getMainLooper());
    }

    private void log(String msg){
        if (isMainThread()){
            logs.add(0,msg+"  (main thread) ");
            adapter.clear();
            adapter.addAll(logs);
        }else{
            logs.add(0,msg+"  (not main thread) ");
            adapter.clear();
            adapter.addAll(logs);
        }
    }

}
