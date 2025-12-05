package com.example.streamingapp.presentation.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;

public class PipActionReceiver extends BroadcastReceiver {

    // 1. REMOVE Listener Interface

    // 2. REMOVE Listener Field
    // private final Listener listener;

    // 3. Update Constructor to accept no listener
    public PipActionReceiver() {
        // this.listener = listener; // REMOVE
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null || intent.getAction() == null) return;
        String action = intent.getAction();

        // 4. Send the Intent directly to the Activity/Context
        // We assume context is HomeActivity since it registers the receiver.
        Intent activityIntent = new Intent(context, HomeActivity.class);
        activityIntent.setAction(action);
        activityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(activityIntent);
    }
}
