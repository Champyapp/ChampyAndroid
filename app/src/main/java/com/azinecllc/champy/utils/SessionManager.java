package com.azinecllc.champy.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
import android.util.Log;

import com.azinecllc.champy.activity.RoleControllerActivity;
import com.facebook.login.LoginManager;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static com.azinecllc.champy.activity.MainActivity.CURRENT_TAG;
import static com.azinecllc.champy.activity.MainActivity.navItemIndex;
import static com.azinecllc.champy.utils.Constants.TAG_CHALLENGES;
import static com.azinecllc.champy.utils.Constants.path;

public class SessionManager {

    private static SessionManager instance = null;
    private static final String IS_USER_LOGIN = "IsUserLoggedIn"; // All Shared Preferences Keys
    private static final String TOKEN_ANDROID = "token_android"; // Google CloudM Token Android
    private static final String PREFER_NAME = "Champy_pref"; // SharedPreference file name
    private static final String KEY_PATH = "path_to_pic"; // Users path to picture
    private static final String KEY_ID = "facebook_id"; // Users facebook id
    private static final String KEY_EMAIL = "email"; // Email address
    private static final String KEY_NAME = "name"; // User name
    private static final String KEY_GCM = "gcm"; // User's Google Cloud Messaging id (for notification)
    private static final int PRIVATE_MODE = 0;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    // Constructor
    @SuppressLint("CommitPrefEdits")
    private SessionManager(Context context) {
        pref = context.getSharedPreferences(PREFER_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public static SessionManager getInstance(Context context) {
        if (instance == null) {
            instance = new SessionManager(context);
        }
        return instance;
    }

    // Create login session
    public void createUserLoginSession(String name, String email, String facebook_id, String path_to_pic, String token, String id,
                                       String pushN, String newChallengeReq, String acceptedYour, String challengeEnd,
                                       String challengesForToday, String updateDB, String gcm, String token_android) {

        editor.putBoolean(IS_USER_LOGIN,        true);               // Storing login value as TRUE
        editor.putString(KEY_NAME,              name);               // Storing name in pref
        editor.putString(KEY_EMAIL,             email);              // Storing email in pref
        editor.putString(KEY_ID,                facebook_id);        // Storing facebookId
        editor.putString(KEY_PATH,              path_to_pic);        // Storing path to picture (user photo)
        editor.putString("token",               token);              // Storing token
        editor.putString("id",                  id);                 // Storing userId
        editor.putString("pushN",               pushN);              // Storing notification: PushNotifications
        editor.putString("newChallReq",         newChallengeReq);    // Storing notification: new challenge request
        editor.putString("acceptedYour",        acceptedYour);       // Storing notification: accepted your challenge
        editor.putString("challengeEnd",        challengeEnd);       // Storing notification: challenge end
        editor.putString("challengesForToday",  challengesForToday); // Storing notification: daily remind
        editor.putString("updateDB",            updateDB);           // Storing database update
        editor.putString(KEY_GCM,               gcm);                // Storing GCM key
        editor.putString(TOKEN_ANDROID,         token_android);      // Storing GCM key

        Log.i("YO", "LOGGED ID");
        editor.commit(); // commit changes
    }


    public void setChampyOptions(String challenges, String wins, String total, String level) {
        editor.putString("challenges", challenges);
        editor.putString("wins",  wins);
        editor.putString("total", total);
        editor.putString("level", level);
        editor.commit();
    }

//    public void setWakeUpTime(String hour, String min, String requestCode, String inProgressID) {
//        editor.putString("hour", hour);
//        editor.putString("min", min);
//        editor.putString("requestCode", requestCode);
//        editor.putString("inProgressID", inProgressID);
//    }

    public void toggleNewChallengeRequest(String t) {
        editor.putString("newChallReq", t);
        editor.commit();
    }

    public void toggleAcceptYourChallenge(String t) {
        editor.putString("acceptedYour", t);
        editor.commit();
    }

    public void toggleChallengesForToday(String t) {
        editor.putString("challengesForToday", t);
        editor.commit();
    }

    public void togglePushNotification(String t) {
        editor.putString("pushN", t);
        editor.commit();
    }

    public void toggleChallengeEnd(String t) {
        editor.putString("challengeEnd", t);
        editor.commit();
    }

    public void setRefreshPending(String refresh) {
        editor.putString("pendingRefresh", refresh);
        editor.commit();
    }

    public void setRefreshFriends(String refresh) {
        editor.putString("friendsRefresh", refresh);
        editor.commit();
    }

    public void setRefreshOthers(String refresh) {
        editor.putString("otherRefresh", refresh);
        editor.commit();
    }

    public void setDuelPending(String count) {
        editor.putString("duel_pending", count);
        editor.commit();
    }

    public void setUserPicture(String url) {
        editor.putString(KEY_PATH, url);
        editor.commit();
    }

    public void setUserName(String name) {
        editor.putString(KEY_NAME, name);
        editor.commit();
    }

    public void setSelfSize(int size) {
        editor.putInt("SelfSize", size);
        editor.commit();
    }

    public void logout(Activity activity) {
        LoginManager.getInstance().logOut();
        // Clearing all user data from Shared Preferences
        editor.clear();
        editor.commit();

        // go to login activity
        //File blurred = new File(path, "blurred.png");
        //blurred.delete();
        String root = Environment.getExternalStorageDirectory().toString();
        String path = "/android/data/com.azinecllc.champy/images";
        File avatar = new File(root + path, "profile.jpg");
        avatar.delete();

        CURRENT_TAG = TAG_CHALLENGES;
        navItemIndex = 0;
        Intent intent = new Intent(activity, RoleControllerActivity.class);
        activity.startActivity(intent);
    }



    public boolean isUserLoggedIn(){
        return pref.getBoolean(IS_USER_LOGIN, false);
    }

    public int getDuelPending() {
        return Integer.parseInt(pref.getString("duel_pending", ""));
    }

    public String getRefreshPending() {
        return pref.getString("pendingRefresh", "");
    }

    public String getRefreshFriends() {
        return pref.getString("friendsRefresh", "");
    }

    public String getRefreshOthers() {
        return pref.getString("otherRefresh", "");
    }

    public String getTokenAndroid() {
        return pref.getString("token_android", "");
    }

    public String getFacebookId() {
        return pref.getString("facebook_id", "");
    }

    public String getUserPicture() {
        return pref.getString(KEY_PATH, "");
    }

    public String getUserEmail() {
        return pref.getString("email", "");
    }

    public String getUserName() {
        return pref.getString("name", "");
    }

    public String getToken() {
        return pref.getString("token", "");
    }

    public String getUserId() {
        return pref.getString("id", "");
    }

    public String getGCM() {
        return pref.getString(KEY_GCM, "");
    }

    public int getSelfSize() {
        return pref.getInt("SelfSize", 0);
    }

    public HashMap<String, String> getChampyOptions() {
        HashMap<String, String> champy = new HashMap<>();
        champy.put("challenges", pref.getString("challenges", ""));
        champy.put("wins",       pref.getString("wins",       ""));
        champy.put("total",      pref.getString("total",      ""));
        champy.put("level",      pref.getString("level",      ""));
        return champy;
    }

    public HashMap<String, String> getUserDetails() {
        //Use HashMap to store user credentials
        HashMap<String, String> user = new HashMap<>();
        user.put(KEY_NAME,              pref.getString(KEY_NAME,              ""));
        user.put(KEY_EMAIL,             pref.getString(KEY_EMAIL,             ""));
        user.put(KEY_ID,                pref.getString(KEY_EMAIL,             ""));
        user.put(KEY_PATH,              pref.getString(KEY_PATH,              ""));
        user.put("token",               pref.getString("token",               ""));
        user.put("id",                  pref.getString("id",                  ""));
        user.put("pushN",               pref.getString("pushN",               ""));
        user.put("newChallReq",         pref.getString("newChallReq",         ""));
        user.put("acceptedYour",        pref.getString("acceptedYour",        ""));
        user.put("challengeEnd",        pref.getString("challengeEnd",        ""));
        user.put("challengesForToday",  pref.getString("challengesForToday",  ""));
        user.put("updateDB",            pref.getString("updateDB",            ""));

        return user;
    }


//    public HashMap<String, String> getWakeUpDetails() {
//        HashMap<String, String> wakeUpOptions = new HashMap<>();
//        wakeUpOptions.put("hour",         pref.getString("hour",          ""));
//        wakeUpOptions.put("min",          pref.getString("min",           ""));
//        wakeUpOptions.put("requestCode",  pref.getString("requestCode",   ""));
//        wakeUpOptions.put("inProgressID", pref.getString("inProgressID",  ""));
//        return wakeUpOptions;
//    }

}
