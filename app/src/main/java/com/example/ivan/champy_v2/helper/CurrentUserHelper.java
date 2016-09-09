package com.example.ivan.champy_v2.helper;

import android.content.Context;

import com.example.ivan.champy_v2.SessionManager;

import java.util.HashMap;

/**
 * This class so much help us, when we need to take users data in current session.
 * So we can call needled method and be happy.
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

    public String getPathToPic() {
        return this.currentSession.getPathToPic();
    }

    public String sasd() {
        return this.currentSession.get_duel_pending();
    }

    public String getUserObjectId() {
        return this.currentSession.getObjectId();
    }

}
