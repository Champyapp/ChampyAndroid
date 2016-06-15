package com.example.ivan.champy_v2;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by ivan on 16.01.16.
 */
public class OfflineMode {

    public boolean isInternetAvailable(Activity activity) {
        ConnectivityManager cm =
                (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = cm.getActiveNetworkInfo();
         return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    /*public boolean isInternetAvailable(Activity activity) {
        ConnectivityManager cm =
                (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = cm.getActiveNetworkInfo();
        return activeNetworkInfo.isConnectedOrConnecting();
    }*/
}
