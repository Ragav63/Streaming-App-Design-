package com.example.streamingapp.presentation.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.streamingapp.presentation.view.Constants;
import com.example.streamingapp.presentation.view.SeriesPlayerScreenFragment;

public class PipActionReceiver extends BroadcastReceiver {
    private SeriesPlayerScreenFragment fragment;

    public PipActionReceiver(SeriesPlayerScreenFragment fragment) {
        this.fragment = fragment;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (fragment == null) return;

        String action = intent.getAction();
        if (Constants.ACTION_PLAY.equals(action)) {
            fragment.onPlayActionFromActivity();
        } else if (Constants.ACTION_PAUSE.equals(action)) {
            fragment.onPauseActionFromActivity();
        }
    }
}
