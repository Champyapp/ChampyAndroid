package com.azinecllc.champy;

import android.app.Application;
import android.content.Context;

import com.facebook.FacebookSdk;

/**
 * Created by Azinec Development Team on 1/10/17.
 * Copyright © 2017 Azinec LLC. All rights reserved.
 * Developed by Sasha Khyzhun.
 * My contacts:
 * sasha.khyzhun@azinec.com
 * sasha.khyzhun@gmail.com
 */

public class Champy extends Application {

    private static Champy instance;

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
