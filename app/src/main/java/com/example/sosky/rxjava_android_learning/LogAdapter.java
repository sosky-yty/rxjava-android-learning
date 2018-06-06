package com.example.sosky.rxjava_android_learning;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.ArrayAdapter;

import java.util.List;

public class LogAdapter extends ArrayAdapter<String> {
    public LogAdapter(@NonNull Context context, List<String> logs) {
        super(context, R.layout.log_item,R.id.item_log,logs);
    }
}
