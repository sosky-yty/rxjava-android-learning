package com.example.sosky.rxjava_android_learning.Fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.sosky.rxjava_android_learning.R;
import com.jakewharton.rxbinding2.widget.RxTextView;

import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.subscribers.DisposableSubscriber;
import timber.log.Timber;

import static android.text.TextUtils.isEmpty;
import static android.util.Patterns.EMAIL_ADDRESS;

public class Inputtest_fragment extends BaseFargment {

    @BindView(R.id.email_address)
    EditText editText_email;

    @BindView(R.id.user_name)
    EditText editText_name;

    @BindView(R.id.password)
    EditText editText_password;

    @BindView(R.id.login_btn)
    Button button_login;

    private Unbinder unbinder;
    private DisposableSubscriber<Boolean> disposableObserver = null;
    private Flowable<CharSequence> charSequenceFlowable_email;
    private Flowable<CharSequence> charSequenceFlowable_name;
    private Flowable<CharSequence> charSequenceFlowable_password;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_inputtest,container,false);
        unbinder =  ButterKnife.bind(this,layout);
        charSequenceFlowable_email = RxTextView.textChanges(editText_email).skip(1).toFlowable(BackpressureStrategy.LATEST);
        charSequenceFlowable_name = RxTextView.textChanges(editText_name).skip(1).toFlowable(BackpressureStrategy.LATEST);
        charSequenceFlowable_password = RxTextView.textChanges(editText_password).skip(1).toFlowable(BackpressureStrategy.LATEST);
        comlineEvent();
        return layout;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        disposableObserver.dispose();
    }

    public void comlineEvent(){
        disposableObserver = new DisposableSubscriber<Boolean>() {
            @Override
            public void onNext(Boolean aBoolean) {
                if (aBoolean) {
                    button_login.setBackgroundColor(
                            ContextCompat.getColor(getContext(), R.color.blue));
                } else {
                    button_login.setBackgroundColor(
                            ContextCompat.getColor(getContext(), R.color.gray));
                }
            }

            @Override
            public void onError(Throwable t) {
                Timber.e(t, "there was an error");
            }

            @Override
            public void onComplete() {
                Timber.d("completed");
            }
        };

        Flowable.combineLatest(
                charSequenceFlowable_email,
                charSequenceFlowable_name,
                charSequenceFlowable_password,
                (newEmail, newname, newPassword) -> {
                    boolean emailValid = !isEmpty(newEmail) && EMAIL_ADDRESS.matcher(newEmail).matches();
                    if (!emailValid) {
                        editText_email.setError("Invalid Email!");
                    }

                    boolean passValid = !isEmpty(newname) && newname.length() > 8;
                    if (!passValid) {
                        editText_name.setError("Invalid Password!");
                    }

                    boolean numValid = !isEmpty(newPassword);
                    if (numValid) {
                        int num = Integer.parseInt(newPassword.toString());
                        numValid = num > 8 && num <= 20;
                    }
                    if (!numValid) {
                        editText_password.setError("Invalid Number!");
                    }

                    return emailValid && passValid && numValid;
                })
                .subscribe(disposableObserver);
    }

    public static boolean isInteger(String str) {
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        return pattern.matcher(str).matches();
    }
}
