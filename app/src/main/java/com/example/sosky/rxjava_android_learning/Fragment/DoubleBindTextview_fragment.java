package com.example.sosky.rxjava_android_learning.Fragment;

import android.os.Bundle;
import android.preference.EditTextPreference;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.example.sosky.rxjava_android_learning.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnTextChanged;
import butterknife.Unbinder;
import io.reactivex.disposables.Disposable;
import io.reactivex.processors.PublishProcessor;

import static android.text.TextUtils.isEmpty;

public class DoubleBindTextview_fragment extends BaseFargment {
    @BindView(R.id.edit_num1)
    EditText enum1;

    @BindView(R.id.edit_num2)
    EditText enum2;

    @BindView(R.id.double_binding_result)
    TextView result;

    private Unbinder unbinder;
    private Disposable disposable;
    private PublishProcessor<Float> resultEmitterSubject;



    @Override
    public void onDestroy() {
        super.onDestroy();
        disposable.dispose();
        unbinder.unbind();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View layout = inflater.inflate(R.layout.fragment_doublebindingtextview,container,false);
            unbinder = ButterKnife.bind(this,layout);
            resultEmitterSubject = PublishProcessor.create();
            disposable = resultEmitterSubject.subscribe(aFloat -> {result.setText(String.valueOf(aFloat));});
            enum2.requestFocus();
            return layout;
    }

    @OnTextChanged({R.id.edit_num1, R.id.edit_num2})
    public void onNumberChanged() {
        float num1 = 0;
        float num2 = 0;

        if (!isEmpty(enum1.getText().toString())) {
            num1 = Float.parseFloat(enum1.getText().toString());
        }

        if (!isEmpty(enum2.getText().toString())) {
            num2 = Float.parseFloat(enum2.getText().toString());
        }

        resultEmitterSubject.onNext(num1 + num2);
    }

}
