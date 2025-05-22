package com.example.flowershop;

import android.app.Application;

import com.yandex.mapkit.MapKitFactory;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        MapKitFactory.setApiKey("e1dd47ae-8b52-47dc-8a62-98002f8f601e");
    }
}
