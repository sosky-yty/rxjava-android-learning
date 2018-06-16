package com.example.sosky.rxjava_android_learning.rxbus;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.sosky.rxjava_android_learning.Fragment.BaseFargment;
import com.example.sosky.rxjava_android_learning.MainActivity;
import com.example.sosky.rxjava_android_learning.R;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.flowables.ConnectableFlowable;

@SuppressLint("ValidFragment")
class RxBusDemo_BottomFragment extends BaseFargment {

    @BindView(R.id.demo_rxbus_tap_txt)
    TextView _tapEventTxtShow;

    @BindView(R.id.demo_rxbus_tap_count)
    TextView _tapEventCountShow;

    private Unbinder unbinder;
    private RxBus _rxBus;
    private CompositeDisposable _disposables;

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        _disposables.clear();
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        _rxBus = ((MainActivity) getActivity()).getRxBusSingleton();
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_rxbus_bottom,container,false);
        unbinder = ButterKnife.bind(this,layout);
        return layout;
    }

    @Override
    public void onStart() {
        super.onStart();
        _disposables = new CompositeDisposable();

        ConnectableFlowable<Object> tapEventEmitter = _rxBus.asFlowable().publish();

        _disposables
                .add(
                        tapEventEmitter.subscribe(
                                event -> {
                                    if (event instanceof Rxbus_fragment.TapEvent) {
                                        _showTapText();
                                    }
                                }));
        _disposables.add(
                tapEventEmitter
                        .publish(stream -> stream.buffer(stream.debounce(1, TimeUnit.SECONDS)))
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                taps -> {
                                    _showTapCount(taps.size());
                                }));

        _disposables.add(tapEventEmitter.connect());
    }

    private void _showTapText() {
        _tapEventTxtShow.setVisibility(View.VISIBLE);
        _tapEventTxtShow.setAlpha(1f);
        ViewCompat.animate(_tapEventTxtShow).alphaBy(-1f).setDuration(400);
    }

    private void _showTapCount(int size) {
        _tapEventCountShow.setText(String.valueOf(size));
        _tapEventCountShow.setVisibility(View.VISIBLE);
        _tapEventCountShow.setScaleX(1f);
        _tapEventCountShow.setScaleY(1f);
        ViewCompat.animate(_tapEventCountShow)
                .scaleXBy(-1f)
                .scaleYBy(-1f)
                .setDuration(800)
                .setStartDelay(100);
    }
}
