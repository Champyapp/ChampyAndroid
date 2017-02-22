package com.azinecllc.champy;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;

import com.facebook.FacebookSdk;

/**
 * /*
 * Copyright (C) 2017 AZINEC LLC. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Created by Azinec Development Team on 1/10/17.
 * Copyright © 2017 Azinec LLC. All rights reserved.
 * Developed by Sasha Khyzhun.
 *
 * My contacts:
 * sasha.khyzhun@azinec.com
 * sasha.khyzhun@gmail.com
 */

public class Champy extends Application {

    private static Champy instance = null;

    public static Champy getInstance() {
        return instance;
    }


    // Called when the application is starting, before any other application objects have been created.
    // Overriding this method is totally optional!
    @Override
    public void onCreate() {
        super.onCreate();
        FacebookSdk.sdkInitialize(getApplicationContext());
        System.out.println("CHAMPY! onCreate");
        instance = this;
    }

    // Called by the system when the device configuration changes while your component is running.
    // Overriding this method is totally optional!
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        System.out.println("CHAMPY! onConfigurationChanged");
    }

    // This is called when the overall system is running low on memory,
    // and would like actively running processes to tighten their belts.
    // Overriding this method is totally optional!
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        System.out.println("CHAMPY! onLowMemory");
    }





    public static Context getContext() {
        return instance;
    }

}
