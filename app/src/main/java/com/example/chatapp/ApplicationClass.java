package com.example.chatapp;

import android.app.Application;

import com.onesignal.OneSignal;

public class ApplicationClass extends Application {

    private static final String ONESIGNAL_APP_ID = "f4528dcc-11f4-4011-90af-99d4052e9311";
    @Override
    public void onCreate() {
        super.onCreate();

        // Enable verbose OneSignal logging to debug issues if needed.
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);

        // OneSignal Initialization
        OneSignal.initWithContext(this);
        OneSignal.setAppId(ONESIGNAL_APP_ID);

    }
}
