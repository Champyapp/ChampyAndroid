package com.example.ivan.champy_v2;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

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

    public boolean isConnectedToRemoteAPI(Activity activity) {
        if (!isInternetAvailable(activity)) {
            Toast.makeText(activity, "Lost internet connection!", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    /*public boolean isInternetAvailable(Activity activity) {
        ConnectivityManager cm =
                (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = cm.getActiveNetworkInfo();
        return activeNetworkInfo.isConnectedOrConnecting();
    }*/
}
