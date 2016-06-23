package com.example.ivan.champy_v2.helper;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import com.example.ivan.champy_v2.OfflineMode;

public class CHExtensions {

    public boolean isConnectedToRemoteAPI(Activity activity) {
        OfflineMode offlineMode = new OfflineMode();
        Log.i("OfflineMode", "NO INTERNET CONNECTION");
        return offlineMode.isInternetAvailable(activity);

    }

}
