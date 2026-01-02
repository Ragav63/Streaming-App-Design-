package com.example.streamingapp;

import android.app.Application;

import com.example.streamingapp.data.local.LocalManager;
import com.example.streamingapp.di.AppModule;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        AppModule.initialize(this);
        LocalManager.init(this);

    }
}
