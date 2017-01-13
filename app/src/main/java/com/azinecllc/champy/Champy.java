package com.azinecllc.champy;

import android.app.Application;
import android.content.Context;

import com.facebook.FacebookSdk;

/**
 * Created by SashaKhyzhun on 1/10/17.
 */

public class Champy extends Application {

    private static Champy instance;

//    public Champy() {
//        instance = this;
//    }

    @Override
    public void onCreate() {
        super.onCreate();
        FacebookSdk.sdkInitialize(getApplicationContext());
        instance = this;
    }

    public static Context getContext() {
        return instance;
    }

}