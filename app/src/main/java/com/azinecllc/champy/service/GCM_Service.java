package com.azinecllc.champy.service;

import android.app.IntentService;
import android.content.Intent;
import android.content.IntentSender;
import android.util.Log;

import com.azinecllc.champy.R;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import java.io.IOException;

/**
 * Created by SashaKhyzhun on 2/22/17.
 */

public class GCM_Service extends IntentService {

    private static final String TAG = "RegIntentService";
    private static final String[] TOPICS = {"global"};

    public GCM_Service() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        InstanceID instanceID = InstanceID.getInstance(this);
        String token = null;
        try {
            token = instanceID.getToken(getString(R.string.gcm_defaultSenderId), GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // [END get_token]
        Log.d(TAG, "GCM Registration Token: " + token);
    }

}