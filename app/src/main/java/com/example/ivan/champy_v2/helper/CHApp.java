package com.example.ivan.champy_v2.helper;

import android.app.Activity;
import android.app.Application;

public class CHApp extends Application {

    private Activity mCurrentActivity = null;

    public void onCreate() {
        super.onCreate();
    }

    public Activity getCurrentActivity(){
        return mCurrentActivity;
    }

    public void setCurrentActivity(Activity mCurrentActivity){
        this.mCurrentActivity = mCurrentActivity;
    }

}
