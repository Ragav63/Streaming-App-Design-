package com.example.streamingapp.di;

import android.app.Application;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        AppModule.initialize(this);
    }
}
