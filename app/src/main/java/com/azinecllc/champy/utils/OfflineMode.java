package com.azinecllc.champy.utils;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

public class OfflineMode {

    private static OfflineMode instance = null;

    private OfflineMode() {
    }

    public static OfflineMode getInstance() {
        if (instance == null) {
            instance = new OfflineMode();
        }
        return instance;
    }

    public boolean isConnectedToRemoteAPI(Activity activity) {
        if (isInternetAvailable(activity)) { return true; }
        Toast.makeText(activity, "Lost internet connection!", Toast.LENGTH_SHORT).show();
        return false;
    }

    private boolean isInternetAvailable(Activity activity) {
        ConnectivityManager cm = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = cm.getActiveNetworkInfo();

        return (activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting());
    }

}
