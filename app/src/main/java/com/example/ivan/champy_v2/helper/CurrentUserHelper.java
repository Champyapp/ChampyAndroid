package com.example.ivan.champy_v2.helper;

import android.content.Context;

import com.example.ivan.champy_v2.SessionManager;

import java.util.HashMap;

/**
 * Created by azinecdevelopment on 8/25/16.
 */
public class CurrentUserHelper {

    Context generalContext;
    SessionManager currentSession;

    public CurrentUserHelper(Context context) {
        this.generalContext = context;
        this.currentSession = new SessionManager(context);
    }

    public String getToken() {
        HashMap<String, String> user;
        user = this.currentSession.getUserDetails();
        return user.get("token");
    }

    public String getFbId() {

        return this.currentSession.getFacebookId();

    }

    public String getGCM() {
        return this.currentSession.getGCM();

    }

    public String getUserObjectId() {
        return this.currentSession.getObjectId();
    }

}
