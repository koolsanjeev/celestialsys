package com.sanjeev.celestialsys;

import android.app.Application;
import android.util.Log;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

/**
 * Created by HP on 1/15/17.
 */
public class CelestialSysApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // Initialize the SDK before executing any other operations,
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        Log.d("HashKey", FacebookSdk.getApplicationSignature(getApplicationContext()));
    }
}
